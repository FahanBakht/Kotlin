package com.farhan.moviepocket.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

data class Movie(
    val data: ArrayList<Data>
)

@Entity(tableName = "movie")
data class Data(
    val genre: String,
    @PrimaryKey
    val id: Int,
    val poster: String,
    val title: String,
    val year: String,
    val imgBase64: String
)