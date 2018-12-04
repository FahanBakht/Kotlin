package com.example.muhammadfarhan.demoauthfirebasekotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle


class MainActivity : AppCompatActivity() {

    private var mTwitterInterFace: TwitterInterFace? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (mTwitterInterFace != null)
            mTwitterInterFace?.myOnActivityResult(requestCode, resultCode, data)
    }

    fun setTwitterInterFace(twitterInterFace: TwitterInterFace) {
        this.mTwitterInterFace = twitterInterFace
    }

    interface TwitterInterFace {
        fun myOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    }
}
