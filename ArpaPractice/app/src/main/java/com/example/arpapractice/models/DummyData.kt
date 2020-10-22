package com.example.arpapractice.models

import androidx.room.Entity

@Entity(tableName = "myDatabase")
data class DummyData(var id:Int, var name:String)