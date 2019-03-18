package com.farhan.moviepocket.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Base64
import android.widget.Toast

object Utils {

    private var toast: Toast? = null
    fun showToast(context: Context, message: String) {
        if (toast != null)
            toast!!.cancel()
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast!!.show()
    }

    fun convertBase64ToBitmap(base64String: String): Bitmap {
        val decodedString = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo: NetworkInfo?
        netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

    fun calculateNoOfColumns(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val scalingFactor = 500
        var noOfColumns = (dpWidth / scalingFactor).toInt()
        if (noOfColumns < 2)
            noOfColumns = 2
        return noOfColumns
    }
}