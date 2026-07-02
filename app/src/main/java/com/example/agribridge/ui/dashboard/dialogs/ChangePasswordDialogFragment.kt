package com.example.agribridge.ui.dashboard.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.agribridge.api.listener.ProfileApiRequest
import com.example.agribridge.api.repository.ProfileRepository
import com.example.agribridge.databinding.DialogChangePasswordBinding
import com.example.agribridge.model.ProfileUpdateResponseData
import com.example.agribridge.utils.request
import com.example.agribridge.utils.toast
import com.example.agribridge.viewmodel.ProfileViewModel

class ChangePasswordDialogFragment : DialogFragment() {

    private var _binding: DialogChangePasswordBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by lazy {
        val factory = viewModelFactory {
            initializer {
                ProfileViewModel(ProfileRepository(requireContext().request(ProfileApiRequest::class.java)))
            }
        }
        ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivClose.setOnClickListener { dismiss() }

        binding.btnChangePassword.setOnClickListener {
            val password = binding.etNewPassword.text?.toString()?.trim().orEmpty()
            val confirmPassword = binding.etConfirmNewPassword.text?.toString()?.trim().orEmpty()

            if (password.isEmpty()) {
                requireContext().toast("Please enter a new password")
                return@setOnClickListener
            }
            if (password.length < 6) {
                requireContext().toast("Password must be at least 6 characters")
                return@setOnClickListener
            }
            if (confirmPassword.isEmpty()) {
                requireContext().toast("Please confirm your new password")
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                requireContext().toast("Passwords do not match")
                return@setOnClickListener
            }

            profileViewModel.updateProfile(password = password)
        }

        setObservers()
    }

    private fun setObservers() {
        profileViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.btnChangePassword.isEnabled = false
                binding.btnChangePassword.text = "Updating..."
            } else {
                binding.btnChangePassword.isEnabled = true
                binding.btnChangePassword.text = "Update Password"
            }
        }

        profileViewModel.profileUpdateResult.observe(viewLifecycleOwner) { apiResponse ->
            if (apiResponse == null) return@observe
            if (apiResponse.statusCode == 200 || apiResponse.type?.equals("success", ignoreCase = true) == true) {
                val dataObj = com.example.agribridge.utils.getData(apiResponse.data, ProfileUpdateResponseData::class.java)
                dataObj?.let {
                    storeUpdatedUserDetails(it.toUserData())
                }
                requireContext().toast(apiResponse.message ?: "Password updated successfully!")
                dismiss()
            } else {
                requireContext().toast(apiResponse.message ?: "Failed to update password")
            }
            profileViewModel.clearProfileUpdateResult()
        }
    }

    private fun storeUpdatedUserDetails(user: com.example.agribridge.model.UserData) {
        val preference = com.example.agribridge.utils.Preference(requireContext())
        preference.setStringPreferenceForUser(com.example.agribridge.utils.Constant.UserDetails.USER_ID, user._id)
        preference.setStringPreferenceForUser(com.example.agribridge.utils.Constant.UserDetails.USER_FIRST_NAME, user.first_name)
        preference.setStringPreferenceForUser(com.example.agribridge.utils.Constant.UserDetails.USER_LAST_NAME, user.last_name)
        preference.setStringPreferenceForUser(com.example.agribridge.utils.Constant.UserDetails.USER_EMAIL, user.email)
        preference.setStringPreferenceForUser(com.example.agribridge.utils.Constant.UserDetails.USER_PHONE_NUMBER, user.phone_number)
        preference.setStringPreferenceForUser(com.example.agribridge.utils.Constant.UserDetails.USER_STATE, user.state)
        preference.setStringPreferenceForUser(com.example.agribridge.utils.Constant.UserDetails.USER_DISTRICT, user.district)
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
        const val TAG = "ChangePasswordDialogFragment"
        fun newInstance() = ChangePasswordDialogFragment()
    }
}
