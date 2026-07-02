package com.example.agribridge.ui.dashboard.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.example.agribridge.R
import com.example.agribridge.databinding.ActivityDashboardBinding
import com.example.agribridge.ui.dashboard.fragments.ChatFragment
import com.example.agribridge.ui.dashboard.fragments.HomeFragment
import com.example.agribridge.ui.dashboard.fragments.ProfileFragment
import com.example.agribridge.utils.Constant.ExtraKey.SEARCH_QUERY
import com.example.agribridge.utils.Constant.Preference.IS_FROM
import com.example.agribridge.utils.addOnBackPressedDispatcher
import com.example.agribridge.utils.applySystemBarsPadding2
import com.example.agribridge.utils.prefManager
import com.example.agribridge.utils.viewGone
import com.example.agribridge.utils.viewVisible

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var currentFragment: Fragment
    private var queryString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.applySystemBarsPadding2(binding.root, binding.toolbar)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        init()
        onClick()
        addOnBackPressedDispatcher { handleBackPress() }
    }

    // ─── Init ─────────────────────────────────────────────────────────────────

    private fun init() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        queryString = intent.extras?.getString(SEARCH_QUERY, "") ?: ""
        if (queryString.isNotEmpty()) {
            prefManager().setStringPreference(IS_FROM, getString(R.string.search_result))
            getString(R.string.search_result)
        } else {
            prefManager().setStringPreference(IS_FROM, getString(R.string.home))
            getString(R.string.home)
        }

        val startFragment = when (prefManager().getStringPreference(IS_FROM)) {
            getString(R.string.chat) -> ChatFragment()
            getString(R.string.profile) -> ProfileFragment()
            else -> HomeFragment()
        }
        navigateToFragment(startFragment, addToBackStack = false)
    }

    // ─── Click listeners ─────────────────────────────────────────────────────

    private fun onClick() {
        with(binding) {
            llHome.setOnClickListener { switchToFragment(HomeFragment()) }
            llChat.setOnClickListener { switchToFragment(ChatFragment()) }
            llProfile.setOnClickListener { switchToFragment(ProfileFragment()) }

        }
    }

    // ─── Fragment navigation ─────────────────────────────────────────────────

    /**
     * Single source of truth for navigating to a fragment.
     * Replaces the old duplicated pair of navigateToFragment() + addedTheFragment().
     */
    private fun navigateToFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction =
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(fragment::class.java.simpleName)
        }
        transaction.commit()

        currentFragment = fragment
        syncUI(fragment)
    }

    /**
     * Switch tabs — only navigates if a different tab is selected.
     */
    private fun switchToFragment(fragment: Fragment) {
        if (currentFragment::class != fragment::class) {
            navigateToFragment(fragment)
        }
    }

    // ─── Back press ──────────────────────────────────────────────────────────

    private fun handleBackPress() {
        val fm = supportFragmentManager
        val visible = fm.findFragmentById(R.id.fragmentContainerView)

        if (fm.backStackEntryCount > 0) {
            fm.popBackStackImmediate()
            syncUIFromBackStack()
        } else {
            if (visible is HomeFragment) finishAndRemoveTask()
            else fm.popBackStackImmediate()
        }
    }

    private fun syncUIFromBackStack() {
        val visible =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) ?: HomeFragment()
        currentFragment = visible
        syncUI(visible)
    }

    // ─── UI sync ─────────────────────────────────────────────────────────────

    /**
     * Updates top bar visibility AND bottom nav selected state to match [fragment].
     */
    private fun fragmentName(fragment: Fragment) = when (fragment) {
        is HomeFragment -> getString(R.string.home)
        is ChatFragment -> getString(R.string.toolbar_chat_title)
        is ProfileFragment -> getString(R.string.toolbar_profile_title)
        else -> getString(R.string.home)
    }

    private fun syncUI(fragment: Fragment) {

        if (fragment is ChatFragment) {
            viewGone(binding.toolbar)
        } else {
            viewVisible(binding.toolbar)
        }

        binding.tvToolbarTitle.text = fragmentName(fragment)
        binding.toolbar.setBackgroundColor(
            ContextCompat.getColor(
                this, R.color.light_green
            )
        ) // Toolbar Title Color
        binding.tvToolbarTitle.setTextColor(ContextCompat.getColor(this, R.color.white))
        updateBottomNav(fragment)
    }
    /**
     *
     * Shows/hides the correct top-bar mode:
     *  • Search-result mode  → llSearchOption visible, logo + search icon hidden
     *  • Normal mode         → llLogo + ivSearch visible, search options hidden
     */

    /**
     * Highlights the active bottom nav tab:
     *  • Active  : green icon tint + visible label + top indicator shown
     *  • Inactive: gray icon tint  + label hidden  + indicator hidden
     *
     * FIX 1: uses tvProfile (not tvChat duplicate) for the Profile tab
     * FIX 2: only references views that exist in the layout
     */
    private fun updateBottomNav(selected: Fragment) {

        with(binding) {
            viewGone(tvHome)
            viewGone(tvChat)
            viewGone(tvProfile)

            // Highlight the selected tab
            when (selected) {
                is HomeFragment -> {
                    viewVisible(tvHome)
                }

                is ChatFragment -> {
                    viewVisible(tvChat)
                }

                is ProfileFragment -> {
                    viewVisible(tvProfile)
                }
            }
        }
    }
}