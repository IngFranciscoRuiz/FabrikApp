package com.fjrh.FabrikApp.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.fjrh.FabrikApp.domain.result.Result
import com.fjrh.FabrikApp.domain.exception.SubscriptionException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.emptyList

@Singleton
class BillingService @Inject constructor(
    private val context: Context
) {
    private var billingClient: BillingClient? = null
    
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    
    private val _purchaseStatus = MutableStateFlow<PurchaseStatus?>(null)
    val purchaseStatus: StateFlow<PurchaseStatus?> = _purchaseStatus.asStateFlow()
    
    // Nuevos StateFlows para offerTokens
    private val _monthlyOfferToken = MutableStateFlow<String?>(null)
    val monthlyOfferToken: StateFlow<String?> = _monthlyOfferToken.asStateFlow()
    
    private val _yearlyOfferToken = MutableStateFlow<String?>(null)
    val yearlyOfferToken: StateFlow<String?> = _yearlyOfferToken.asStateFlow()
    
    // StateFlow para estado real de suscripción
    private val _isPremiumActive = MutableStateFlow(false)
    val isPremiumActive: StateFlow<Boolean> = _isPremiumActive.asStateFlow()
    
    sealed class PurchaseStatus {
        object Success : PurchaseStatus()
        object Pending : PurchaseStatus()
        data class Error(val message: String) : PurchaseStatus()
    }
    
    companion object {
        const val PRODUCT_ID = "fabrikapp_premium"
        const val BASE_PLAN_MONTHLY = "premium-mensual"
        const val BASE_PLAN_YEARLY = "premium-anual"
        const val OFFER_MONTHLY = "trial-mensual-7"
        const val OFFER_YEARLY = "trial-anual-7"
    }
    
    fun initializeBilling() {
        billingClient = BillingClient.newBuilder(context)
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                    _purchaseStatus.value = PurchaseStatus.Error("Compra cancelada por el usuario")
                } else {
                    _purchaseStatus.value = PurchaseStatus.Error("Error en la compra: ${billingResult.debugMessage}")
                }
            }
            .enablePendingPurchases()
            .build()
        
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _isConnected.value = true
                    // Cargar productos y verificar suscripción al conectar
                    loadProductDetails()
                    refreshSubscriptionStatus()
                } else {
                    _isConnected.value = false
                }
            }
            
            override fun onBillingServiceDisconnected() {
                _isConnected.value = false
            }
        })
    }
    
    // Nueva función para cargar detalles del producto y obtener offerTokens
    private fun loadProductDetails() {
        if (!_isConnected.value) return
        
        val productDetailsParamsList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )
        
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productDetailsParamsList)
            .build()
        
        billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val productDetails = productDetailsList.firstOrNull()
                if (productDetails != null) {
                    val offers = productDetails.subscriptionOfferDetails.orEmpty()
                    
                    // Buscar ofertas específicas por offerId
                    val monthlyOffer = offers.find { offer ->
                        offer.basePlanId == BASE_PLAN_MONTHLY && 
                        offer.offerId == OFFER_MONTHLY
                    }
                    val yearlyOffer = offers.find { offer ->
                        offer.basePlanId == BASE_PLAN_YEARLY && 
                        offer.offerId == OFFER_YEARLY
                    }
                    
                    _monthlyOfferToken.value = monthlyOffer?.offerToken
                    _yearlyOfferToken.value = yearlyOffer?.offerToken
                }
            }
        }
    }
    
    // Nueva función para verificar estado real de suscripción
    fun refreshSubscriptionStatus() {
        if (!_isConnected.value) return
        
        // Si estamos en modo testing, no sobrescribir el estado
        if (isTestingMode) return
        
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        
        billingClient?.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // queryPurchasesAsync solo devuelve compras ACTIVAS
                val premiumActive = purchases.any { purchase ->
                    purchase.products.contains(PRODUCT_ID) &&
                    purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                }
                _isPremiumActive.value = premiumActive
            } else {
                _isPremiumActive.value = false
            }
        }
    }
    
    // Función actualizada para comprar suscripción específica
    fun purchaseSubscription(activity: Activity, isMonthly: Boolean) {
        if (!_isConnected.value) {
            _purchaseStatus.value = PurchaseStatus.Error("Billing no está conectado")
            return
        }
        
        val offerToken = if (isMonthly) {
            _monthlyOfferToken.value
        } else {
            _yearlyOfferToken.value
        }
        
        if (offerToken == null) {
            _purchaseStatus.value = PurchaseStatus.Error("Oferta no disponible")
            return
        }
        
        // Obtener ProductDetails para usar en el flujo de compra
        val productDetailsParamsList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )
        
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productDetailsParamsList)
            .build()
        
        billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val productDetails = productDetailsList.firstOrNull()
                if (productDetails != null) {
                    launchBillingFlow(activity, productDetails, offerToken)
                } else {
                    _purchaseStatus.value = PurchaseStatus.Error("Producto no encontrado")
                }
            } else {
                _purchaseStatus.value = PurchaseStatus.Error("Error al consultar productos: ${billingResult.debugMessage}")
            }
        }
    }
    
    private fun launchBillingFlow(
        activity: Activity,
        productDetails: ProductDetails,
        offerToken: String
    ) {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )
        
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        
        billingClient?.launchBillingFlow(activity, billingFlowParams)
    }
    
    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                
                billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        _purchaseStatus.value = PurchaseStatus.Success
                        // Refrescar estado de suscripción después de compra exitosa
                        refreshSubscriptionStatus()
                    } else {
                        _purchaseStatus.value = PurchaseStatus.Error("Error al confirmar compra")
                    }
                }
            } else {
                _purchaseStatus.value = PurchaseStatus.Success
                refreshSubscriptionStatus()
            }
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            _purchaseStatus.value = PurchaseStatus.Pending
        } else {
            _purchaseStatus.value = PurchaseStatus.Error("Estado de compra no válido")
        }
    }
    
    fun queryPurchases(): Result<List<Purchase>> {
        return try {
            if (!_isConnected.value) {
                return Result.Error(SubscriptionException("Billing no está conectado"))
            }
            
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
            
            // Por ahora retornamos una lista vacía ya que queryPurchasesAsync es asíncrono
            // En una implementación completa, necesitarías manejar esto de forma asíncrona
            Result.Success(emptyList())
        } catch (e: Exception) {
            Result.Error(SubscriptionException("Error al consultar compras: ${e.message}"))
        }
    }
    
    fun disconnect() {
        billingClient?.endConnection()
        billingClient = null
        _isConnected.value = false
    }
    
    // Variable para controlar si estamos en modo testing
    private var isTestingMode = false
    
    // Función para simular premium en testing (SOLO PARA TESTING)
    fun simulatePremiumForTesting() {
        isTestingMode = true
        _isPremiumActive.value = true
    }
    
    // Función para salir del modo testing
    fun exitTestingMode() {
        isTestingMode = false
    }
}
