package com.fjrh.laxcleanfactory.ui.utils

// Funciones de validación compartidas
fun validarLitros(texto: String): Boolean {
    val regex = Regex("^\\d{0,6}(\\.\\d{0,3})?$")
    return regex.matches(texto)
}

fun validarPrecio(texto: String): Boolean {
    val regex = Regex("^\\d{0,6}(\\.\\d{0,2})?$")
    return regex.matches(texto)
}

fun formatearPrecio(texto: String): String {
    return if (texto.isNotBlank()) "$${texto}" else texto
} 