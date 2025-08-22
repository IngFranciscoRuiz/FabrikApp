package com.fjrh.FabrikApp.domain.usecase

import android.content.Context
import android.content.SharedPreferences
import com.fjrh.FabrikApp.domain.model.SubscriptionInfo
import com.fjrh.FabrikApp.domain.model.SubscriptionStatus
import com.fjrh.FabrikApp.domain.result.Result
import com.fjrh.FabrikApp.domain.exception.SubscriptionException
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestor de suscripción con protecciones anti-trampa
 */
@Singleton
class SubscriptionManager @Inject constructor(
    private val context: Context,
    private val billingService: com.fjrh.FabrikApp.data.billing.BillingService
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        SUBSCRIPTION_PREFS, Context.MODE_PRIVATE
    )
    
    companion object {
        private const val SUBSCRIPTION_PREFS = "subscription_prefs"
        private const val KEY_IS_PREMIUM = "is_premium"
        private const val KEY_IS_BLOCKED = "is_blocked"
        private const val KEY_LAST_CHECK = "last_check"
        
        // Funcionalidades que se bloquean sin suscripción
        // En el sistema simplificado, Google Play maneja todo
        val BLOCKED_FEATURES = listOf<String>()
    }
    

    
    /**
     * Obtiene la información actual de suscripción
     */
    fun getCurrentSubscriptionInfo(): SubscriptionInfo {
        val isPremium = prefs.getBoolean(KEY_IS_PREMIUM, false)
        val isBlocked = prefs.getBoolean(KEY_IS_BLOCKED, false)
        
        val status = when {
            isPremium -> SubscriptionStatus.Active
            isBlocked -> SubscriptionStatus.Blocked
            else -> SubscriptionStatus.Expired // Sin trial local, solo premium o bloqueado
        }
        
        return SubscriptionInfo(
            status = status,
            trialStartDate = 0L,
            trialEndDate = 0L,
            isPremium = isPremium,
            daysRemaining = 0,
            isBlocked = isBlocked
        )
    }
    
    /**
     * Verifica si una funcionalidad está disponible
     */
    fun isFeatureAvailable(featureName: String): Boolean {
        val info = getCurrentSubscriptionInfo()
        
        // Verificar estado real de suscripción en Google Play
        val isPremiumActive = billingService.isPremiumActive.value
        
        // Si Google Play confirma que es premium, todo está disponible
        if (isPremiumActive) return true
        
        // Si está bloqueado, nada está disponible
        if (info.isBlocked) return false
        
        // En el sistema simplificado, Google Play maneja todo
        // Si no es premium, Google Play redirigirá al Paywall automáticamente
        return true
    }
    
    /**
     * Activa la suscripción premium
     */
    fun activatePremium(): Result<SubscriptionInfo> {
        return try {
            prefs.edit()
                .putBoolean(KEY_IS_PREMIUM, true)
                .putBoolean(KEY_IS_BLOCKED, false)
                .putLong(KEY_LAST_CHECK, System.currentTimeMillis())
                .apply()
            
            Result.Success(getCurrentSubscriptionInfo())
        } catch (e: Exception) {
            Result.Error(SubscriptionException("Error al activar premium: ${e.message}"))
        }
    }
    
    /**
     * Bloquea la aplicación (para casos de manipulación detectada)
     */
    fun blockApplication(): Result<SubscriptionInfo> {
        return try {
            prefs.edit()
                .putBoolean(KEY_IS_BLOCKED, true)
                .putLong(KEY_LAST_CHECK, System.currentTimeMillis())
                .apply()
            
            Result.Success(getCurrentSubscriptionInfo())
        } catch (e: Exception) {
            Result.Error(SubscriptionException("Error al bloquear aplicación: ${e.message}"))
        }
    }
    

    
    /**
     * Limpia todos los datos de suscripción (para testing)
     */
    fun clearSubscriptionData() {
        prefs.edit().clear().apply()
    }
    

    
    /**
     * Activa premium usando un código de activación
     */
    suspend fun activatePremiumWithCode(code: String, firebaseService: com.fjrh.FabrikApp.data.remote.FirebaseService): Result<SubscriptionInfo> {
        return try {
            val verificationResult = firebaseService.verifyActivationCode(code)
            
            when (verificationResult) {
                is Result.Success -> {
                    // Código válido, activar premium
                    prefs.edit()
                        .putBoolean(KEY_IS_PREMIUM, true)
                        .putBoolean(KEY_IS_BLOCKED, false)
                        .putLong(KEY_LAST_CHECK, System.currentTimeMillis())
                        .apply()
                    
                    Result.Success(getCurrentSubscriptionInfo())
                }
                is Result.Error -> {
                    Result.Error(verificationResult.exception)
                }
                is Result.Loading -> {
                    Result.Error(SubscriptionException("Error inesperado"))
                }
            }
        } catch (e: Exception) {
            Result.Error(SubscriptionException("Error al activar premium: ${e.message}"))
        }
    }
}
