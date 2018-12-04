package com.example.muhammadfarhan.demoauthfirebasejava;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import static com.example.muhammadfarhan.demoauthfirebasejava.Constants.FIREBASE_PARENT_NODE_NAME;

public class SignUpFragment extends Fragment {

    // Global Variable
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private Utils utils = Utils.getInstance();
    private Context context;
    private ProgressDialog progressDialog;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private EditText userName;
    private String uid;

    public SignUpFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    // This method runs after View is created and every thing starts
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        // Btn Go back to Sign In Fragment
        view.findViewById(R.id.goToSignInFrag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.goToSignIn);
            }
        });

        // Btn Sign Up
        view.findViewById(R.id.btn_sing_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    // Helper Method which first check inputs validations and then Sign Up
    private void signUp() {

        if (TextUtils.isEmpty(userName.getText().toString())) {
            userName.setError(getString(R.string.empty_edittext));
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

        if (TextUtils.isEmpty(confirmPassword.getText().toString())) {
            confirmPassword.setError(getString(R.string.empty_edittext));
            return;
        }

        if (password.getText().toString().length() < 6) {
            password.setError(getString(R.string.password_less_digit_error));
            return;
        }

        if (confirmPassword.getText().toString().length() < 6) {
            confirmPassword.setError(getString(R.string.password_less_digit_error));
            return;
        }

        if (!utils.isValidEmail(email.getText().toString())) {
            email.setError(getString(R.string.error_email_format));
            return;
        }

        if (!(password.getText().toString().equals(confirmPassword.getText().toString()))) {
            confirmPassword.setError(getString(R.string.password_not_matched));
            return;
        }

        progressDialog.show();
        // Logic's to Sign up from Fire Base Auth
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            uid = mAuth.getCurrentUser().getUid();
                            mDatabase.getReference().child(FIREBASE_PARENT_NODE_NAME).child(Constants.REFERENCE_USERS).child(uid).setValue(new User(uid, userName.getText().toString(), email.getText().toString())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        NavHostFragment.findNavController(SignUpFragment.this).navigate(R.id.goToHomeAfterSignUp);
                                    } else {
                                        utils.showToast(context, "Error: " + task.getException().getLocalizedMessage());
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        } else {
                            utils.showToast(context, getString(R.string.error_register_user) + task.getException().getMessage());
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    // Helper Method to initialize Global Variables
    private void initViews(View view) {
        context = getContext();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(getString(R.string.please_wait));
        userName = view.findViewById(R.id.et_sign_up_name);
        email = view.findViewById(R.id.et_sign_up_email);
        password = view.findViewById(R.id.et_sign_up_password);
        confirmPassword = view.findViewById(R.id.et_sign_up_confirm_password);
    }
}
