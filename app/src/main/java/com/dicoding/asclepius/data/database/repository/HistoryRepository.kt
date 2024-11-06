package com.dicoding.asclepius.data.database.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.dicoding.asclepius.data.database.room.HistoryDao
import com.dicoding.asclepius.data.database.room.HistoryDatabase
import com.dicoding.asclepius.data.database.room.HistoryEntity
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HistoryRepository(application: Application) {

    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private val historyDao: HistoryDao

    init {
        val db = HistoryDatabase.getDatabase(application)
        historyDao = db.historyDao()
    }

    fun insert(news: HistoryEntity){
        executorService.execute { historyDao.insert(news) }
    }

    fun delete(news: HistoryEntity){
        executorService.execute { historyDao.delete(news) }
    }

    fun getAllNews(): LiveData<List<HistoryEntity>> = historyDao.getAllNews()
}