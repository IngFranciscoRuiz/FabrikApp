package com.fjrh.FabrikApp

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FabrikApp : Application() {
    
    @Inject
    lateinit var unidadesService: com.fjrh.FabrikApp.data.local.service.UnidadesService
    
    override fun onCreate() {
        super.onCreate()
        
        // Inicializar Firebase
        try {
            FirebaseApp.initializeApp(this)
            println("Firebase inicializado correctamente")
            
            // Configurar Firestore
            FirebaseFirestore.getInstance().firestoreSettings = 
                FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build()
            println("Firestore configurado con persistencia")
        } catch (e: Exception) {
            println("Error al inicializar Firebase: ${e.message}")
            e.printStackTrace()
        }
        
        // Precargar unidades básicas
        unidadesService.precargarUnidadesBasicas()
    }
}
