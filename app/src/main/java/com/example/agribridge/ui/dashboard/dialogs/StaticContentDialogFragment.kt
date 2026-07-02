package com.example.agribridge.ui.dashboard.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.agribridge.databinding.DialogStaticContentBinding

class StaticContentDialogFragment : DialogFragment() {

    private var _binding: DialogStaticContentBinding? = null
    private val binding get() = _binding!!

    private var title: String = ""
    private var content: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogStaticContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDialogTitle.text = title
        binding.tvDialogContent.text = content

        binding.ivClose.setOnClickListener { dismiss() }
        binding.btnAction.setOnClickListener { dismiss() }
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
        const val TAG = "StaticContentDialogFragment"

        fun newInstance(title: String, content: String): StaticContentDialogFragment {
            return StaticContentDialogFragment().apply {
                this.title = title
                this.content = content
            }
        }
    }
}
