package com.example.agribridge.ui.dashboard.adapters

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.example.agribridge.databinding.ItemSuggestionBinding

class SuggestionAdapter(private val suggestions: ArrayList<String>, private val onClick: (String) -> Unit) : RecyclerView.Adapter<SuggestionAdapter.ViewHolder>() {
    
    class ViewHolder(val binding: ItemSuggestionBinding) : RecyclerView.ViewHolder(binding.root)
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        
        val binding = ItemSuggestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = suggestions[position]
        
        with(holder.binding) {
            tvSuggestion.text = item
            
            root.setOnClickListener {
                onClick(item)
            }
        }
    }
    
    override fun getItemCount(): Int {
        return suggestions.size
    }
}