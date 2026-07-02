package com.example.agribridge.ui.dashboard.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.agribridge.R
import com.example.agribridge.databinding.ItemSubsidyBinding
import com.example.agribridge.model.SubsidyModel
import com.example.agribridge.model.SubsidyModel.AccentColor

class SubsidyAdapter(
    private val onApplyClick: (SubsidyModel) -> Unit = {}
) : ListAdapter<SubsidyModel, SubsidyAdapter.ViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<SubsidyModel>() {
        override fun areItemsTheSame(old: SubsidyModel, new: SubsidyModel) =
            old.subsidyName == new.subsidyName

        override fun areContentsTheSame(old: SubsidyModel, new: SubsidyModel) = old == new
    }

    class ViewHolder(val binding: ItemSubsidyBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemSubsidyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val ctx = holder.binding.root.context

        with(holder.binding) {

            // ── 1. Accent stripe + chip (by applicable_category) ──────────
            val accent = item.accent
            viewSubsidyStripe.setBackgroundColor(ContextCompat.getColor(ctx, stripeColor(accent)))
            chipCategory.setChipBackgroundColorResource(chipBg(accent))
            chipCategory.setTextColor(ContextCompat.getColor(ctx, chipText(accent)))
            chipCategory.text = item.applicableCategory ?: "General"

            // ── 2. State badge ────────────────────────────────────────────
            tvState.text = item.state ?: "National"

            // ── 3. Subsidy name ───────────────────────────────────────────
            tvSubsidyName.text = item.subsidyName

            // ── 4. Hero % + label ─────────────────────────────────────────
            tvSubsidyPercent.text = item.percentHero

            // ── 6. Eligibility ────────────────────────────────────────────
            tvEligibility.text = item.eligibility ?: "See eligibility criteria"

            // ── 7. Deadline ───────────────────────────────────────────────
            tvDeadline.text = item.deadline ?: "Ongoing"

            // ── 8. Source link ────────────────────────────────────────────
            tvSourceLink.text = item.displaySourceLink

            // ── Apply Now → open URL ──────────────────────────────────────
            btnApplyNow.setOnClickListener {
                item.sourceLink?.takeIf { it.isNotBlank() }
                    ?.let { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it))) }
                onApplyClick(item)
            }
            root.setOnClickListener { onApplyClick(item) }
        }
    }

    // ─── Breakdown pills ─────────────────────────────────────────────────────

    /**
     * Clears [container] and inflates one pill TextView per breakdown part.
     * Hides the entire row if no breakdown is available.
     */
    private fun populateBreakdown(
        ctx: Context, container: LinearLayout, parts: List<String>
    ) {
        container.removeAllViews()
        if (parts.isEmpty()) {
            container.visibility = View.GONE
            return
        }
        container.visibility = View.VISIBLE

        val inflater = LayoutInflater.from(ctx)
        val gapPx = ctx.dp(7).toInt()

        parts.take(3).forEachIndexed { idx, part ->
            val pill = inflater.inflate(R.layout.item_breakdown_pill, container, false) as TextView
            pill.text = part
            val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { if (idx < parts.lastIndex) marginEnd = gapPx }
            pill.layoutParams = lp
            container.addView(pill)
        }
    }

    // ─── Color helpers ────────────────────────────────────────────────────────

    private fun stripeColor(accent: AccentColor): Int = when (accent) {
        AccentColor.PURPLE -> R.color.subsidy_purple
        AccentColor.BLUE -> R.color.blue_primary
        AccentColor.GREEN -> R.color.green_primary
        AccentColor.TEAL -> R.color.teal_primary
        AccentColor.AMBER -> R.color.amber_primary
    }

    private fun chipBg(accent: AccentColor): Int = when (accent) {
        AccentColor.PURPLE -> R.color.subsidy_chip_bg
        AccentColor.BLUE -> R.color.blue_chip_bg
        AccentColor.GREEN -> R.color.green_chip_bg
        AccentColor.TEAL -> R.color.teal_chip_bg
        AccentColor.AMBER -> R.color.amber_chip_bg
    }

    private fun chipText(accent: AccentColor): Int = when (accent) {
        AccentColor.PURPLE -> R.color.subsidy_chip_text
        AccentColor.BLUE -> R.color.blue_chip_text
        AccentColor.GREEN -> R.color.green_chip_text
        AccentColor.TEAL -> R.color.teal_chip_text
        AccentColor.AMBER -> R.color.amber_chip_text
    }

    private fun Context.dp(v: Int) = v * resources.displayMetrics.density
}