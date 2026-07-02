package com.example.agribridge.ui.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agribridge.databinding.ItemChatSessionBinding
import com.example.agribridge.model.ConversationItem

class ChatSessionAdapter(
    private val items: List<ConversationItem>,
    private val onSessionClicked: (ConversationItem) -> Unit
) : RecyclerView.Adapter<ChatSessionAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemChatSessionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvSessionTitle.text = if (item.title.isNullOrBlank()) "Untitled Chat" else item.title
            
            val rawTime = item.updatedAt ?: item.createdAt
            tvSessionDate.text = if (!rawTime.isNullOrEmpty()) formatSessionTime(rawTime) else ""
            
            root.setOnClickListener { onSessionClicked(item) }
        }
    }

    private fun formatSessionTime(timestamp: String): String {
        return try {
            val parser = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
            val date = parser.parse(timestamp)
            java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(date ?: java.util.Date())
        } catch (e: Exception) {
            try {
                val parser = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                val date = parser.parse(timestamp)
                java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(date ?: java.util.Date())
            } catch (e2: Exception) {
                timestamp
            }
        }
    }

    override fun getItemCount() = items.size
}
