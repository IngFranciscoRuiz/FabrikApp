package com.fjrh.laxcleanfactory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.laxcleanfactory.data.local.repository.FormulaRepository
import com.fjrh.laxcleanfactory.data.local.ConfiguracionDataStore
import com.fjrh.laxcleanfactory.domain.model.ConfiguracionStock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class StockProductosViewModel @Inject constructor(
    private val repository: FormulaRepository,
    private val configuracionDataStore: ConfiguracionDataStore
) : ViewModel() {

    val stockProductos = repository.getStockProductos()

    val configuracion: StateFlow<ConfiguracionStock> = configuracionDataStore.configuracion.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ConfiguracionStock()
    )
} 