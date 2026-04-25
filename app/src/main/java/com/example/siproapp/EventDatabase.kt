package com.example.siproapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.siproapp.Anggota.AnggotaDao
import com.example.siproapp.Anggota.AnggotaModel
import com.example.siproapp.User.UserDao
import com.example.siproapp.User.UserModel
import com.example.siproapp.berkas.BerkasDao
import com.example.siproapp.berkas.BerkasModel

@Database(
    entities = [
        EventModel::class,
        UserModel::class,
        AnggotaModel::class,
        BerkasModel::class
    ],
    version = 5,
    exportSchema = false
)
abstract class EventDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao
    abstract fun userDao(): UserDao
    abstract fun anggotaDao(): AnggotaDao
    abstract fun berkasDao(): BerkasDao

    companion object {

        @Volatile
        private var INSTANCE: EventDatabase? = null

        fun getDB(context: Context): EventDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java,
                    "sipro_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}