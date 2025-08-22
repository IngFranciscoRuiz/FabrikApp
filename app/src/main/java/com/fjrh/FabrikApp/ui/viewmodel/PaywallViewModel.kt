package com.fjrh.FabrikApp.ui.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.FabrikApp.data.billing.BillingService
import com.fjrh.FabrikApp.domain.usecase.SubscriptionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val billingService: BillingService,
    private val subscriptionManager: SubscriptionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaywallUiState>(PaywallUiState.Idle)
    val uiState: StateFlow<PaywallUiState> = _uiState.asStateFlow()

    sealed class PaywallUiState {
        object Idle : PaywallUiState()
        object Loading : PaywallUiState()
        object Success : PaywallUiState()
        data class Error(val message: String) : PaywallUiState()
    }

    fun purchaseSubscription(activity: Activity, isMonthly: Boolean) {
        viewModelScope.launch {
            _uiState.value = PaywallUiState.Loading
            
            try {
                billingService.purchaseSubscription(activity, isMonthly)
                _uiState.value = PaywallUiState.Success
            } catch (e: Exception) {
                _uiState.value = PaywallUiState.Error("Error al procesar suscripción: ${e.message}")
            }
        }
    }

    fun restoreSubscription() {
        viewModelScope.launch {
            _uiState.value = PaywallUiState.Loading
            
            try {
                // Verificar suscripción real en Google Play
                billingService.refreshSubscriptionStatus()
                
                // Esperar un momento para que se actualice el estado
                kotlinx.coroutines.delay(1000)
                
                // Verificar si realmente es premium
                val isPremiumActive = billingService.isPremiumActive.value
                
                if (isPremiumActive) {
                    // Si es premium real, activar en SubscriptionManager
                    subscriptionManager.activatePremium()
                    _uiState.value = PaywallUiState.Success
                } else {
                    _uiState.value = PaywallUiState.Error("No se encontró una suscripción activa")
                }
            } catch (e: Exception) {
                _uiState.value = PaywallUiState.Error("Error al restaurar suscripción: ${e.message}")
            }
        }
    }





    fun clearState() {
        _uiState.value = PaywallUiState.Idle
    }
    
    fun initializeBilling() {
        billingService.initializeBilling()
    }
}
