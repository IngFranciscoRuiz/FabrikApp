package com.fjrh.FabrikApp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.fjrh.FabrikApp.domain.model.ConfiguracionStock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "configuracion_stock")

class ConfiguracionDataStore @Inject constructor(
    private val context: Context
) {
    
                companion object {
                private val STOCK_ALTO_PRODUCTOS = floatPreferencesKey("stock_alto_productos")
                private val STOCK_MEDIO_PRODUCTOS = floatPreferencesKey("stock_medio_productos")
                private val STOCK_BAJO_PRODUCTOS = floatPreferencesKey("stock_bajo_productos")

                private val STOCK_ALTO_INSUMOS = floatPreferencesKey("stock_alto_insumos")
                private val STOCK_MEDIO_INSUMOS = floatPreferencesKey("stock_medio_insumos")
                private val STOCK_BAJO_INSUMOS = floatPreferencesKey("stock_bajo_insumos")
            }

                val configuracion: Flow<ConfiguracionStock> = context.dataStore.data.map { preferences ->
                ConfiguracionStock(
                    stockAltoProductos = preferences[STOCK_ALTO_PRODUCTOS] ?: 100f,
                    stockMedioProductos = preferences[STOCK_MEDIO_PRODUCTOS] ?: 50f,
                    stockBajoProductos = preferences[STOCK_BAJO_PRODUCTOS] ?: 25f,
                    stockAltoInsumos = preferences[STOCK_ALTO_INSUMOS] ?: 200f,
                    stockMedioInsumos = preferences[STOCK_MEDIO_INSUMOS] ?: 100f,
                    stockBajoInsumos = preferences[STOCK_BAJO_INSUMOS] ?: 50f
                )
            }

                suspend fun guardarConfiguracion(config: ConfiguracionStock) {
                context.dataStore.edit { preferences ->
                    preferences[STOCK_ALTO_PRODUCTOS] = config.stockAltoProductos
                    preferences[STOCK_MEDIO_PRODUCTOS] = config.stockMedioProductos
                    preferences[STOCK_BAJO_PRODUCTOS] = config.stockBajoProductos
                    preferences[STOCK_ALTO_INSUMOS] = config.stockAltoInsumos
                    preferences[STOCK_MEDIO_INSUMOS] = config.stockMedioInsumos
                    preferences[STOCK_BAJO_INSUMOS] = config.stockBajoInsumos
                }
            }
} 