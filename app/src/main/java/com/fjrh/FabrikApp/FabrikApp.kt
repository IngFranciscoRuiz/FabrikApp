package com.fjrh.FabrikApp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FabrikApp : Application() {
    
    @Inject
    lateinit var unidadesService: com.fjrh.FabrikApp.data.local.service.UnidadesService
    
    override fun onCreate() {
        super.onCreate()
        
        // Precargar unidades básicas
        unidadesService.precargarUnidadesBasicas()
    }
}
