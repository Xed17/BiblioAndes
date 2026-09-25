package pe.edu.upeu.bilbioandes.presentation.detalle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upeu.bilbioandes.presentation.common.UiState

@Composable
fun DetalleLibroScreen(
    viewModel: DetalleLibroViewModel,
    onVolver: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    when (val resultado = state.estado) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is UiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = resultado.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onVolver) {
                        Text("Volver")
                    }
                }
            }
        }

        is UiState.Success -> {
            val libro = resultado.data

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = libro.titulo,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            DetalleItem("Autor", libro.autor)
                            DetalleItem("Año", libro.anio.toString())
                            DetalleItem("Categoría", libro.categoria)
                            DetalleItem("Sede", libro.sede)
                            DetalleItem(
                                "Ejemplares disponibles",
                                libro.ejemplaresDisponibles.toString()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón de solicitar préstamo
                    val botonHabilitado = libro.ejemplaresDisponibles > 0 && !state.limitePrestamosAlcanzado

                    Button(
                        onClick = { viewModel.mostrarDialogoConfirmacion() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = botonHabilitado
                    ) {
                        Text(
                            text = if (libro.ejemplaresDisponibles == 0)
                                "Sin ejemplares disponibles"
                            else
                                "Solicitar préstamo"
                        )
                    }

                    if (state.limitePrestamosAlcanzado) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Límite alcanzado",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Has alcanzado el límite máximo de préstamos activos (RN-01). Debes devolver un libro antes de solicitar uno nuevo.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    } else if (libro.ejemplaresDisponibles == 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Este libro no tiene ejemplares disponibles en este momento.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                // Mensaje de resultado del préstamo
                state.resultadoPrestamo?.let { mensaje ->
                    Snackbar(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        action = {
                            TextButton(onClick = { viewModel.limpiarResultado() }) {
                                Text("Cerrar")
                            }
                        }
                    ) {
                        Text(text = mensaje)
                    }
                }
            }

            // Diálogo de confirmación
            if (state.mostrarDialogo) {
                AlertDialog(
                    onDismissRequest = { viewModel.ocultarDialogo() },
                    title = {
                        Text("Confirmar préstamo")
                    },
                    text = {
                        Text("¿Deseas solicitar el préstamo de este libro?")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { viewModel.solicitarPrestamoLibro() }
                        ) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { viewModel.ocultarDialogo() }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun DetalleItem(
    etiqueta: String,
    valor: String
) {
    Column(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
