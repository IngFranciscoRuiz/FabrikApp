package com.fjrh.karycleanfactory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.fjrh.karycleanfactory.data.local.AppDatabase
import com.fjrh.karycleanfactory.ui.navigation.AppNavigation
import com.fjrh.karycleanfactory.ui.theme.KaryCleanTheme
import com.fjrh.karycleanfactory.ui.viewmodel.FormulaViewModel
import com.fjrh.karycleanfactory.ui.viewmodel.FormulaViewModelFactory
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "karyclean_db"
        ).build()

        val formulaDao = database.formulaDao()
        val factory = FormulaViewModelFactory(formulaDao)

        setContent {
            KaryCleanTheme {
                val navController = rememberNavController()
                val viewModel: FormulaViewModel = viewModel(factory = factory)

                AppNavigation(navController = navController, viewModel = viewModel)
            }
        }
    }
}
