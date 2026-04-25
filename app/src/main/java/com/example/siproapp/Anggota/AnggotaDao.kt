package com.example.siproapp.Anggota

import androidx.room.*

@Dao
interface AnggotaDao {

    @Insert
    suspend fun insert(data: AnggotaModel)

    @Query("SELECT * FROM anggota ORDER BY id DESC")
    suspend fun getAll(): List<AnggotaModel>

    @Delete
    suspend fun delete(data: AnggotaModel)
}