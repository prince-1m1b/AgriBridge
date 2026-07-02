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
import com.example.agribridge.databinding.ItemLoanBinding
import com.example.agribridge.model.LoanModel

class LoanAdapter(
    private val onApplyClick: (LoanModel) -> Unit = {}
) : ListAdapter<LoanModel, LoanAdapter.ViewHolder>(DiffCallback()) {

    // ─── DiffUtil ────────────────────────────────────────────────────────────

    class DiffCallback : DiffUtil.ItemCallback<LoanModel>() {
        override fun areItemsTheSame(old: LoanModel, new: LoanModel) = old.loanName == new.loanName
        override fun areContentsTheSame(old: LoanModel, new: LoanModel) = old == new
    }

    // ─── ViewHolder ──────────────────────────────────────────────────────────

    class ViewHolder(val binding: ItemLoanBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemLoanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    // ─── Binding ─────────────────────────────────────────────────────────────

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val ctx = holder.binding.root.context

        with(holder.binding) {
            viewAccentStripe.setBackgroundColor(
                ContextCompat.getColor(ctx, accentColor(item.loanCategory))
            )

            chipCategory.text = item.loanCategory ?: "General Loan"
            chipCategory.setChipBackgroundColorResource(categoryChipBg(item.loanCategory))
            chipCategory.setTextColor(
                ContextCompat.getColor(
                    ctx, categoryChipText(item.loanCategory)
                )
            )

            tvState.text = item.state ?: "National"

            tvLoanName.text = item.displayName

            tvBankProvider.text = item.bankProvider ?: "—"

            tvInterestRate.text = item.interestShort

            tvMaxAmount.text = item.amountShort
            tvProcedureHint.text =
                item.procedure?.substringBefore(".")?.trim() ?: item.officialUrl?.toDisplayUrl()
                        ?: "Contact your local bank"
            btnApplyNow.setOnClickListener {
                item.officialUrl?.let { url ->
                    ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
                onApplyClick(item)
            }

            // ── Full card click ───────────────────────────────────────────
            root.setOnClickListener { onApplyClick(item) }
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /**
     * Inflates a TextView tag per document into [container].
     * Uses `bg_doc_tag` drawable and `text_secondary` color.
     */
    private fun populateDocTags(
        ctx: Context, container: LinearLayout, docs: List<String>?
    ) {
        container.removeAllViews()
        if (docs.isNullOrEmpty()) return

        val inflater = LayoutInflater.from(ctx)
        val gapPx = ctx.dp(6).toInt()

        docs.forEach { doc ->
            val tag = inflater.inflate(R.layout.item_loan_doc_tag, container, false) as TextView
            tag.text = doc
            val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { marginEnd = gapPx }
            tag.layoutParams = lp
            container.addView(tag)
        }
    }

    /**
     * Left accent stripe color per loan category.
     * Blue for credit, teal for crop loans, amber for term loans, etc.
     */
    private fun accentColor(category: String?): Int = when {
        category == null -> R.color.blue_primary
        category.contains("crop", ignoreCase = true) || category.contains(
            "short", ignoreCase = true
        ) -> R.color.blue_primary

        category.contains("term", ignoreCase = true) -> R.color.teal_primary
        category.contains("subsidy", ignoreCase = true) -> R.color.green_primary
        category.contains("mudra", ignoreCase = true) -> R.color.teal_primary
        else -> R.color.blue_primary
    }

    /** Chip background color per loan category. */
    private fun categoryChipBg(category: String?): Int = when {
        category?.contains("crop", ignoreCase = true) == true || category?.contains(
            "short", ignoreCase = true
        ) == true -> R.color.blue_chip_bg

        category?.contains("term", ignoreCase = true) == true -> R.color.teal_chip_bg
        else -> R.color.blue_chip_bg
    }

    /** Chip text color per loan category. */
    private fun categoryChipText(category: String?): Int = when {
        category?.contains("term", ignoreCase = true) == true -> R.color.teal_chip_text
        else -> R.color.blue_chip_text
    }

    private fun String.toDisplayUrl() =
        removePrefix("https://").removePrefix("http://").trimEnd('/')

    private fun Context.dp(value: Int): Float = value * resources.displayMetrics.density
}