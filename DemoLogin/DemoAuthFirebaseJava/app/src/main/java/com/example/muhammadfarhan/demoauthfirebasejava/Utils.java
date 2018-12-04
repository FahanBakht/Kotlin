package com.example.muhammadfarhan.demoauthfirebasejava;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.view.WindowManager;
import android.widget.Toast;

// Utility Singleton Class for Global Helper method
class Utils {

    private static Utils appUtils;

    private Utils() {
    }

    static Utils getInstance() {
        if (appUtils == null) {
            appUtils = new Utils();
        }
        return appUtils;
    }

    private Toast toast;

    // To Show Toast efficiently
    void showToast(Context context, String message) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    // Check is email is valid or not
    boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    // Check Network State
    static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
