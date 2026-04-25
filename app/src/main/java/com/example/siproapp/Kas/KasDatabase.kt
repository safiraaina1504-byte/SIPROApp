package com.example.siproapp.kas

import android.content.Context
import androidx.room.*

@Database(
    entities = [KasModel::class],
    version = 1,
    exportSchema = false
)
abstract class KasDatabase : RoomDatabase() {

    abstract fun kasDao(): KasDao

    companion object {
        @Volatile
        private var INSTANCE: KasDatabase? = null

        fun getDB(context: Context): KasDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KasDatabase::class.java,
                    "kas_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}