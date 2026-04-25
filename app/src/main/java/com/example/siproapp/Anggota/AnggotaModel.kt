package com.example.siproapp.Anggota

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anggota")
data class AnggotaModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nama: String,
    val jabatan: String,
    val status: String
)