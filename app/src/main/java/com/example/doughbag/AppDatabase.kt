package com.example.doughbag

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// This tells Room which entities (tables) to include and what version the database is.
@Database(entities = [User::class, Owned_Skins::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun skinDao(): SkinDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        // This ensures we only ever open one connection to the database at a time
        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "doughbag_database"
                )
                    .fallbackToDestructiveMigration() // updates database if needed
                    .build().also { Instance = it }
            }
        }
    }
}