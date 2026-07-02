package com.example.agribridge.ui.dashboard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.agribridge.R
import com.example.agribridge.api.listener.LogoutApiRequest
import com.example.agribridge.api.repository.LogoutRepository
import com.example.agribridge.api.listener.ChatApiRequest
import com.example.agribridge.api.repository.ChatRepository
import com.example.agribridge.viewmodel.ChatViewModel
import com.example.agribridge.databinding.FragmentProfileBinding
import com.example.agribridge.ui.authUser.dialogs.LanguageSelectionDialogFragment
import com.example.agribridge.utils.*
import com.example.agribridge.viewmodel.LogoutViewModel
import com.example.agribridge.ui.dashboard.dialogs.SelectLocationDialogFragment
import com.google.gson.JsonObject

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var preference: Preference

    private val logoutViewModel: LogoutViewModel by lazy {
        val factory = viewModelFactory {
            initializer {
                LogoutViewModel(LogoutRepository(requireContext().request(LogoutApiRequest::class.java)))
            }
        }
        ViewModelProvider(this, factory)[LogoutViewModel::class.java]
    }

    private val chatViewModel: ChatViewModel by lazy {
        val factory = viewModelFactory {
            initializer {
                ChatViewModel(ChatRepository(requireContext().request(ChatApiRequest::class.java)))
            }
        }
        ViewModelProvider(this, factory)[ChatViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        init()
        onClicks()
        setAdapter()
        setObserver()
    }

    private fun init() {
        preference = Preference(requireActivity())

        // Get details from Preference
        val firstName = preference.getStringPreferenceForUser(Constant.UserDetails.USER_FIRST_NAME)
        val lastName = preference.getStringPreferenceForUser(Constant.UserDetails.USER_LAST_NAME)
        val state = preference.getStringPreferenceForUser(Constant.UserDetails.USER_STATE)
        val district = preference.getStringPreferenceForUser(Constant.UserDetails.USER_DISTRICT)
        val phone = preference.getStringPreferenceForUser(Constant.UserDetails.USER_PHONE_NUMBER)
        val email = preference.getStringPreferenceForUser(Constant.UserDetails.USER_EMAIL)

        // Set username
        if (firstName.isNotEmpty() || lastName.isNotEmpty()) {
            binding.tvUserName.text = "$firstName $lastName".trim()
        } else {
            binding.tvUserName.text = getString(R.string.farmer_user)
        }

        // Set phone & email details dynamically
        binding.tvPhoneValue.text = phone.ifEmpty { "N/A" }
        binding.tvEmailValue.text = email.ifEmpty { "N/A" }

        // Set location values dynamically
        if (state.isNotEmpty() || district.isNotEmpty()) {
            binding.tvLocationValue.text = listOfNotNull(district.ifEmpty { null }, state.ifEmpty { null }).joinToString(", ")
        } else {
            binding.tvLocationValue.text = getString(R.string.location_not_set)
        }
    }

    private fun onClicks() {
        binding.clLanguage.setOnClickListener {
            val dialog = LanguageSelectionDialogFragment.newInstance { _ ->
                activity?.recreate()
            }
            dialog.show(parentFragmentManager, LanguageSelectionDialogFragment.TAG)
        }

        binding.clChangePassword.setOnClickListener {
            val dialog = com.example.agribridge.ui.dashboard.dialogs.ChangePasswordDialogFragment.newInstance()
            dialog.show(parentFragmentManager, com.example.agribridge.ui.dashboard.dialogs.ChangePasswordDialogFragment.TAG)
        }

        binding.clLocation.setOnClickListener {
            val dialog = SelectLocationDialogFragment.newInstance {
                init() // Reload updated preferences values
            }
            dialog.show(parentFragmentManager, SelectLocationDialogFragment.TAG)
        }

        binding.clClearChat.setOnClickListener {
            val dialog = com.example.agribridge.ui.dashboard.dialogs.ConfirmClearChatDialogFragment.newInstance {
                viewVisible(binding.progressBarContainer)
                chatViewModel.clearChatHistory()
            }
            dialog.show(parentFragmentManager, com.example.agribridge.ui.dashboard.dialogs.ConfirmClearChatDialogFragment.TAG)
        }

        binding.clTerms.setOnClickListener {
            val title = getString(R.string.terms_conditions)
            val content = getString(R.string.terms_conditions_content)
            val dialog = com.example.agribridge.ui.dashboard.dialogs.StaticContentDialogFragment.newInstance(title, content)
            dialog.show(parentFragmentManager, com.example.agribridge.ui.dashboard.dialogs.StaticContentDialogFragment.TAG)
        }

        binding.clPrivacy.setOnClickListener {
            val title = getString(R.string.privacy_policy)
            val content = getString(R.string.privacy_policy_content)
            val dialog = com.example.agribridge.ui.dashboard.dialogs.StaticContentDialogFragment.newInstance(title, content)
            dialog.show(parentFragmentManager, com.example.agribridge.ui.dashboard.dialogs.StaticContentDialogFragment.TAG)
        }

        binding.clHelp.setOnClickListener {
            val title = getString(R.string.help_and_support)
            val content = getString(R.string.help_support_content)
            val dialog = com.example.agribridge.ui.dashboard.dialogs.StaticContentDialogFragment.newInstance(title, content)
            dialog.show(parentFragmentManager, com.example.agribridge.ui.dashboard.dialogs.StaticContentDialogFragment.TAG)
        }

        binding.clLogout.setOnClickListener {
            viewVisible(binding.progressBarContainer)
            val token = preference.getStringPreferenceForUser(Constant.UserDetails.ACCESS_TOKEN)
            val jsonObj = JsonObject().apply {
                addProperty("token", token)
            }
            logoutViewModel.apiCallForUserLogout(jsonObj)
        }
    }

    private fun setAdapter() {
    }

    private fun setObserver() {
        logoutViewModel.getUserLogoutResponse.observe(viewLifecycleOwner) { response ->
            viewGone(binding.progressBarContainer)
            activity?.logout()
        }

        chatViewModel.clearChatResult.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                viewGone(binding.progressBarContainer)
                if (response.statusCode == 200 || response.type?.equals("success", ignoreCase = true) == true) {
                    requireContext().toast(response.message ?: "Chat history cleared successfully")
                } else {
                    requireContext().toast(response.message ?: "Failed to clear chat history")
                }
                chatViewModel.clearClearChatResult()
            }
        }
    }
}