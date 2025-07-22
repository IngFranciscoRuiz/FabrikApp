package com.fjrh.karycleanfactory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fjrh.karycleanfactory.data.local.dao.FormulaDao

class FormulaViewModelFactory(private val dao: FormulaDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FormulaViewModel(dao) as T
    }
}
