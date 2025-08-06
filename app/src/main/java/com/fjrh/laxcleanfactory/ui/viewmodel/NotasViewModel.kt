package com.fjrh.laxcleanfactory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.laxcleanfactory.data.local.entity.NotaEntity
import com.fjrh.laxcleanfactory.data.local.repository.FormulaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotasViewModel @Inject constructor(
    private val repository: FormulaRepository
) : ViewModel() {

    val notas: StateFlow<List<NotaEntity>> =
        repository.getNotas()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun agregarNota(nota: NotaEntity) {
        viewModelScope.launch {
            repository.insertarNota(nota)
        }
    }

    fun actualizarNota(nota: NotaEntity) {
        viewModelScope.launch {
            repository.actualizarNota(nota)
        }
    }

    fun eliminarNota(nota: NotaEntity) {
        viewModelScope.launch {
            repository.eliminarNota(nota)
        }
    }
} 
