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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val billingService: BillingService,
    private val subscriptionManager: SubscriptionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaywallUiState>(PaywallUiState.Idle)
    val uiState: StateFlow<PaywallUiState> = _uiState.asStateFlow()
    
    // Estado para tracking de inicialización
    private val _isBillingReady = MutableStateFlow(false)
    val isBillingReady: StateFlow<Boolean> = _isBillingReady.asStateFlow()

    sealed class PaywallUiState {
        object Idle : PaywallUiState()
        object Loading : PaywallUiState()
        object Success : PaywallUiState()
        data class Error(val message: String) : PaywallUiState()
    }

    fun purchaseSubscription(activity: Activity, isMonthly: Boolean) {
        viewModelScope.launch {
            // Verificar que billing esté listo
            if (!_isBillingReady.value) {
                _uiState.value = PaywallUiState.Error("Billing no está listo. Intenta de nuevo.")
                return@launch
            }
            
            // Verificar que el offer token esté disponible
            val offerToken = if (isMonthly) {
                billingService.monthlyOfferToken.value
            } else {
                billingService.yearlyOfferToken.value
            }
            
            if (offerToken == null) {
                _uiState.value = PaywallUiState.Error("Oferta no disponible. Intenta de nuevo.")
                return@launch
            }
            
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
        viewModelScope.launch {
            _uiState.value = PaywallUiState.Loading
            
            try {
                billingService.initializeBilling()
                
                // Observar cuando billing esté conectado y los tokens estén cargados
                combine(
                    billingService.isConnected,
                    billingService.monthlyOfferToken,
                    billingService.yearlyOfferToken
                ) { isConnected, monthlyToken, yearlyToken ->
                    Triple(isConnected, monthlyToken, yearlyToken)
                }.collect { (isConnected, monthlyToken, yearlyToken) ->
                    if (isConnected && monthlyToken != null && yearlyToken != null) {
                        _isBillingReady.value = true
                        _uiState.value = PaywallUiState.Idle
                        println("PaywallViewModel: Billing ready! Monthly: $monthlyToken, Yearly: $yearlyToken")
                    } else if (isConnected && (monthlyToken == null || yearlyToken == null)) {
                        _isBillingReady.value = false
                        _uiState.value = PaywallUiState.Error("Error: No se pudieron cargar las ofertas")
                        println("PaywallViewModel: Connected but tokens missing. Monthly: $monthlyToken, Yearly: $yearlyToken")
                    } else {
                        _isBillingReady.value = false
                        println("PaywallViewModel: Not connected yet. Connected: $isConnected")
                    }
                }
            } catch (e: Exception) {
                _isBillingReady.value = false
                _uiState.value = PaywallUiState.Error("Error al inicializar billing: ${e.message}")
                println("PaywallViewModel: Error initializing billing: ${e.message}")
            }
        }
    }
}
