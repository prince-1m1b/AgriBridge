package com.example.agribridge.ui.authUser.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.agribridge.databinding.DialogSuccessSignupBinding

class SuccessDialogFragment : DialogFragment() {

    private var _binding: DialogSuccessSignupBinding? = null
    private val binding get() = _binding!!
    
    private var onOkClickListener: (() -> Unit)? = null

    fun setOnOkClickListener(listener: () -> Unit) {
        onOkClickListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSuccessSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        binding.btnOk.setOnClickListener {
            dismiss()
            onOkClickListener?.invoke()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SuccessDialogFragment"
        
        fun newInstance(onOkClick: () -> Unit): SuccessDialogFragment {
            return SuccessDialogFragment().apply {
                setOnOkClickListener(onOkClick)
            }
        }
    }
}