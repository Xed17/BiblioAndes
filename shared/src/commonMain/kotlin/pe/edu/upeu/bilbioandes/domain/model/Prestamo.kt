package pe.edu.upeu.bilbioandes.domain.model

data class Prestamo(
    val id: Int,
    val libro: Libro,
    val fechaLimite: String,
    val estado: EstadoPrestamo
)
