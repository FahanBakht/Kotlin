package com.example.muhammadfarhan.demoauthfirebasekotlin


import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.example.muhammadfarhan.demoauthfirebasekotlin.Constants.FIREBASE_PARENT_NODE_NAME
import com.example.muhammadfarhan.demoauthfirebasekotlin.Constants.KEY_CAMEFROM
import com.example.muhammadfarhan.demoauthfirebasekotlin.Constants.KEY_EMAIL
import com.example.muhammadfarhan.demoauthfirebasekotlin.Constants.KEY_NAME
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.TwitterAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import kotlinx.android.synthetic.main.fragment_sign_in.*
import org.json.JSONException
import java.util.*

private const val TAG = "SignInFragment"
private const val Google_RC_SIGN_IN = 2
private const val FACEBOOK_CONS = 64206

class SignInFragment : Fragment() {

    // Global Variable
    private var email: EditText? = null
    private var password: EditText? = null
    private lateinit var contextOfThis: Context
    private lateinit var progressDialog: ProgressDialog
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mCallbackManager: CallbackManager? = null
    private var twitterAuthClient: TwitterAuthClient? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)

        // Helper Method to initialize Global Variables
        initViews(view)

        // Getting the Twitter result back to Fragment from its Parent Activity
        (activity as MainActivity).setTwitterInterFace(object : MainActivity.TwitterInterFace {
            override fun myOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                if (twitterAuthClient != null)
                    twitterAuthClient?.onActivityResult(requestCode, resultCode, data)
            }
        })


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if a Person is already Login or not
        progressDialog.show()
        if (mAuth?.currentUser != null) {
            progressDialog.dismiss()
            Navigation.findNavController(view).navigate(R.id.goToHomeAfterSignIn)
        } else {
            progressDialog.dismiss()
        }

        // TextView for go to Sign Up Fragment
        goToSignUpFrag.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.goToSignUp)
        }

        // Btn Facebook show dialog
        btn_fb.setOnClickListener {
            showFacebookLoginType()
        }

        // Btn Google
        btn_google.setOnClickListener {
            // Configure Google Sign In
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            // Build a GoogleSignInClient with the options specified by gso.
            mGoogleSignInClient = GoogleSignIn.getClient(contextOfThis, gso)
            googleSignIn()
        }

        // Btn Twitter
        btn_twitter.setOnClickListener {
            // init Twitter

            val config = TwitterConfig.Builder(activity!!)
                .logger(DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(
                    TwitterAuthConfig(
                        getString(R.string.twitter_key),
                        getString(R.string.twitter_secret)
                    )
                )
                .debug(true)
                .build()
            Twitter.initialize(config)

            Twitter.initialize(context)
            // Client for requesting authorization and email from the user.
            twitterAuthClient = TwitterAuthClient()


            showTwitterLoginType()
        }

        // Btn Sign In
        btn_sing_in.setOnClickListener {
            signInViaFireBaseAuth()
        }
    }

    // Helper Method which first check inputs validations then Sign In Via Fire base Auth
    private fun signInViaFireBaseAuth() {

        if (!Utils.isNetworkAvailable(contextOfThis)) {
            Utils.showToast(contextOfThis, getString(R.string.no_internet_connection_error))
            return
        }

        if (TextUtils.isEmpty(email?.text.toString())) {
            email?.error = getString(R.string.empty_edittext)
            return
        }

        if (TextUtils.isEmpty(password?.text.toString())) {
            password?.error = getString(R.string.empty_edittext)
            return
        }

        if (!Utils.isValidEmail(email?.text.toString())) {
            email?.error = getString(R.string.error_email_format)
            return
        }

        if (password?.text.toString().length < 6) {
            password?.error = getString(R.string.password_less_digit_error)
            return
        }

        progressDialog.show()

        // FireBase Sign check from Auth
        mAuth?.signInWithEmailAndPassword(email?.text.toString(), password?.text.toString())!!
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Utils.showToast(contextOfThis, getString(R.string.sign_in_successfully))
                    progressDialog.dismiss()
                    NavHostFragment.findNavController(this@SignInFragment).navigate(R.id.goToHomeAfterSignIn)

                } else {
                    progressDialog.dismiss()
                    Utils.showToast(
                        contextOfThis, getString(R.string.error_while_sign_in) + task.exception!!.localizedMessage
                    )
                }
            }
    }

    // Helper Method Which Calls Google Sign In Intent
    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, Google_RC_SIGN_IN)
    }

    // Helper Method to FireBase Login after Google Login
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.e(TAG, "firebaseAuthWithGoogle")
        progressDialog.show()
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth?.signInWithCredential(credential)!!
            .addOnCompleteListener(
                activity!!
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth?.currentUser
                    mDatabase?.reference?.child(FIREBASE_PARENT_NODE_NAME)?.child(Constants.REFERENCE_USERS)!!
                        .child(mAuth?.uid!!).setValue(User(mAuth?.uid, user!!.displayName, user.email))
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                progressDialog.dismiss()
                                NavHostFragment.findNavController(this@SignInFragment)
                                    .navigate(R.id.goToHomeAfterSignIn)
                            } else {
                                progressDialog.dismiss()
                                Utils.showToast(contextOfThis, "Error: " + (task.exception!!.localizedMessage))
                            }
                        }

                } else {
                    // If sign in fails, display a message to the user.
                    progressDialog.dismiss()
                    Utils.showToast(contextOfThis, getString(R.string.Auth_failed))
                }
            }
    }

    // Show About us Facebook Login Type
    private fun showFacebookLoginType() {
        val dialog = AlertDialog.Builder(context)
        val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rootView = inflater.inflate(R.layout.about_us, null)
        dialog.setView(rootView)
        val alertDialog = dialog.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.material_color_white)))
        alertDialog.window!!.attributes.windowAnimations = R.style.AboutUsDialogTheme
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        // Facebook FireBase Login Button to Login into Facebook then gives Credentials to Firebase
        rootView.findViewById<View>(R.id.fb_sign_in_via_firebase).setOnClickListener {
            faceBookInit(0, alertDialog)
        }

        // Facebook Direct Button
        rootView.findViewById<View>(R.id.fb_sign_in_via_facbook).setOnClickListener {
            faceBookInit(1, alertDialog)
        }
    }

    // Helper method to init Facebook and Auth in Facebook then call FireBase Auth to check and handel
    // cameFrom = 0 --> Button Facebook Login via FireBase
    // cameFrom = 1 --> Button Facebook Login Direct
    private fun faceBookInit(cameFrom: Int, alertDialog: AlertDialog) {
        LoginManager.getInstance()
            .logInWithReadPermissions(this@SignInFragment, Arrays.asList("email", "public_profile"))

        LoginManager.getInstance().registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                if (cameFrom == 0) {
                    handleFacebookAccessToken(loginResult.accessToken, alertDialog)
                } else if (cameFrom == 1) {

                    // The Graph API is the primary way to get data in and out of Facebook's social graph
                    val request = GraphRequest.newMeRequest(loginResult.accessToken) { jsonObject, response ->
                        Log.e(TAG, response.toString())

                        // Application code
                        try {
                            val name = jsonObject.getString("name")
                            val email = jsonObject.getString("email")
                            val bundle = Bundle().apply {
                                putString(KEY_NAME, name)
                                putString(KEY_EMAIL, email)
                                putInt(KEY_CAMEFROM, 0)
                            }
                            alertDialog.dismiss()
                            NavHostFragment.findNavController(this@SignInFragment)
                                .navigate(R.id.goToHomeAfterSignIn, bundle)

                        } catch (e: JSONException) {
                            Log.e(TAG, "Error" + e.localizedMessage)
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "name,email")
                    request.parameters = parameters
                    request.executeAsync()
                }
                Log.e(TAG, "Facebook OnSuccess:" + loginResult.accessToken)
            }

            override fun onCancel() {
                Log.e(TAG, "Login Canceled")
                Utils.showToast(contextOfThis, "Login Canceled")
            }

            override fun onError(error: FacebookException) {
                Log.e(TAG, "Error : $error")
                Utils.showToast(contextOfThis, "Error : $error")
            }
        })
    }

    // Helper Method to Login Into Facebook By Firebase
    private fun handleFacebookAccessToken(token: AccessToken, alertDialog: AlertDialog) {
        progressDialog.show()
        Log.e(TAG, "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth?.signInWithCredential(credential)!!
            .addOnCompleteListener(
                activity!!
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth?.currentUser
                    mDatabase?.reference?.child(FIREBASE_PARENT_NODE_NAME)?.child(Constants.REFERENCE_USERS)!!
                        .child(mAuth?.uid!!).setValue(User(mAuth?.uid, user!!.displayName, user.email))
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                progressDialog.dismiss()
                                alertDialog.dismiss()
                                NavHostFragment.findNavController(this@SignInFragment)
                                    .navigate(R.id.goToHomeAfterSignIn)
                            } else {
                                progressDialog.dismiss()
                                Utils.showToast(contextOfThis, "Error: " + (task.exception!!.localizedMessage))
                            }
                        }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.e(TAG, "signInWithCredential:failure", task.exception)
                    progressDialog.dismiss()
                    Utils.showToast(contextOfThis, "Authentication failed. " + task.exception!!.localizedMessage)
                }
            }
    }

    // Show About us Twitter Login Type
    private fun showTwitterLoginType() {
        val dialog = AlertDialog.Builder(context)
        val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rootView = inflater.inflate(R.layout.dialog_twitter_sign_in, null)
        dialog.setView(rootView)
        val alertDialog = dialog.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.material_color_white)))
        alertDialog.window!!.attributes.windowAnimations = R.style.AboutUsDialogTheme
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        // Facebook FireBase Login Button to Login into Facebook then gives Credentials to Firebase
        rootView.findViewById<View>(R.id.twitter_sign_in_via_firebase)
            .setOnClickListener { twitterAuthInit(0, alertDialog) }

        // Facebook Direct Login Button
        rootView.findViewById<View>(R.id.twitter_sign_in_direct).setOnClickListener { twitterAuthInit(1, alertDialog) }
    }

    // Helper method to init Twitter and Auth in Twitter then call FireBase Auth to check and handel
    // cameFrom = 0 --> Button Twitter Login via FireBase
    // cameFrom = 1 --> Button Twitter Login Direct
    private fun twitterAuthInit(cameFrom: Int, alertDialog: AlertDialog) {
        //make the call to login
        twitterAuthClient?.authorize(activity!!, object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {
                Log.e(TAG, "twitterLogin:success")
                if (cameFrom == 0) {
                    handleTwitterSession(result.data, alertDialog)
                } else if (cameFrom == 1) {
                    twitterAuthClient?.requestEmail(result.data, object : Callback<String>() {
                        override fun success(resultEmail: Result<String>) {
                            // Do something with the result, which provides the email address
                            val name = result.data.userName
                            val email = resultEmail.data
                            alertDialog.dismiss()
                            val bundle = Bundle()
                            bundle.putString(KEY_NAME, name)
                            bundle.putString(KEY_EMAIL, email)
                            bundle.putInt(KEY_CAMEFROM, 1)

                            NavHostFragment.findNavController(this@SignInFragment)
                                .navigate(R.id.goToHomeAfterSignIn, bundle)
                        }

                        override fun failure(exception: TwitterException) {
                            // Do something on failure
                            Log.e(TAG, "Error while getting Twitter Email " + exception.localizedMessage)
                        }
                    })
                }
            }

            override fun failure(e: TwitterException) {
                //feedback
                Log.e(TAG, "Login Cancel Failure" + e.message)
                progressDialog.dismiss()
                Utils.showToast(contextOfThis, "Login Cancel Failure" + e.message)
            }
        })
    }

    // Helper Method to Login Into Twitter By Firebase
    private fun handleTwitterSession(session: TwitterSession, alertDialog: AlertDialog) {
        Log.e(TAG, "handleTwitterSession:$session")
        progressDialog.show()
        val credential = TwitterAuthProvider.getCredential(
            session.authToken.token,
            session.authToken.secret
        )

        mAuth?.signInWithCredential(credential)!!
            .addOnCompleteListener(
                activity!!
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.e(TAG, "signInWithCredential:success")
                    val user = mAuth?.currentUser
                    mDatabase?.reference?.child(FIREBASE_PARENT_NODE_NAME)?.child(Constants.REFERENCE_USERS)!!
                        .child(mAuth?.uid!!).setValue(User(mAuth?.uid, user!!.displayName, user.email))
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                progressDialog.dismiss()
                                alertDialog.dismiss()
                                NavHostFragment.findNavController(this@SignInFragment)
                                    .navigate(R.id.goToHomeAfterSignIn)
                            } else {
                                progressDialog.dismiss()
                                Utils.showToast(contextOfThis, "Error: " + (task.exception!!.localizedMessage))
                            }
                        }

                } else {
                    // If sign in fails, display a message to the user.
                    progressDialog.dismiss()
                    Log.e(TAG, "signInWithCredential:failure", task.exception)
                    Utils.showToast(contextOfThis, "Authentication failed.")

                }
            }
    }


    // Helper Method to initialize Global Variables
    private fun initViews(view: View) {
        contextOfThis = context!!
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage(getString(R.string.please_wait))
        email = view.findViewById(R.id.et_sign_in_email)
        password = view.findViewById(R.id.et_sign_in_password)
        mCallbackManager = CallbackManager.Factory.create()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e(TAG, "onActivityResult")

        when (requestCode) {
            Google_RC_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)
                    Log.e(TAG, "google Sign onActivityResult If ")
                    firebaseAuthWithGoogle(account!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.e(TAG, "google Sign fails e $e")
                    Log.e(TAG, "google Sign fails e ${e.message}")
                    Log.e(TAG, "google Sign fails e ${e.localizedMessage}")
                    Log.e(TAG, "google Sign fails e ${e.stackTrace}")
                    Log.e(TAG, "google Sign fails e ${e.cause}")
                }

            }
            FACEBOOK_CONS -> {
                //If not request code is RC_SIGN_IN it must be facebook
                Log.e(TAG, "Callback Facebook")
                mCallbackManager?.onActivityResult(requestCode, resultCode, data)

            }
            //TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE
            twitterAuthClient?.requestCode -> {
                // Pass the activity result to the login button.
                Log.e(TAG, "Callback twitterAuthClient")
                twitterAuthClient?.onActivityResult(requestCode, resultCode, data)
            }
        }


    }

}
