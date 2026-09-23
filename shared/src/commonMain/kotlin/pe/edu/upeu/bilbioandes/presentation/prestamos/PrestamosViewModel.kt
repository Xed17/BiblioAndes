package pe.edu.upeu.bilbioandes.presentation.prestamos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.bilbioandes.domain.model.EstadoPrestamo
import pe.edu.upeu.bilbioandes.domain.model.Prestamo
import pe.edu.upeu.bilbioandes.domain.usecase.ObtenerPrestamosUseCase
import pe.edu.upeu.bilbioandes.presentation.common.FiltroPrestamo
import pe.edu.upeu.bilbioandes.presentation.common.UiState

class PrestamosViewModel(
    private val obtenerPrestamos: ObtenerPrestamosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamosState())
    val uiState: StateFlow<PrestamosState> = _uiState.asStateFlow()

    init {
        cargarPrestamos()
    }

    fun cargarPrestamos() {
        viewModelScope.launch {
            _uiState.update { it.copy(estado = UiState.Loading) }

            delay(800)

            try {
                val prestamos = obtenerPrestamos()

                _uiState.update {
                    it.copy(
                        prestamos = prestamos,
                        estado = UiState.Success(prestamos)
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        estado = UiState.Error(
                            "No se pudieron cargar los préstamos"
                        )
                    )
                }
            }
        }
    }

    fun cambiarFiltro(filtro: FiltroPrestamo) {
        _uiState.update { it.copy(filtro = filtro) }
    }

    fun obtenerPrestamosFiltrados(): List<Prestamo> {
        val state = _uiState.value
        return when (state.filtro) {
            FiltroPrestamo.TODOS ->
                state.prestamos

            FiltroPrestamo.ACTIVOS ->
                state.prestamos.filter {
                    it.estado is EstadoPrestamo.Activo
                }

            FiltroPrestamo.DEVUELTOS ->
                state.prestamos.filter {
                    it.estado is EstadoPrestamo.Devuelto
                }

            FiltroPrestamo.VENCIDOS ->
                state.prestamos.filter {
                    it.estado is EstadoPrestamo.Vencido
                }
        }.sortedBy { it.fechaLimite }
    }
}
