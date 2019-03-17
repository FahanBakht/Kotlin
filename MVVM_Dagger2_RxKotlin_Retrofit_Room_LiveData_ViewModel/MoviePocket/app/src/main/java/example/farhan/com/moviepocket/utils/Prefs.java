package example.farhan.com.moviepocket.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static example.farhan.com.moviepocket.utils.Constants.PREF_KEY_USER_NAME;
import static example.farhan.com.moviepocket.utils.Constants.USER_NAME;

// Singleton Prefs to Observe Username
public class Prefs {

    private static final String TAG = "Prefs";
    private static Prefs prefs;


    private Prefs() {
    }

    public static Prefs getInstance() {
        if (prefs == null) {
            prefs = new Prefs();
        }
        return prefs;
    }

    public void saveUserNamePref(Context context, String userName) {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREF_KEY_USER_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(USER_NAME, userName);
        mEditor.apply();
    }

    public String getUserNameFromPref(Context context) {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREF_KEY_USER_NAME, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(USER_NAME, "");
    }

    public  void clearUserNamePref(Context context) {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREF_KEY_USER_NAME, Context.MODE_PRIVATE);
        if (mSharedPreferences != null) {
            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
            mEditor.clear();
            mEditor.apply();
        } else {
            Log.e(TAG, "clearRecipePref: else is null");
        }
    }
}
