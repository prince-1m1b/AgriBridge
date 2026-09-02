package com.example.agribridge.api.repository

import com.example.agribridge.api.ApiRequestResponse
import com.example.agribridge.api.listener.ChatApiRequest
import com.example.agribridge.model.ChatRequest

class ChatRepository(private val apiRequest: ChatApiRequest?) : ApiRequestResponse() {

    suspend fun sendMessage(body: ChatRequest) = apiRequest {
        apiRequest?.sendMessage(body)
    }

    suspend fun createNewConversation() = apiRequest {
        apiRequest?.createNewConversation()
    }

    suspend fun listConversations() = apiRequest {
        apiRequest?.listConversations()
    }

    suspend fun getConversationHistory(conversationId: String) = apiRequest {
        apiRequest?.getConversationHistory(conversationId)
    }

    suspend fun clearChatHistory() = apiRequest {
        apiRequest?.clearChatHistory()
    }

    suspend fun getChatLimitStatus() = apiRequest {
        apiRequest?.getChatLimitStatus()
    }
}
