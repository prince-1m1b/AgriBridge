package com.example.agribridge.ui.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agribridge.databinding.ItemBannerBinding
import com.example.agribridge.model.BannerModel

class BannerAdapter(private val list: List<BannerModel>) :
    RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemBannerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBannerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.tvBannerTitle.text = item.title
        holder.binding.tvBannerDescription.text = item.description
    }

    override fun getItemCount() = list.size
}