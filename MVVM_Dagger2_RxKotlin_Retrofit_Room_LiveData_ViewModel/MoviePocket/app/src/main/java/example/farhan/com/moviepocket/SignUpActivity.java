package example.farhan.com.moviepocket;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.farhan.com.moviepocket.model.User;
import example.farhan.com.moviepocket.utils.Constants;
import example.farhan.com.moviepocket.utils.LoadingDialog;
import example.farhan.com.moviepocket.utils.NetworkUtils;
import example.farhan.com.moviepocket.utils.Prefs;
import example.farhan.com.moviepocket.utils.Utils;

import static example.farhan.com.moviepocket.utils.Constants.FIREBASE_PARENT_NODE_NAME;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_navigate_to_sign_up)
    TextView btnToSignIn;
    @BindView(R.id.img_sign_up_profile_image)
    de.hdodenhof.circleimageview.CircleImageView profileImage;
    @BindView(R.id.et_sign_up_username)
    EditText userName;
    @BindView(R.id.et_sign_up_email)
    EditText email;
    @BindView(R.id.et_sign_up_password)
    EditText password;
    @BindView(R.id.et_sign_up_confirm_password)
    EditText confirmPassword;
    @BindView(R.id.btn_sign_in)
    Button btnSignUp;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDb;
    private Utils utils = Utils.getInstance();
    private Uri imageUri;
    private String uid;
    private LoadingDialog loadingDialog = LoadingDialog.getInstance();
    private Prefs mPrefs = Prefs.getInstance();

    @Override
    protected void onResume() {
        super.onResume();
        loadingDialog.setLoadingDialog(SignUpActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseDatabase.getInstance();

        btnToSignIn.setOnClickListener(this);
        profileImage.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    //Sign up
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

        if (imageUri == null) {
            utils.showToast(SignUpActivity.this, getString(R.string.select_profile_image));
            return;
        }
        loadingDialog.showLoadingDialog();
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            uid = mAuth.getCurrentUser().getUid();
                            uploadImageAndUploadUserData(imageUri);
                        } else {
                            utils.showToast(SignUpActivity.this, getString(R.string.error_register_user) + task.getException().getMessage());
                            loadingDialog.dismissLoadingDialog();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //Navigate To SignIn
            case R.id.btn_navigate_to_sign_up: {
                onBackPressed();
                break;
            }
            // Profile Image Picker
            case R.id.img_sign_up_profile_image: {
                openImagePicker();
                break;
            }
            //Sign Up Button
            case R.id.btn_sign_in: {
                if (NetworkUtils.isNetworkAvailable(SignUpActivity.this)) {
                    signUp();
                } else {
                    utils.showToast(SignUpActivity.this, getString(R.string.no_internet_connection_error));
                }
                break;
            }
        }
    }

    private void openImagePicker() {
        PickImageDialog.build(new PickSetup())
                .setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        if (r.getError() == null) {
                            r.getBitmap();
                            Glide.with(SignUpActivity.this).asBitmap().load(r.getBitmap()).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)).into(profileImage);
                            imageUri = r.getUri();
                        } else {
                            utils.showToast(SignUpActivity.this, "Error: " + r.getError().getMessage());
                        }
                    }
                })
                .setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {

                    }
                }).show(getSupportFragmentManager());

    }

    // Upload image to Fire base
    private void uploadImageAndUploadUserData(Uri file) {
        if (file != null) {

            final FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            final StorageReference ref = storageRef.child("images/" + file.getLastPathSegment());
            UploadTask uploadTask = ref.putFile(file);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (!task.isSuccessful()) {
                        utils.showToast(SignUpActivity.this, getString(R.string.toast_fail_to_upload) + task.getException().getLocalizedMessage());
                        loadingDialog.dismissLoadingDialog();
                    }
                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        final String downloadUri = task.getResult().toString();
                        mDb.getReference().child(FIREBASE_PARENT_NODE_NAME).child(Constants.REFERENCE_USERS).child(uid).setValue(new User(uid, userName.getText().toString(), email.getText().toString(), downloadUri));
                        utils.showToast(SignUpActivity.this, getString(R.string.register_ok));
                        mPrefs.saveUserNamePref(SignUpActivity.this, userName.getText().toString());
                        loadingDialog.dismissLoadingDialog();
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        SignUpActivity.this.finish();
                    } else {
                        // Handle failures
                        utils.showToast(SignUpActivity.this, getString(R.string.toast_fail_to_upload) + task.getException().getLocalizedMessage());
                        loadingDialog.dismissLoadingDialog();
                    }
                }
            });
        }
    }
}
