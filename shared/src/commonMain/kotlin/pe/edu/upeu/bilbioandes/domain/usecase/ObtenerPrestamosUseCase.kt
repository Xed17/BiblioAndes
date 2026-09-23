package pe.edu.upeu.bilbioandes.domain.usecase

import pe.edu.upeu.bilbioandes.domain.model.Prestamo
import pe.edu.upeu.bilbioandes.domain.repository.BibliotecaRepository

class ObtenerPrestamosUseCase(
    private val repository: BibliotecaRepository
) {
    suspend operator fun invoke(): List<Prestamo> {
        return repository.obtenerPrestamos()
            .sortedBy { it.fechaLimite }
    }
}
