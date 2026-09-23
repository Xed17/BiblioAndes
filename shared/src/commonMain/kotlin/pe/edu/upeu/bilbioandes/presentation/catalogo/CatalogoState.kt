package pe.edu.upeu.bilbioandes.presentation.catalogo

import pe.edu.upeu.bilbioandes.domain.model.Libro
import pe.edu.upeu.bilbioandes.presentation.common.UiState

data class CatalogoState(
    val libros: List<Libro> = emptyList(),
    val categorias: List<String> = emptyList(),
    val categoriaSeleccionada: String? = null,
    val textoBusqueda: String = "",
    val estado: UiState<List<Libro>> = UiState.Loading
)
