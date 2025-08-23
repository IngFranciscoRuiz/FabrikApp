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
    
    // StateFlows para offerTokens
    private val _monthlyOfferToken = MutableStateFlow<String?>(null)
    val monthlyOfferToken: StateFlow<String?> = _monthlyOfferToken.asStateFlow()
    
    private val _yearlyOfferToken = MutableStateFlow<String?>(null)
    val yearlyOfferToken: StateFlow<String?> = _yearlyOfferToken.asStateFlow()
    
    // StateFlow para estado real de suscripción
    private val _isPremiumActive = MutableStateFlow(false)
    val isPremiumActive: StateFlow<Boolean> = _isPremiumActive.asStateFlow()
    
    // StateFlows para planes base sin trial
    private val _monthlyStandardProduct = MutableStateFlow<ProductDetails?>(null)
    val monthlyStandardProduct: StateFlow<ProductDetails?> = _monthlyStandardProduct.asStateFlow()
    
    private val _yearlyStandardProduct = MutableStateFlow<ProductDetails?>(null)
    val yearlyStandardProduct: StateFlow<ProductDetails?> = _yearlyStandardProduct.asStateFlow()
    
    sealed class PurchaseStatus {
        object Success : PurchaseStatus()
        object Pending : PurchaseStatus()
        data class Error(val message: String) : PurchaseStatus()
    }
    
    companion object {
        // Product ID principal de Google Play Console
        const val PRODUCT_ID = "fabrikapp_premium"
        
        // Planes básicos con trial (base plans)
        const val BASE_PLAN_MONTHLY = "premium-mensual"
        const val BASE_PLAN_YEARLY = "premium-anual"
        
        // Ofertas de prueba gratuita
        const val OFFER_MONTHLY = "trial-mensual-7"
        const val OFFER_YEARLY = "trial-anual-7"
        
        // Planes base sin trial (fallback)
        const val BASE_PLAN_MONTHLY_STANDARD = "mensual-standar"
        const val BASE_PLAN_YEARLY_STANDARD = "anual-standar"
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
    
    // Función corregida para cargar detalles del producto y obtener offerTokens
    private fun loadProductDetails() {
        if (!_isConnected.value) return
        
        // Cargar productos con trial
        loadTrialProducts()
        
        // Cargar productos base sin trial
        loadStandardProducts()
    }
    
    private fun loadTrialProducts() {
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
                println("BillingService: Trial products query successful, found ${productDetailsList.size} products")
                val productDetails = productDetailsList.firstOrNull()
                if (productDetails != null) {
                    println("BillingService: Main product ID: ${productDetails.productId}")
                    val offers = productDetails.subscriptionOfferDetails.orEmpty()
                    println("BillingService: Found ${offers.size} offers")
                    
                    // Buscar ofertas específicas por basePlanId y offerId
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
                    
                    // Log para debugging
                    println("BillingService: Monthly offer token: ${_monthlyOfferToken.value}")
                    println("BillingService: Yearly offer token: ${_yearlyOfferToken.value}")
                    
                    // Log de todas las ofertas disponibles
                    offers.forEach { offer ->
                        println("BillingService: Available offer - basePlanId: ${offer.basePlanId}, offerId: ${offer.offerId}")
                    }
                    
                    // Si no hay offer tokens disponibles (ya usaste trial), usar los base plans sin trial
                    if (_monthlyOfferToken.value == null && _yearlyOfferToken.value == null) {
                        // Buscar ofertas base sin trial
                        val monthlyBaseOffer = offers.find { offer ->
                            offer.basePlanId == BASE_PLAN_MONTHLY && offer.offerId == null
                        }
                        val yearlyBaseOffer = offers.find { offer ->
                            offer.basePlanId == BASE_PLAN_YEARLY && offer.offerId == null
                        }
                        
                        if (monthlyBaseOffer != null) {
                            _monthlyStandardProduct.value = productDetails
                            println("BillingService: Monthly standard product loaded from base plan without trial")
                        }
                        
                        if (yearlyBaseOffer != null) {
                            _yearlyStandardProduct.value = productDetails
                            println("BillingService: Yearly standard product loaded from base plan without trial")
                        }
                    }
                }
            } else {
                println("BillingService: Error loading trial products: ${billingResult.debugMessage}")
                println("BillingService: Response code: ${billingResult.responseCode}")
            }
        }
    }
    
    private fun loadStandardProducts() {
        // Los productos base sin trial son base plans del producto principal
        // Los cargamos junto con los productos con trial
        println("BillingService: Standard products will be loaded from main product base plans")
    }
    
    // Función para verificar estado real de suscripción
    fun refreshSubscriptionStatus() {
        if (!_isConnected.value) return

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
                println("BillingService: Premium active: $premiumActive")
            } else {
                _isPremiumActive.value = false
                println("BillingService: Error querying purchases: ${billingResult.debugMessage}")
            }
        }
    }
    
    // Función corregida para comprar suscripción específica con fallback automático
    fun purchaseSubscription(activity: Activity, isMonthly: Boolean) {
        if (!_isConnected.value) {
            _purchaseStatus.value = PurchaseStatus.Error("Billing no está conectado")
            return
        }
        
        // Intentar usar trial primero
        val offerToken = if (isMonthly) {
            _monthlyOfferToken.value
        } else {
            _yearlyOfferToken.value
        }
        
        if (offerToken != null) {
            // Usar trial disponible
            println("BillingService: Using trial offer token: $offerToken")
            launchTrialPurchase(activity, isMonthly, offerToken)
        } else {
            // Usar plan base sin trial
            val standardProduct = if (isMonthly) {
                _monthlyStandardProduct.value
            } else {
                _yearlyStandardProduct.value
            }
            
            if (standardProduct != null) {
                println("BillingService: Using standard product without trial")
                launchStandardPurchase(activity, standardProduct)
            } else {
                _purchaseStatus.value = PurchaseStatus.Error("Producto no disponible. Intenta de nuevo.")
            }
        }
    }
    
    private fun launchTrialPurchase(activity: Activity, isMonthly: Boolean, offerToken: String) {
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
    
    private fun launchStandardPurchase(activity: Activity, productDetails: ProductDetails) {
        // Para productos base sin trial, necesitamos encontrar el offer correcto
        val offers = productDetails.subscriptionOfferDetails.orEmpty()
        println("BillingService: Looking for standard product offer among ${offers.size} offers")
        
        // Buscar el offer del base plan sin trial (offerId = null)
        val standardOffer = offers.find { offer ->
            (offer.basePlanId == BASE_PLAN_MONTHLY || offer.basePlanId == BASE_PLAN_YEARLY) && 
            offer.offerId == null
        }
        
        if (standardOffer != null) {
            println("BillingService: Using standard offer: basePlanId=${standardOffer.basePlanId}, offerToken=${standardOffer.offerToken}")
            launchBillingFlow(activity, productDetails, standardOffer.offerToken)
        } else {
            println("BillingService: No standard offer found")
            _purchaseStatus.value = PurchaseStatus.Error("No se encontró oferta para el producto base")
        }
    }
    
    private fun launchBillingFlow(
        activity: Activity,
        productDetails: ProductDetails,
        offerToken: String?
    ) {
        val productDetailsParamsBuilder = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
        
        // Solo agregar offer token si está disponible
        if (offerToken != null) {
            productDetailsParamsBuilder.setOfferToken(offerToken)
        }
        
        val productDetailsParamsList = listOf(productDetailsParamsBuilder.build())
        
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        
        val billingResult = billingClient?.launchBillingFlow(activity, billingFlowParams)
        
        if (billingResult?.responseCode != BillingClient.BillingResponseCode.OK) {
            _purchaseStatus.value = PurchaseStatus.Error("Error al iniciar flujo de compra: ${billingResult?.debugMessage}")
        }
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
}
