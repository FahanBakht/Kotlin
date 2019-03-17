package com.example.farhan.practicekotlinrecyclearview

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var customRCView: CustomAdapter
    lateinit var data: ArrayList<String>
    lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reservation_form_b)

        recyclerView = findViewById(R.id.form_b_RV)
        data = arrayListOf()

        customRCView = CustomAdapter(data)

        data.add("test")
        data.add("test")
        data.add("test")

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = customRCView


        /*btn_Show.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.reservation_dialog_view, null)

            builder.setView(view)
            builder.create().show()

        }*/

    }
}
