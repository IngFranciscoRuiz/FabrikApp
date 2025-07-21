package com.fjrh.karycleanfactory.data.local

import android.content.Context
import androidx.room.Room

object DatabaseModule {

    private var INSTANCE: AppDatabase? = null

    fun provideDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "kary_clean_db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
