package com.farhan.moviepocket.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

data class Movie(
    val data: ArrayList<Data>
)

@Entity(tableName = "movie")
data class Data(
    val genre: String,
    @PrimaryKey(autoGenerate = true)
    val roomId :Int,
    val id: Int,
    val poster: String,
    val title: String,
    val year: String,
    var imgBase64: String
)