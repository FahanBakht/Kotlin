package com.example.myapplication.views.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.views.main.viewmodel.MainViewModel
import com.example.myapplication.views.main.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val application = requireNotNull(this@MainActivity).application

        val dataSource = AppDatabase.getInstance(application).mainDao

        val viewModelFactory = MainViewModelFactory(dataSource, application)

        val mainViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(MainViewModel::class.java)

        mainViewModel.getAllValues.observe(this, Observer {
            if (it != null){

            }
        })
    }
}
