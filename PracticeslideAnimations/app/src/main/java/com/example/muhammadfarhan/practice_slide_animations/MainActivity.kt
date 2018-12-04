package com.example.muhammadfarhan.practice_slide_animations

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.view.animation.AnimationUtils
import android.view.animation.Animation
import android.widget.ArrayAdapter
import android.content.ClipData.Item
import android.content.Intent
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dummyArrayList = arrayListOf<String>()
        dummyArrayList.add("dataHolder 1")
        dummyArrayList.add("dataHolder 2")
        dummyArrayList.add("dataHolder 3")
        dummyArrayList.add("dataHolder 4")
        dummyArrayList.add("dataHolder 5")
        dummyArrayList.add("dataHolder 6")

        val mAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dummyArrayList)
        // list_view.adapter = mAdapter

        val slideDown = AnimationUtils.loadAnimation(applicationContext,
                R.anim.slide_down)

        val slideUp = AnimationUtils.loadAnimation(applicationContext,
                R.anim.slide_up)


        val items = arrayListOf<dataHolder>()
        for (i in 0..41) {
            items.add(dataHolder("dataHolder $i"))
        }

        pickerView.setItems(items) { item ->
            Toast.makeText(this@MainActivity, item.itemValue!!, Toast.LENGTH_SHORT).show()
        }
        pickerView.selectedItemPosition = 4


        btn_show.setOnClickListener {
            if (linear_layout_to_show.visibility == View.GONE) {
                linear_layout_to_show.visibility = View.VISIBLE
                linear_layout_to_show.startAnimation(slideUp)

            } else {
                linear_layout_to_show.startAnimation(slideDown)
                linear_layout_to_show.visibility = View.GONE
            }
        }


        btn_open_next_activity.setOnClickListener {
            startActivity(Intent(this@MainActivity , Main2Activity::class.java))
        }

    }
}
