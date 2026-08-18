package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class AiAssistantService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun getHealthAssistantResponse(userPrompt: String, history: List<Pair<String, String>> = emptyList()): AiResult<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                Log.e("HealthMateAI", "Gemini API key is not configured in Secrets")
                return@withContext AiResult.AuthError
            }

            val systemInstruction = """
                You are HealthMate AI, an intelligent, empathetic, and responsible healthcare medication assistant.
                
                YOUR ROLE:
                - Help users understand medication label terms (e.g. what 'mg', 'mL', 'active ingredient', 'batch number' mean).
                - Explain common packaging language and organize medication reminders.
                - Assist users in reading and organizing their prescribed medication schedules.

                STRICT MEDICAL SAFETY RULES (NON-NEGOTIABLE):
                1. NEVER diagnose illnesses or medical conditions.
                2. NEVER prescribe medications or suggest specific drug treatments.
                3. NEVER recommend starting, stopping, increasing, decreasing, or substituting any prescribed medication.
                4. NEVER guess missing details from unclear packages.
                5. If a user asks "Should I take this medicine?" respond: "I can help explain information printed on package labels, but whether you should take a specific medicine depends on instructions from your doctor or pharmacist."
                6. For urgent or emergency questions, immediately urge the user to contact emergency services or a qualified healthcare professional.
                7. Always keep responses clear, friendly, direct, and concise.
            """.trimIndent()

            val contentsArray = JSONArray()

            // Add history context if any
            for ((role, text) in history) {
                contentsArray.put(JSONObject().apply {
                    put("role", if (role == "user") "user" else "model")
                    put("parts", JSONArray().put(JSONObject().put("text", text)))
                })
            }

            // Current prompt
            contentsArray.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().put("text", userPrompt)))
            })

            val jsonRequest = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", systemInstruction)))
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.3)
                    put("maxOutputTokens", 800)
                })
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
                Log.e("HealthMateAI", "[AI Assistant] Gemini HTTP error $responseCode: $responseBodyString")

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
                    return@withContext AiResult.UnknownError("HealthMate AI is temporarily unavailable.")
                }
            }

            val jsonResponse = JSONObject(responseBodyString)
            val candidates = jsonResponse.optJSONArray("candidates")
            val text = candidates?.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)?.optString("text")
                ?: return@withContext AiResult.InvalidResponse

            AiResult.Success(text)
        } catch (e: IOException) {
            Log.e("HealthMateAI", "[AI Assistant] Network error", e)
            AiResult.NetworkError
        } catch (e: Exception) {
            Log.e("HealthMateAI", "[AI Assistant] Unexpected exception", e)
            val msg = e.message ?: ""
            if (msg.contains("429") || msg.contains("RESOURCE_EXHAUSTED", ignoreCase = true)) {
                AiResult.RateLimited(parseRetryDelaySeconds(msg) ?: 30)
            } else {
                AiResult.UnknownError("HealthMate AI is temporarily unavailable.")
            }
        }
    }
}
