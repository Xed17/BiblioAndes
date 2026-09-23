package pe.edu.upeu.bilbioandes.domain.repository

import pe.edu.upeu.bilbioandes.domain.model.Estudiante
import pe.edu.upeu.bilbioandes.domain.model.Libro
import pe.edu.upeu.bilbioandes.domain.model.Prestamo
import pe.edu.upeu.bilbioandes.domain.model.ResultadoPrestamo

interface BibliotecaRepository {

    suspend fun obtenerEstudiante(): Estudiante

    suspend fun obtenerCategorias(): List<String>

    suspend fun obtenerLibros(): List<Libro>

    suspend fun obtenerLibroPorId(id: Int): Libro?

    suspend fun obtenerPrestamos(): List<Prestamo>

    suspend fun solicitarPrestamo(libroId: Int): ResultadoPrestamo
}
