package pe.edu.upeu.bilbioandes.data.local

import pe.edu.upeu.bilbioandes.domain.model.EstadoPrestamo
import pe.edu.upeu.bilbioandes.domain.model.Estudiante
import pe.edu.upeu.bilbioandes.domain.model.Libro
import pe.edu.upeu.bilbioandes.domain.model.Prestamo

object DatosSimulados {

    val estudiante = Estudiante(
        nombre = "Carlos Mendoza",
        codigo = "202612345",
        carrera = "Ingeniería de Software",
        correo = "carlos.mendoza@biblioandes.edu.pe"
    )

    val libros = listOf(
        Libro(
            id = 1,
            titulo = "Clean Code",
            autor = "Robert C. Martin",
            anio = 2008,
            categoria = "Programación",
            sede = "Sede Central",
            ejemplaresDisponibles = 3
        ),
        Libro(
            id = 2,
            titulo = "Kotlin Multiplatform",
            autor = "María Torres",
            anio = 2024,
            categoria = "Programación",
            sede = "Sede Norte",
            ejemplaresDisponibles = 0
        ),
        Libro(
            id = 3,
            titulo = "Estructuras de Datos",
            autor = "Luis Herrera",
            anio = 2022,
            categoria = "Programación",
            sede = "Sede Central",
            ejemplaresDisponibles = 2
        ),
        Libro(
            id = 4,
            titulo = "Matemática Discreta",
            autor = "Ralph Grimaldi",
            anio = 2019,
            categoria = "Matemática",
            sede = "Sede Sur",
            ejemplaresDisponibles = 1
        ),
        Libro(
            id = 5,
            titulo = "Álgebra Lineal",
            autor = "David Lay",
            anio = 2020,
            categoria = "Matemática",
            sede = "Sede Central",
            ejemplaresDisponibles = 0
        ),
        Libro(
            id = 6,
            titulo = "Redes de Computadoras",
            autor = "Andrew Tanenbaum",
            anio = 2021,
            categoria = "Redes",
            sede = "Sede Norte",
            ejemplaresDisponibles = 4
        ),
        Libro(
            id = 7,
            titulo = "Seguridad Informática",
            autor = "William Stallings",
            anio = 2022,
            categoria = "Redes",
            sede = "Sede Central",
            ejemplaresDisponibles = 1
        ),
        Libro(
            id = 8,
            titulo = "Gestión de Proyectos",
            autor = "Harold Kerzner",
            anio = 2018,
            categoria = "Gestión",
            sede = "Sede Sur",
            ejemplaresDisponibles = 2
        ),
        Libro(
            id = 9,
            titulo = "Scrum: The Art of Doing Twice the Work",
            autor = "Jeff Sutherland",
            anio = 2015,
            categoria = "Gestión",
            sede = "Sede Central",
            ejemplaresDisponibles = 1
        ),
        Libro(
            id = 10,
            titulo = "Cien años de soledad",
            autor = "Gabriel García Márquez",
            anio = 1967,
            categoria = "Literatura",
            sede = "Sede Norte",
            ejemplaresDisponibles = 2
        ),
        Libro(
            id = 11,
            titulo = "La ciudad y los perros",
            autor = "Mario Vargas Llosa",
            anio = 1963,
            categoria = "Literatura",
            sede = "Sede Sur",
            ejemplaresDisponibles = 1
        ),
        Libro(
            id = 12,
            titulo = "Rayuela",
            autor = "Julio Cortázar",
            anio = 1963,
            categoria = "Literatura",
            sede = "Sede Central",
            ejemplaresDisponibles = 3
        )
    )

    val prestamos = mutableListOf(
        Prestamo(
            id = 1,
            libro = libros[0],
            fechaLimite = "2026-09-28",
            estado = EstadoPrestamo.Activo(diasRestantes = 5)
        ),
        Prestamo(
            id = 2,
            libro = libros[2],
            fechaLimite = "2026-10-02",
            estado = EstadoPrestamo.Activo(diasRestantes = 9)
        ),
        Prestamo(
            id = 3,
            libro = libros[5],
            fechaLimite = "2026-09-10",
            estado = EstadoPrestamo.Devuelto(
                fechaDevolucion = "2026-09-08"
            )
        ),
        Prestamo(
            id = 4,
            libro = libros[7],
            fechaLimite = "2026-09-05",
            estado = EstadoPrestamo.Devuelto(
                fechaDevolucion = "2026-09-04"
            )
        ),
        Prestamo(
            id = 5,
            libro = libros[9],
            fechaLimite = "2026-09-12",
            estado = EstadoPrestamo.Vencido(
                diasAtraso = 11
            )
        )
    )
}
