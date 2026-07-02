package com.example.agribridge.ui.dashboard.dialogs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.agribridge.databinding.DialogLoanDetailBinding
import com.example.agribridge.model.LoanModel
import com.google.gson.Gson

class LoanDetailDialogFragment : DialogFragment() {

    private var _binding: DialogLoanDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogLoanDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val modelJson = arguments?.getString(KEY_MODEL) ?: return
        val item = Gson().fromJson(modelJson, LoanModel::class.java) ?: return

        with(binding) {
            tvDetailTitle.text = item.loanName
            tvCategoryBadge.text = item.loanCategory ?: "Crop Loan"
            tvProvider.text = item.bankProvider ?: "All Commercial Banks"
            tvInterest.text = item.interestRate ?: "N/A"

            // Header Image
            if (!item.imageUrl.isNullOrEmpty()) {
                com.bumptech.glide.Glide.with(this@LoanDetailDialogFragment)
                    .load(item.imageUrl)
                    .into(ivHeaderImage)
                ivHeaderImage.visibility = View.VISIBLE
            } else {
                cardHeaderImage.visibility = View.GONE
            }

            if (item.maxAmount.isNullOrEmpty()) {
                tvMaxAmountLabel.visibility = View.GONE
                tvMaxAmount.visibility = View.GONE
            } else {
                tvMaxAmountLabel.visibility = View.VISIBLE
                tvMaxAmount.visibility = View.VISIBLE
                tvMaxAmount.text = item.maxAmount
            }

            if (item.eligibility.isNullOrEmpty()) {
                tvEligibilityLabel.visibility = View.GONE
                tvEligibility.visibility = View.GONE
            } else {
                tvEligibilityLabel.visibility = View.VISIBLE
                tvEligibility.visibility = View.VISIBLE
                tvEligibility.text = item.eligibility
            }

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
        const val TAG = "LoanDetailDialogFragment"
        private const val KEY_MODEL = "key_model"

        fun newInstance(model: LoanModel): LoanDetailDialogFragment {
            return LoanDetailDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_MODEL, Gson().toJson(model))
                }
            }
        }
    }
}
