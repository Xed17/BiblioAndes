package pe.edu.upeu.bilbioandes.domain.usecase

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.bilbioandes.domain.model.Prestamo
import pe.edu.upeu.bilbioandes.domain.repository.BibliotecaRepository

/**
 * Caso de uso: Observar el flujo reactivo de préstamos en tiempo real.
 */
class ObservarPrestamosUseCase(
    private val repository: BibliotecaRepository
) {
    operator fun invoke(): Flow<List<Prestamo>> {
        return repository.observarPrestamos()
    }
}
