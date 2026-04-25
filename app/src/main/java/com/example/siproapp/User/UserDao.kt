package com.example.siproapp.User

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Insert
    suspend fun insert(user: UserModel)

    @Query("SELECT * FROM user WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserModel?

    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getUser(): UserModel?
}