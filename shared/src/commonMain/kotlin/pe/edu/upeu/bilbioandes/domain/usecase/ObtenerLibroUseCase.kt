package pe.edu.upeu.bilbioandes.domain.usecase

import pe.edu.upeu.bilbioandes.domain.model.Libro
import pe.edu.upeu.bilbioandes.domain.repository.BibliotecaRepository

class ObtenerLibroUseCase(
    private val repository: BibliotecaRepository
) {
    suspend operator fun invoke(id: Int): Libro? {
        return repository.obtenerLibroPorId(id)
    }
}
