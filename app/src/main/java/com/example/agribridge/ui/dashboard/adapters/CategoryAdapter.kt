package com.example.agribridge.ui.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.agribridge.R
import com.example.agribridge.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val list: List<String>,
    private val onCategoryClick: ((String, Int) -> Unit)? = null
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    // First item selected by default
    private var selectedPosition = 0

    class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvCategory.text = list[position]

        // Selection Logic
        if (position == selectedPosition) {
            holder.binding.tvCategory.setBackgroundResource(R.drawable.bg_category_selected_n)
            holder.binding.tvCategory.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.white
                )
            )
        } else {
            holder.binding.tvCategory.setBackgroundResource(R.drawable.ic_bg_rounded_white_15_dp)
            holder.binding.tvCategory.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.black
                )
            )
        }

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position

            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            onCategoryClick?.invoke(list[position], position)
        }
    }

    override fun getItemCount() = list.size
}