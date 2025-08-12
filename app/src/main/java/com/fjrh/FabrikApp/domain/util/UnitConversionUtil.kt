package com.fjrh.FabrikApp.domain.util

import com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity

object UnitConversionUtil {
    fun calcularCostoPorUnidad(
        ingrediente: IngredienteInventarioEntity,
        unidadSeleccionada: String
    ): Double {
        val costoOriginal = ingrediente.costoPorUnidad
        val unidadOriginal = ingrediente.unidad

        return when {
            unidadOriginal == unidadSeleccionada -> costoOriginal
            unidadOriginal == "Kg" && unidadSeleccionada == "gr" -> costoOriginal / 1000.0
            unidadOriginal == "gr" && unidadSeleccionada == "Kg" -> costoOriginal * 1000.0
            unidadOriginal == "L" && unidadSeleccionada == "ml" -> costoOriginal / 1000.0
            unidadOriginal == "ml" && unidadSeleccionada == "L" -> costoOriginal * 1000.0
            unidadSeleccionada == "Pzas" -> costoOriginal
            else -> costoOriginal
        }
    }
}
