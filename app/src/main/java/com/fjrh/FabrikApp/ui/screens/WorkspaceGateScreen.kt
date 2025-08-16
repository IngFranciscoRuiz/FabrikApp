package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fjrh.FabrikApp.data.firebase.WorkspaceService
import com.fjrh.FabrikApp.data.firebase.WorkspaceHolder
import com.fjrh.FabrikApp.ui.navigation.Routes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceGateScreen(navController: NavController) {
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var joinCode by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        runCatching {
            WorkspaceService.getCurrentWidOrNull()
        }.onSuccess { wid ->
            if (!wid.isNullOrBlank()) {
                WorkspaceHolder.set(wid)
                navController.navigate(Routes.Menu) {
                    popUpTo(Routes.WorkspaceGate) { inclusive = true }
                    launchSingleTop = true
                    restoreState = true
                }
            } else {
                loading = false
            }
        }.onFailure {
            loading = false
            error = it.message
        }
    }

    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            text = "Elige cómo empezar",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Crea un nuevo workspace o únete a uno existente",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        // Mostrar error si existe
        if (error != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Botón Crear Workspace
        Button(
            onClick = {
                error = null
                loading = true
                scope.launch {
                    runCatching {
                        val wid = WorkspaceService.createWorkspaceForCurrentUser()
                        WorkspaceHolder.set(wid)
                        println("WorkspaceGateScreen: Workspace creado: $wid")
                    }.onSuccess {
                        navController.navigate(Routes.Menu) {
                            popUpTo(Routes.WorkspaceGate) { inclusive = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }.onFailure { e ->
                        loading = false
                        error = e.message
                        println("WorkspaceGateScreen: Error creando workspace: ${e.message}")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Crear nuevo workspace",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Separador
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(modifier = Modifier.weight(1f))
            Text(
                text = "O",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Divider(modifier = Modifier.weight(1f))
        }

        // Campo para código de workspace
        OutlinedTextField(
            value = joinCode,
            onValueChange = { joinCode = it.trim() },
            label = { Text("Código de workspace (WID)") },
            placeholder = { Text("Pega el código aquí") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Botón Unirse
        Button(
            onClick = {
                error = null
                scope.launch {
                    runCatching {
                        require(joinCode.isNotBlank()) { "Ingresa un código" }
                        WorkspaceService.joinWorkspace(joinCode)
                        WorkspaceHolder.set(joinCode)
                        println("WorkspaceGateScreen: Unido al workspace: $joinCode")
                    }.onSuccess {
                        navController.navigate(Routes.Menu) {
                            popUpTo(Routes.WorkspaceGate) { inclusive = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }.onFailure { e ->
                        error = e.message
                        println("WorkspaceGateScreen: Error uniéndose al workspace: ${e.message}")
                    }
                }
            },
            enabled = joinCode.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(
                text = "Unirme a workspace",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Información adicional
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "💡 ¿Cómo funciona?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Crea un workspace si es la primera vez que usas la app\n" +
                           "• Únete a un workspace existente si alguien te compartió un código\n" +
                           "• Todos los datos se sincronizarán automáticamente",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
