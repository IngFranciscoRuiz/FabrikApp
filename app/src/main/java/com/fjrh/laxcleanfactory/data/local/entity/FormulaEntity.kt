package com.fjrh.laxcleanfactory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "formulas")
data class FormulaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String
)
