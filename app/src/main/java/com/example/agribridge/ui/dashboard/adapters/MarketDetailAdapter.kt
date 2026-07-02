package com.example.agribridge.ui.dashboard.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.agribridge.R
import com.example.agribridge.databinding.ItemMarketDetailsBinding
import com.example.agribridge.model.MarketModel
import com.example.agribridge.model.MarketModel.Category

/**
 * Vertical RecyclerView adapter — full detail market cards.
 *
 * Usage:
 *   val adapter = MarketDetailAdapter { market -> openMap(market) }
 *   binding.rvMarketsDetail.layoutManager = LinearLayoutManager(context)
 *   binding.rvMarketsDetail.adapter = adapter
 *   adapter.submitList(markets)
 */
class MarketDetailAdapter(
    private val onDirectionsClick: (MarketModel) -> Unit = {}
) : ListAdapter<MarketModel, MarketDetailAdapter.ViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<MarketModel>() {
        override fun areItemsTheSame(old: MarketModel, new: MarketModel) =
            old.marketplaceName == new.marketplaceName

        override fun areContentsTheSame(old: MarketModel, new: MarketModel) = old == new
    }

    class ViewHolder(val binding: ItemMarketDetailsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemMarketDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val ctx = holder.binding.root.context

        with(holder.binding) {

            // ── Top stripe by category ────────────────────────────────
            viewStripeDetail.setBackgroundColor(
                ContextCompat.getColor(ctx, stripeColor(item.category))
            )

            // ── Header ────────────────────────────────────────────────
            tvDetailMarketName.text = item.marketplaceName
            tvDetailState.text = item.state ?: item.district ?: "—"
            tvDetailDescription.text = item.shortDescription ?: item.description ?: "—"

            // ── Info boxes ────────────────────────────────────────────
            tvDetailHours.text = item.operatingHours ?: "—"
            tvDetailSize.text = item.sizeShort.ifBlank { "—" }

            tvDetailBuying.text = if (item.buyingAvailable) "Available ✓" else "Not available"
            tvDetailSelling.text = if (item.sellingAvailable) "Available ✓" else "Not available"
            tvDetailBuying.setTextColor(
                ContextCompat.getColor(
                    ctx, if (item.buyingAvailable) R.color.green_primary else R.color.text_secondary
                )
            )
            tvDetailSelling.setTextColor(
                ContextCompat.getColor(
                    ctx, if (item.sellingAvailable) R.color.blue_primary else R.color.text_secondary
                )
            )

            // ── Vegetable categories (horizontal chips) ───────────────
            populateTags(
                ctx,
                layoutVegCategories,
                item.vegetableCategories,
                bgRes = R.drawable.bg_veg_tag,
                textColorRes = R.color.veg_tag_text
            )

            // ── Operating days (horizontal day pills) ─────────────────
            val allDays =
                listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
            val openSet = item.operatingDays?.map { it.lowercase() }?.toSet() ?: emptySet()
            populateDayChips(ctx, layoutDayChips, allDays, openSet)

            // ── Transport ─────────────────────────────────────────────
            tvDetailTransport.text = item.transportInformation ?: item.nearbyAreaInformation ?: "—"

            // ── Contact ───────────────────────────────────────────────
            tvDetailContact.text = item.contactInformation ?: "—"

            // ── Get Directions ────────────────────────────────────────
            btnGetDirections.setOnClickListener {
                val query = Uri.encode("${item.marketplaceName} ${item.address}")
                val uri = Uri.parse("geo:0,0?q=$query")
                ctx.startActivity(Intent(Intent.ACTION_VIEW, uri))
                onDirectionsClick(item)
            }
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private fun populateTags(
        ctx: Context, container: LinearLayout, items: List<String>?, bgRes: Int, textColorRes: Int
    ) {
        container.removeAllViews()
        if (items.isNullOrEmpty()) return

        val inflater = LayoutInflater.from(ctx)
        val gapPx = ctx.dp(6).toInt()
        val textColor = ContextCompat.getColor(ctx, textColorRes)

        items.forEach { label ->
            val tag = inflater.inflate(R.layout.item_veg_tag, container, false) as TextView
            tag.text = label
            tag.setBackgroundResource(bgRes)
            tag.setTextColor(textColor)
            val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { marginEnd = gapPx }
            tag.layoutParams = lp
            container.addView(tag)
        }
    }

    /**
     * Renders Mon/Tue/... day pills — green highlight if the market is open that day,
     * neutral gray if closed.
     */
    private fun populateDayChips(
        ctx: Context, container: LinearLayout, allDays: List<String>, openSet: Set<String>
    ) {
        container.removeAllViews()
        val inflater = LayoutInflater.from(ctx)
        val gapPx = ctx.dp(5).toInt()

        allDays.forEach { day ->
            val isOpen = openSet.contains(day.lowercase())
            val chip = inflater.inflate(
                if (isOpen) R.layout.item_day_open else R.layout.item_day_closed, container, false
            ) as TextView
            chip.text = day.take(3)
            val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { marginEnd = gapPx }
            chip.layoutParams = lp
            container.addView(chip)
        }
    }

    private fun stripeColor(cat: Category): Int = when (cat) {
        Category.RETAIL_WHOLESALE -> R.color.market_orange
        Category.APMC_MANDI -> R.color.teal_primary
        Category.EMARKET -> R.color.blue_primary
        Category.OTHER -> R.color.market_orange
    }

    private fun Context.dp(v: Int) = v * resources.displayMetrics.density
}