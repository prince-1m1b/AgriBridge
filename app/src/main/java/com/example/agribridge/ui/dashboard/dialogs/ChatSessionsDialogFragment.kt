package com.example.agribridge.ui.dashboard.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agribridge.databinding.DialogChatSessionsBinding
import com.example.agribridge.model.ConversationItem
import com.example.agribridge.ui.dashboard.adapters.ChatSessionAdapter

class ChatSessionsDialogFragment : DialogFragment() {

    private var _binding: DialogChatSessionsBinding? = null
    private val binding get() = _binding!!

    private var sessions: List<ConversationItem> = emptyList()
    private var onSessionSelected: ((ConversationItem) -> Unit)? = null
    private var onCreateNewChat: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogChatSessionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvSessions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSessions.adapter = ChatSessionAdapter(sessions) { item ->
            onSessionSelected?.invoke(item)
            dismiss()
        }

        binding.ivClose.setOnClickListener { dismiss() }
        binding.btnNewChat.setOnClickListener {
            onCreateNewChat?.invoke()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            val displayMetrics = resources.displayMetrics
            val marginInPx = (20 * displayMetrics.density).toInt()
            val width = displayMetrics.widthPixels - (2 * marginInPx)
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawableResource(android.R.color.transparent)
            window.decorView.setPadding(0, 0, 0, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ChatSessionsDialogFragment"

        fun newInstance(
            sessions: List<ConversationItem>,
            onSessionSelected: (ConversationItem) -> Unit,
            onCreateNewChat: () -> Unit
        ): ChatSessionsDialogFragment {
            return ChatSessionsDialogFragment().apply {
                this.sessions = sessions
                this.onSessionSelected = onSessionSelected
                this.onCreateNewChat = onCreateNewChat
            }
        }
    }
}
