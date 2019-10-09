package com.example.myapplication.views.main.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "valueHolder")
data class ValueHolder(@PrimaryKey(autoGenerate = true) var id:Int, var value:Any?)