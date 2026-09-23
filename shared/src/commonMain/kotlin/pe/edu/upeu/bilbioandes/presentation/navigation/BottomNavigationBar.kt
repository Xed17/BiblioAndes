package pe.edu.upeu.bilbioandes.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun BottomNavigationBar(
    rutaActual: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = rutaActual == AppRoute.Inicio.route,
            onClick = { onNavigate(AppRoute.Inicio.route) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )

        NavigationBarItem(
            selected = rutaActual == AppRoute.Catalogo.route,
            onClick = { onNavigate(AppRoute.Catalogo.route) },
            icon = { Icon(Icons.Default.Book, contentDescription = "Catálogo") },
            label = { Text("Catálogo") }
        )

        NavigationBarItem(
            selected = rutaActual == AppRoute.Prestamos.route,
            onClick = { onNavigate(AppRoute.Prestamos.route) },
            icon = { Icon(Icons.Default.Bookmark, contentDescription = "Préstamos") },
            label = { Text("Préstamos") }
        )
    }
}
