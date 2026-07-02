package com.example.agribridge.ui.dashboard.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.agribridge.api.listener.ProfileApiRequest
import com.example.agribridge.api.repository.ProfileRepository
import com.example.agribridge.databinding.DialogSelectLocationBinding
import com.example.agribridge.model.DistrictsResponseData
import com.example.agribridge.model.ProfileUpdateResponseData
import com.example.agribridge.model.StatesResponseData
import com.example.agribridge.utils.request
import com.example.agribridge.utils.toast
import com.example.agribridge.viewmodel.ProfileViewModel

class SelectLocationDialogFragment : DialogFragment() {

    private var _binding: DialogSelectLocationBinding? = null
    private val binding get() = _binding!!

    private var onLocationUpdated: (() -> Unit)? = null

    private val profileViewModel: ProfileViewModel by lazy {
        val factory = viewModelFactory {
            initializer {
                ProfileViewModel(ProfileRepository(requireContext().request(ProfileApiRequest::class.java)))
            }
        }
        ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    private var selectedState: String = ""
    private var selectedDistrict: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogSelectLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivClose.setOnClickListener { dismiss() }

        setObservers()

        // Fetch states on launch
        profileViewModel.getStates()

        binding.btnUpdateLocation.setOnClickListener {
            if (selectedState.isEmpty()) {
                requireContext().toast("Please select a state")
                return@setOnClickListener
            }
            if (selectedDistrict.isEmpty()) {
                requireContext().toast("Please select a district")
                return@setOnClickListener
            }
            profileViewModel.updateProfile(state = selectedState, district = selectedDistrict)
        }
    }

    private fun setObservers() {
        profileViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.locationProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnUpdateLocation.isEnabled = !isLoading
        }

        profileViewModel.statesResult.observe(viewLifecycleOwner) { apiResponse ->
            if (apiResponse == null) return@observe
            if (apiResponse.statusCode == 200 || apiResponse.type?.equals("success", ignoreCase = true) == true) {
                val dataObj = com.example.agribridge.utils.getData(apiResponse.data, StatesResponseData::class.java)
                val statesList = dataObj?.states ?: emptyList()

                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, statesList)
                binding.actvState.setAdapter(adapter)
                binding.actvState.setOnItemClickListener { _, _, position, _ ->
                    val chosenState = adapter.getItem(position) ?: ""
                    if (chosenState != selectedState) {
                        selectedState = chosenState
                        selectedDistrict = ""
                        binding.actvDistrict.setText("", false)
                        profileViewModel.getDistricts(chosenState)
                    }
                }
            } else {
                requireContext().toast(apiResponse.message ?: "Failed to retrieve states")
            }
            profileViewModel.clearStatesResult()
        }

        profileViewModel.districtsResult.observe(viewLifecycleOwner) { apiResponse ->
            if (apiResponse == null) return@observe
            if (apiResponse.statusCode == 200 || apiResponse.type?.equals("success", ignoreCase = true) == true) {
                val dataObj = com.example.agribridge.utils.getData(apiResponse.data, DistrictsResponseData::class.java)
                val districtsList = dataObj?.districts ?: emptyList()

                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, districtsList)
                binding.actvDistrict.setAdapter(adapter)
                binding.actvDistrict.setOnItemClickListener { _, _, position, _ ->
                    selectedDistrict = adapter.getItem(position) ?: ""
                }
            } else {
                requireContext().toast(apiResponse.message ?: "Failed to retrieve districts")
            }
            profileViewModel.clearDistrictsResult()
        }

        profileViewModel.profileUpdateResult.observe(viewLifecycleOwner) { apiResponse ->
            if (apiResponse == null) return@observe
            if (apiResponse.statusCode == 200 || apiResponse.type?.equals("success", ignoreCase = true) == true) {
                val dataObj = com.example.agribridge.utils.getData(apiResponse.data, ProfileUpdateResponseData::class.java)
                dataObj?.let {
                    storeUpdatedUserDetails(it.toUserData())
                }
                
                // Explicitly save the selected state & district into preference in case they are not returned in the API payload!
                val preference = com.example.agribridge.utils.Preference(requireContext())
                if (selectedState.isNotEmpty()) {
                    preference.setStringPreferenceForUser(com.example.agribridge.utils.Constant.UserDetails.USER_STATE, selectedState)
                }
                if (selectedDistrict.isNotEmpty()) {
                    preference.setStringPreferenceForUser(com.example.agribridge.utils.Constant.UserDetails.USER_DISTRICT, selectedDistrict)
                }

                requireContext().toast(apiResponse.message ?: "Location updated successfully!")
                onLocationUpdated?.invoke()
                dismiss()
            } else {
                requireContext().toast(apiResponse.message ?: "Failed to update location")
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
        const val TAG = "SelectLocationDialogFragment"

        fun newInstance(onLocationUpdated: () -> Unit): SelectLocationDialogFragment {
            return SelectLocationDialogFragment().apply {
                this.onLocationUpdated = onLocationUpdated
            }
        }
    }
}
