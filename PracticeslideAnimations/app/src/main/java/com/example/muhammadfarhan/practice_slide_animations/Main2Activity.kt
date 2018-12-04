package com.example.muhammadfarhan.practice_slide_animations

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_main2.*
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.support.v4.view.ViewCompat.animate
import android.R.attr.translationY


class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val slideDown = AnimationUtils.loadAnimation(applicationContext,
                R.anim.slide_down)

        val slideUp = AnimationUtils.loadAnimation(applicationContext,
                R.anim.slide_up)

        var i = 0


        to_show.setOnClickListener {
            /*if (card_to_show.visibility == View.GONE) {
                card_to_show.visibility = View.VISIBLE
                card_to_show.startAnimation(slideDown)

            } else {
                card_to_show.startAnimation(slideUp)
                card_to_show.visibility = View.GONE
            }*/

            if (i == 0) {
                i = 1
                card_to_show.animate()
                        .translationY(500f)
                        .alpha(50f)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                //card_to_show.setVisibility(View.VISIBLE)

                            }
                        })

            } else {
                i = 0
                card_to_show.animate()
                        .translationX(500f)
                        .alpha(50f)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                //card_to_show.setVisibility(View.VISIBLE)
                            }
                        })

            }


        }
    }
}
