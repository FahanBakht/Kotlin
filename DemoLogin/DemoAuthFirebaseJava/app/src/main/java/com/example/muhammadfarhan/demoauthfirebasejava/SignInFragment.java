package com.example.muhammadfarhan.demoauthfirebasejava;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import static com.example.muhammadfarhan.demoauthfirebasejava.Constants.FIREBASE_PARENT_NODE_NAME;
import static com.example.muhammadfarhan.demoauthfirebasejava.Constants.KEY_CAMEFROM;
import static com.example.muhammadfarhan.demoauthfirebasejava.Constants.KEY_EMAIL;
import static com.example.muhammadfarhan.demoauthfirebasejava.Constants.KEY_NAME;


public class SignInFragment extends Fragment {

    // Global Variable
    private static final String TAG = "SignInFragment";
    private EditText email;
    private EditText password;
    private Utils utils = Utils.getInstance();
    private Context context;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int Google_RC_SIGN_IN = 2;
    private static final int FACEBOOK_CONS = 64206;
    private CallbackManager mCallbackManager;
    private TwitterAuthClient twitterAuthClient;

    public SignInFragment() {
        // Required empty public constructor
    }


    // This method runs to create View
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        // Helper Method to initialize Global Variables
        initViews(view);

        // Getting the Twitter result back to Fragment from its Parent Activity
        ((MainActivity) getActivity()).setTwitterInterFace(new MainActivity.twitterInterFace() {
            @Override
            public void myOnActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                if (twitterAuthClient != null)
                    twitterAuthClient.onActivityResult(requestCode, resultCode, data);
            }
        });
        return view;
    }

    // This method runs after View is created and every thing starts
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog.show();
        if (mAuth.getCurrentUser() != null) {
            progressDialog.dismiss();
            Navigation.findNavController(view).navigate(R.id.goToHomeAfterSignIn);
        } else {
            progressDialog.dismiss();
        }

        // TextView for go back to Sign In Fragment
        view.findViewById(R.id.goToSignUpFrag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.goToSignUp);
            }
        });

        // Btn Facebook show dialog
        view.findViewById(R.id.btn_fb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFacebookLoginType();
            }
        });

        // Btn Google
        view.findViewById(R.id.btn_google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Configure Google Sign In
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                // Build a GoogleSignInClient with the options specified by gso.
                mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
                googleSignIn();
            }
        });

        // Btn Twitter
        view.findViewById(R.id.btn_twitter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // init Twitter
                TwitterConfig config = new TwitterConfig.Builder(getActivity())
                        .logger(new DefaultLogger(Log.DEBUG))
                        .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.twitter_key), getString(R.string.twitter_secret)))
                        .debug(true)
                        .build();
                Twitter.initialize(config);

                // Client for requesting authorization and email from the user.
                twitterAuthClient = new TwitterAuthClient();
                showTwitterLoginType();
            }
        });

        // Btn Sign In
        view.findViewById(R.id.btn_sing_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInViaFireBaseAuth();
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
        email = view.findViewById(R.id.et_sign_in_email);
        password = view.findViewById(R.id.et_sign_in_password);
        mCallbackManager = CallbackManager.Factory.create();

    }

    // Runs every time when there is another Activity or Fragment opens and returns back to this current Fragment or Activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult");

        if (requestCode == Google_RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.e(TAG, "google Sign onActivityResult If ");
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "googlemAuth Sign fails e " + e);
            }
        } else if (requestCode == FACEBOOK_CONS) {
            //If not request code is RC_SIGN_IN it must be facebook
            Log.e(TAG, "Callback Facebook");
            mCallbackManager.onActivityResult(requestCode, resultCode, data);

        } else if (twitterAuthClient.getRequestCode() == requestCode) {
            // Pass the activity result to the login button.
            Log.e(TAG, "Callback twitterAuthClientclient");
            twitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }

    }

    // Helper Method which first check inputs validations then Sign In Via Fire base Auth
    private void signInViaFireBaseAuth() {

        if (!utils.isNetworkAvailable(getContext())) {
            utils.showToast(context, getString(R.string.no_internet_connection_error));
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

        progressDialog.show();

        // FireBase Sign check from Auth
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            utils.showToast(context, getString(R.string.sign_in_successfully));
                            progressDialog.dismiss();
                            NavHostFragment.findNavController(SignInFragment.this).navigate(R.id.goToHomeAfterSignIn);
                        } else {
                            progressDialog.dismiss();
                            utils.showToast(context, getString(R.string.error_while_sign_in) + task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    // Helper Method Which Calls Google Sign In Intent
    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Google_RC_SIGN_IN);
    }

    // Helper Method to FireBase Login after Google Login
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e(TAG, "firebaseAuthWithGoogle");
        progressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            mDatabase.getReference().child(FIREBASE_PARENT_NODE_NAME).child(Constants.REFERENCE_USERS).child(mAuth.getUid()).setValue(new User(mAuth.getUid(), user.getDisplayName(), user.getEmail())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        NavHostFragment.findNavController(SignInFragment.this).navigate(R.id.goToHomeAfterSignIn);
                                    } else {
                                        progressDialog.dismiss();
                                        utils.showToast(context, "Error: " + (task.getException().getLocalizedMessage()));
                                    }
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            utils.showToast(context, getString(R.string.Auth_failed));
                        }
                    }
                });
    }

    // Show About us Facebook Login Type
    private void showFacebookLoginType() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.dialog_fb_sign_in, null);
        dialog.setView(rootView);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.material_color_white)));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.AboutUsDialogTheme;
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        // Facebook FireBase Login Button to Login into Facebook then gives Credentials to Firebase
        rootView.findViewById(R.id.fb_sign_in_via_firebase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                faceBookInit(0, alertDialog);
            }
        });

        // Facebook Direct Login Button
        rootView.findViewById(R.id.fb_sign_in_via_facbook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                faceBookInit(1, alertDialog);
            }
        });
    }

    // Helper method to init Facebook and Auth in Facebook then call FireBase Auth to check and handel
    // cameFrom = 0 --> Button Facebook Login via FireBase
    // cameFrom = 1 --> Button Facebook Login Direct
    private void faceBookInit(final int cameFrom, final AlertDialog alertDialog) {
        LoginManager.getInstance().logInWithReadPermissions(SignInFragment.this, Arrays.asList("email", "public_profile"));

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (cameFrom == 0) {
                    handleFacebookAccessToken(loginResult.getAccessToken(), alertDialog);
                } else if (cameFrom == 1) {

                    // The Graph API is the primary way to get data in and out of Facebook's social graph
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    Log.v("LoginActivity", response.toString());

                                    // Application code
                                    try {
                                        String name = object.getString("name");
                                        String email = object.getString("email");
                                        Bundle bundle = new Bundle();
                                        bundle.putString(KEY_NAME, name);
                                        bundle.putString(KEY_EMAIL, email);
                                        bundle.putInt(KEY_CAMEFROM, 0);
                                        alertDialog.dismiss();
                                        NavHostFragment.findNavController(SignInFragment.this).navigate(R.id.goToHomeAfterSignIn, bundle);

                                    } catch (JSONException e) {
                                        Log.e(TAG, "Error" + e.getLocalizedMessage());
                                    }

                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "name,email");
                    request.setParameters(parameters);
                    request.executeAsync();
                }
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "Login Canceled");
                utils.showToast(context, "Login Canceled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Error : " + error);
                utils.showToast(context, "Error : " + error);
            }
        });
    }

    // Helper Method to Login Into Facebook By Firebase
    private void handleFacebookAccessToken(AccessToken token, final AlertDialog alertDialog) {
        progressDialog.show();
        Log.e(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            mDatabase.getReference().child(FIREBASE_PARENT_NODE_NAME).child(Constants.REFERENCE_USERS).child(mAuth.getUid())
                                    .setValue(new User(mAuth.getUid(), user.getDisplayName(), user.getEmail()))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        alertDialog.dismiss();
                                        NavHostFragment.findNavController(SignInFragment.this).navigate(R.id.goToHomeAfterSignIn);
                                    } else {
                                        progressDialog.dismiss();
                                        utils.showToast(context, "Error: " + (task.getException().getLocalizedMessage()));
                                    }
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            progressDialog.dismiss();
                            utils.showToast(context, "Authentication failed. " + task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    // Show About us Twitter Login Type
    private void showTwitterLoginType() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.dialog_twitter_sign_in, null);
        dialog.setView(rootView);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.material_color_white)));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.AboutUsDialogTheme;
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        // Facebook FireBase Login Button to Login into Facebook then gives Credentials to Firebase
        rootView.findViewById(R.id.twitter_sign_in_via_firebase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twitterAuthInit(0, alertDialog);
            }
        });

        // Facebook Direct Login Button
        rootView.findViewById(R.id.twitter_sign_in_direct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twitterAuthInit(1, alertDialog);
            }
        });
    }

    // Helper method to init Twitter and Auth in Twitter then call FireBase Auth to check and handel
    // cameFrom = 0 --> Button Twitter Login via FireBase
    // cameFrom = 1 --> Button Twitter Login Direct
    private void twitterAuthInit(final int cameFrom, final AlertDialog alertDialog) {
        //make the call to login
        twitterAuthClient.authorize(getActivity(), new Callback<TwitterSession>() {
            @Override
            public void success(final Result<TwitterSession> result) {
                Log.e(TAG, "twitterLogin:success");
                if (cameFrom == 0) {
                    handleTwitterSession(result.data, alertDialog);
                } else if (cameFrom == 1) {
                    twitterAuthClient.requestEmail(result.data, new Callback<String>() {
                        @Override
                        public void success(Result<String> resultEmail) {
                            // Do something with the result, which provides the email address
                            String name = result.data.getUserName();
                            String email = resultEmail.data;
                            Log.e(TAG, "For Email result data email: " + email);
                            alertDialog.dismiss();
                            Bundle bundle = new Bundle();
                            bundle.putString(KEY_NAME, name);
                            bundle.putString(KEY_EMAIL, email);
                            bundle.putInt(KEY_CAMEFROM, 1);
                            NavHostFragment.findNavController(SignInFragment.this).navigate(R.id.goToHomeAfterSignIn, bundle);
                        }

                        @Override
                        public void failure(TwitterException exception) {
                            // Do something on failure
                            Log.e(TAG, "Error while getting Twitter Email " + exception.getLocalizedMessage());
                        }
                    });
                }
            }

            @Override
            public void failure(TwitterException e) {
                //feedback
                Log.e(TAG, "Login Cancel Failure" + e.getMessage());
                utils.showToast(context, "Login Cancel Failure" + e.getMessage());
            }
        });
    }

    // Helper Method to Login Into Twitter By Firebase
    private void handleTwitterSession(TwitterSession session, final AlertDialog alertDialog) {
        progressDialog.show();
        Log.e(TAG, "handleTwitterSession:" + session);
        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        Log.e(TAG, "session.getAuthToken().token " + session.getAuthToken().token);
        Log.e(TAG, "session.getAuthToken().secret " + session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mDatabase.getReference().child(FIREBASE_PARENT_NODE_NAME).child(Constants.REFERENCE_USERS).child(mAuth.getUid()).setValue(new User(mAuth.getUid(), user.getDisplayName(), user.getEmail())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        alertDialog.dismiss();
                                        progressDialog.dismiss();
                                        NavHostFragment.findNavController(SignInFragment.this).navigate(R.id.goToHomeAfterSignIn);
                                    } else {
                                        progressDialog.dismiss();
                                        utils.showToast(context, "Error: " + (task.getException().getLocalizedMessage()));
                                    }
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            utils.showToast(context, "Authentication failed.");

                        }
                    }
                });
    }
}

