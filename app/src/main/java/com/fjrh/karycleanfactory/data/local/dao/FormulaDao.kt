package com.fjrh.karycleanfactory.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fjrh.karycleanfactory.data.local.entity.FormulaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FormulaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(formula: FormulaEntity)

    @Query("SELECT * FROM formulas ORDER BY id DESC")
    fun getAll(): Flow<List<FormulaEntity>>
}
