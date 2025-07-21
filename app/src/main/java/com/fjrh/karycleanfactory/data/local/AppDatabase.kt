package com.fjrh.karycleanfactory.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fjrh.karycleanfactory.data.local.dao.FormulaDao
import com.fjrh.karycleanfactory.data.local.entity.FormulaEntity
import com.fjrh.karycleanfactory.data.local.entity.IngredienteEntity

@Database(
    entities = [FormulaEntity::class, IngredienteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun formulaDao(): FormulaDao
}
