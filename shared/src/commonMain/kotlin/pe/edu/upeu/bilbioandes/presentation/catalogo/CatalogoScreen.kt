package pe.edu.upeu.bilbioandes.presentation.catalogo

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upeu.bilbioandes.domain.model.Libro
import pe.edu.upeu.bilbioandes.presentation.common.UiState

@Composable
fun CatalogoScreen(
    viewModel: CatalogoViewModel,
    onLibroClick: (Int) -> Unit
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
                    Button(onClick = { viewModel.cargarCatalogo() }) {
                        Text("Reintentar")
                    }
                }
            }
        }

        is UiState.Success -> {
            val librosFiltrados = viewModel.obtenerLibrosFiltrados()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Barra de búsqueda
                OutlinedTextField(
                    value = state.textoBusqueda,
                    onValueChange = { viewModel.cambiarBusqueda(it) },
                    label = { Text("Buscar por título o autor") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Filtros de categoría
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.categoriaSeleccionada == null,
                        onClick = { viewModel.cambiarCategoria(null) },
                        label = { Text("Todas") }
                    )
                    state.categorias.forEach { categoria ->
                        FilterChip(
                            selected = state.categoriaSeleccionada == categoria,
                            onClick = { viewModel.cambiarCategoria(categoria) },
                            label = { Text(categoria) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón para simular error (para la defensa)
                TextButton(
                    onClick = { viewModel.alternarError() }
                ) {
                    Text(
                        text = "⚠ Simular error",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                if (librosFiltrados.isEmpty()) {
                    // Estado vacío
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron libros para esta búsqueda.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    // Lista de libros
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(librosFiltrados) { libro ->
                            LibroCard(
                                libro = libro,
                                onClick = { onLibroClick(libro.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LibroCard(
    libro: Libro,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = libro.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = libro.autor,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = libro.categoria,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = libro.sede,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (libro.ejemplaresDisponibles > 0)
                    "Disponibles: ${libro.ejemplaresDisponibles}"
                else
                    "Sin ejemplares",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = if (libro.ejemplaresDisponibles > 0)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )
        }
    }
}
