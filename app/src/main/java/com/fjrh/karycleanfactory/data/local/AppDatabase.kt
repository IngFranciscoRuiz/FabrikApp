package com.fjrh.karycleanfactory.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fjrh.karycleanfactory.data.local.dao.FormulaDao
import com.fjrh.karycleanfactory.data.local.entity.FormulaEntity


@Database(entities = [FormulaEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun formulaDao(): FormulaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "formulas_db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}
