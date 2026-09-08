package com.example.agribridge.ui.authUser.activities

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import android.text.TextWatcher
import android.text.Editable
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.agribridge.R
import com.example.agribridge.api.listener.SignUpApiRequest
import com.example.agribridge.api.repository.SignUpRepository
import com.example.agribridge.api.listener.ProfileApiRequest
import com.example.agribridge.api.repository.ProfileRepository
import com.example.agribridge.viewmodel.ProfileViewModel
import com.example.agribridge.model.StatesResponseData
import com.example.agribridge.model.DistrictsResponseData
import com.example.agribridge.utils.getData
import com.example.agribridge.databinding.ActivitySignUpBinding
import com.example.agribridge.ui.authUser.dialogs.SuccessDialogFragment
import com.example.agribridge.utils.Constant.ExtraKey.USER_MOBILE_NUMBER
import com.example.agribridge.utils.KeyboardUtil
import com.example.agribridge.utils.applySystemBarsPadding2
import com.example.agribridge.utils.finishAffinityAndNavigateTo
import com.example.agribridge.utils.getText
import com.example.agribridge.utils.request
import com.example.agribridge.utils.setDrawableEndClickListener
import com.example.agribridge.utils.setText
import com.example.agribridge.utils.toast
import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.agribridge.ui.authUser.activities.OtpVerificationActivity
import com.example.agribridge.viewmodel.SignUpViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private var verifiedPhoneNumber = ""
    private var activeVerificationToken = ""

    private val otpLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val token = result.data?.getStringExtra(OtpVerificationActivity.EXTRA_VERIFICATION_TOKEN) ?: ""
            val phone = result.data?.getStringExtra(OtpVerificationActivity.EXTRA_PHONE_NUMBER) ?: ""
            if (token.isNotEmpty()) {
                activeVerificationToken = token
                verifiedPhoneNumber = phone
                binding.etPhone.isEnabled = false
                toast("Phone number verified successfully!")
                submitRegistrationIfReady()
            }
        }
    }


    // VIEW MODEL
    private val signUpViewModel: SignUpViewModel by viewModels {
        viewModelFactory {
            initializer {
                SignUpViewModel(SignUpRepository(request(SignUpApiRequest::class.java)))
            }
        }
    }

    private val profileViewModel: ProfileViewModel by lazy {
        val factory = viewModelFactory {
            initializer {
                ProfileViewModel(ProfileRepository(request(ProfileApiRequest::class.java)))
            }
        }
        ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    // VARIABLE LIST
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    private var strUserMobileNumber = ""
    private var selectedState: String = ""
    private var selectedDistrict: String = ""
    private var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        onClicks()
        setObserver()
    }

    private fun init() {
        
        binding.root.applySystemBarsPadding2(binding.root, binding.toolbar)
        
        KeyboardUtil.assistActivity(this) // Enable fullscreen

        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }
        val bundle = intent?.extras
        strUserMobileNumber = bundle?.getString(USER_MOBILE_NUMBER, "").toString()
        if (strUserMobileNumber.isNotEmpty()) {
            setText(binding.etPhone, strUserMobileNumber)
        }
        
        // Fetch states list
        profileViewModel.getStates()
    }

    private fun onClicks() {
        binding.apply {
            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            
            // Password Visibility Toggle
            etPassword.setDrawableEndClickListener {
                togglePasswordVisibility()
            }

            etPassword.doAfterTextChanged {
                val drawableEnd = if (it.isNullOrEmpty()) 0
                else if (isPasswordVisible) R.drawable.ic_show_password else R.drawable.ic_hide_password
                etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, drawableEnd, 0)
            }

            // Confirm Password Visibility Toggle
            etConfirmPassword.setDrawableEndClickListener {
                toggleConfirmPasswordVisibility()
            }

            etConfirmPassword.doAfterTextChanged {
                val drawableEnd = if (it.isNullOrEmpty()) 0
                else if (isConfirmPasswordVisible) R.drawable.ic_show_password else R.drawable.ic_hide_password
                etConfirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0, 0, drawableEnd, 0
                )
            }

            btnContinue.setOnClickListener {
                if (isValid()) {
                    val phone = getText(etPhone).replace(" ", "")
                    if (activeVerificationToken.isEmpty() || verifiedPhoneNumber != phone) {
                        val intent = Intent(this@SignUpActivity, OtpVerificationActivity::class.java).apply {
                            putExtra(OtpVerificationActivity.EXTRA_PHONE_NUMBER, phone)
                        }
                        otpLauncher.launch(intent)
                        return@setOnClickListener
                    }

                    submitRegistrationIfReady()
                }
            }


            tvLogin.setOnClickListener {
                finish() // Since they likely came from Login, just finish
            }

            etPhone.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    try {
                        if (isEditing) return
                        isEditing = true
                        val raw = s.toString().replace(" ", "")
                        val formatted = StringBuilder()
                        for (i in raw.indices) {
                            formatted.append(raw[i])
                            if (i == 4 && i != raw.lastIndex) {
                                formatted.append(" ")
                            }
                        }
                        etPhone.setText(formatted.toString())
                        etPhone.setSelection(formatted.length)
                        isEditing = false
                    } catch (e: Exception) {
                        toast(e.message)
                    }
                }
            })
        }
    }

    private fun togglePasswordVisibility() {
        binding.apply {
            isPasswordVisible = !isPasswordVisible
            etPassword.transformationMethod =
                if (isPasswordVisible) null else PasswordTransformationMethod.getInstance()

            val drawableEnd = if (getText(etPassword).isEmpty()) 0
            else if (isPasswordVisible) R.drawable.ic_show_password else R.drawable.ic_hide_password

            etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, drawableEnd, 0)
            etPassword.setSelection(etPassword.text?.length ?: 0)
        }
    }

    private fun toggleConfirmPasswordVisibility() {
        binding.apply {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            etConfirmPassword.transformationMethod =
                if (isConfirmPasswordVisible) null else PasswordTransformationMethod.getInstance()

            val drawableEnd = if (getText(etConfirmPassword).isEmpty()) 0
            else if (isConfirmPasswordVisible) R.drawable.ic_show_password else R.drawable.ic_hide_password

            etConfirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, drawableEnd, 0)
            etConfirmPassword.setSelection(etConfirmPassword.text?.length ?: 0)
        }
    }

    private fun isValid(): Boolean {
        binding.apply {
            val firstName = getText(etFirstName).trim()
            val lastName = getText(etLastName).trim()
            val phone = getText(etPhone).trim()
            val email = getText(etEmail).trim()

            // First Name Validation
            if (firstName.isEmpty()) {
                etFirstName.error = getString(R.string.error_first_name_required)
                etFirstName.requestFocus()
                return false
            }
            if (firstName.length < 2) {
                etFirstName.error = getString(R.string.error_first_name_short)
                etFirstName.requestFocus()
                return false
            }

            // Last Name Validation
            if (lastName.isEmpty()) {
                etLastName.error = getString(R.string.error_last_name_required)
                etLastName.requestFocus()
                return false
            }

            // Phone Validation
            if (phone.isEmpty()) {
                etPhone.error = getString(R.string.error_phone_required)
                etPhone.requestFocus()
                return false
            }
            if (phone.length < 10) {
                etPhone.error = getString(R.string.error_phone_invalid)
                etPhone.requestFocus()
                return false
            }

            // Email Validation (Optional but must be valid if entered)
            if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches()
            ) {
                etEmail.error = getString(R.string.error_email_invalid)
                etEmail.requestFocus()
                return false
            }

            // State Validation
            if (selectedState.isEmpty()) {
                etState.error = "State is required"
                etState.requestFocus()
                return false
            }

            // District Validation
            if (selectedDistrict.isEmpty()) {
                etDistrict.error = "District is required"
                etDistrict.requestFocus()
                return false
            }

            return true
        }
    }

    private fun submitRegistrationIfReady() {
        binding.apply {
            val childJson = JsonObject().apply {
                addProperty("phone_number", getText(etPhone).replace(" ", ""))
                addProperty("verification_token", activeVerificationToken)
                addProperty("password", getText(etPassword))
                addProperty("first_name", getText(etFirstName))
                addProperty("last_name", getText(etLastName))
                addProperty("email", getText(etEmail))
                addProperty("state", selectedState)
                addProperty("district", selectedDistrict)
            }
            Log.d("TAG", "api>>parsing>>information :: ${Gson().toJson(childJson)} ")
            signUpViewModel.apiCallForUserSignUp(childJson)
        }
    }

    private fun setObserver() {

        signUpViewModel.getUserSignUpResponse.observe(this) { apiResponse ->
            if (apiResponse?.statusCode == 201 || apiResponse?.type?.lowercase() == "success") {
                val successDialog = SuccessDialogFragment.newInstance {
                    finishAffinityAndNavigateTo(LoginActivity::class.java)
                }
                successDialog.isCancelable = false
                successDialog.show(supportFragmentManager, SuccessDialogFragment.TAG)

            } else {
                toast(apiResponse?.message ?: getString(R.string.error_registration_failed))
            }
        }

        profileViewModel.statesResult.observe(this) { apiResponse ->
            if (apiResponse == null) return@observe
            if (apiResponse.statusCode == 200 || apiResponse.type?.equals("success", ignoreCase = true) == true) {
                val dataObj = getData(apiResponse.data, StatesResponseData::class.java)
                val statesList = dataObj?.states ?: emptyList()

                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, statesList)
                binding.etState.setAdapter(adapter)
                binding.etState.setOnItemClickListener { _, _, position, _ ->
                    val chosenState = adapter.getItem(position) ?: ""
                    if (chosenState != selectedState) {
                        selectedState = chosenState
                        selectedDistrict = ""
                        binding.etDistrict.setText("", false)
                        profileViewModel.getDistricts(chosenState)
                    }
                }
            } else {
                toast(apiResponse.message ?: "Failed to retrieve states")
            }
            profileViewModel.clearStatesResult()
        }

        profileViewModel.districtsResult.observe(this) { apiResponse ->
            if (apiResponse == null) return@observe
            if (apiResponse.statusCode == 200 || apiResponse.type?.equals("success", ignoreCase = true) == true) {
                val dataObj = getData(apiResponse.data, DistrictsResponseData::class.java)
                val districtsList = dataObj?.districts ?: emptyList()

                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, districtsList)
                binding.etDistrict.setAdapter(adapter)
                binding.etDistrict.setOnItemClickListener { _, _, position, _ ->
                    selectedDistrict = adapter.getItem(position) ?: ""
                }
            } else {
                toast(apiResponse.message ?: "Failed to retrieve districts")
            }
            profileViewModel.clearDistrictsResult()
        }
    }
}
