package com.example.siproapp.kas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kas")
data class KasModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val judul: String,
    val jumlah: Int,
    val tipe: String,
    val kategori: String
)