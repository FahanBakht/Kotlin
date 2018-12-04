package com.example.muhammadfarhan.demoauthfirebasejava;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


// Parent Class which Hold all Fragments
public class MainActivity extends AppCompatActivity {

    private twitterInterFace twitterInterFace;

    public void setTwitterInterFace(twitterInterFace twitterInterFace) {
        this.twitterInterFace = twitterInterFace;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (twitterInterFace != null)
            twitterInterFace.myOnActivityResult(requestCode, resultCode, data);
    }

    public interface twitterInterFace {
        void myOnActivityResult(int requestCode, int resultCode, @Nullable Intent data);
    }
}
