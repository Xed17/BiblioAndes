package pe.edu.upeu.bilbioandes.presentation.detalle

import pe.edu.upeu.bilbioandes.domain.model.Libro
import pe.edu.upeu.bilbioandes.presentation.common.UiState

data class DetalleLibroState(
    val libro: Libro? = null,
    val estado: UiState<Libro> = UiState.Loading,
    val resultadoPrestamo: String? = null,
    val mostrarDialogo: Boolean = false,
    val limitePrestamosAlcanzado: Boolean = false
)

