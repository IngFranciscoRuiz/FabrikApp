package com.fjrh.FabrikApp.di

import android.app.Application
import android.content.Context
import com.fjrh.FabrikApp.data.local.AppDatabase
import com.fjrh.FabrikApp.data.local.dao.FormulaDao
import com.fjrh.FabrikApp.data.local.ConfiguracionDataStore
import com.fjrh.FabrikApp.data.local.repository.FormulaRepository
import com.fjrh.FabrikApp.data.local.repository.InventarioRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application): AppDatabase {
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
    fun provideFormulaRepository(db: AppDatabase): FormulaRepository {
        return FormulaRepository(db.formulaDao())
    }

    @Provides
    @Singleton
    fun provideInventarioRepository(db: AppDatabase): InventarioRepository {
        return InventarioRepository(db.formulaDao())
    }
}

