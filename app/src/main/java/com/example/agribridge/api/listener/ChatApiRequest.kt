package com.example.agribridge.api.listener

import com.example.agribridge.model.ChatRequest
import com.example.agribridge.model.ConversationItem
import com.example.agribridge.model.ChatHistoryItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApiRequest {

    @POST("chat/")
    suspend fun sendMessage(@Body body: ChatRequest): Response<Any?>?

    @POST("chat/new")
    suspend fun createNewConversation(): Response<Any?>?

    @GET("chat/conversations")
    suspend fun listConversations(): Response<Any?>?

    @GET("chat/history/{conversation_id}")
    suspend fun getConversationHistory(@Path("conversation_id") conversationId: String): Response<Any?>?

    @DELETE("chat/conversations")
    suspend fun clearChatHistory(): Response<Any?>?

    @GET("chat/limit-status")
    suspend fun getChatLimitStatus(): Response<Any?>?
}
