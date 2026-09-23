package pe.edu.upeu.bilbioandes.domain.model

sealed class ResultadoPrestamo {
    data object Exitoso : ResultadoPrestamo()
    data class Rechazado(val mensaje: String) : ResultadoPrestamo()
}
