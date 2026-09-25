package pe.edu.upeu.bilbioandes.domain.usecase

import pe.edu.upeu.bilbioandes.domain.model.EstadoPrestamo
import pe.edu.upeu.bilbioandes.domain.model.Libro
import pe.edu.upeu.bilbioandes.domain.model.Prestamo
import pe.edu.upeu.bilbioandes.domain.model.ResultadoPrestamo
import pe.edu.upeu.bilbioandes.domain.repository.BibliotecaRepository

/**
 * Caso de uso: Solicitar préstamo de libro.
 *
 * Contiene la implementación central de las Reglas de Negocio en la CAPA DE DOMINIO:
 * - RN-01: Máximo de 3 préstamos activos simultáneos.
 * - RN-02: Disponibilidad de ejemplares mayor a 0.
 * - RN-03: Duración de préstamo (7 días) y modelado de vencimiento (EstadoPrestamo).
 * - RN-04: Bloqueo de solicitudes si el estudiante tiene préstamos vencidos.
 */
class SolicitarPrestamoUseCase(
    private val repository: BibliotecaRepository
) {
    suspend operator fun invoke(
        libroId: Int
    ): ResultadoPrestamo {
        val libro = repository.obtenerLibroPorId(libroId)
            ?: return ResultadoPrestamo.Rechazado("El libro no existe")

        val prestamos = repository.obtenerPrestamos()

        // 1. RN-04: Bloquear solicitud si tiene al menos un préstamo vencido
        val errorVencido = validarPrestamoVencido(prestamos)
        if (errorVencido != null) {
            return ResultadoPrestamo.Rechazado(errorVencido)
        }

        // 2. RN-01: Bloquear si ya alcanzó el límite de 3 préstamos activos
        val errorLimite = validarLimitePrestamos(prestamos)
        if (errorLimite != null) {
            return ResultadoPrestamo.Rechazado(errorLimite)
        }

        // 3. RN-02: Bloquear si no hay ejemplares disponibles (<= 0)
        val errorDisponibilidad = validarDisponibilidad(libro)
        if (errorDisponibilidad != null) {
            return ResultadoPrestamo.Rechazado(errorDisponibilidad)
        }

        // Si pasa todas las reglas de negocio del dominio, se persiste el préstamo
        return repository.solicitarPrestamo(libroId)
    }

    companion object {
        const val MAXIMO_PRESTAMOS_ACTIVOS = 3
    }

    /**
     * RN-01: Cuenta el número de préstamos en estado Activo de un estudiante.
     */
    fun contarPrestamosActivos(prestamos: List<Prestamo>): Int {
        return prestamos.count { it.estado is EstadoPrestamo.Activo }
    }

    /**
     * RN-01: Determina si el estudiante ha alcanzado el límite máximo permitido de préstamos activos (3).
     */
    fun haAlcanzadoLimitePrestamos(prestamos: List<Prestamo>): Boolean {
        return contarPrestamosActivos(prestamos) >= MAXIMO_PRESTAMOS_ACTIVOS
    }

    /**
     * RN-01: Un estudiante no puede tener más de tres préstamos en estado Activo de forma simultánea.
     */
    fun validarLimitePrestamos(prestamos: List<Prestamo>): String? {
        return if (haAlcanzadoLimitePrestamos(prestamos)) {
            "El estudiante ya tiene tres préstamos activos"
        } else {
            null
        }
    }

    /**
     * RN-02: No se puede solicitar un libro cuyo número de ejemplares disponibles sea cero.
     */
    fun validarDisponibilidad(libro: Libro): String? {
        return if (libro.ejemplaresDisponibles <= 0) {
            "No hay ejemplares disponibles"
        } else {
            null
        }
    }

    /**
     * RN-04: Un estudiante con al menos un préstamo Vencido no puede solicitar un libro nuevo hasta regularizarlo.
     */
    fun validarPrestamoVencido(prestamos: List<Prestamo>): String? {
        val tieneVencido = prestamos.any { it.estado is EstadoPrestamo.Vencido }
        return if (tieneVencido) {
            "No puedes solicitar otro libro hasta regularizar tu préstamo vencido"
        } else {
            null
        }
    }
}

