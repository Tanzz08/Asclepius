package com.dicoding.asclepius.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.database.repository.HistoryRepository
import com.dicoding.asclepius.data.database.room.HistoryEntity
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {

    private val mHistoryRepository : HistoryRepository = HistoryRepository(application)

    fun getAllNews(): LiveData<List<HistoryEntity>> = mHistoryRepository.getAllNews()

    fun insert(news: HistoryEntity){
        viewModelScope.launch {
            mHistoryRepository.insert(news)
        }
    }

    fun delete(news: HistoryEntity){
        viewModelScope.launch {
            mHistoryRepository.delete(news)
        }
    }

}