package pe.edu.upeu.bilbioandes.domain.model

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
