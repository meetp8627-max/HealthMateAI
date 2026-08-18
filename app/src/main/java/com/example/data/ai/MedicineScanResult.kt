package com.example.data.ai

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MedicineScanResult(
    @Json(name = "medicine_name") val medicineName: String = "Not clearly visible",
    @Json(name = "brand_name") val brandName: String = "Not clearly visible",
    @Json(name = "active_ingredients") val activeIngredients: List<String> = emptyList(),
    @Json(name = "strength") val strength: String = "Not clearly visible",
    @Json(name = "dosage_form") val dosageForm: String = "Tablet",
    @Json(name = "manufacturer") val manufacturer: String = "Not clearly visible",
    @Json(name = "expiry_date") val expiryDate: String = "Not clearly visible",
    @Json(name = "batch_number") val batchNumber: String = "Not clearly visible",
    @Json(name = "quantity") val quantity: String = "Not clearly visible",
    @Json(name = "visible_warnings") val visibleWarnings: List<String> = emptyList(),
    @Json(name = "confidence") val confidence: String = "Medium", // High, Medium, Low
    @Json(name = "uncertain_fields") val uncertainFields: List<String> = emptyList(),
    @Json(name = "needs_manual_verification") val needsManualVerification: Boolean = true
)
