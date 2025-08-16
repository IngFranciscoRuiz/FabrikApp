package com.fjrh.FabrikApp.domain.model

import java.util.Date

/**
 * Estado de suscripción de la aplicación
 */
sealed class SubscriptionStatus {
    object Trial : SubscriptionStatus()
    object Active : SubscriptionStatus()
    object Expired : SubscriptionStatus()
    object Blocked : SubscriptionStatus()
}

/**
 * Información de la suscripción
 */
data class SubscriptionInfo(
    val status: SubscriptionStatus,
    val trialStartDate: Long,
    val trialEndDate: Long,
    val isPremium: Boolean,
    val daysRemaining: Int,
    val isBlocked: Boolean
) {
    companion object {
        const val TRIAL_DURATION_DAYS = 7L
        const val MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000L
    }
    
    fun isTrialExpired(): Boolean {
        return System.currentTimeMillis() > trialEndDate
    }
    
    fun calculateDaysRemaining(): Int {
        val currentTime = System.currentTimeMillis()
        if (currentTime > trialEndDate) return 0
        
        val remainingMillis = trialEndDate - currentTime
        return (remainingMillis / MILLISECONDS_PER_DAY).toInt()
    }
    
    fun shouldBlockAccess(): Boolean {
        return isBlocked || (status == SubscriptionStatus.Trial && isTrialExpired())
    }
}
