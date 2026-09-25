package pe.edu.upeu.bilbioandes.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import pe.edu.upeu.bilbioandes.domain.usecase.ObservarPrestamosUseCase
import pe.edu.upeu.bilbioandes.domain.usecase.SolicitarPrestamoUseCase

/**
 * ViewModel principal a nivel de navegación/Scaffold.
 *
 * Cumple con SC-B:
 * - Mantiene el conteo reactivo de préstamos activos en tiempo real para el Badge.
 * - Utiliza la función de dominio [SolicitarPrestamoUseCase.contarPrestamosActivos] (RN-01).
 */
class MainViewModel(
    observarPrestamos: ObservarPrestamosUseCase,
    solicitarPrestamoUseCase: SolicitarPrestamoUseCase
) : ViewModel() {

    val prestamosActivosCount: StateFlow<Int> = observarPrestamos()
        .map { prestamos ->
            solicitarPrestamoUseCase.contarPrestamosActivos(prestamos)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
}
