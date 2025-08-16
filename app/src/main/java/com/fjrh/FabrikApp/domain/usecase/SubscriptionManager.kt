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
    private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        SUBSCRIPTION_PREFS, Context.MODE_PRIVATE
    )
    
    companion object {
        private const val SUBSCRIPTION_PREFS = "subscription_prefs"
        private const val KEY_TRIAL_START = "trial_start_date"
        private const val KEY_TRIAL_END = "trial_end_date"
        private const val KEY_IS_PREMIUM = "is_premium"
        private const val KEY_IS_BLOCKED = "is_blocked"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_LAST_CHECK = "last_check"
        private const val KEY_CHECKSUM = "checksum"
        
        // Funcionalidades que se bloquean después del trial
        val BLOCKED_FEATURES = listOf(
            "add_ingredient",
            "edit_ingredient", 
            "delete_ingredient",
            "add_formula",
            "edit_formula",
            "delete_formula",
            "add_sale",
            "edit_sale",
            "delete_sale",
            "export_data",
            "import_data",
            "backup_data",
            "advanced_analytics",
            "multiple_users"
        )
    }
    
    /**
     * Inicializa el trial si es la primera vez
     */
    fun initializeTrial(): Result<SubscriptionInfo> {
        return try {
            // Verificar si ya existe un trial
            if (prefs.contains(KEY_TRIAL_START)) {
                return Result.Success(getCurrentSubscriptionInfo())
            }
            
            // Crear nuevo trial
            val currentTime = System.currentTimeMillis()
            val trialEndTime = currentTime + (SubscriptionInfo.TRIAL_DURATION_DAYS * SubscriptionInfo.MILLISECONDS_PER_DAY)
            
            // Guardar datos del trial
            prefs.edit()
                .putLong(KEY_TRIAL_START, currentTime)
                .putLong(KEY_TRIAL_END, trialEndTime)
                .putBoolean(KEY_IS_PREMIUM, false)
                .putBoolean(KEY_IS_BLOCKED, false)
                .putString(KEY_DEVICE_ID, generateDeviceId())
                .putLong(KEY_LAST_CHECK, currentTime)
                .putString(KEY_CHECKSUM, generateChecksum(currentTime, trialEndTime))
                .apply()
            
            val info = SubscriptionInfo(
                status = SubscriptionStatus.Trial,
                trialStartDate = currentTime,
                trialEndDate = trialEndTime,
                isPremium = false,
                daysRemaining = SubscriptionInfo.TRIAL_DURATION_DAYS.toInt(),
                isBlocked = false
            )
            
            Result.Success(info)
        } catch (e: Exception) {
            Result.Error(SubscriptionException("Error al inicializar trial: ${e.message}"))
        }
    }
    
    /**
     * Obtiene la información actual de suscripción
     */
    fun getCurrentSubscriptionInfo(): SubscriptionInfo {
        val trialStart = prefs.getLong(KEY_TRIAL_START, 0L)
        val trialEnd = prefs.getLong(KEY_TRIAL_END, 0L)
        val isPremium = prefs.getBoolean(KEY_IS_PREMIUM, false)
        val isBlocked = prefs.getBoolean(KEY_IS_BLOCKED, false)
        
        // Verificar integridad de datos
        if (!verifyDataIntegrity(trialStart, trialEnd)) {
            // Datos corruptos, bloquear acceso
            return SubscriptionInfo(
                status = SubscriptionStatus.Blocked,
                trialStartDate = trialStart,
                trialEndDate = trialEnd,
                isPremium = false,
                daysRemaining = 0,
                isBlocked = true
            )
        }
        
        val status = when {
            isPremium -> SubscriptionStatus.Active
            isBlocked -> SubscriptionStatus.Blocked
            System.currentTimeMillis() > trialEnd -> SubscriptionStatus.Expired
            else -> SubscriptionStatus.Trial
        }
        
        return SubscriptionInfo(
            status = status,
            trialStartDate = trialStart,
            trialEndDate = trialEnd,
            isPremium = isPremium,
            daysRemaining = if (status == SubscriptionStatus.Trial) {
                val remaining = trialEnd - System.currentTimeMillis()
                (remaining / SubscriptionInfo.MILLISECONDS_PER_DAY).toInt().coerceAtLeast(0)
            } else 0,
            isBlocked = isBlocked
        )
    }
    
    /**
     * Verifica si una funcionalidad está disponible
     */
    fun isFeatureAvailable(featureName: String): Boolean {
        val info = getCurrentSubscriptionInfo()
        
        // Si es premium, todo está disponible
        if (info.isPremium) return true
        
        // Si está bloqueado, nada está disponible
        if (info.isBlocked) return false
        
        // Si el trial expiró, solo funcionalidades básicas
        if (info.isTrialExpired()) {
            return !BLOCKED_FEATURES.contains(featureName)
        }
        
        // Durante el trial, todo está disponible
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
     * Verifica la integridad de los datos
     */
    private fun verifyDataIntegrity(trialStart: Long, trialEnd: Long): Boolean {
        // Para desarrollo, simplificar la verificación
        // Verificar que las fechas sean lógicas
        if (trialStart <= 0 || trialEnd <= 0 || trialEnd <= trialStart) {
            return false
        }
        
        // Verificar que el trial no sea muy antiguo (más de 30 días)
        val thirtyDaysAgo = System.currentTimeMillis() - (30 * SubscriptionInfo.MILLISECONDS_PER_DAY)
        if (trialStart < thirtyDaysAgo) {
            return false
        }
        
        return true
    }
    
    /**
     * Genera un ID único del dispositivo
     */
    private fun generateDeviceId(): String {
        val androidId = android.provider.Settings.Secure.getString(
            context.contentResolver, 
            android.provider.Settings.Secure.ANDROID_ID
        )
        return hashString(androidId + context.packageName)
    }
    
    /**
     * Genera un checksum para verificar integridad
     */
    private fun generateChecksum(trialStart: Long, trialEnd: Long): String {
        val deviceId = prefs.getString(KEY_DEVICE_ID, "") ?: ""
        val data = "$trialStart:$trialEnd:$deviceId:${context.packageName}"
        return hashString(data)
    }
    
    /**
     * Genera un hash SHA-256
     */
    private fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Limpia todos los datos de suscripción (para testing)
     */
    fun clearSubscriptionData() {
        prefs.edit().clear().apply()
    }
    
    // Funciones de testing - ELIMINAR EN PRODUCCIÓN
    fun simulateTrialDays(daysRemaining: Int): Result<SubscriptionInfo> {
        return try {
            val currentTime = System.currentTimeMillis()
            val trialEndTime = currentTime + (daysRemaining * SubscriptionInfo.MILLISECONDS_PER_DAY)
            
            prefs.edit()
                .putLong(KEY_TRIAL_START, currentTime - ((7 - daysRemaining) * SubscriptionInfo.MILLISECONDS_PER_DAY))
                .putLong(KEY_TRIAL_END, trialEndTime)
                .putBoolean(KEY_IS_PREMIUM, false)
                .putBoolean(KEY_IS_BLOCKED, false)
                .putString(KEY_DEVICE_ID, generateDeviceId())
                .putLong(KEY_LAST_CHECK, currentTime)
                .putString(KEY_CHECKSUM, generateChecksum(currentTime, trialEndTime))
                .apply()
            
            val info = SubscriptionInfo(
                status = SubscriptionStatus.Trial,
                trialStartDate = currentTime - ((7 - daysRemaining) * SubscriptionInfo.MILLISECONDS_PER_DAY),
                trialEndDate = trialEndTime,
                isPremium = false,
                daysRemaining = daysRemaining,
                isBlocked = false
            )
            
            Result.Success(info)
        } catch (e: Exception) {
            Result.Error(SubscriptionException("Error al simular trial: ${e.message}"))
        }
    }
    
    fun simulateTrialExpired(): Result<SubscriptionInfo> {
        return try {
            val currentTime = System.currentTimeMillis()
            val trialEndTime = currentTime - (1 * SubscriptionInfo.MILLISECONDS_PER_DAY) // 1 día atrás
            
            prefs.edit()
                .putLong(KEY_TRIAL_START, currentTime - (8 * SubscriptionInfo.MILLISECONDS_PER_DAY))
                .putLong(KEY_TRIAL_END, trialEndTime)
                .putBoolean(KEY_IS_PREMIUM, false)
                .putBoolean(KEY_IS_BLOCKED, false)
                .putString(KEY_DEVICE_ID, generateDeviceId())
                .putLong(KEY_LAST_CHECK, currentTime)
                .putString(KEY_CHECKSUM, generateChecksum(currentTime, trialEndTime))
                .apply()
            
            val info = SubscriptionInfo(
                status = SubscriptionStatus.Expired,
                trialStartDate = currentTime - (8 * SubscriptionInfo.MILLISECONDS_PER_DAY),
                trialEndDate = trialEndTime,
                isPremium = false,
                daysRemaining = 0,
                isBlocked = false
            )
            
            Result.Success(info)
        } catch (e: Exception) {
            Result.Error(SubscriptionException("Error al simular trial expirado: ${e.message}"))
        }
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
