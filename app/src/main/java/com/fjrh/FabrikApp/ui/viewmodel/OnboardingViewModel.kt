package com.fjrh.FabrikApp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.FabrikApp.data.local.OnboardingDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingDataStore: OnboardingDataStore
) : ViewModel() {
    
    val hasSeenOnboarding: Flow<Boolean> = onboardingDataStore.hasSeenOnboarding
    
    fun markOnboardingAsSeen() {
        viewModelScope.launch {
            onboardingDataStore.markOnboardingAsSeen()
        }
    }
}
