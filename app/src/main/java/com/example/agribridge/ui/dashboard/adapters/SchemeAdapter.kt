package com.example.agribridge.ui.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.agribridge.R
import com.example.agribridge.databinding.ItemSchemeBinding
import com.example.agribridge.model.SchemeModel

class SchemeAdapter(
    private val onItemClick: (SchemeModel) -> Unit = {}
) : ListAdapter<SchemeModel, SchemeAdapter.ViewHolder>(DiffCallback()) {

    // ─── DiffUtil ────────────────────────────────────────────────────────────

    class DiffCallback : DiffUtil.ItemCallback<SchemeModel>() {
        override fun areItemsTheSame(old: SchemeModel, new: SchemeModel) = old.title == new.title
        override fun areContentsTheSame(old: SchemeModel, new: SchemeModel) = old == new
    }

    // ─── ViewHolder ──────────────────────────────────────────────────────────

    class ViewHolder(val binding: ItemSchemeBinding) : RecyclerView.ViewHolder(binding.root)

    // ─── Inflation ───────────────────────────────────────────────────────────

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemSchemeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    // ─── Binding ─────────────────────────────────────────────────────────────

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val ctx = holder.binding.root.context

        with(holder.binding) {

            // ── Scheme image (Glide) ─────────────────────────────────────────
            Glide.with(ctx).load(item.imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .placeholder(R.color.green_image_placeholder).error(R.color.green_image_placeholder)
                .centerCrop().into(ivSchemeImage)

            // ── State / location badge ────────────────────────────────────────
            tvState.text = item.state ?: "National"

            // ── Text content ──────────────────────────────────────────────────
            tvSchemeName.text = item.title
            tvSubtitle.text = item.subtitle.orEmpty()
            tvDescription.text = item.description.orEmpty()

            // ── Official link ─────────────────────────────────────────────────
            tvOfficialLink.text =
                item.officialUrl?.removePrefix("https://")?.removeSuffix("/").orEmpty()

            // ── Click handlers ────────────────────────────────────────────────
            root.setOnClickListener { onItemClick(item) }

            tvOfficialLink.setOnClickListener {
                item.officialUrl?.let { url ->
                    ctx.startActivity(
                        android.content.Intent(
                            android.content.Intent.ACTION_VIEW, url.toUri()
                        )
                    )
                }
            }
        }
    }
}