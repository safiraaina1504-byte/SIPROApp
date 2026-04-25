package com.example.siproapp.berkas

import androidx.room.*

@Dao
interface BerkasDao {

    @Insert
    suspend fun insert(data: BerkasModel)

    @Update
    suspend fun update(data: BerkasModel)

    @Delete
    suspend fun delete(data: BerkasModel)

    @Query("SELECT * FROM berkas ORDER BY id DESC")
    suspend fun getAll(): List<BerkasModel>
}