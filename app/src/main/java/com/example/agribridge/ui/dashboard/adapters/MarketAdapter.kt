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
import com.example.agribridge.databinding.ItemMarketCardBinding
import com.example.agribridge.model.MarketModel
import com.example.agribridge.model.MarketModel.Category

/**
 * Horizontal RecyclerView adapter — compact market cards (260dp wide).
 *
 * Usage in Fragment / Activity:
 *
 *   val adapter = MarketAdapter { market -> openDetailScreen(market) }
 *   binding.rvMarkets.layoutManager =
 *       LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
 *   binding.rvMarkets.adapter = adapter
 *   adapter.submitList(markets)
 */
class MarketAdapter(
    private val onCardClick: (MarketModel) -> Unit = {}
) : ListAdapter<MarketModel, MarketAdapter.ViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<MarketModel>() {
        override fun areItemsTheSame(old: MarketModel, new: MarketModel) =
            old.marketplaceName == new.marketplaceName
        override fun areContentsTheSame(old: MarketModel, new: MarketModel) =
            old == new
    }

    class ViewHolder(val binding: ItemMarketCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemMarketCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val ctx  = holder.binding.root.context

        with(holder.binding) {

            // ── Top stripe + chip color by category ──────────────────
            val (stripeColor, chipBg, chipText) = categoryColors(ctx, item.category)
            viewCategoryStripe.setBackgroundColor(stripeColor)
            chipMarketType.setChipBackgroundColorResource(chipBgRes(item.category))
            chipMarketType.setTextColor(ContextCompat.getColor(ctx, chipTextRes(item.category)))
            chipMarketType.text = item.marketplaceType ?: "Market"

            // ── Core text ─────────────────────────────────────────────
            tvMarketName.text  = item.marketplaceName
            tvAddress.text     = item.address ?: item.nearbyAreaInformation ?: "—"
            tvOperatingHours.text = item.hoursShort
            tvSizeLabel.text   = "${item.sizeShort} · ${item.daysShort}"

            // ── Buy / Sell badges ─────────────────────────────────────
            layoutBuyBadge.visibility  = if (item.buyingAvailable)  View.VISIBLE else View.GONE
            layoutSellBadge.visibility = if (item.sellingAvailable) View.VISIBLE else View.GONE

            // ── Vegetable category tags ────────────────────────────────
            populateTags(ctx, layoutVegTags, item.vegetableCategories, maxVisible = 3)

            // ── Card click ────────────────────────────────────────────
            root.setOnClickListener { onCardClick(item) }
        }
    }

    // ─── Helper: vegetable / day tag row ─────────────────────────────────────

    fun populateTags(
        ctx: Context,
        container: LinearLayout,
        items: List<String>?,
        maxVisible: Int = 4
    ) {
        container.removeAllViews()
        if (items.isNullOrEmpty()) return

        val inflater = LayoutInflater.from(ctx)
        val gapPx    = ctx.dp(5).toInt()

        items.take(maxVisible).forEachIndexed { idx, label ->
            val tag = inflater.inflate(R.layout.item_veg_tag, container, false) as TextView
            tag.text = label
            val lp   = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { marginEnd = gapPx }
            tag.layoutParams = lp
            container.addView(tag)
        }

        val overflow = items.size - maxVisible
        if (overflow > 0) {
            val more = inflater.inflate(R.layout.item_more_tag, container, false) as TextView
            more.text = "+$overflow"
            container.addView(more)
        }
    }

    // ─── Category color helpers ───────────────────────────────────────────────

    private fun categoryColors(ctx: Context, cat: Category): Triple<Int, Int, Int> {
        val stripe   = ContextCompat.getColor(ctx, stripeColorRes(cat))
        val chipBg   = ContextCompat.getColor(ctx, chipBgRes(cat))
        val chipText = ContextCompat.getColor(ctx, chipTextRes(cat))
        return Triple(stripe, chipBg, chipText)
    }

    private fun stripeColorRes(cat: Category): Int = when (cat) {
        Category.RETAIL_WHOLESALE -> R.color.market_orange
        Category.APMC_MANDI       -> R.color.teal_primary
        Category.EMARKET          -> R.color.blue_primary
        Category.OTHER            -> R.color.market_orange
    }

    private fun chipBgRes(cat: Category): Int = when (cat) {
        Category.RETAIL_WHOLESALE -> R.color.market_chip_bg
        Category.APMC_MANDI       -> R.color.teal_chip_bg
        Category.EMARKET          -> R.color.blue_chip_bg
        Category.OTHER            -> R.color.market_chip_bg
    }

    private fun chipTextRes(cat: Category): Int = when (cat) {
        Category.RETAIL_WHOLESALE -> R.color.market_chip_text
        Category.APMC_MANDI       -> R.color.teal_chip_text
        Category.EMARKET          -> R.color.blue_chip_text
        Category.OTHER            -> R.color.market_chip_text
    }

    private fun Context.dp(v: Int) = v * resources.displayMetrics.density
}