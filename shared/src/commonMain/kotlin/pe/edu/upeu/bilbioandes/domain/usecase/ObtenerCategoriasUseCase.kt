package pe.edu.upeu.bilbioandes.domain.usecase

import pe.edu.upeu.bilbioandes.domain.repository.BibliotecaRepository

class ObtenerCategoriasUseCase(
    private val repository: BibliotecaRepository
) {
    suspend operator fun invoke(): List<String> {
        return repository.obtenerCategorias()
    }
}
