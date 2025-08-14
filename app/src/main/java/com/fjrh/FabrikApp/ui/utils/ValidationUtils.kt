package com.fjrh.FabrikApp.ui.utils

import java.text.NumberFormat
import java.util.*

// Funciones de validación compartidas
fun validarLitros(texto: String): Boolean {
    val regex = Regex("^\\d{0,6}(\\.\\d{0,3})?$")
    return regex.matches(texto)
}

fun validarPrecio(texto: String): Boolean {
    val regex = Regex("^\\d{0,6}(\\.\\d{0,2})?$")
    return regex.matches(texto)
}

fun validarCantidad(texto: String): Boolean {
    val regex = Regex("^\\d{0,6}(\\.\\d{0,3})?$")
    return regex.matches(texto)
}

fun formatearPrecio(texto: String): String {
    return if (texto.isNotBlank()) "$${texto}" else texto
}

fun formatearPrecioConComas(texto: String): String {
    return if (texto.isNotBlank()) {
        val numero = texto.toDoubleOrNull() ?: 0.0
        "$${String.format("%,.2f", numero)}"
    } else texto
}

fun formatearPrecioMoneda(valor: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    return formatter.format(valor)
}

fun formatearPrecioMoneda(valor: String): String {
    val numero = valor.toDoubleOrNull() ?: 0.0
    return formatearPrecioMoneda(numero)
}

fun formatearCantidad(valor: Double): String {
    return String.format("%.2f", valor)
}

fun formatearCantidad(valor: String): String {
    val numero = valor.toDoubleOrNull() ?: 0.0
    return formatearCantidad(numero)
}

fun formatearCantidadEntera(valor: Double): String {
    return String.format("%.0f", valor)
}

fun formatearCantidadEntera(valor: String): String {
    val numero = valor.toDoubleOrNull() ?: 0.0
    return formatearCantidadEntera(numero)
}

fun limpiarFormatoMoneda(texto: String): String {
    return texto.replace(Regex("[^\\d.]"), "")
}

fun validarSoloNumeros(texto: String): Boolean {
    return texto.matches(Regex("^\\d*\\.?\\d*$"))
}

fun obtenerValorInicial(valor: Double): String {
    return if (valor == 0.0) "" else valor.toString()
}

fun obtenerValorInicial(valor: Float): String {
    return if (valor == 0f) "" else valor.toString()
}

fun limpiarCerosInicio(texto: String): String {
    return if (texto.startsWith("0") && texto.length > 1 && !texto.startsWith("0.")) {
        texto.substring(1)
    } else texto
}

fun formatearMientrasEscribe(texto: String): String {
    return limpiarCerosInicio(texto)
} 