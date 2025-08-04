package com.fjrh.karycleanfactory

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class KaryCleanFactoryApp : Application() {
    
    @Inject
    lateinit var unidadesService: com.fjrh.karycleanfactory.data.local.service.UnidadesService
    
    override fun onCreate() {
        super.onCreate()
        
        // Precargar unidades básicas
        unidadesService.precargarUnidadesBasicas()
    }
}
