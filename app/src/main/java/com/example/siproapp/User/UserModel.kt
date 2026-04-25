package com.example.siproapp.User

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nama: String,
    val email: String,
    val password: String,
    val role: String
)