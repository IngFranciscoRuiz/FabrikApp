package com.fjrh.FabrikApp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.FabrikApp.domain.model.SubscriptionInfo
import com.fjrh.FabrikApp.domain.result.Result
import com.fjrh.FabrikApp.domain.usecase.SubscriptionManager
import com.fjrh.FabrikApp.data.billing.BillingService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val subscriptionManager: SubscriptionManager,
    private val billingService: BillingService
) : ViewModel() {

    private val _subscriptionInfo = MutableStateFlow<SubscriptionInfo?>(null)
    val subscriptionInfo: StateFlow<SubscriptionInfo?> = _subscriptionInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        loadSubscriptionInfo()
    }

    fun initializeTrial() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            // Solo inicializar si no existe un trial previo
            val currentInfo = subscriptionManager.getCurrentSubscriptionInfo()
            if (currentInfo.trialStartDate == 0L) {
                // No hay trial previo, crear uno nuevo
                val result = subscriptionManager.initializeTrial()
                when (result) {
                    is Result.Success -> {
                        _subscriptionInfo.value = result.data
                        _successMessage.value = "Trial inicializado correctamente"
                    }
                    is Result.Error -> {
                        _errorMessage.value = result.exception.message
                    }
                    is Result.Loading -> {
                        // No debería ocurrir aquí
                    }
                }
            } else {
                // Ya existe un trial, solo cargar la información actual
                _subscriptionInfo.value = currentInfo
            }
            _isLoading.value = false
        }
    }

    fun activatePremium() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = subscriptionManager.activatePremium()
            when (result) {
                is Result.Success -> {
                    _subscriptionInfo.value = result.data
                    _successMessage.value = "¡Suscripción Premium activada!"
                }
                is Result.Error -> {
                    _errorMessage.value = result.exception.message
                }
                is Result.Loading -> {
                    // No debería ocurrir aquí
                }
            }
            _isLoading.value = false
        }
    }

    fun checkFeatureAvailability(featureName: String): Boolean {
        return subscriptionManager.isFeatureAvailable(featureName)
    }

    fun loadSubscriptionInfo() {
        _subscriptionInfo.value = subscriptionManager.getCurrentSubscriptionInfo()
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSuccess() {
        _successMessage.value = null
    }

    fun clearSubscriptionData() {
        subscriptionManager.clearSubscriptionData()
        loadSubscriptionInfo()
    }
    
    fun initializeBilling() {
        billingService.initializeBilling()
    }
    
    fun purchaseMonthlySubscription(activity: android.app.Activity) {
        billingService.purchaseSubscription(activity, true)
    }
    
    fun purchaseYearlySubscription(activity: android.app.Activity) {
        billingService.purchaseSubscription(activity, false)
    }
    
    fun getBillingStatus() = billingService.purchaseStatus
    
    fun getBillingConnectionStatus() = billingService.isConnected
    
    fun getPremiumStatus() = billingService.isPremiumActive
    
    fun getBillingService() = billingService
    
    // Funciones de testing - ELIMINAR EN PRODUCCIÓN
    fun simulateTrialDays(daysRemaining: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = subscriptionManager.simulateTrialDays(daysRemaining)
            when (result) {
                is Result.Success -> {
                    _subscriptionInfo.value = result.data
                    _successMessage.value = "Trial simulado: $daysRemaining días restantes"
                }
                is Result.Error -> {
                    _errorMessage.value = result.exception.message
                }
                is Result.Loading -> {
                    // No debería ocurrir aquí
                }
            }
            _isLoading.value = false
        }
    }
    
    fun simulateTrialExpired() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = subscriptionManager.simulateTrialExpired()
            when (result) {
                is Result.Success -> {
                    _subscriptionInfo.value = result.data
                    _successMessage.value = "Trial simulado: Expirado"
                }
                is Result.Error -> {
                    _errorMessage.value = result.exception.message
                }
                is Result.Loading -> {
                    // No debería ocurrir aquí
                }
            }
            _isLoading.value = false
        }
    }
    
    fun activatePremiumWithCode(code: String, firebaseService: com.fjrh.FabrikApp.data.remote.FirebaseService) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = subscriptionManager.activatePremiumWithCode(code, firebaseService)
            when (result) {
                is Result.Success -> {
                    _subscriptionInfo.value = result.data
                    _successMessage.value = "¡Premium activado con código!"
                }
                is Result.Error -> {
                    _errorMessage.value = result.exception.message
                }
                is Result.Loading -> {
                    // No debería ocurrir aquí
                }
            }
            _isLoading.value = false
        }
    }
}
