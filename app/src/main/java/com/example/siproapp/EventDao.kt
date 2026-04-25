package com.example.siproapp

import androidx.room.*

@Dao
interface EventDao {

    @Insert
    suspend fun insert(event: EventModel)

    @Update
    suspend fun update(event: EventModel)

    @Delete
    suspend fun delete(event: EventModel)

    @Query("SELECT * FROM event ORDER BY id DESC")
    suspend fun getAll(): List<EventModel>
}