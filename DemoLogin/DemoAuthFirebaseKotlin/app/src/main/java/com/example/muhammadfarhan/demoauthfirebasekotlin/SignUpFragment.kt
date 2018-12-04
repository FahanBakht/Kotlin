package com.example.muhammadfarhan.demoauthfirebasekotlin


import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.example.muhammadfarhan.demoauthfirebasekotlin.Constants.FIREBASE_PARENT_NODE_NAME
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : Fragment() {

    // Global Variable
    private var mAuth: FirebaseAuth? = null
    private var mDb: FirebaseDatabase? = null
    private lateinit var contextOfThis: Context
    private lateinit var progressDialog: ProgressDialog
    private var email: EditText? = null
    private var password: EditText? = null
    private var confirmPassword: EditText? = null
    private var userName: EditText? = null
    private var uid: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        initViews(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Btn Go back to Sign In Fragment
        goToSignInFrag.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.goToSignIn)
        }

        // Btn Sign Up
        btn_sing_up.setOnClickListener {
            signUp()
        }
    }


    // Helper Method which first check inputs validations and then Sign Up
    private fun signUp() {

        if (TextUtils.isEmpty(userName?.text.toString())) {
            userName?.error = getString(R.string.empty_edittext)
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

        if (TextUtils.isEmpty(confirmPassword?.text.toString())) {
            confirmPassword?.error = getString(R.string.empty_edittext)
            return
        }

        if (password?.text.toString().length < 6) {
            password?.error = getString(R.string.password_less_digit_error)
            return
        }

        if (confirmPassword?.text.toString().length < 6) {
            confirmPassword?.error = getString(R.string.password_less_digit_error)
            return
        }

        if (!Utils.isValidEmail(email?.text.toString())) {
            email?.error = getString(R.string.error_email_format)
            return
        }

        if (password?.text.toString() != confirmPassword?.text.toString()) {
            confirmPassword?.error = getString(R.string.password_not_matched)
            return
        }

        progressDialog.show()
        // Logic's to Sign up from Fire Base Auth
        mAuth?.createUserWithEmailAndPassword(email?.text.toString(), password?.text.toString())!!
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uid = mAuth?.currentUser!!.uid
                    mDb?.reference?.child(FIREBASE_PARENT_NODE_NAME)?.child(Constants.REFERENCE_USERS)?.child(uid!!)!!
                        .setValue(User(uid, userName?.text.toString(), email?.text.toString()))
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                progressDialog.dismiss()
                                NavHostFragment.findNavController(this@SignUpFragment)
                                    .navigate(R.id.goToHomeAfterSignUp)
                            } else {
                                Utils.showToast(contextOfThis, "Error: " + task.exception!!.localizedMessage)
                                progressDialog.dismiss()
                            }
                        }
                } else {
                    Utils.showToast(contextOfThis, getString(R.string.error_register_user) + task.exception!!.message)
                    progressDialog.dismiss()
                }
            }
    }

    // Helper Method to initialize Global Variables
    private fun initViews(view: View) {
        contextOfThis = context!!
        mAuth = FirebaseAuth.getInstance()
        mDb = FirebaseDatabase.getInstance()
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage(getString(R.string.please_wait))
        userName = view.findViewById(R.id.et_sign_up_name)
        email = view.findViewById(R.id.et_sign_up_email)
        password = view.findViewById(R.id.et_sign_up_password)
        confirmPassword = view.findViewById(R.id.et_sign_up_confirm_password)
    }

}
