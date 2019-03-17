package example.farhan.com.moviepocket;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.farhan.com.moviepocket.utils.LoadingDialog;
import example.farhan.com.moviepocket.utils.NetworkUtils;
import example.farhan.com.moviepocket.utils.Utils;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_sign_in)
    Button btnSignIn;
    @BindView(R.id.btn_navigate_to_sign_up)
    TextView btnToSignUp;
    @BindView(R.id.et_sign_in_email)
    EditText email;
    @BindView(R.id.et_sign_in_password)
    EditText password;
    @BindView(R.id.btn_forget_pass)
    TextView btnForgetPass;

    private FirebaseAuth mAuth;
    private Utils utils = Utils.getInstance();
    private LoadingDialog loadingDialog = LoadingDialog.getInstance();

    @Override
    protected void onResume() {
        super.onResume();
        loadingDialog.setLoadingDialog(SignInActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        loadingDialog.setLoadingDialog(SignInActivity.this);
        mAuth = FirebaseAuth.getInstance();

        // Checking if user is Sign-in or not
        loadingDialog.showLoadingDialog();
        if (mAuth.getCurrentUser() != null) {
            loadingDialog.dismissLoadingDialog();
            SignInActivity.this.finish();
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
        } else {
            loadingDialog.dismissLoadingDialog();
        }

        btnSignIn.setOnClickListener(this);
        btnToSignUp.setOnClickListener(this);
        btnForgetPass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //Navigate To SignUp
            case R.id.btn_navigate_to_sign_up: {
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            }
            //Sign In Button
            case R.id.btn_sign_in: {
                if (NetworkUtils.isNetworkAvailable(SignInActivity.this)) {
                    signIn();
                } else {
                    utils.showToast(SignInActivity.this, getString(R.string.no_internet_connection_error));
                }
                break;
            }
            case R.id.btn_forget_pass: {
                showForgetPasswordDialog();
                break;
            }
        }
    }

    // Sign in method which Validate from Firebase Auth
    private void signIn() {

        if (!NetworkUtils.isNetworkAvailable(SignInActivity.this)) {
            utils.showToast(SignInActivity.this, getString(R.string.no_internet_connection_error));
            return;
        }

        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError(getString(R.string.empty_edittext));
            return;
        }

        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError(getString(R.string.empty_edittext));
            return;
        }

        if (!utils.isValidEmail(email.getText().toString())) {
            email.setError(getString(R.string.error_email_format));
            return;
        }

        if (password.getText().toString().length() < 6) {
            password.setError(getString(R.string.password_less_digit_error));
            return;
        }

        loadingDialog.showLoadingDialog();

        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            utils.showToast(SignInActivity.this, getString(R.string.sign_in_successfully));
                            loadingDialog.dismissLoadingDialog();
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                            SignInActivity.this.finish();
                        } else {
                            loadingDialog.dismissLoadingDialog();
                            utils.showToast(SignInActivity.this, getString(R.string.error_while_sign_in) + task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    public void showForgetPasswordDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.forget_password_dialog, null);
        dialog.setView(rootView);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.AboutUsDialogTheme;
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final EditText emailForgetPass = rootView.findViewById(R.id.et_forget_pass_email);
        Button cancel = rootView.findViewById(R.id.btn_forget_pass_cancel);
        Button send = rootView.findViewById(R.id.btn_forget_pass_send);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtils.isNetworkAvailable(SignInActivity.this)) {
                    utils.showToast(SignInActivity.this, getString(R.string.no_internet_connection_error));
                    return;
                }

                if (TextUtils.isEmpty(emailForgetPass.getText().toString())) {
                    emailForgetPass.setError(getString(R.string.empty_edittext));
                    return;
                }

                if (!utils.isValidEmail(emailForgetPass.getText().toString())) {
                    emailForgetPass.setError(getString(R.string.error_email_format));
                    return;
                }
                loadingDialog.showLoadingDialog();
                mAuth.sendPasswordResetEmail(emailForgetPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            utils.showToast(SignInActivity.this, getString(R.string.reset_pass_send) + emailForgetPass);
                            loadingDialog.dismissLoadingDialog();
                            alertDialog.dismiss();
                        } else {
                            utils.showToast(SignInActivity.this, getString(R.string.erro_reset_pass) + task.getException().getMessage());
                            loadingDialog.dismissLoadingDialog();
                            alertDialog.dismiss();
                        }
                    }
                });

            }
        });

        alertDialog.show();
        utils.doKeepDialog(alertDialog);
    }

}
