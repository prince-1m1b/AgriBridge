package com.example.agribridge.ui.authUser.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.*
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.viewmodel.*
import com.example.agribridge.R
import com.example.agribridge.api.listener.LoginApiRequest
import com.example.agribridge.api.repository.LoginRepository
import com.example.agribridge.databinding.ActivityLoginBinding
import com.example.agribridge.model.*
import com.example.agribridge.ui.dashboard.activities.DashboardActivity
import com.example.agribridge.ui.authUser.dialogs.LanguageSelectionDialogFragment
import com.example.agribridge.utils.*
import com.example.agribridge.utils.Constant.ExtraKey.COME_FROM
import com.example.agribridge.viewmodel.LoginViewModel
import com.google.gson.JsonObject

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    
    //VIEW MODEL
    private val loginViewModel: LoginViewModel by viewModels {
        viewModelFactory {
            initializer {
                LoginViewModel(LoginRepository(request(LoginApiRequest::class.java)))
            }
        }
    }
    
    //VARIABLE LIST
    private var strComeFrom: String? = null
    private var strCountryCode: String? = null
    private var isPasswordVisible = false
    private var isEditing = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        LanguageManager.applySavedLocale(this)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        init()
        onClicks()
        setObserver()
        checkLanguageSelection()
    }
    
    private fun init() {
        binding.root.applySystemBarsPadding2(binding.root, binding.toolbar)
        
        val bundle = intent?.extras
        strComeFrom = bundle?.getString(COME_FROM, "")
        
        if (Preference(this).getStringPreferenceForUser(Constant.UserDetails.ACCESS_TOKEN).isNotEmpty()) {
            finishAffinityAndNavigateTo(DashboardActivity::class.java)
        }
    }
    
    private fun onClicks() {
        binding.apply {
            etPassword.setDrawableEndClickListener {
                togglePasswordVisibility()
            }
            
            etPassword.doAfterTextChanged {
                val drawableEnd = if (it.isNullOrEmpty()) 0
                else if (isPasswordVisible) R.drawable.ic_show_password else R.drawable.ic_hide_password
                
                etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_password, 0, drawableEnd, 0)
            }
            
            btnLogin.setOnClickListener {
                Log.d("TAG", "onClicks>>phone number : $strCountryCode")
                val phone = getText(etPhone)
                val password = getText(etPassword)
                
                if (phone.isEmpty() || password.isEmpty()) {
                    toast(getString(R.string.e_please_enter_phone_password))
                    return@setOnClickListener
                }

                val jsonObject = JsonObject().apply {
                    addProperty("phone_number", getText(etPhone).replace(" ", ""))
                    addProperty("password", getText(etPassword))
                }

                loginViewModel.apiCallForUserLogin(jsonObject)
            }
            
            tvSignUp.setOnClickListener {
                navigateTo(SignUpActivity::class.java)
            }
            
            etPhone.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                
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
            etPassword.transformationMethod = if (isPasswordVisible) null else PasswordTransformationMethod.getInstance()
            etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_password, 0, if (isPasswordVisible) R.drawable.ic_show_password else R.drawable.ic_hide_password, 0)
            etPassword.setSelection(etPassword.text?.length ?: 0)
            etPassword.requestFocus()
        }
    }
    
    @SuppressLint("GestureBackNavigation")
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }
        
        return super.onKeyDown(keyCode, event)
    }
    
    private fun setObserver() {
        binding.apply {
            loginViewModel.getUserLoginResponse.observe(this@LoginActivity) { apiResponse ->
                if (apiResponse?.statusCode == 200 || apiResponse?.type?.equals("success", ignoreCase = true) == true) {
                    apiResponse.data?.let { data ->
                        val value = getData(data, LoginResponse::class.java)
                        storeUserDetails(value)
                        toast(apiResponse.message ?: getString(R.string.msg_logged_in))
                        finishAffinityAndNavigateTo(DashboardActivity::class.java)
                    }
                    
                } else {
                    toast(apiResponse?.message ?: getString(R.string.error_login_failed))
                }
            }
        }
    }

    private fun checkLanguageSelection() {
        if (!LanguageManager.isLanguageSelected(this)) {
            val dialog = LanguageSelectionDialogFragment.newInstance { _ ->
                // Activity will automatically recreate itself when locale changes
            }
            dialog.isCancelable = false
            dialog.show(supportFragmentManager, LanguageSelectionDialogFragment.TAG)
        }
    }
}