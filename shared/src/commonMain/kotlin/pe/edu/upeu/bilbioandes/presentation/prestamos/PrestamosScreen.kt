package pe.edu.upeu.bilbioandes.presentation.prestamos

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upeu.bilbioandes.domain.model.EstadoPrestamo
import pe.edu.upeu.bilbioandes.domain.model.Prestamo
import pe.edu.upeu.bilbioandes.presentation.common.FiltroPrestamo
import pe.edu.upeu.bilbioandes.presentation.common.UiState

@Composable
fun PrestamosScreen(
    viewModel: PrestamosViewModel
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
                    Button(onClick = { viewModel.cargarPrestamos() }) {
                        Text("Reintentar")
                    }
                }
            }
        }

        is UiState.Success -> {
            val prestamosFiltrados = viewModel.obtenerPrestamosFiltrados()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Filtros de estado
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FiltroPrestamo.entries.forEach { filtro ->
                        FilterChip(
                            selected = state.filtro == filtro,
                            onClick = { viewModel.cambiarFiltro(filtro) },
                            label = {
                                Text(
                                    when (filtro) {
                                        FiltroPrestamo.TODOS -> "Todos"
                                        FiltroPrestamo.ACTIVOS -> "Activos"
                                        FiltroPrestamo.DEVUELTOS -> "Devueltos"
                                        FiltroPrestamo.VENCIDOS -> "Vencidos"
                                    }
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (prestamosFiltrados.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay préstamos con este filtro.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(prestamosFiltrados) { prestamo ->
                            PrestamoCard(prestamo = prestamo)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrestamoCard(
    prestamo: Prestamo
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = prestamo.libro.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = prestamo.libro.autor,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Fecha límite: ${prestamo.fechaLimite}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Información específica según el tipo de estado (sealed class)
            EstadoPrestamoView(estado = prestamo.estado)
        }
    }
}

@Composable
fun EstadoPrestamoView(
    estado: EstadoPrestamo
) {
    when (estado) {
        is EstadoPrestamo.Activo -> {
            Text(
                text = "Activo: ${estado.diasRestantes} días restantes",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        is EstadoPrestamo.Devuelto -> {
            Text(
                text = "Devuelto el ${estado.fechaDevolucion}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        is EstadoPrestamo.Vencido -> {
            Text(
                text = "Vencido: ${estado.diasAtraso} días de atraso",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
