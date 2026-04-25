package com.example.siproapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event")
data class EventModel(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nama: String,
    val tanggal: String,
    val jam: String,
    val kebutuhan: String,
    val status: String
)