package com.dicoding.asclepius.data.database.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(historyEntity: HistoryEntity)

    @Update
    fun update(historyEntity: HistoryEntity)

    @Delete
    fun delete(historyEntity: HistoryEntity)

    @Query("SELECT * FROM history_table")
    fun getAllNews(): LiveData<List<HistoryEntity>>
}