package example.farhan.com.moviepocket.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.WindowManager;
import android.widget.Toast;

// Utility Class for Global Helper method
public class Utils {

    private static Utils appUtils;

    private Utils() {
    }

    public static Utils getInstance() {
        if (appUtils == null) {
            appUtils = new Utils();
        }
        return appUtils;
    }

    private Toast toast;

    public void showToast(Context context, String message) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public String getYear(String date) {
        if (date != null) {
            return date.substring(0, 4);
        }
        return "";
    }

    // Prevent dialog dismiss when orientation changes
    public void doKeepDialog(Dialog dialog) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
    }

    public Bitmap convertBase64ToBitmap(String base64String){
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return  BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
