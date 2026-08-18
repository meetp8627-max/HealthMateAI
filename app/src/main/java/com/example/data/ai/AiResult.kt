package com.example.data.ai

sealed class AiResult<out T> {
    data class Success<out T>(val data: T) : AiResult<T>()
    data class RateLimited(val retryAfterSeconds: Int = 30) : AiResult<Nothing>()
    object NetworkError : AiResult<Nothing>()
    object InvalidResponse : AiResult<Nothing>()
    object AuthError : AiResult<Nothing>()
    data class UnknownError(val userFriendlyMessage: String) : AiResult<Nothing>()
}

fun parseRetryDelaySeconds(text: String): Int? {
    try {
        val regex = Regex("""(?i)(?:retry in|retry after|wait)\s*(\d+)\s*s""", RegexOption.IGNORE_CASE)
        val match = regex.find(text)
        if (match != null) {
            return match.groupValues[1].toIntOrNull()
        }
        val secondsRegex = Regex("""(\d+)\s*second""", RegexOption.IGNORE_CASE)
        val secondMatch = secondsRegex.find(text)
        if (secondMatch != null) {
            return secondMatch.groupValues[1].toIntOrNull()
        }
    } catch (_: Exception) {
    }
    return null
}
