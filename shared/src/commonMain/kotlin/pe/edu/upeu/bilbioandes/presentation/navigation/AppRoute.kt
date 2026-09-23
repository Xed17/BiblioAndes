package pe.edu.upeu.bilbioandes.presentation.navigation

sealed class AppRoute(val route: String) {
    data object Inicio : AppRoute("inicio")
    data object Catalogo : AppRoute("catalogo")
    data object Prestamos : AppRoute("prestamos")
    data object Perfil : AppRoute("perfil")

    data object DetalleLibro : AppRoute("detalle_libro/{libroId}") {
        fun crearRoute(libroId: Int): String {
            return "detalle_libro/$libroId"
        }
    }
}
