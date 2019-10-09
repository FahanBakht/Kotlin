package com.example.myapplication.views.main.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myapplication.views.main.model.ValueHolder

@Dao
interface MainDao {

    @Query("SELECT * FROM valueHolder")
    abstract fun loadAllValues(): LiveData<List<ValueHolder>>

    @Insert
    abstract fun insertValue(value: ValueHolder)

    @Query("SELECT * FROM valueHolder WHERE id = :id")
    abstract fun loadFavoriteById(id: Int): LiveData<ValueHolder>

    @Query("DELETE FROM valueHolder WHERE id = :id")
    abstract fun delete(id: Int)

    @Query("SELECT * FROM valueHolder")
    abstract fun getAllValues(): List<ValueHolder>
}