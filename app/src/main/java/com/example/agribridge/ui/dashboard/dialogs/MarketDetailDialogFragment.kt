package com.example.agribridge.ui.dashboard.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.agribridge.databinding.DialogMarketDetailBinding
import com.example.agribridge.model.MarketModel
import com.google.gson.Gson

class MarketDetailDialogFragment : DialogFragment() {

    private var _binding: DialogMarketDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogMarketDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val modelJson = arguments?.getString(KEY_MODEL) ?: return
        val item = Gson().fromJson(modelJson, MarketModel::class.java) ?: return

        with(binding) {
            tvDetailTitle.text = item.marketplaceName
            tvTypeBadge.text = item.marketplaceType ?: "Market / Mandi"
            tvStateBadge.text = item.state ?: "Local"
            val addressParts = listOfNotNull(item.address, item.nearbyAreaInformation).joinToString("\n")
            tvAddress.text = addressParts.ifEmpty { "N/A" }

            // Header Image
            if (!item.imageUrl.isNullOrEmpty()) {
                com.bumptech.glide.Glide.with(this@MarketDetailDialogFragment)
                    .load(item.imageUrl)
                    .into(ivHeaderImage)
                ivHeaderImage.visibility = View.VISIBLE
            } else {
                cardHeaderImage.visibility = View.GONE
            }

            // Timings & days
            val daysStr = item.operatingDays?.joinToString(", ") ?: "Daily"
            val hoursStr = item.operatingHours ?: "Open"
            tvTimings.text = "$hoursStr\n($daysStr)"

            // Commodities
            val categoriesStr = item.vegetableCategories?.joinToString(", ")
            if (categoriesStr.isNullOrEmpty()) {
                tvProduceLabel.visibility = View.GONE
                tvProduce.visibility = View.GONE
            } else {
                tvProduceLabel.visibility = View.VISIBLE
                tvProduce.visibility = View.VISIBLE
                tvProduce.text = categoriesStr
            }

            // Description
            if (item.shortDescription.isNullOrEmpty() && item.description.isNullOrEmpty()) {
                tvDescriptionLabel.visibility = View.GONE
                tvDescription.visibility = View.GONE
            } else {
                tvDescriptionLabel.visibility = View.VISIBLE
                tvDescription.visibility = View.VISIBLE
                tvDescription.text = item.shortDescription ?: item.description
            }

            // Transport
            if (item.transportInformation.isNullOrEmpty()) {
                tvTransportLabel.visibility = View.GONE
                tvTransport.visibility = View.GONE
            } else {
                tvTransportLabel.visibility = View.VISIBLE
                tvTransport.visibility = View.VISIBLE
                tvTransport.text = item.transportInformation
            }

            // Seller/Popularity/Contacts
            val popStr = listOfNotNull(item.marketSizePopularity, item.contactInformation).joinToString("\n")
            if (popStr.isEmpty()) {
                tvPopularityLabel.visibility = View.GONE
                tvPopularity.visibility = View.GONE
            } else {
                tvPopularityLabel.visibility = View.VISIBLE
                tvPopularity.visibility = View.VISIBLE
                tvPopularity.text = popStr
            }

            ivClose.setOnClickListener { dismiss() }
            btnAction.setOnClickListener { dismiss() }
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
        const val TAG = "MarketDetailDialogFragment"
        private const val KEY_MODEL = "key_model"

        fun newInstance(model: MarketModel): MarketDetailDialogFragment {
            return MarketDetailDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_MODEL, Gson().toJson(model))
                }
            }
        }
    }
}
