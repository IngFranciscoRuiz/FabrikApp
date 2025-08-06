package com.fjrh.laxcleanfactory.domain.model

data class Venta(
    val id: Long = 0,
    val nombreProducto: String,
    val litrosVendidos: Float,
    val precioPorLitro: Double,
    val fecha: Long,
    val cliente: String? = null,
    val total: Double = litrosVendidos * precioPorLitro
) 
