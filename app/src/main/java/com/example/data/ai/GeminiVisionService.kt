package com.example.data.ai

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

class GeminiVisionService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val adapter = moshi.adapter(MedicineScanResult::class.java)

    suspend fun analyzeMedicinePackage(bitmap: Bitmap): AiResult<MedicineScanResult> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                Log.e("HealthMateAI", "Gemini API key is missing or unconfigured")
                return@withContext AiResult.AuthError
            }

            // Convert Bitmap to Base64 JPEG
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            val base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

            val promptText = """
                You are a medication packaging information extraction assistant.
                Analyze ONLY the visible information in the provided medicine packaging image.
                Extract text and medication-related information exactly as printed whenever possible.
                Do NOT guess or invent missing information. If a field cannot be clearly read, set its value to "Not clearly visible".
                Do NOT infer a medicine's dosage schedule or prescription instructions.
                Do NOT prescribe or recommend starting/stopping/substituting medication.

                Return ONLY a JSON object with the exact following schema:
                {
                  "medicine_name": "Name of medicine/drug or 'Not clearly visible'",
                  "brand_name": "Brand name or 'Not clearly visible'",
                  "active_ingredients": ["Ingredient 1", "Ingredient 2"],
                  "strength": "e.g. 500 mg, 10mg/5mL or 'Not clearly visible'",
                  "dosage_form": "Tablet, Capsule, Syrup, Cream, Drops, Injection, Powder, or Other",
                  "manufacturer": "Manufacturer name or 'Not clearly visible'",
                  "expiry_date": "MM/YYYY or DD/MM/YYYY or 'Not clearly visible'",
                  "batch_number": "Batch/Lot number or 'Not clearly visible'",
                  "quantity": "Pack size / tablet count or 'Not clearly visible'",
                  "visible_warnings": ["Warning 1", "Warning 2"],
                  "confidence": "High" or "Medium" or "Low",
                  "uncertain_fields": ["field_names_that_were_unclear"],
                  "needs_manual_verification": true
                }
            """.trimIndent()

            val jsonRequest = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            // Text Part
                            put(JSONObject().apply { put("text", promptText) })
                            // Image Part
                            put(JSONObject().apply {
                                put("inlineData", JSONObject().apply {
                                    put("mimeType", "image/jpeg")
                                    put("data", base64Image)
                                })
                            })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)

                // Enforce JSON response format
                val generationConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.1)
                }
                put("generationConfig", generationConfig)
            }

            val requestUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(requestUrl)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseCode = response.code
            val responseBodyString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e("HealthMateAI", "[AI Scanner] Gemini HTTP error code $responseCode: $responseBodyString")

                if (responseCode == 429 ||
                    responseBodyString.contains("RESOURCE_EXHAUSTED", ignoreCase = true) ||
                    responseBodyString.contains("quota", ignoreCase = true) ||
                    responseBodyString.contains("rate_limit", ignoreCase = true) ||
                    responseBodyString.contains("free_tier", ignoreCase = true)
                ) {
                    val retrySeconds = parseRetryDelaySeconds(responseBodyString) ?: 30
                    return@withContext AiResult.RateLimited(retrySeconds)
                } else if (responseCode == 401 || responseCode == 403 || responseBodyString.contains("API_KEY_INVALID", ignoreCase = true)) {
                    return@withContext AiResult.AuthError
                } else {
                    return@withContext AiResult.UnknownError("Unable to analyze medicine package image. Please try again or enter details manually.")
                }
            }

            if (responseBodyString.isBlank()) {
                Log.e("HealthMateAI", "[AI Scanner] Gemini response body was blank")
                return@withContext AiResult.InvalidResponse
            }

            val jsonResponse = try {
                JSONObject(responseBodyString)
            } catch (e: Exception) {
                Log.e("HealthMateAI", "[AI Scanner] Failed to parse Gemini root response JSON", e)
                return@withContext AiResult.InvalidResponse
            }

            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                Log.e("HealthMateAI", "[AI Scanner] Gemini response contained zero candidates")
                return@withContext AiResult.InvalidResponse
            }

            val firstCandidate = candidates.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val rawText = parts?.optJSONObject(0)?.optString("text", "") ?: ""

            if (rawText.isBlank()) {
                Log.e("HealthMateAI", "[AI Scanner] Gemini returned empty text part")
                return@withContext AiResult.InvalidResponse
            }

            val cleanedJson = cleanJsonText(rawText)

            val scanResult = try {
                adapter.fromJson(cleanedJson)
            } catch (e: Exception) {
                Log.e("HealthMateAI", "[AI Scanner] Moshi failed to parse scan result: $cleanedJson", e)
                null
            } ?: return@withContext AiResult.InvalidResponse

            AiResult.Success(scanResult)
        } catch (e: IOException) {
            Log.e("HealthMateAI", "[AI Scanner] Network exception", e)
            AiResult.NetworkError
        } catch (e: Exception) {
            Log.e("HealthMateAI", "[AI Scanner] Unexpected exception", e)
            val msg = e.message ?: ""
            if (msg.contains("429") || msg.contains("RESOURCE_EXHAUSTED", ignoreCase = true)) {
                AiResult.RateLimited(parseRetryDelaySeconds(msg) ?: 30)
            } else {
                AiResult.UnknownError("Unable to analyze medicine package image. Please try again or enter details manually.")
            }
        }
    }

    private fun cleanJsonText(text: String): String {
        var cleaned = text.trim()
        if (cleaned.startsWith("```json", ignoreCase = true)) {
            cleaned = cleaned.substring(7)
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3)
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length - 3)
        }
        cleaned = cleaned.trim()

        val firstBrace = cleaned.indexOf('{')
        val lastBrace = cleaned.lastIndexOf('}')
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            cleaned = cleaned.substring(firstBrace, lastBrace + 1)
        }
        return cleaned
    }
}
