package pe.edu.upeu.bilbioandes.domain.usecase

import pe.edu.upeu.bilbioandes.domain.model.Libro
import pe.edu.upeu.bilbioandes.domain.repository.BibliotecaRepository

class ObtenerLibrosUseCase(
    private val repository: BibliotecaRepository
) {
    suspend operator fun invoke(): List<Libro> {
        return repository.obtenerLibros()
    }
}
