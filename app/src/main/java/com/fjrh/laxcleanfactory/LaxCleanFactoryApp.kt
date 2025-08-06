package com.fjrh.laxcleanfactory

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class LaxCleanFactoryApp : Application() {
    
    @Inject
    lateinit var unidadesService: com.fjrh.laxcleanfactory.data.local.service.UnidadesService
    
    override fun onCreate() {
        super.onCreate()
        
        // Precargar unidades básicas
        unidadesService.precargarUnidadesBasicas()
    }
} 
