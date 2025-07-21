package com.fjrh.karycleanfactory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun TextScreen(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF4F2)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFF944D2E)
        )
    }
}
