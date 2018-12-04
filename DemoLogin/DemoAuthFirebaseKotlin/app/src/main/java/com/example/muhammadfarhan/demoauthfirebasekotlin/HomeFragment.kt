package com.example.muhammadfarhan.demoauthfirebasekotlin


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import com.example.muhammadfarhan.demoauthfirebasekotlin.Constants.FIREBASE_PARENT_NODE_NAME
import com.example.muhammadfarhan.demoauthfirebasekotlin.Constants.KEY_CAMEFROM
import com.example.muhammadfarhan.demoauthfirebasekotlin.Constants.KEY_EMAIL
import com.example.muhammadfarhan.demoauthfirebasekotlin.Constants.KEY_NAME
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.twitter.sdk.android.core.TwitterCore
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    // Global Variable
    private var userName: TextView? = null
    private var email: TextView? = null
    private var mAuth: FirebaseAuth? = null
    private var mDatabaseReference: DatabaseReference? = null
    private lateinit var contextOfThis: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Helper Method to initialize Global Variables
        initViews(view)

        // Check if we pass data from Direct Login or We came from through Firebase login
        if (arguments != null) {
            if (arguments?.getInt(KEY_CAMEFROM) != 2) {
                userName?.text = arguments?.getString(KEY_NAME)
                email?.text = arguments?.getString(KEY_EMAIL)
            } else {
                // Get User Details from FireBase Data Base
                mDatabaseReference?.child(FIREBASE_PARENT_NODE_NAME)?.child(Constants.REFERENCE_USERS)?.child(mAuth?.uid!!)!!
                    .addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot != null) {
                                val userObj = dataSnapshot.getValue(User::class.java)
                                if (userObj != null) {
                                    userName?.text = userObj.name
                                    email?.text = userObj.email
                                }
                            } else {
                                Utils.showToast(contextOfThis, getString(R.string.error_in_geting_user_name))
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Utils.showToast(contextOfThis, "Error " + databaseError.message)
                        }
                    })
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // FAB Sign out Button
        fab_btn_sign_out.setOnClickListener {
            if (arguments != null) {
                if (arguments!!.getInt(KEY_CAMEFROM) == 0) {
                    facebookLogOut()
                    Utils.showToast(contextOfThis, "FaceBook Sign Out..!")
                } else if (arguments!!.getInt(KEY_CAMEFROM) == 1) {
                    TwitterCore.getInstance().sessionManager.clearActiveSession()
                    Utils.showToast(contextOfThis, "Twitter Sign Out..!")
                } else {
                    mAuth?.signOut()
                    Utils.showToast(contextOfThis, "From Firebase Sign Out..!")
                }
            }
            Navigation.findNavController(view).navigate(R.id.actionSignOut)
        }
    }

    // Helper Method to initialize Global Variables
    private fun initViews(view: View) {
        contextOfThis = context!!
        mAuth = FirebaseAuth.getInstance()
        val mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase.reference
        userName = view.findViewById(R.id.tv_home_userName)
        email = view.findViewById(R.id.tv_home_email)
    }

    // Facebook LogOut
    private fun facebookLogOut() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return  // already logged out
        }
        LoginManager.getInstance().logOut()
        Utils.showToast(contextOfThis, "FaceBook Sign Out..!")

    }

}
