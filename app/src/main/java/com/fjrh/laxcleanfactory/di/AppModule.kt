package com.fjrh.laxcleanfactory.di

import android.app.Application
import android.content.Context
import com.fjrh.laxcleanfactory.data.local.AppDatabase
import com.fjrh.laxcleanfactory.data.local.dao.FormulaDao
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
}

