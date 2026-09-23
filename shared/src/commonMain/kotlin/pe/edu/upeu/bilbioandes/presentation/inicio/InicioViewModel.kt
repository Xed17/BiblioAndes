package pe.edu.upeu.bilbioandes.presentation.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.bilbioandes.domain.model.EstadoPrestamo
import pe.edu.upeu.bilbioandes.domain.usecase.ObtenerEstudianteUseCase
import pe.edu.upeu.bilbioandes.domain.usecase.ObtenerPrestamosUseCase
import pe.edu.upeu.bilbioandes.presentation.common.UiState

class InicioViewModel(
    private val obtenerEstudiante: ObtenerEstudianteUseCase,
    private val obtenerPrestamos: ObtenerPrestamosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InicioState())
    val uiState: StateFlow<InicioState> = _uiState.asStateFlow()

    init {
        cargarInicio()
    }

    fun cargarInicio() {
        viewModelScope.launch {
            _uiState.update { it.copy(estado = UiState.Loading) }

            delay(800)

            try {
                val estudiante = obtenerEstudiante()
                val prestamos = obtenerPrestamos()

                val prestamoProximo = prestamos
                    .filter { it.estado is EstadoPrestamo.Activo }
                    .minByOrNull { it.fechaLimite }

                _uiState.update {
                    it.copy(
                        estudiante = estudiante,
                        prestamoProximo = prestamoProximo,
                        estado = UiState.Success(Unit)
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        estado = UiState.Error(
                            "No se pudo cargar la información"
                        )
                    )
                }
            }
        }
    }
}
