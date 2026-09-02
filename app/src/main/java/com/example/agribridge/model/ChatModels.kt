package com.example.agribridge.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ChatMessage(
    val message: String,
    val time: String,
    val type: MessageType
) : Serializable

// --- Request/Response structures for the Chat API ---

data class ChatRequest(
    @SerializedName("message") val message: String,
    @SerializedName("conversation_id") val conversationId: String? = null,
    @SerializedName("preferred_language") val preferredLanguage: String? = null
)

data class ChatResponseData(
    @SerializedName("response") val response: String? = null,
    @SerializedName("detected_language") val detectedLanguage: String? = null,
    @SerializedName("response_language") val responseLanguage: String? = null,
    @SerializedName("conversation_id") val conversationId: String? = null
)

data class ChatNewResponseData(
    @SerializedName("conversation_id") val conversationId: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class ConversationItem(
    @SerializedName("conversation_id") val conversationId: String,
    @SerializedName("title") val title: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class ChatHistoryItem(
    @SerializedName("user_message") val userMessage: String? = null,
    @SerializedName("bot_response") val botResponse: String? = null,
    @SerializedName("detected_language") val detectedLanguage: String? = null,
    @SerializedName("response_language") val responseLanguage: String? = null,
    @SerializedName("timestamp") val timestamp: String? = null
)

data class LimitStatusResponseData(
    @SerializedName("daily_limit") val dailyLimit: Int = 10,
    @SerializedName("questions_asked") val questionsAsked: Int = 0,
    @SerializedName("questions_remaining") val questionsRemaining: Int = 0,
    @SerializedName("limit_reached") val limitReached: Boolean = false,
    @SerializedName("reset_time_utc") val resetTimeUtc: String? = null,
    @SerializedName("seconds_until_reset") val secondsUntilReset: Long = 0
)
