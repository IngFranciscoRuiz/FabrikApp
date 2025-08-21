package com.fjrh.FabrikApp.di

import android.app.Application
import android.content.Context
import com.fjrh.FabrikApp.data.local.AppDatabase
import com.fjrh.FabrikApp.data.local.ConfiguracionDataStore
import com.fjrh.FabrikApp.data.local.OnboardingDataStore
import com.fjrh.FabrikApp.data.local.MultiUserDataStore
import com.fjrh.FabrikApp.data.local.dao.FormulaDao
import com.fjrh.FabrikApp.data.local.repository.FormulaRepository
import com.fjrh.FabrikApp.data.local.repository.InventarioRepository
import com.fjrh.FabrikApp.data.local.service.UnidadesService
import com.fjrh.FabrikApp.domain.usecase.ErrorHandler
import com.fjrh.FabrikApp.domain.usecase.SubscriptionManager
import com.fjrh.FabrikApp.data.remote.FirebaseService
import com.fjrh.FabrikApp.data.billing.BillingService
import com.fjrh.FabrikApp.data.local.SAFService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): AppDatabase {
        return AppDatabase.getDatabase(application)
    }

    @Provides
    fun provideFormulaDao(db: AppDatabase): FormulaDao {
        return db.formulaDao()
    }

    @Provides
    @Singleton
    fun provideConfiguracionDataStore(application: Application): ConfiguracionDataStore {
        return ConfiguracionDataStore(application)
    }

    @Provides
    @Singleton
    fun provideOnboardingDataStore(@ApplicationContext context: Context): OnboardingDataStore {
        return OnboardingDataStore(context)
    }

    @Provides
    @Singleton
    fun provideMultiUserDataStore(@ApplicationContext context: Context): MultiUserDataStore {
        return MultiUserDataStore(context)
    }

    @Provides
    @Singleton
    fun provideFormulaRepository(db: AppDatabase): FormulaRepository {
        return FormulaRepository(db.formulaDao(), db)
    }

    @Provides
    @Singleton
    fun provideInventarioRepository(db: AppDatabase): InventarioRepository {
        return InventarioRepository(db.formulaDao())
    }

    @Provides
    @Singleton
    fun provideUnidadesService(formulaRepository: FormulaRepository): UnidadesService {
        return UnidadesService(formulaRepository)
    }

    @Provides
    @Singleton
    fun provideErrorHandler(@ApplicationContext context: Context): ErrorHandler {
        return ErrorHandler(context)
    }

    @Provides
    @Singleton
    fun provideSubscriptionManager(
        @ApplicationContext context: Context,
        billingService: BillingService
    ): SubscriptionManager {
        return SubscriptionManager(context, billingService)
    }
    
    @Provides
    @Singleton
    fun provideFirebaseService(): com.fjrh.FabrikApp.data.remote.FirebaseService {
        return com.fjrh.FabrikApp.data.remote.FirebaseService()
    }
    
    @Provides
    @Singleton
    fun provideSyncManager(
        firebaseService: com.fjrh.FabrikApp.data.remote.FirebaseService,
        formulaDao: com.fjrh.FabrikApp.data.local.dao.FormulaDao
    ): com.fjrh.FabrikApp.domain.usecase.SyncManager {
        return com.fjrh.FabrikApp.domain.usecase.SyncManager(
            firebaseService, formulaDao
        )
    }
    
    @Provides
    @Singleton
    fun provideBillingService(@ApplicationContext context: Context): BillingService {
        return BillingService(context)
    }
    
    @Provides
    @Singleton
    fun provideSAFService(@ApplicationContext context: Context): SAFService {
        return SAFService(context)
    }
}

