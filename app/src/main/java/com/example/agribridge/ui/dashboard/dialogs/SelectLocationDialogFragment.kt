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
import com.example.agribridge.utils.Constant.UserDetails
import com.example.agribridge.utils.Preference
import com.example.agribridge.utils.getData
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

        // Read saved location from arguments or user preferences
        val preference = Preference(requireContext())
        val prefState = preference.getStringPreferenceForUser(UserDetails.USER_STATE)
        val prefDistrict = preference.getStringPreferenceForUser(UserDetails.USER_DISTRICT)

        val argState = arguments?.getString(ARG_STATE)
        val argDistrict = arguments?.getString(ARG_DISTRICT)

        val stateToPreselect = when {
            !argState.isNullOrEmpty() -> argState
            prefState.isNotEmpty() -> prefState
            else -> ""
        }

        val districtToPreselect = when {
            !argDistrict.isNullOrEmpty() -> argDistrict
            prefDistrict.isNotEmpty() -> prefDistrict
            else -> ""
        }

        if (stateToPreselect.isNotEmpty()) {
            selectedState = stateToPreselect
            binding.actvState.setText(stateToPreselect, false)
            // Immediately fetch districts for the preselected state so the district dropdown is ready
            profileViewModel.getDistricts(stateToPreselect)
        }

        if (districtToPreselect.isNotEmpty()) {
            selectedDistrict = districtToPreselect
            binding.actvDistrict.setText(districtToPreselect, false)
        }

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
                val dataObj = getData(apiResponse.data, StatesResponseData::class.java)
                val statesList = dataObj?.states ?: emptyList()

                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, statesList)
                binding.actvState.setAdapter(adapter)

                // Match and preserve the preselected state in the dropdown
                if (selectedState.isNotEmpty()) {
                    val matchingState = statesList.find { it.equals(selectedState, ignoreCase = true) } ?: selectedState
                    selectedState = matchingState
                    binding.actvState.setText(matchingState, false)
                }

                binding.actvState.setOnItemClickListener { _, _, position, _ ->
                    val chosenState = adapter.getItem(position) ?: ""
                    if (chosenState != selectedState) {
                        selectedState = chosenState
                        selectedDistrict = ""
                        binding.actvDistrict.setText("", false)
                        binding.actvDistrict.setAdapter(null)
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
                val dataObj = getData(apiResponse.data, DistrictsResponseData::class.java)
                val districtsList = dataObj?.districts ?: emptyList()

                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, districtsList)
                binding.actvDistrict.setAdapter(adapter)

                // Match and preserve the preselected district in the dropdown
                if (selectedDistrict.isNotEmpty()) {
                    val matchingDistrict = districtsList.find { it.equals(selectedDistrict, ignoreCase = true) } ?: selectedDistrict
                    selectedDistrict = matchingDistrict
                    binding.actvDistrict.setText(matchingDistrict, false)
                }

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
                val dataObj = getData(apiResponse.data, ProfileUpdateResponseData::class.java)
                dataObj?.let {
                    storeUpdatedUserDetails(it.toUserData())
                }
                
                // Explicitly save the selected state & district into preference in case they are not returned in the API payload!
                val preference = Preference(requireContext())
                if (selectedState.isNotEmpty()) {
                    preference.setStringPreferenceForUser(UserDetails.USER_STATE, selectedState)
                }
                if (selectedDistrict.isNotEmpty()) {
                    preference.setStringPreferenceForUser(UserDetails.USER_DISTRICT, selectedDistrict)
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
        val preference = Preference(requireContext())
        preference.setStringPreferenceForUser(UserDetails.USER_ID, user._id)
        preference.setStringPreferenceForUser(UserDetails.USER_FIRST_NAME, user.first_name)
        preference.setStringPreferenceForUser(UserDetails.USER_LAST_NAME, user.last_name)
        preference.setStringPreferenceForUser(UserDetails.USER_EMAIL, user.email)
        preference.setStringPreferenceForUser(UserDetails.USER_PHONE_NUMBER, user.phone_number)
        preference.setStringPreferenceForUser(UserDetails.USER_STATE, user.state)
        preference.setStringPreferenceForUser(UserDetails.USER_DISTRICT, user.district)
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
        private const val ARG_STATE = "arg_state"
        private const val ARG_DISTRICT = "arg_district"

        fun newInstance(
            initialState: String? = null,
            initialDistrict: String? = null,
            onLocationUpdated: () -> Unit
        ): SelectLocationDialogFragment {
            return SelectLocationDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_STATE, initialState)
                    putString(ARG_DISTRICT, initialDistrict)
                }
                this.onLocationUpdated = onLocationUpdated
            }
        }

        fun newInstance(onLocationUpdated: () -> Unit): SelectLocationDialogFragment {
            return newInstance(null, null, onLocationUpdated)
        }
    }
}
