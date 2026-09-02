package com.example.agribridge.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agribridge.api.ApiResponse
import com.example.agribridge.api.repository.ChatRepository
import com.example.agribridge.model.ChatRequest
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    private val _sendMessageResult = MutableLiveData<ApiResponse?>()
    val sendMessageResult: LiveData<ApiResponse?> = _sendMessageResult

    private val _newConversationResult = MutableLiveData<ApiResponse?>()
    val newConversationResult: LiveData<ApiResponse?> = _newConversationResult

    private val _conversationsResult = MutableLiveData<ApiResponse?>()
    val conversationsResult: LiveData<ApiResponse?> = _conversationsResult

    private val _historyResult = MutableLiveData<ApiResponse?>()
    val historyResult: LiveData<ApiResponse?> = _historyResult

    private val _clearChatResult = MutableLiveData<ApiResponse?>()
    val clearChatResult: LiveData<ApiResponse?> = _clearChatResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun sendMessage(message: String, conversationId: String?, preferredLanguage: String?) {
        _loading.value = true
        viewModelScope.launch {
            val request = ChatRequest(message, conversationId, preferredLanguage)
            val response = repository.sendMessage(request)
            _sendMessageResult.value = response
            _loading.value = false
        }
    }

    fun createNewConversation() {
        _loading.value = true
        viewModelScope.launch {
            val response = repository.createNewConversation()
            _newConversationResult.value = response
            _loading.value = false
        }
    }

    fun listConversations() {
        _loading.value = true
        viewModelScope.launch {
            val response = repository.listConversations()
            _conversationsResult.value = response
            _loading.value = false
        }
    }

    fun getConversationHistory(conversationId: String) {
        _loading.value = true
        viewModelScope.launch {
            val response = repository.getConversationHistory(conversationId)
            _historyResult.value = response
            _loading.value = false
        }
    }

    fun clearChatHistory() {
        _loading.value = true
        viewModelScope.launch {
            val response = repository.clearChatHistory()
            _clearChatResult.value = response
            _loading.value = false
        }
    }

    fun clearSendMessageResult() {
        _sendMessageResult.value = null
    }

    fun clearNewConversationResult() {
        _newConversationResult.value = null
    }

    fun clearClearChatResult() {
        _clearChatResult.value = null
    }
    private val _limitStatusResult = MutableLiveData<ApiResponse?>()
    val limitStatusResult: LiveData<ApiResponse?> = _limitStatusResult

    fun getChatLimitStatus() {
        _loading.value = true
        viewModelScope.launch {
            val response = repository.getChatLimitStatus()
            _limitStatusResult.value = response
            _loading.value = false
        }
    }
}
