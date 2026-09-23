package pe.edu.upeu.bilbioandes.domain.model

/**
 * Modela el estado de un préstamo de libro (Capa de Dominio).
 *
 * Cumple con:
 * - RN-03: Todo préstamo dura siete días; si la fecha de devolución ya pasó,
 *   el préstamo se modela con el estado Vencido(diasAtraso), no como simple texto.
 */
sealed class EstadoPrestamo {

    data class Activo(
        val diasRestantes: Int
    ) : EstadoPrestamo()

    data class Devuelto(
        val fechaDevolucion: String
    ) : EstadoPrestamo()

    data class Vencido(
        val diasAtraso: Int
    ) : EstadoPrestamo()
}
