package pe.edu.upeu.bilbioandes.domain.usecase

import pe.edu.upeu.bilbioandes.domain.model.ResultadoPrestamo
import pe.edu.upeu.bilbioandes.domain.repository.BibliotecaRepository

class SolicitarPrestamoUseCase(
    private val repository: BibliotecaRepository
) {
    suspend operator fun invoke(
        libroId: Int
    ): ResultadoPrestamo {
        return repository.solicitarPrestamo(libroId)
    }
}
