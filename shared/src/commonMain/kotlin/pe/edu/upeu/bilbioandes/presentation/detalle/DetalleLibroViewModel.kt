package pe.edu.upeu.bilbioandes.presentation.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.bilbioandes.domain.model.ResultadoPrestamo
import pe.edu.upeu.bilbioandes.domain.usecase.ObtenerLibroUseCase
import pe.edu.upeu.bilbioandes.domain.usecase.ObtenerPrestamosUseCase
import pe.edu.upeu.bilbioandes.domain.usecase.SolicitarPrestamoUseCase
import pe.edu.upeu.bilbioandes.presentation.common.UiState

class DetalleLibroViewModel(
    private val libroId: Int,
    private val obtenerLibro: ObtenerLibroUseCase,
    private val obtenerPrestamos: ObtenerPrestamosUseCase,
    private val solicitarPrestamo: SolicitarPrestamoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleLibroState())
    val uiState: StateFlow<DetalleLibroState> = _uiState.asStateFlow()

    init {
        cargarLibro()
    }

    private fun cargarLibro() {
        viewModelScope.launch {
            _uiState.update { it.copy(estado = UiState.Loading) }

            delay(800)

            try {
                val libro = obtenerLibro(libroId)
                val prestamos = obtenerPrestamos()
                val limiteAlcanzado = solicitarPrestamo.haAlcanzadoLimitePrestamos(prestamos)

                if (libro != null) {
                    _uiState.update {
                        it.copy(
                            libro = libro,
                            estado = UiState.Success(libro),
                            limitePrestamosAlcanzado = limiteAlcanzado
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            estado = UiState.Error(
                                "Libro no encontrado"
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        estado = UiState.Error(
                            "Error al cargar el libro"
                        )
                    )
                }
            }
        }
    }

    fun mostrarDialogoConfirmacion() {
        _uiState.update { it.copy(mostrarDialogo = true) }
    }

    fun ocultarDialogo() {
        _uiState.update { it.copy(mostrarDialogo = false) }
    }

    fun solicitarPrestamoLibro() {
        viewModelScope.launch {
            _uiState.update { it.copy(mostrarDialogo = false) }

            val resultado = solicitarPrestamo(libroId)

            val mensaje = when (resultado) {
                is ResultadoPrestamo.Exitoso ->
                    "¡Préstamo solicitado exitosamente!"
                is ResultadoPrestamo.Rechazado ->
                    resultado.mensaje
            }

            val libroActualizado = obtenerLibro(libroId)
            val prestamosActualizados = obtenerPrestamos()
            val limiteAlcanzado = solicitarPrestamo.haAlcanzadoLimitePrestamos(prestamosActualizados)

            _uiState.update {
                it.copy(
                    libro = libroActualizado ?: it.libro,
                    resultadoPrestamo = mensaje,
                    limitePrestamosAlcanzado = limiteAlcanzado
                )
            }
        }
    }

    fun limpiarResultado() {
        _uiState.update { it.copy(resultadoPrestamo = null) }
    }
}
