package pe.edu.upeu.bilbioandes.data.repository

import pe.edu.upeu.bilbioandes.data.local.DatosSimulados
import pe.edu.upeu.bilbioandes.domain.model.EstadoPrestamo
import pe.edu.upeu.bilbioandes.domain.model.Estudiante
import pe.edu.upeu.bilbioandes.domain.model.Libro
import pe.edu.upeu.bilbioandes.domain.model.Prestamo
import pe.edu.upeu.bilbioandes.domain.model.ResultadoPrestamo
import pe.edu.upeu.bilbioandes.domain.repository.BibliotecaRepository

class BibliotecaRepositoryFake : BibliotecaRepository {

    private val estudiante = DatosSimulados.estudiante
    private val libros = DatosSimulados.libros.toMutableList()
    private val prestamos = DatosSimulados.prestamos.toMutableList()

    override suspend fun obtenerEstudiante(): Estudiante {
        return estudiante
    }

    override suspend fun obtenerCategorias(): List<String> {
        return libros
            .map { it.categoria }
            .distinct()
            .sorted()
    }

    override suspend fun obtenerLibros(): List<Libro> {
        return libros.toList()
    }

    override suspend fun obtenerLibroPorId(id: Int): Libro? {
        return libros.firstOrNull { it.id == id }
    }

    override suspend fun obtenerPrestamos(): List<Prestamo> {
        return prestamos.toList()
    }

    override suspend fun solicitarPrestamo(
        libroId: Int
    ): ResultadoPrestamo {
        val libro = libros.firstOrNull { it.id == libroId }
            ?: return ResultadoPrestamo.Rechazado(
                "El libro no existe"
            )

        val prestamosActivos = prestamos.count {
            it.estado is EstadoPrestamo.Activo
        }

        if (prestamosActivos >= 3) {
            return ResultadoPrestamo.Rechazado(
                "No puedes tener más de tres préstamos activos"
            )
        }

        if (libro.ejemplaresDisponibles == 0) {
            return ResultadoPrestamo.Rechazado(
                "No hay ejemplares disponibles"
            )
        }

        val tieneVencido = prestamos.any {
            it.estado is EstadoPrestamo.Vencido
        }

        if (tieneVencido) {
            return ResultadoPrestamo.Rechazado(
                "Debes regularizar tu préstamo vencido"
            )
        }

        val nuevoPrestamo = Prestamo(
            id = (prestamos.maxOfOrNull { it.id } ?: 0) + 1,
            libro = libro.copy(
                ejemplaresDisponibles =
                    libro.ejemplaresDisponibles - 1
            ),
            fechaLimite = "2026-10-01",
            estado = EstadoPrestamo.Activo(
                diasRestantes = 7 // RN-03: Todo préstamo dura siete días
            )
        )

        prestamos.add(nuevoPrestamo)

        // Actualizar el libro en la lista para reflejar el cambio de ejemplares
        val index = libros.indexOfFirst { it.id == libroId }
        if (index != -1) {
            libros[index] = libro.copy(
                ejemplaresDisponibles = libro.ejemplaresDisponibles - 1
            )
        }

        return ResultadoPrestamo.Exitoso
    }
}
