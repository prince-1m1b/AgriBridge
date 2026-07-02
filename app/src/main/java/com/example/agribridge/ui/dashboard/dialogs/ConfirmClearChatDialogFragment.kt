package com.example.agribridge.ui.dashboard.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.agribridge.databinding.DialogConfirmClearChatBinding

class ConfirmClearChatDialogFragment : DialogFragment() {

    private var _binding: DialogConfirmClearChatBinding? = null
    private val binding get() = _binding!!

    private var onConfirmClicked: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogConfirmClearChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivClose.setOnClickListener { dismiss() }
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnConfirm.setOnClickListener {
            onConfirmClicked?.invoke()
            dismiss()
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
        const val TAG = "ConfirmClearChatDialogFragment"

        fun newInstance(onConfirm: () -> Unit): ConfirmClearChatDialogFragment {
            return ConfirmClearChatDialogFragment().apply {
                this.onConfirmClicked = onConfirm
            }
        }
    }
}
