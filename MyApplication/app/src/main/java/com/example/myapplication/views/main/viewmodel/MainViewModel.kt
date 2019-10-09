package com.example.myapplication.views.main.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.views.main.dao.MainDao
import com.example.myapplication.views.main.model.ValueHolder
import kotlinx.coroutines.*

class MainViewModel(val database: MainDao, application: Application) : AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _getAllValues = MutableLiveData<List<ValueHolder?>>()

    val getAllValues: LiveData<List<ValueHolder?>>
        get() = _getAllValues

    init {
        initializeValues()
    }

    private fun initializeValues() {
        uiScope.launch {
            _getAllValues.value = getValuesFromDatabase()
        }
    }

    private suspend fun getValuesFromDatabase(): List<ValueHolder?> {
        return withContext(Dispatchers.IO) {
           database.getAllValues()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}