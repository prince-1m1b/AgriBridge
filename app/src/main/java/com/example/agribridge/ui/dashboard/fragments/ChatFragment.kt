package com.example.agribridge.ui.dashboard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agribridge.api.listener.ChatApiRequest
import com.example.agribridge.api.repository.ChatRepository
import com.example.agribridge.databinding.FragmentChatBinding
import com.example.agribridge.model.*
import com.example.agribridge.ui.dashboard.adapters.ChatAdapter
import com.example.agribridge.ui.dashboard.adapters.SuggestionAdapter
import com.example.agribridge.ui.dashboard.dialogs.ChatSessionsDialogFragment
import com.example.agribridge.utils.LanguageManager
import com.example.agribridge.utils.Preference
import com.example.agribridge.utils.getData
import com.example.agribridge.utils.request
import com.example.agribridge.utils.toast
import com.example.agribridge.viewmodel.ChatViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private var questions: ArrayList<String> = arrayListOf()
    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()

    private val chatViewModel: ChatViewModel by lazy {
        val factory = viewModelFactory {
            initializer {
                ChatViewModel(ChatRepository(requireContext().request(ChatApiRequest::class.java)))
            }
        }
        ViewModelProvider(this, factory)[ChatViewModel::class.java]
    }

    private var currentConversationId: String? = null
    private var conversationSessions: List<ConversationItem> = emptyList()
    private var isManualSessionsRequest = false
    private var isSendingMessage = false
    private var isLimitReached = false
    private var countDownTimer: android.os.CountDownTimer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        onClicks()
        setAdapter()
        setObservers()

        // Fetch user's conversation logs
        chatViewModel.listConversations()
        
        // Fetch daily chat limit status
        chatViewModel.getChatLimitStatus()
    }

    private fun init() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val extraPadding = (12 * resources.displayMetrics.density).toInt()
            binding.chatHeader.updatePadding(top = statusBars + extraPadding)

            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

            binding.chatInputContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = if (imeVisible) imeHeight else 0
            }

            if (imeVisible) {
                scrollToBottom()
            }
            insets
        }

        questions = arrayListOf(
            "What is crop insurance?",
            "Weather forecast",
            "Best fertilizer",
            "Government schemes",
            "Market prices",
            "Pest control",
            "Soil health",
            "Crop planning"
        )
    }

    private fun onClicks() {
        binding.apply {
            etChat.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    rvChat.postDelayed({
                        scrollToBottom()
                    }, 150)
                }
            }

            ivSend.setOnClickListener {
                if (isLimitReached) {
                    chatViewModel.getChatLimitStatus() // Re-check limit
                    return@setOnClickListener
                }
                val text = etChat.text?.toString()?.trim().orEmpty()
                if (text.isEmpty()) return@setOnClickListener
                etChat.text?.clear()
                sendUserMessage(text)
            }

            ivNewChat.setOnClickListener {
                chatViewModel.createNewConversation()
            }

            ivSessions.setOnClickListener {
                isManualSessionsRequest = true
                chatViewModel.listConversations()
            }
        }
    }

    private fun setAdapter() {
        binding.apply {
            chatAdapter = ChatAdapter(chatMessages)
            rvChat.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = chatAdapter
                itemAnimator = null
                setHasFixedSize(false)
                overScrollMode = View.OVER_SCROLL_NEVER
            }

            chatAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    scrollToBottom()
                }
            })

            rvSuggestions.layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
            }

            rvSuggestions.adapter = SuggestionAdapter(questions) { question ->
                hideSuggestions()
                sendUserMessage(question)
            }
        }
    }

    private fun hideSuggestions() {
        binding.rvSuggestions.visibility = View.GONE
    }

    private fun showSuggestions() {
        binding.rvSuggestions.visibility = View.VISIBLE
    }

    private fun sendUserMessage(text: String) {
        hideSuggestions()
        addMessage(ChatMessage(message = text, time = getCurrentTime(), type = MessageType.USER))

        val langCode = LanguageManager.getLanguage(requireContext())
        val language = when (langCode) {
            "hi" -> "hindi"
            "kn" -> "kannada"
            "mr" -> "marathi"
            else -> "english"
        }

        isSendingMessage = true
        chatViewModel.sendMessage(text, currentConversationId, language)
    }

    private fun addMessage(message: ChatMessage) {
        chatMessages.add(message)
        chatAdapter.notifyItemInserted(chatMessages.lastIndex)
        scrollToBottom()
    }

    private fun scrollToBottom() {
        binding.rvChat.post {
            val lm = binding.rvChat.layoutManager as LinearLayoutManager
            val lastPosition = chatAdapter.itemCount - 1
            if (lastPosition >= 0) {
                lm.scrollToPositionWithOffset(lastPosition, 0)
            }
        }
    }

    private fun getCurrentTime(timestamp: String? = null): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = parser.parse(timestamp ?: "")
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date ?: Date())
        } catch (e: Exception) {
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        }
    }

    private fun setObservers() {
        chatViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isSendingMessage) {
                binding.chatLoading.visibility = View.GONE
                binding.botResponseLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
                if (!isLoading) {
                    isSendingMessage = false
                } else {
                    scrollToBottom()
                }
            } else {
                binding.chatLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.botResponseLoading.visibility = View.GONE
            }
        }

        chatViewModel.sendMessageResult.observe(viewLifecycleOwner) { apiResponse ->
            isSendingMessage = false
            binding.botResponseLoading.visibility = View.GONE
            if (apiResponse == null) return@observe
            if (apiResponse.statusCode == 200 || apiResponse.type?.equals("success", ignoreCase = true) == true) {
                val dataObj = getData(apiResponse.data, ChatResponseData::class.java)
                dataObj?.let {
                    currentConversationId = it.conversationId
                    val botMessage = ChatMessage(
                        message = it.response ?: "",
                        time = getCurrentTime(),
                        type = MessageType.BOT
                    )
                    addMessage(botMessage)
                }
            } else {
                requireContext().toast(apiResponse.message ?: "Failed to generate AI response")
            }
            chatViewModel.clearSendMessageResult()
        }

        chatViewModel.newConversationResult.observe(viewLifecycleOwner) { apiResponse ->
            if (apiResponse == null) return@observe
            if (apiResponse.statusCode == 201 || apiResponse.type?.equals("success", ignoreCase = true) == true) {
                val dataObj = getData(apiResponse.data, ChatNewResponseData::class.java)
                dataObj?.let {
                    currentConversationId = it.conversationId
                    binding.tvConversationTitle.text = it.title ?: "New Chat"
                    chatMessages.clear()
                    chatAdapter.notifyDataSetChanged()
                    requireContext().toast("New conversation session started")
                    showSuggestions()
                }
            } else {
                requireContext().toast(apiResponse.message ?: "Failed to create conversation session")
            }
            chatViewModel.clearNewConversationResult()
        }

        chatViewModel.conversationsResult.observe(viewLifecycleOwner) { apiResponse ->
            if (apiResponse == null) return@observe
            if (apiResponse.statusCode == 200 || apiResponse.type?.equals("success", ignoreCase = true) == true) {
                val jsonString = Gson().toJson(apiResponse.data)
                val type = object : TypeToken<List<ConversationItem>>() {}.type
                val items: List<ConversationItem> = Gson().fromJson(jsonString, type) ?: emptyList()
                conversationSessions = items

                if (currentConversationId == null && items.isNotEmpty()) {
                    val latest = items.first()
                    currentConversationId = latest.conversationId
                    binding.tvConversationTitle.text = latest.title ?: "Chat"
                    chatViewModel.getConversationHistory(latest.conversationId)
                } else if (items.isEmpty() && currentConversationId == null && chatMessages.isEmpty()) {
                    addMessage(ChatMessage(
                        message = "👋 Welcome to AgriBridge Assistant. Ask me anything about farming.",
                        time = getCurrentTime(),
                        type = MessageType.BOT
                    ))
                    showSuggestions()
                }

                // If user triggered list fetch manually via ivSessions button, show dialog
                if (isManualSessionsRequest) {
                    isManualSessionsRequest = false
                    val dialog = ChatSessionsDialogFragment.newInstance(
                        items,
                        onSessionSelected = { item ->
                            currentConversationId = item.conversationId
                            binding.tvConversationTitle.text = item.title ?: "Chat"
                            chatMessages.clear()
                            chatAdapter.notifyDataSetChanged()
                            chatViewModel.getConversationHistory(item.conversationId)
                        },
                        onCreateNewChat = {
                            chatViewModel.createNewConversation()
                        }
                    )
                    dialog.show(parentFragmentManager, ChatSessionsDialogFragment.TAG)
                }
            }
        }

        chatViewModel.historyResult.observe(viewLifecycleOwner) { apiResponse ->
            if (apiResponse == null) return@observe
            if (apiResponse.statusCode == 200 || apiResponse.type?.equals("success", ignoreCase = true) == true) {
                val jsonString = Gson().toJson(apiResponse.data)
                val type = object : TypeToken<List<ChatHistoryItem>>() {}.type
                val items: List<ChatHistoryItem> = Gson().fromJson(jsonString, type) ?: emptyList()

                chatMessages.clear()
                items.forEach { history ->
                    history.userMessage?.let { msg ->
                        chatMessages.add(ChatMessage(msg, getCurrentTime(history.timestamp), MessageType.USER))
                    }
                    history.botResponse?.let { res ->
                        chatMessages.add(ChatMessage(res, getCurrentTime(history.timestamp), MessageType.BOT))
                    }
                }
                chatAdapter.notifyDataSetChanged()
                scrollToBottom()

                if (items.isEmpty()) {
                    showSuggestions()
                } else {
                    hideSuggestions()
                }
            }
        }

        chatViewModel.limitStatusResult.observe(viewLifecycleOwner) { apiResponse ->
            if (apiResponse == null) return@observe
            if (apiResponse.statusCode == 200 || apiResponse.type?.equals("success", ignoreCase = true) == true) {
                val dataObj = getData(apiResponse.data, LimitStatusResponseData::class.java)
                dataObj?.let {
                    isLimitReached = it.limitReached
                    if (it.limitReached) {
                        // Disable chat UI
                        binding.etChat.isEnabled = false
                        binding.etChat.hint = "Daily limit reached"
                        hideSuggestions()
                        
                        // Hide chat UI and toolbar icons
                        binding.rvChat.visibility = View.GONE
                        binding.chatInputContainer.visibility = View.GONE
                        binding.ivSessions.visibility = View.GONE
                        binding.ivNewChat.visibility = View.GONE
                        
                        // Show overlay
                        binding.limitOverlayContainer.visibility = View.VISIBLE
                        
                        // Setup timer
                        startLimitTimer(it.secondsUntilReset)
                        
                        // Setup dynamic limit message
                        binding.limitOverlay.tvMessage.text = "You have reached your daily limit of ${it.dailyLimit} questions. You can ask more questions after the timer resets."
                        
                        // Reset refresh button state (in case it was loading)
                        binding.limitOverlay.btnRefresh.text = "Refresh"
                        binding.limitOverlay.btnRefresh.isEnabled = true
                        
                        // Setup refresh button
                        binding.limitOverlay.btnRefresh.setOnClickListener {
                            binding.limitOverlay.btnRefresh.text = "Refreshing..."
                            binding.limitOverlay.btnRefresh.isEnabled = false
                            chatViewModel.getChatLimitStatus()
                        }
                    } else {
                        // Hide overlay
                        binding.limitOverlayContainer.visibility = View.GONE
                        countDownTimer?.cancel()

                        // Show chat UI and toolbar icons
                        binding.rvChat.visibility = View.VISIBLE
                        binding.chatInputContainer.visibility = View.VISIBLE
                        binding.ivSessions.visibility = View.VISIBLE
                        binding.ivNewChat.visibility = View.VISIBLE

                        // Enable chat input
                        binding.etChat.isEnabled = true
                        binding.etChat.hint = getString(com.example.agribridge.R.string.ask_anything)
                        if (chatMessages.isEmpty()) {
                            showSuggestions()
                        }
                    }
                }
            }
        }
    }

    private fun startLimitTimer(secondsUntilReset: Long) {
        countDownTimer?.cancel()
        countDownTimer = object : android.os.CountDownTimer(secondsUntilReset * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val h = seconds / 3600
                val m = (seconds % 3600) / 60
                val s = seconds % 60
                
                binding.limitOverlay.tvCountdown.text = String.format("%02d:%02d:%02d", h, m, s)
            }

            override fun onFinish() {
                binding.limitOverlay.tvCountdown.text = "00:00:00"
                binding.limitOverlay.btnRefresh.text = "Start Chatting"
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }
}