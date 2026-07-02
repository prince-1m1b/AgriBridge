package com.example.agribridge.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.agribridge.ui.authUser.activities.LoginActivity
import com.example.agribridge.ui.authUser.dialogs.LanguageSelectionDialogFragment
import com.example.agribridge.ui.dashboard.activities.DashboardActivity
import com.example.agribridge.utils.Constant
import com.example.agribridge.utils.LocaleHelper
import com.example.agribridge.utils.Preference
import com.example.agribridge.utils.finishAndNavigateTo
import com.example.agribridge.utils.LanguageManager
import com.example.agribridge.utils.setStatusBar
import com.example.agribridge.databinding.ActivitySplashBinding as SplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: SplashBinding
    private lateinit var preference: Preference

    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge()
        setStatusBar()

        super.onCreate(savedInstanceState)
        binding = SplashBinding.inflate(layoutInflater)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

            binding.root.updatePadding(bottom = if (imeVisible) 0 else navBars)

            insets
        }

        setContentView(binding.root)
        preference = Preference(this)

        Handler(Looper.getMainLooper()).postDelayed({
            LanguageManager.applySavedLocale(this)

            if (isUserLoggedIn()) {
                navigateToHome()

            } else {
                finishAndNavigateTo(LoginActivity::class.java)
            }
        }, 2000) // 2 seconds delay
    }


    private fun isUserLoggedIn(): Boolean {
        val phone = preference.getStringPreferenceForUser(Constant.UserDetails.USER_PHONE_NUMBER)
        val token = preference.getStringPreferenceForUser(Constant.UserDetails.ACCESS_TOKEN)
        return phone.isNotEmpty() && token.isNotEmpty()
    }

    private fun navigateToHome() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}