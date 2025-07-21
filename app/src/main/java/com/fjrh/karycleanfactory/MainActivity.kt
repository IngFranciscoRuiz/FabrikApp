package com.fjrh.karycleanfactory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fjrh.karycleanfactory.data.local.AppDatabase

import com.fjrh.karycleanfactory.ui.screens.FormulaListScreen
import com.fjrh.karycleanfactory.ui.viewmodel.FormulaViewModel
import com.fjrh.karycleanfactory.ui.viewmodel.FormulaViewModelFactory
import com.fjrh.karycleanfactory.ui.theme.KaryCleanFactoryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KaryCleanFactoryTheme {
                val dao = AppDatabase.getDatabase(applicationContext).formulaDao()
                val viewModel: FormulaViewModel = viewModel(
                    factory = FormulaViewModelFactory(dao)
                )

                FormulaListScreen(viewModel = viewModel)
            }
        }
    }
}
