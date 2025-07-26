package com.fjrh.karycleanfactory.domain.model

data class Ingrediente(
    val nombre: String,
    val unidad: String,
    val cantidad: String,
    val costoPorUnidad: Double = 0.0
) 