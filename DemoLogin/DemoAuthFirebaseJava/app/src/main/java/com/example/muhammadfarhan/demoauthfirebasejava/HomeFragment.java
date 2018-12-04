package com.example.muhammadfarhan.demoauthfirebasejava;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import androidx.navigation.Navigation;

import static com.example.muhammadfarhan.demoauthfirebasejava.Constants.FIREBASE_PARENT_NODE_NAME;
import static com.example.muhammadfarhan.demoauthfirebasejava.Constants.KEY_CAMEFROM;
import static com.example.muhammadfarhan.demoauthfirebasejava.Constants.KEY_EMAIL;
import static com.example.muhammadfarhan.demoauthfirebasejava.Constants.KEY_NAME;

public class HomeFragment extends Fragment {


    // Global Variable
    private static final String TAG = "HomeFragment";
    private TextView userName;
    private TextView email;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private Utils utils = Utils.getInstance();
    private Context context;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Helper Method to initialize Global Variables
        initViews(view);

        // Check if we pass data from Direct Login or We came from through Firebase login
        if (getArguments() != null) {
            if (getArguments().getInt(KEY_CAMEFROM) != 2) {
                Log.e(TAG, "If getArguments" + getArguments().getInt(KEY_CAMEFROM));
                userName.setText(getArguments().getString(KEY_NAME));
                email.setText(getArguments().getString(KEY_EMAIL));
            } else {
                Log.e(TAG, "Else getArguments");
                // Get User Details from FireBase Data Base
                mDatabaseReference.child(FIREBASE_PARENT_NODE_NAME).child(Constants.REFERENCE_USERS).child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            User userObj = dataSnapshot.getValue(User.class);
                            if (userObj != null) {
                                userName.setText(userObj.getName());
                                email.setText(userObj.getEmail());
                            }
                        } else {
                            utils.showToast(context, getString(R.string.error_in_geting_user_name));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        utils.showToast(context, "Error " + databaseError.getMessage());
                    }
                });
            }
        }
        return view;
    }

    // This method runs after View is created and every thing starts
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // FAB Sign out Button
        view.findViewById(R.id.fab_btn_sign_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getArguments() != null) {
                    Log.e(TAG, "We have Argument: " + getArguments().getInt(KEY_CAMEFROM));
                    if (getArguments().getInt(KEY_CAMEFROM) == 0) {
                        facebookLogOut();
                        utils.showToast(context, "FaceBook Sign Out..!");
                    } else if (getArguments().getInt(KEY_CAMEFROM) == 1) {
                        TwitterCore.getInstance().getSessionManager().clearActiveSession();
                        utils.showToast(context, "Twitter Sign Out..!");
                    } else {
                        Log.e(TAG, "Else Firebase");
                        mAuth.signOut();
                        utils.showToast(context, "From Firebase Sign Out..!");
                    }
                }
                Navigation.findNavController(view).navigate(R.id.actionSignOut);
            }
        });
    }

    // Helper Method to initialize Global Variables
    private void initViews(View view) {
        context = getContext();
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
        userName = view.findViewById(R.id.tv_home_userName);
        email = view.findViewById(R.id.tv_home_email);
    }

    // Facebook LogOut
    private void facebookLogOut() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        LoginManager.getInstance().logOut();
    }
}

