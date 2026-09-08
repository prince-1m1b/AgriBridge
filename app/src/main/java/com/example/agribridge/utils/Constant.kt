package com.example.agribridge.utils

object Constant {
    
    object MobileLength {
        
        val phoneLengthsPerCountry = mapOf(
            "IN" to 10,
            "US" to 10,
            "GB" to 10,
            "AE" to 9,
            "FR" to 9,
            "DE" to 11,
            "PK" to 10,
        )
    }
    
    //PREFERENCE KEY
    object Preference {
        
        const val PREFERENCE_NAME = "AgriBridge"
        const val USER = "user"
        const val SESSION_TOKEN = "sessionToken"
        const val TOKEN = "token"
        const val LOGIN_CODE = "loginCode"
        const val VEHICLE_ID = "vehicleId"
        const val IS_FROM = "isFrom"
        const val IS_CARD_LIST_EMPTY = "isCardListEmpty"
        const val SAVE_CARD_DETAILS = "saveCardDetails"
        const val VERSION = "version"
        const val SHOW_UPDATE_ALERT = "showUpdateAlert"
        const val LATEST_VERSION = "latestVersion"
        const val ON_ACCOUNT_PERMISSION = "display_on_account_option"
        const val LAST_SEARCH_QUERY = "last_search_query"
        const val PHONE_NUMBER = "phone"
        const val PASSWORD = "password"
        const val IS_LANGUAGE_SELECTED = "is_language_selected"
        const val SELECTED_LANGUAGE = "selected_language"
    }
    
    object UserDetails {
        
        const val USER_PHONE_NUMBER = "phone"
        const val USER_FIRST_NAME = "first_name"
        const val USER_LAST_NAME = "last_name"
        const val USER_EMAIL = "email"
        const val USER_STATE = "state"
        const val USER_DISTRICT = "district"
        const val USER_ID = "userId"
        const val ACCESS_TOKEN = "access_token"
        const val USER_IS_ACTIVE = "is_active"
        const val USER_CREATED_AT = "created_at"
    }
    
    //API KEY
    object Api {
        
        const val X_API_KEY = "X-Api-Key"
        const val AUTHORIZATION = "Authorization"
        
    }
    
    object ServiceStatus {
        
        const val ON_ROUTE = "on-route"
        const val IN_TOW = "in-tow"
    }
    
    //EXTRA KEY
    object ExtraKey {
        
        const val KEY = "key"
        const val COME_FROM = "come_from"
        const val OTP = "otp"
        const val USER_MOBILE_NUMBER = "user_mobile_number"
        const val IS_NEW_USER = "isNewUser"
        const val FORGOT_PASSWORD = "ForgotPassword"
        const val CHANGE_PASSWORD = "change_password"
        const val SEARCH_QUERY = "search_query"
        const val SEARCH_RESULTS = "Search_result"
        const val HOME = "home"
        const val EDIT_TEXT = "edit_text"
        const val SAVE_ACCOUNT = "save_account"
        const val DIRECT_LOGIN = "login_account"
        const val VERIFICATION_TOKEN = "verification_token"
    }

    
    //DATE FORMAT KEY
    object DateFormat {
        
        const val YYYYMMDD_HHMMSS = "yyyyMMdd_HHmmss"
        const val DDMMYYYY_HHMMSS = "dd/MM/yyyy HH:mm:ss"
        const val YYYYMMDD_T_HHMMSS_SSS_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        const val MMDDYYYY = "MM-dd-yyyy"
        
    }
    
}