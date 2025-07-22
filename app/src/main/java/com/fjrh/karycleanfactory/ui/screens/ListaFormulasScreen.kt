package com.fjrh.karycleanfactory.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fjrh.karycleanfactory.data.local.entity.FormulaConIngredientes
import com.fjrh.karycleanfactory.ui.viewmodel.FormulaViewModel
import com.fjrh.karycleanfactory.ui.viewmodel.FormulaViewModelFactory

@Composable
fun ListaFormulasScreen(
    viewModel: FormulaViewModel,
    onEdit: (FormulaConIngredientes) -> Unit,
    onProduccion: (FormulaConIngredientes) -> Unit
) {
    val listaFormulas by viewModel.formulas.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF000000),
                        Color(0xFF0D1A2F)
                    )
                )
            )
            .padding(16.dp)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(listaFormulas) { formula ->
                FormulaAccordionCard(
                    formula = formula,
                    onEdit = { onEdit(formula) },
                    onProduccion = { onProduccion(formula) }
                )
            }
        }
    }
}

@Composable
fun FormulaAccordionCard(
    formula: FormulaConIngredientes,
    onEdit: () -> Unit,
    onProduccion: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val cardColor = Color(0xFFEAF0F6) // Blanco azulado metálico
    val textColor = Color(0xFF1C2A3A) // Azul metálico oscuro

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable {
                if (!expanded) onProduccion()
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formula.formula.nombre,
                    fontSize = 18.sp,
                    color = textColor
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "Expandir",
                        tint = textColor
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                formula.ingredientes.forEach {
                    Text(
                        text = "• ${it.nombre}",
                        fontSize = 14.sp,
                        color = textColor
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C2A3A)),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar", color = Color.White)
                }
            }
        }
    }
}
