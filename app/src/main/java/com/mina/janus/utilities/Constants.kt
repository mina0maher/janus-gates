package com.mina.janus.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast


object Constants {
    const val KEY_IS_SIGNED_IN = "isSignedIn"
    const val KEY_USER_NAME="userName"
    const val KEY_USER_EMAIL="userEmail"
    const val KEY_JSESSOIONID="jsessionid"
    const val KEY_USER_PASSWORD="userPassword"
    const val KEY_PREFERENCE_NAME = "marketXPreference"
    const val KEY_HOME_SAVED_INSTANCE = "homeSavedInstance"
    const val KEY_PRODUCT_SAVED_INSTANCE = "productSavedInstance"
    const val KEY_RECYCLER_SAVED_INSTANCE = "recyclerSavedInstance"
    const val KEY_IS_LOGIN_CLICKED = "isLoginClicked"
    const val KEY_IS_SIGNUP_CLICKED = "isSignUpClicked"
    private var toast: Toast? = null
     fun showToast(message: String,context: Context) {
        if(toast !=null){
            toast!!.cancel()

        }
        toast =  Toast.makeText(context,message,Toast.LENGTH_SHORT)
        toast!!.show()
    }

    fun isOnline(context: Context): Boolean {
         val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
         val n = cm.activeNetwork
         if (n != null) {
             val nc = cm.getNetworkCapabilities(n)
             return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
         }
         return false
     }


}