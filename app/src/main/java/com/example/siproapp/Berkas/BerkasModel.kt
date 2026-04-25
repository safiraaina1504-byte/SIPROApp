package com.example.siproapp.berkas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "berkas")
data class BerkasModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val judul: String,
    val penulis: String,
    val tanggal: String,
    val ukuran: String,
    val jenis: String,
    val status: String,
    val fileUri: String
)