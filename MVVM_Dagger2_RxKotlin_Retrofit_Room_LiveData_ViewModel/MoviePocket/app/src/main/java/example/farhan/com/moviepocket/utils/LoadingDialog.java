package example.farhan.com.moviepocket.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import example.farhan.com.moviepocket.R;

// Custom Made Loading Dialog Singleton class
public class LoadingDialog {

    private LoadingDialog() {
    }

    private static LoadingDialog loadingDialog;
    public static LoadingDialog getInstance() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog();
        }
        return loadingDialog;
    }

    private static AlertDialog alertDialog;
    private static com.victor.loading.rotate.RotateLoading loadingBar;

    public void setLoadingDialog(Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.LoadingDialogTheme);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.loading_progrees_dialog, null);
        dialog.setView(rootView);
        alertDialog = dialog.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingBar = rootView.findViewById(R.id.rotateLoading);
    }

    public void showLoadingDialog() {
        alertDialog.show();
        loadingBar.start();
    }

    public void dismissLoadingDialog() {
        alertDialog.dismiss();
        loadingBar.stop();
    }
}
