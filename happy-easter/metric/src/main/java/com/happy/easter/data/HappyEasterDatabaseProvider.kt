package com.happy.easter.data

import android.content.Context
import androidx.room.Room

private const val DATABASE_NAME = "happy_easter_database"

internal object HappyEasterDatabaseProvider {
    @Volatile
    private var database: HappyEasterDatabase? = null

    fun getDatabase(context: Context): HappyEasterDatabase = database ?: synchronized(this) {
        database ?: Room.databaseBuilder(
            context,
            HappyEasterDatabase::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
