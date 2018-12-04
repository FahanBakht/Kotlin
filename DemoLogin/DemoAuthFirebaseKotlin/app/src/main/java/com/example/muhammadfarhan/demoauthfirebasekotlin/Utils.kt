package com.example.muhammadfarhan.demoauthfirebasekotlin

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast

object Utils {

    private var toast: Toast? = null

    // To Show Toast efficiently
    fun showToast(context: Context, message: String) {
        if (toast != null)
            toast!!.cancel()
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast!!.show()
    }

    // Check is email is valid or not
    fun isValidEmail(target: CharSequence?): Boolean {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    // Check Network State
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var netInfo: NetworkInfo? = null
        if (cm != null) {
            netInfo = cm.activeNetworkInfo
        }
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}