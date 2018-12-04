package com.example.muhammadfarhan.practice_listview_selector

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView

class MainActivity : AppCompatActivity() {

    private lateinit var mListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mListView = findViewById(R.id.main_list_view)

        val dummyListData = arrayListOf<String>()
        dummyListData.add("Hello Android..!")
        dummyListData.add("Hello Android..!")
        dummyListData.add("Hello Android..!")
        dummyListData.add("Hello Android..!")
        dummyListData.add("Hello Android..!")


        val mAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dummyListData)
        mListView.adapter = mAdapter


        mListView.setOnItemClickListener { parent, view, position, id ->


        }

    }
}
