package com.example.agribridge.ui.authUser.dialogs

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.agribridge.R
import com.example.agribridge.databinding.DialogLanguageSelectionBinding
import com.example.agribridge.utils.Constant
import com.example.agribridge.utils.LocaleHelper
import com.example.agribridge.utils.Preference
import com.example.agribridge.utils.LanguageManager

class LanguageSelectionDialogFragment : DialogFragment() {

    private var _binding: DialogLanguageSelectionBinding? = null
    private val binding get() = _binding!!

    private var selectedLanguageCode = "en"
    private var onOkClickListener: ((String) -> Unit)? = null

    fun setOnOkClickListener(listener: (String) -> Unit) {
        onOkClickListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogLanguageSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Read current saved locale or default locale using LanguageManager
        selectedLanguageCode = LanguageManager.getLanguage(requireContext())

        updateUI()

        binding.cardEnglish.setOnClickListener {
            selectedLanguageCode = "en"
            updateUI()
        }

        binding.cardHindi.setOnClickListener {
            selectedLanguageCode = "hi"
            updateUI()
        }

        binding.cardKannada.setOnClickListener {
            selectedLanguageCode = "kn"
            updateUI()
        }

        binding.cardMarathi.setOnClickListener {
            selectedLanguageCode = "mr"
            updateUI()
        }

        binding.btnOk.setOnClickListener {
            LanguageManager.setLanguage(requireContext(), selectedLanguageCode)
            onOkClickListener?.invoke(selectedLanguageCode)
            dismiss()
        }
    }

    private fun updateUI() {
        binding.apply {
            rbEnglish.isChecked = (selectedLanguageCode == "en")
            rbHindi.isChecked = (selectedLanguageCode == "hi")
            rbKannada.isChecked = (selectedLanguageCode == "kn")
            rbMarathi.isChecked = (selectedLanguageCode == "mr")

            val activeColor = ContextCompat.getColor(requireContext(), R.color.green_primary)
            val inactiveColor = ContextCompat.getColor(requireContext(), R.color.green_card_stroke)

            cardEnglish.strokeColor = if (selectedLanguageCode == "en") activeColor else inactiveColor
            cardHindi.strokeColor = if (selectedLanguageCode == "hi") activeColor else inactiveColor
            cardKannada.strokeColor = if (selectedLanguageCode == "kn") activeColor else inactiveColor
            cardMarathi.strokeColor = if (selectedLanguageCode == "mr") activeColor else inactiveColor
            
            // Adjust card elevations for selected state
            cardEnglish.cardElevation = if (selectedLanguageCode == "en") 6f else 1f
            cardHindi.cardElevation = if (selectedLanguageCode == "hi") 6f else 1f
            cardKannada.cardElevation = if (selectedLanguageCode == "kn") 6f else 1f
            cardMarathi.cardElevation = if (selectedLanguageCode == "mr") 6f else 1f
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            val displayMetrics = resources.displayMetrics
            val marginInPx = (30 * displayMetrics.density).toInt()
            val width = displayMetrics.widthPixels - (2 * marginInPx)
            window.setLayout(
                width,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window.setBackgroundDrawableResource(android.R.color.transparent)
            window.decorView.setPadding(0, 0, 0, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "LanguageSelectionDialogFragment"

        fun newInstance(onOkClick: (String) -> Unit): LanguageSelectionDialogFragment {
            return LanguageSelectionDialogFragment().apply {
                setOnOkClickListener(onOkClick)
            }
        }
    }
}
