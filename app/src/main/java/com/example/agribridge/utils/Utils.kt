package com.example.agribridge.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.agribridge.App
import com.example.agribridge.R
import com.example.agribridge.api.ApiClient
import com.example.agribridge.api.ApiResponse
import com.example.agribridge.model.LoginResponse
import com.example.agribridge.model.UserData
import com.example.agribridge.ui.authUser.activities.LoginActivity

fun View.applySystemBarsPadding(rootView: ViewGroup, toolbarView: View?) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val statusBarHeight = systemBars.top
        val navigationBarHeight = systemBars.bottom

        toolbarView?.updatePadding(top = statusBarHeight)
        rootView.updatePadding(bottom = navigationBarHeight)

        insets
    }
}

fun View.applySystemBarsPadding2(rootView: ViewGroup, toolbarView: View?) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
        val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
        val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
        val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

        toolbarView?.updatePadding(top = statusBars + 20) // If keyboard is visible, avoid adding extra bottom padding
        rootView.updatePadding(bottom = if (imeVisible) 0 else navBars)

        insets
    }
}

fun Context?.toast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

//FOR SHARED PREFERENCES
fun Context?.prefManager(): Preference {
    return Preference(this)
}

//GET USER
fun Context?.getUser(): User {
    return App.gson.fromJson(
        prefManager().getStringPreference(Constant.Preference.USER),
        User::class.java
    )
}

//LOGOUT FROM APP
fun Activity.logout() {
    Preference(this).removePreferenceFileForUser()
    finishAffinityAndNavigateTo(LoginActivity::class.java)
    finishAffinity()
}

// PASSWORD VISIBILITY MANAGEMENT
@SuppressLint("ClickableViewAccessibility")
fun AppCompatEditText.setDrawableEndClickListener(onClick: () -> Unit) {
    setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            val drawableEnd = compoundDrawablesRelative[2]
            if (drawableEnd != null) {
                val bounds = drawableEnd.bounds
                val x = event.x.toInt()
                val width = width
                val drawableWidth = bounds.width()
                if (x >= width - drawableWidth - paddingEnd) {
                    onClick()
                    return@setOnTouchListener true
                }
            }
        }
        false
    }
}

//GET DATA
fun <T> getData(data: Any?, aClass: Class<T>): T? {
    return data?.let {
        if (data is String) {
            App.gson.fromJson(data, aClass)

        } else {
            App.gson.fromJson(App.gson.toJson(data), aClass)
        }
    }
}

//RETROFIT API REQUEST
fun <T> Context.request(classT: Class<T>): T {
    return ApiClient(this).retrofit.create(classT)
}

//SHOW RESPONSE ERROR SHOW
fun Context?.showErrorToast(apiResponse: ApiResponse?) {
    if (apiResponse?.type?.equals(this?.getString(R.string.success)) == false) {
        toast(apiResponse.message)
    }
}

fun AppCompatActivity?.replaceFragment(
    fragment: Fragment?, frameId: Int?, bundle: Bundle?, isAddToBackStack: Boolean = true
) {
    val backStateName = fragment?.let { fragment1 -> fragment1::class.java.name }
    val manager: FragmentManager? = this?.supportFragmentManager
    val ft: FragmentTransaction? = manager?.beginTransaction()
    frameId?.let { frame ->
        fragment?.let { fragment1 -> fragment1::class.java }
            ?.let { fragmentClass -> ft?.replace(frame, fragmentClass, bundle, backStateName) }
    }
    ft?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    if (isAddToBackStack) {
        ft?.addToBackStack(backStateName)
    }
    ft?.commit()
}

//ADD FRAGMENT
fun AppCompatActivity?.addFragment(fragment: Fragment, frameId: Int?, bundle: Bundle?) {
    this?.supportFragmentManager?.inTransaction {
        frameId?.let { frameId ->
            add(frameId, fragment::class.java, bundle, fragment::class.java.simpleName)
        }
        addToBackStack(fragment::class.java.simpleName)
    }
}

fun setImage(imageView: ImageView?, url: Int?) {
    imageView?.let { view ->
        Glide.with(view.context).load(url).centerCrop().into(view)
    }
}

fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

//START ACTIVITY
fun <T> Activity.navigateTo(mClass: Class<T>, bundle: (Bundle.() -> Unit) = {}) {
    val intent = Intent(this, mClass)
    intent.putExtras(Bundle().apply(bundle))
    startActivity(intent)
}

//START ACTIVITY
fun <T> Activity.finishAndNavigateTo(mClass: Class<T>, bundle: (Bundle.() -> Unit) = {}) {
    val intent = Intent(this, mClass)
    intent.putExtras(Bundle().apply(bundle))
    startActivity(intent)
    finish()
}

//REMOVE ALL ACTIVITY
fun <T> Activity.finishAffinityAndNavigateTo(mClass: Class<T>, bundle: (Bundle.() -> Unit) = {}) {
    val intent = Intent(this, mClass)
    intent.putExtras(Bundle().apply(bundle))
    startActivity(intent)
    finishAffinity()
}

//TO MAKE VIEW VISIBILITY GONE
fun viewGone(view: View?) {
    view?.visibility = View.GONE
}

//TO MAKE VIEW VISIBILITY VISIBLE
fun viewVisible(view: View?) {
    view?.visibility = View.VISIBLE
}

//IS EMPTY OR NULL
fun isEmpty(editText: EditText): Boolean {
    return getText(editText).trim().isEmpty()
}

//IS EMPTY OR NULL
fun isEmpty(textView: AppCompatTextView): Boolean {
    return getText(textView).trim().isEmpty()
}

//SET TEXT
fun setText(textView: AppCompatTextView, string: String?) {
    textView.text = string ?: ""
}

//SET TEXT
fun setText(textView: TextView, string: String?) {
    textView.text = string ?: ""
}

//SET TEXT
fun setTextWithTextColor(textView: AppCompatTextView, string: String?, color: Int) {
    textView.text = string ?: ""
}

//SET TEXT
fun setTextWithTextColor(textView: TextView, string: String?, color: Int) {
    textView.text = string ?: ""
}

//SET TEXT
fun setText(editText: AppCompatEditText, string: String?) {
    editText.setText(string ?: "")
}

//SET TEXT
fun setText(editText: EditText, string: String?) {
    editText.setText(string ?: "")
}

//GET TEXT
fun getText(textView: AppCompatTextView): String {
    return textView.text.toString()
}

//GET TEXT
fun getText(editText: EditText): String {
    return editText.text.toString().trim()
}

/*
*
* */

fun Activity.storeUserDetails(value: LoginResponse?) {
    val preference = Preference(this)

    preference.setStringPreferenceForUser(Constant.UserDetails.USER_ID, value?.user?._id)
    preference.setStringPreferenceForUser(
        Constant.UserDetails.USER_FIRST_NAME, value?.user?.first_name
    )
    preference.setStringPreferenceForUser(
        Constant.UserDetails.USER_LAST_NAME, value?.user?.last_name
    )
    preference.setStringPreferenceForUser(Constant.UserDetails.USER_EMAIL, value?.user?.email)
    preference.setStringPreferenceForUser(
        Constant.UserDetails.USER_PHONE_NUMBER, value?.user?.phone_number
    )
    preference.setStringPreferenceForUser(Constant.UserDetails.USER_STATE, value?.user?.state)
    preference.setStringPreferenceForUser(Constant.UserDetails.USER_DISTRICT, value?.user?.district)
    preference.setStringPreferenceForUser(Constant.UserDetails.ACCESS_TOKEN, value?.access_token)
    preference.setBooleanPreferenceForUser(
        Constant.UserDetails.USER_IS_ACTIVE, value?.user?.is_active ?: false
    )
    preference.setStringPreferenceForUser(
        Constant.UserDetails.USER_CREATED_AT, value?.user?.created_at
    )
}

fun Activity.getUserDetails(): UserData {
    val preference = Preference(this)
    return UserData(
        _id = preference.getStringPreferenceForUser(Constant.UserDetails.USER_ID),
        first_name = preference.getStringPreferenceForUser(Constant.UserDetails.USER_FIRST_NAME),
        last_name = preference.getStringPreferenceForUser(Constant.UserDetails.USER_LAST_NAME),
        email = preference.getStringPreferenceForUser(Constant.UserDetails.USER_EMAIL),
        phone_number = preference.getStringPreferenceForUser(Constant.UserDetails.USER_PHONE_NUMBER),
        state = preference.getStringPreferenceForUser(Constant.UserDetails.USER_STATE),
        district = preference.getStringPreferenceForUser(Constant.UserDetails.USER_DISTRICT),
        is_active = preference.getBooleanPreferenceForUser(Constant.UserDetails.USER_IS_ACTIVE, false),
        created_at = preference.getStringPreferenceForUser(Constant.UserDetails.USER_CREATED_AT)
    )
}

//MANGE BACK PRESS MAINTAIN
fun AppCompatActivity.addOnBackPressedDispatcher(onBackPressed: () -> Unit = { finish() }) {
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressed.invoke()
        }
    })
}

fun Activity.applySystemBarsPadding(
    rootView: View, toolbarView: View, extraToolbarBottomPadding: Int = 0
) { // Enable edge-to-edge layout
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val systemNavigation = SystemNavigation.create(this)
    ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        val bottomPadding = if (systemNavigation == SystemNavigation.THREE_BUTTON) {
            navBars.bottom
        } else {
            0
        }
        rootView.updatePadding(bottom = bottomPadding)

        toolbarView.updatePadding(top = systemBars.top, bottom = extraToolbarBottomPadding)

        insets
    }
}

fun disableScreenTouchInteraction(view: View?, delayTime: Long = 5000) {
    view?.isEnabled = false
    view?.isClickable = false

    Handler(Looper.getMainLooper()).postDelayed({
        view?.isEnabled = true
        view?.isClickable = true
    }, delayTime)
}

fun View.applySystemBarsPadding(
    rootView: ViewGroup,
    toolbarView: View?,
    addExtraPaddingToTopView: Boolean = false,
    considerIme: Boolean = false
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
        val withImeHeight = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
        val onlySystemBarHeight = WindowInsetsCompat.Type.systemBars()
        val systemBars = insets.getInsets(if (considerIme) withImeHeight else onlySystemBarHeight)
        val statusBarHeight =
            if (addExtraPaddingToTopView) systemBars.top + (systemBars.top) / 2 else systemBars.top
        val navigationBarHeight = systemBars.bottom

        toolbarView?.updatePadding(top = statusBarHeight)
        rootView.updatePadding(bottom = navigationBarHeight)

        insets
    }
}

fun Activity.setStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.setSystemBarsAppearance(
            0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
        )
    }
}


fun View.handleScrollEvent(onKeyboardAppear: () -> Unit = {}, onKeyboardHide: () -> Unit = {}) {
    ViewCompat.setOnApplyWindowInsetsListener(this as ViewGroup) { v: View, insets: WindowInsetsCompat ->
        val isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

        if (isKeyboardVisible) {
            onKeyboardAppear()

        } else {
            onKeyboardHide()
        }

        ViewCompat.onApplyWindowInsets(v, insets)
    }
}

fun hideSoftKeyboard(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun AppCompatEditText.showKeyboard() {
    requestFocus()
    Handler(Looper.getMainLooper()).postDelayed({
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }, 200)
}/*
root.handleScrollEvent(onKeyboardHide = {
    if (isAdded) {
        val focusedView = requireActivity().currentFocus
        focusedView?.let { view ->
            if (focusedView is EditText) {
                focusedView.clearFocus()
            }
        }
    }
})*/