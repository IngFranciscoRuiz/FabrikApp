package com.fjrh.karycleanfactory.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fjrh.karycleanfactory.data.local.dao.FormulaDao
import com.fjrh.karycleanfactory.data.local.entity.FormulaEntity
import com.fjrh.karycleanfactory.data.local.entity.IngredienteEntity
import com.fjrh.karycleanfactory.data.local.entity.HistorialProduccionEntity
import com.fjrh.karycleanfactory.data.local.entity.IngredienteInventarioEntity


@Database(
    entities = [
        FormulaEntity::class,
        IngredienteEntity::class,
        HistorialProduccionEntity::class,
        IngredienteInventarioEntity::class,
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun formulaDao(): FormulaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "karyclean_database"
                )
                    .fallbackToDestructiveMigration() // ⚠️ TODO: Quitar antes de publicar la app
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
