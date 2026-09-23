package pe.edu.upeu.bilbioandes.presentation.prestamos

import pe.edu.upeu.bilbioandes.domain.model.Prestamo
import pe.edu.upeu.bilbioandes.presentation.common.FiltroPrestamo
import pe.edu.upeu.bilbioandes.presentation.common.UiState

data class PrestamosState(
    val prestamos: List<Prestamo> = emptyList(),
    val filtro: FiltroPrestamo = FiltroPrestamo.TODOS,
    val estado: UiState<List<Prestamo>> = UiState.Loading
)
