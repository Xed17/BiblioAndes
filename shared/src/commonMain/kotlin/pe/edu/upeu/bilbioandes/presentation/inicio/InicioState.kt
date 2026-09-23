package pe.edu.upeu.bilbioandes.presentation.inicio

import pe.edu.upeu.bilbioandes.domain.model.Estudiante
import pe.edu.upeu.bilbioandes.domain.model.Prestamo
import pe.edu.upeu.bilbioandes.presentation.common.UiState

data class InicioState(
    val estudiante: Estudiante? = null,
    val prestamoProximo: Prestamo? = null,
    val estado: UiState<Unit> = UiState.Loading
)
