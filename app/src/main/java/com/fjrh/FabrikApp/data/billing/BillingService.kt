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
//import com.fjrh.FabrikApp.BuildConfig

@Singleton
class BillingService @Inject constructor(
    private val context: Context
) {
    private var billingClient: BillingClient? = null
    
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    
    private val _purchaseStatus = MutableStateFlow<PurchaseStatus?>(null)
    val purchaseStatus: StateFlow<PurchaseStatus?> = _purchaseStatus.asStateFlow()
    
    sealed class PurchaseStatus {
        object Success : PurchaseStatus()
        object Pending : PurchaseStatus()
        data class Error(val message: String) : PurchaseStatus()
    }
    
    companion object {
        const val SUBSCRIPTION_MONTHLY = "fabrikapp_premium_monthly"
        const val SUBSCRIPTION_YEARLY = "fabrikapp_premium_yearly"
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
                } else {
                    _isConnected.value = false
                }
            }
            
            override fun onBillingServiceDisconnected() {
                _isConnected.value = false
            }
        })
    }
    
    fun purchaseSubscription(activity: Activity, subscriptionId: String) {
        // Por ahora, simular compra exitosa para testing
        // En producción, esto se manejará con Google Play Billing real
        _purchaseStatus.value = PurchaseStatus.Success
        return
        
        // Código real de Google Play Billing (comentado hasta que tengas productos configurados)
        /*
        if (!_isConnected.value) {
            _purchaseStatus.value = PurchaseStatus.Error("Billing no está conectado")
            return
        }
        */
        
        /*
        val productDetailsParamsList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(subscriptionId)
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
                    val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
                    if (offerToken != null) {
                        launchBillingFlow(activity, productDetails, offerToken)
                    } else {
                        _purchaseStatus.value = PurchaseStatus.Error("No se encontró oferta válida")
                    }
                } else {
                    _purchaseStatus.value = PurchaseStatus.Error("Producto no encontrado")
                }
            } else {
                _purchaseStatus.value = PurchaseStatus.Error("Error al consultar productos: ${billingResult.debugMessage}")
            }
        }
        */
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
                    } else {
                        _purchaseStatus.value = PurchaseStatus.Error("Error al confirmar compra")
                    }
                }
            } else {
                _purchaseStatus.value = PurchaseStatus.Success
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
}
