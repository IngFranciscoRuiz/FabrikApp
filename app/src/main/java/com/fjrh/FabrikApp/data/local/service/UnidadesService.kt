package com.fjrh.FabrikApp.data.local.service

import com.fjrh.FabrikApp.data.local.entity.UnidadMedidaEntity
import com.fjrh.FabrikApp.data.local.repository.FormulaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnidadesService @Inject constructor(
    private val repository: FormulaRepository
) {
    
    fun precargarUnidadesBasicas() {
        CoroutineScope(Dispatchers.IO).launch {
            val unidadesExistentes = repository.getUnidadesMedidaSync()
            if (unidadesExistentes.isEmpty()) {
                val unidadesBasicas = listOf(
                    UnidadMedidaEntity(nombre = "L", descripcion = "Litros"),
                    UnidadMedidaEntity(nombre = "ml", descripcion = "Mililitros"),
                    UnidadMedidaEntity(nombre = "gr", descripcion = "Gramos"),
                    UnidadMedidaEntity(nombre = "Kg", descripcion = "Kilogramos"),
                    UnidadMedidaEntity(nombre = "Pzas", descripcion = "Piezas")
                )
                unidadesBasicas.forEach { unidad ->
                    repository.insertarUnidadMedida(unidad)
                }
            }
        }
    }
} 
