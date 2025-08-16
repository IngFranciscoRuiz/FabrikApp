package com.fjrh.FabrikApp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.FabrikApp.data.local.MultiUserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MultiUserViewModel @Inject constructor(
    private val multiUserDataStore: MultiUserDataStore
) : ViewModel() {

    private val _isMultiUserActive = MutableStateFlow(false)
    val isMultiUserActive: StateFlow<Boolean> = _isMultiUserActive.asStateFlow()

    init {
        loadMultiUserState()
    }

    private fun loadMultiUserState() {
        viewModelScope.launch {
            multiUserDataStore.isMultiUserActive.collect { isActive ->
                _isMultiUserActive.value = isActive
                println("MultiUserViewModel: Estado cargado: $isActive")
            }
        }
    }

    fun setMultiUserActive(active: Boolean) {
        viewModelScope.launch {
            multiUserDataStore.setMultiUserActive(active)
            _isMultiUserActive.value = active
            println("MultiUserViewModel: Multiusuario establecido a: $active")
        }
    }

    fun clearMultiUserState() {
        viewModelScope.launch {
            multiUserDataStore.clearMultiUserState()
            _isMultiUserActive.value = false
            println("MultiUserViewModel: Estado de multiusuario limpiado")
        }
    }
}
