package com.example.agribridge.ui.dashboard.dialogs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.agribridge.databinding.DialogSchemeDetailBinding
import com.example.agribridge.model.SchemeModel
import com.google.gson.Gson

class SchemeDetailDialogFragment : DialogFragment() {

    private var _binding: DialogSchemeDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogSchemeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val modelJson = arguments?.getString(KEY_MODEL) ?: return
        val item = Gson().fromJson(modelJson, SchemeModel::class.java) ?: return

        with(binding) {
            tvDetailTitle.text = item.title
            tvTypeBadge.text = item.type ?: "Scheme"
            tvStateBadge.text = item.state ?: "National"
            tvDescription.text = item.description ?: item.subtitle.orEmpty()

            if (item.eligibility.isNullOrEmpty()) {
                tvEligibilityLabel.visibility = View.GONE
                tvEligibility.visibility = View.GONE
            } else {
                tvEligibilityLabel.visibility = View.VISIBLE
                tvEligibility.visibility = View.VISIBLE
                tvEligibility.text = item.eligibility
            }

            if (item.timeline.isNullOrEmpty() && item.startDate.isNullOrEmpty()) {
                tvTimelineLabel.visibility = View.GONE
                tvTimeline.visibility = View.GONE
            } else {
                tvTimelineLabel.visibility = View.VISIBLE
                tvTimeline.visibility = View.VISIBLE
                val timelineText = when {
                    !item.startDate.isNullOrEmpty() && !item.timeline.isNullOrEmpty() -> "${item.startDate} to ${item.timeline}"
                    !item.startDate.isNullOrEmpty() -> "Starts: ${item.startDate}"
                    else -> item.timeline
                }
                tvTimeline.text = timelineText
            }

            // Provider
            if (item.provider.isNullOrEmpty()) {
                tvProviderLabel.visibility = View.GONE
                tvProvider.visibility = View.GONE
            } else {
                tvProviderLabel.visibility = View.VISIBLE
                tvProvider.visibility = View.VISIBLE
                tvProvider.text = item.provider
            }

            // Promo Code
            if (item.promoCode.isNullOrEmpty()) {
                tvPromoCodeLabel.visibility = View.GONE
                tvPromoCode.visibility = View.GONE
            } else {
                tvPromoCodeLabel.visibility = View.VISIBLE
                tvPromoCode.visibility = View.VISIBLE
                tvPromoCode.text = item.promoCode
            }

            // Benefits
            val benefitsText = item.benefits?.filter { it.isNotBlank() }?.joinToString("\n") { "• $it" }
            if (benefitsText.isNullOrEmpty()) {
                tvBenefitsLabel.visibility = View.GONE
                tvBenefits.visibility = View.GONE
            } else {
                tvBenefitsLabel.visibility = View.VISIBLE
                tvBenefits.visibility = View.VISIBLE
                tvBenefits.text = benefitsText
            }

            // Procedure
            if (item.procedure.isNullOrEmpty()) {
                tvProcedureLabel.visibility = View.GONE
                tvProcedure.visibility = View.GONE
            } else {
                tvProcedureLabel.visibility = View.VISIBLE
                tvProcedure.visibility = View.VISIBLE
                tvProcedure.text = item.procedure
            }

            // Documents
            val docsText = item.documentsRequired?.filter { it.isNotBlank() }?.joinToString("\n") { "• $it" }
            if (docsText.isNullOrEmpty()) {
                tvDocumentsLabel.visibility = View.GONE
                tvDocuments.visibility = View.GONE
            } else {
                tvDocumentsLabel.visibility = View.VISIBLE
                tvDocuments.visibility = View.VISIBLE
                tvDocuments.text = docsText
            }

            // Extra Info
            if (item.extraInformation.isNullOrEmpty()) {
                tvExtraInfoLabel.visibility = View.GONE
                tvExtraInfo.visibility = View.GONE
            } else {
                tvExtraInfoLabel.visibility = View.VISIBLE
                tvExtraInfo.visibility = View.VISIBLE
                tvExtraInfo.text = item.extraInformation
            }

            // Header Image
            if (!item.imageUrl.isNullOrEmpty()) {
                com.bumptech.glide.Glide.with(this@SchemeDetailDialogFragment)
                    .load(item.imageUrl)
                    .into(ivHeaderImage)
                ivHeaderImage.visibility = View.VISIBLE
                cardHeaderImage.visibility = View.VISIBLE
            } else {
                cardHeaderImage.visibility = View.GONE
            }

            ivClose.setOnClickListener { dismiss() }

            if (item.officialUrl.isNullOrEmpty()) {
                btnAction.visibility = View.GONE
            } else {
                btnAction.visibility = View.VISIBLE
                btnAction.setOnClickListener {
                    try {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.officialUrl))
                        startActivity(browserIntent)
                    } catch (e: Exception) {
                        // Ignore or show error
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            val displayMetrics = resources.displayMetrics
            val marginInPx = (20 * displayMetrics.density).toInt()
            val width = displayMetrics.widthPixels - (2 * marginInPx)
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawableResource(android.R.color.transparent)
            window.decorView.setPadding(0, 0, 0, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SchemeDetailDialogFragment"
        private const val KEY_MODEL = "key_model"

        fun newInstance(model: SchemeModel): SchemeDetailDialogFragment {
            return SchemeDetailDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_MODEL, Gson().toJson(model))
                }
            }
        }
    }
}
