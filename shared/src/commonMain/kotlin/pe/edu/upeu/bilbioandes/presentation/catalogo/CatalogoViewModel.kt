package pe.edu.upeu.bilbioandes.presentation.catalogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.bilbioandes.domain.usecase.ObtenerCategoriasUseCase
import pe.edu.upeu.bilbioandes.domain.usecase.ObtenerLibrosUseCase
import pe.edu.upeu.bilbioandes.presentation.common.UiState
import pe.edu.upeu.bilbioandes.presentation.common.normalizar

class CatalogoViewModel(
    private val obtenerLibros: ObtenerLibrosUseCase,
    private val obtenerCategorias: ObtenerCategoriasUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogoState())
    val uiState: StateFlow<CatalogoState> = _uiState.asStateFlow()

    // Bandera para simular error durante la defensa
    private var simularError = false

    init {
        cargarCatalogo()
    }

    fun cargarCatalogo() {
        viewModelScope.launch {
            _uiState.update { it.copy(estado = UiState.Loading) }

            delay(800)

            if (simularError) {
                _uiState.update {
                    it.copy(
                        estado = UiState.Error(
                            "No se pudo cargar el catálogo"
                        )
                    )
                }
                return@launch
            }

            try {
                val libros = obtenerLibros()
                val categorias = obtenerCategorias()

                _uiState.update {
                    it.copy(
                        libros = libros,
                        categorias = categorias,
                        estado = UiState.Success(libros)
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        estado = UiState.Error(
                            "No se pudo cargar el catálogo"
                        )
                    )
                }
            }
        }
    }

    fun cambiarBusqueda(texto: String) {
        _uiState.update {
            it.copy(textoBusqueda = texto)
        }
    }

    fun cambiarCategoria(categoria: String?) {
        _uiState.update {
            it.copy(categoriaSeleccionada = categoria)
        }
    }

    // Botón para simular error durante la defensa
    fun alternarError() {
        simularError = !simularError
        cargarCatalogo()
    }

    fun obtenerLibrosFiltrados(): List<pe.edu.upeu.bilbioandes.domain.model.Libro> {
        val state = _uiState.value
        return state.libros.filter { libro ->
            val coincideCategoria =
                state.categoriaSeleccionada == null ||
                    libro.categoria == state.categoriaSeleccionada

            val contenido = (libro.titulo + " " + libro.autor).normalizar()
            val coincideTexto = contenido.contains(
                state.textoBusqueda.normalizar()
            )

            coincideCategoria && coincideTexto
        }
    }
}
