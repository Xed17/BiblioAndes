package pe.edu.upeu.bilbioandes.presentation.catalogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.bilbioandes.domain.model.Libro
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

                _uiState.update { actual ->
                    val filtrados = calcularLibrosFiltrados(
                        libros = libros,
                        categoria = actual.categoriaSeleccionada,
                        texto = actual.textoBusqueda,
                        soloDisponibles = actual.soloDisponibles
                    )
                    actual.copy(
                        libros = libros,
                        librosFiltrados = filtrados,
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
        _uiState.update { actual ->
            actual.copy(
                textoBusqueda = texto,
                librosFiltrados = calcularLibrosFiltrados(
                    libros = actual.libros,
                    categoria = actual.categoriaSeleccionada,
                    texto = texto,
                    soloDisponibles = actual.soloDisponibles
                )
            )
        }
    }

    fun cambiarCategoria(categoria: String?) {
        _uiState.update { actual ->
            actual.copy(
                categoriaSeleccionada = categoria,
                librosFiltrados = calcularLibrosFiltrados(
                    libros = actual.libros,
                    categoria = categoria,
                    texto = actual.textoBusqueda,
                    soloDisponibles = actual.soloDisponibles
                )
            )
        }
    }

    /**
     * Solicitud de Cambio SC-A:
     * Alterna el filtro de «Solo disponibles» y recalcula los libros filtrados.
     */
    fun alternarSoloDisponibles() {
        _uiState.update { actual ->
            val nuevoSoloDisponibles = !actual.soloDisponibles
            actual.copy(
                soloDisponibles = nuevoSoloDisponibles,
                librosFiltrados = calcularLibrosFiltrados(
                    libros = actual.libros,
                    categoria = actual.categoriaSeleccionada,
                    texto = actual.textoBusqueda,
                    soloDisponibles = nuevoSoloDisponibles
                )
            )
        }
    }

    // Botón para simular error durante la defensa
    fun alternarError() {
        simularError = !simularError
        cargarCatalogo()
    }

    /**
     * Lógica central de filtrado (SC-A):
     * Combina categoría, búsqueda de texto (insensible a tildes) y disponibilidad de ejemplares.
     * Se resuelve en el ViewModel, nunca en el Composable.
     */
    private fun calcularLibrosFiltrados(
        libros: List<Libro>,
        categoria: String?,
        texto: String,
        soloDisponibles: Boolean
    ): List<Libro> {
        val textoNorm = texto.normalizar()
        return libros.filter { libro ->
            val coincideCategoria =
                categoria == null || libro.categoria == categoria

            val contenido = (libro.titulo + " " + libro.autor).normalizar()
            val coincideTexto = contenido.contains(textoNorm)

            val coincideDisponibilidad =
                !soloDisponibles || libro.ejemplaresDisponibles > 0

            coincideCategoria && coincideTexto && coincideDisponibilidad
        }
    }

    fun obtenerLibrosFiltrados(): List<Libro> {
        return _uiState.value.librosFiltrados
    }
}
