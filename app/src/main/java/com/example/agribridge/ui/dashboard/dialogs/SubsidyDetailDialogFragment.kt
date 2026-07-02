package com.example.agribridge.ui.dashboard.dialogs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.agribridge.databinding.DialogSubsidyDetailBinding
import com.example.agribridge.model.SubsidyModel
import com.google.gson.Gson

class SubsidyDetailDialogFragment : DialogFragment() {

    private var _binding: DialogSubsidyDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogSubsidyDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val modelJson = arguments?.getString(KEY_MODEL) ?: return
        val item = Gson().fromJson(modelJson, SubsidyModel::class.java) ?: return

        with(binding) {
            tvDetailTitle.text = item.subsidyName
            tvCategoryBadge.text = item.applicableCategory ?: "Subsidy"

            if (item.state.isNullOrEmpty()) {
                tvStateBadge.visibility = View.GONE
            } else {
                tvStateBadge.visibility = View.VISIBLE
                tvStateBadge.text = item.state
            }

            tvDescription.text = item.description ?: "No description provided."
            tvAmount.text = item.subsidyAmount ?: "N/A"
            tvEligibility.text = item.eligibility ?: "All qualified farmers"
            tvDeadline.text = item.deadline ?: "Ongoing"

            // Header Image
            if (!item.imageUrl.isNullOrEmpty()) {
                com.bumptech.glide.Glide.with(this@SubsidyDetailDialogFragment)
                    .load(item.imageUrl)
                    .into(ivHeaderImage)
                ivHeaderImage.visibility = View.VISIBLE
            } else {
                cardHeaderImage.visibility = View.GONE
            }

            ivClose.setOnClickListener { dismiss() }

            if (item.sourceLink.isNullOrEmpty()) {
                btnAction.visibility = View.GONE
            } else {
                btnAction.visibility = View.VISIBLE
                btnAction.setOnClickListener {
                    try {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.sourceLink))
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
        const val TAG = "SubsidyDetailDialogFragment"
        private const val KEY_MODEL = "key_model"

        fun newInstance(model: SubsidyModel): SubsidyDetailDialogFragment {
            return SubsidyDetailDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_MODEL, Gson().toJson(model))
                }
            }
        }
    }
}
