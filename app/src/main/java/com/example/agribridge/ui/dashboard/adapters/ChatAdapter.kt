package com.example.agribridge.ui.dashboard.adapters

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.example.agribridge.databinding.ItemChatBinding
import com.example.agribridge.model.*

class ChatAdapter(private val messages: MutableList<
        ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    
    class ChatViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: ChatMessage) {
            
            when (item.type) {
                
                MessageType.BOT -> {
                    
                    binding.llDispatcherNote.visibility = View.VISIBLE
                    binding.llDriverNote.visibility = View.GONE
                    
                    binding.tvBotMessage.text = item.message
                    binding.tvBotTime.text = item.time
                }
                
                MessageType.USER -> {
                    
                    binding.llDispatcherNote.visibility = View.GONE
                    binding.llDriverNote.visibility = View.VISIBLE
                    
                    binding.tvUserMessage.text = item.message
                    binding.tvUserTime.text = item.time
                }
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        
        return ChatViewHolder(ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position])
    }
    
    override fun getItemCount(): Int = messages.size
}