package pe.edu.upeu.bilbioandes.domain.usecase

import pe.edu.upeu.bilbioandes.domain.model.Estudiante
import pe.edu.upeu.bilbioandes.domain.repository.BibliotecaRepository

class ObtenerEstudianteUseCase(
    private val repository: BibliotecaRepository
) {
    suspend operator fun invoke(): Estudiante {
        return repository.obtenerEstudiante()
    }
}
