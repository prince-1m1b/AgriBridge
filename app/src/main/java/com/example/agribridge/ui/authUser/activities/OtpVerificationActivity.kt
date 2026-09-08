package com.example.agribridge.ui.authUser.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.agribridge.R
import com.example.agribridge.api.listener.OtpApiRequest
import com.example.agribridge.api.repository.OtpRepository
import com.example.agribridge.databinding.ActivityOtpVerificationBinding
import com.example.agribridge.utils.applySystemBarsPadding2
import com.example.agribridge.utils.request
import com.example.agribridge.utils.toast
import com.example.agribridge.viewmodel.OtpViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpVerificationBinding
    private var rawPhoneNumber: String = ""
    private var countDownTimer: CountDownTimer? = null
    private var resendAttemptsCount = 0
    private val maxResends = 3

    private val otpViewModel: OtpViewModel by viewModels {
        viewModelFactory {
            initializer {
                OtpViewModel(OtpRepository(request(OtpApiRequest::class.java)))
            }
        }
    }

    companion object {
        const val EXTRA_PHONE_NUMBER = "extra_phone_number"
        const val EXTRA_VERIFICATION_TOKEN = "extra_verification_token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setupOtpInputListeners()
        setupClickListeners()
        setupObservers()
        startCountdownTimer(60000)
        if (rawPhoneNumber.isNotEmpty()) {
            otpViewModel.sendOtp(rawPhoneNumber)
        }
    }


    private fun initViews() {
        binding.root.applySystemBarsPadding2(binding.root, binding.toolbar)
        rawPhoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER) ?: ""
        binding.tvPhoneNumber.text = rawPhoneNumber
        validateOtpCompletion()
    }

    private fun setupOtpInputListeners() {
        val boxes = arrayOf(
            binding.etDigit1, binding.etDigit2, binding.etDigit3,
            binding.etDigit4, binding.etDigit5, binding.etDigit6
        )

        for (i in boxes.indices) {
            boxes[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty() && i < boxes.size - 1) {
                        boxes[i + 1].requestFocus()
                    }
                    validateOtpCompletion()
                }
            })

            boxes[i].setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    if (boxes[i].text.isNullOrEmpty() && i > 0) {
                        boxes[i - 1].requestFocus()
                        boxes[i - 1].setText("")
                        return@setOnKeyListener true
                    }
                }
                false
            }
        }
    }

    private fun getEnteredOtp(): String {
        return "${binding.etDigit1.text}${binding.etDigit2.text}${binding.etDigit3.text}" +
               "${binding.etDigit4.text}${binding.etDigit5.text}${binding.etDigit6.text}".trim()
    }

    private fun validateOtpCompletion() {
        val otp = getEnteredOtp()
        binding.btnVerify.isEnabled = (otp.length == 6)
        if (otp.length == 6) {
            binding.btnVerify.alpha = 1.0f
        } else {
            binding.btnVerify.alpha = 0.5f
        }
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnEditNumber.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnVerify.setOnClickListener {
            val otp = getEnteredOtp()
            if (otp.length < 6) {
                toast(getString(R.string.enter_six_digit_otp))
                return@setOnClickListener
            }
            binding.progressBar.visibility = View.VISIBLE
            binding.btnVerify.visibility = View.INVISIBLE
            binding.tvErrorMessage.visibility = View.GONE
            otpViewModel.verifyOtp(rawPhoneNumber, otp)
        }

        binding.btnResend.setOnClickListener {
            if (resendAttemptsCount >= maxResends) {
                toast("Maximum resend attempts reached. Please try again later.")
                return@setOnClickListener
            }
            resendAttemptsCount++
            clearInputs()
            binding.progressBar.visibility = View.VISIBLE
            otpViewModel.sendOtp(rawPhoneNumber)
            startCountdownTimer(60000)
        }
    }

    private fun clearInputs() {
        val boxes = arrayOf(
            binding.etDigit1, binding.etDigit2, binding.etDigit3,
            binding.etDigit4, binding.etDigit5, binding.etDigit6
        )
        boxes.forEach { it.setText("") }
        boxes[0].requestFocus()
    }

    private fun startCountdownTimer(millis: Long) {
        countDownTimer?.cancel()
        binding.tvResendTimer.visibility = View.VISIBLE
        binding.btnResend.visibility = View.GONE

        countDownTimer = object : CountDownTimer(millis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding.tvResendTimer.text = String.format("00:%02d", seconds)
            }

            override fun onFinish() {
                binding.tvResendTimer.visibility = View.GONE
                binding.btnResend.visibility = View.VISIBLE
            }
        }.start()
    }

    private fun setupObservers() {
        otpViewModel.sendOtpResult.observe(this) { response ->
            binding.progressBar.visibility = View.GONE
            if (response == null) return@observe
            if (response.statusCode == 200 || response.type?.equals("success", ignoreCase = true) == true) {
                toast(response.message ?: "Verification code resent successfully!")
            } else {
                toast(response.message ?: getString(R.string.e_something_went_wrong))
            }
            otpViewModel.clearSendResult()
        }

        otpViewModel.verifyOtpResult.observe(this) { response ->
            binding.progressBar.visibility = View.GONE
            binding.btnVerify.visibility = View.VISIBLE

            if (response == null) return@observe

            if (response.statusCode == 200 || response.type?.equals("success", ignoreCase = true) == true) {
                var token = ""
                try {
                    val gson = Gson()
                    val jsonElement = gson.toJsonTree(response.data)
                    if (jsonElement != null && jsonElement.isJsonObject) {
                        token = jsonElement.asJsonObject.get("verification_token")?.asString ?: ""
                    }
                    if (token.isEmpty()) {
                        val jsonString = gson.toJson(response.data)
                        val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)
                        token = jsonObject?.get("verification_token")?.asString ?: ""
                    }
                } catch (e: Exception) {
                    android.util.Log.e("OTP>>", "Failed to parse verification token: ${e.message}")
                }

                if (token.isEmpty()) {
                    binding.tvErrorMessage.text = "Failed to retrieve verification session token. Please try again."
                    binding.tvErrorMessage.visibility = View.VISIBLE
                    return@observe
                }

                val returnIntent = Intent().apply {
                    putExtra(EXTRA_VERIFICATION_TOKEN, token)
                    putExtra(EXTRA_PHONE_NUMBER, rawPhoneNumber)
                }
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            } else {
                binding.tvErrorMessage.text = response.message ?: "Invalid OTP entered. Please try again."
                binding.tvErrorMessage.visibility = View.VISIBLE
                clearInputs()
            }
            otpViewModel.clearVerifyResult()
        }
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }
}
