package com.example.siproapp.kas

import androidx.room.*

@Dao
interface KasDao {

    @Insert
    suspend fun insert(data: KasModel)

    @Update
    suspend fun update(data: KasModel)

    @Delete
    suspend fun delete(data: KasModel)

    @Query("SELECT * FROM kas ORDER BY id DESC")
    suspend fun getAll(): List<KasModel>
}