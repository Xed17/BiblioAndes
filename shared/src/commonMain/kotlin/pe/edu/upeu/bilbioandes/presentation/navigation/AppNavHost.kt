package pe.edu.upeu.bilbioandes.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pe.edu.upeu.bilbioandes.presentation.catalogo.CatalogoScreen
import pe.edu.upeu.bilbioandes.presentation.catalogo.CatalogoViewModel
import pe.edu.upeu.bilbioandes.presentation.detalle.DetalleLibroScreen
import pe.edu.upeu.bilbioandes.presentation.detalle.DetalleLibroViewModel
import pe.edu.upeu.bilbioandes.presentation.inicio.InicioScreen
import pe.edu.upeu.bilbioandes.presentation.inicio.InicioViewModel
import pe.edu.upeu.bilbioandes.presentation.main.MainViewModel
import pe.edu.upeu.bilbioandes.presentation.perfil.PerfilScreen
import pe.edu.upeu.bilbioandes.presentation.prestamos.PrestamosScreen
import pe.edu.upeu.bilbioandes.presentation.prestamos.PrestamosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    temaOscuro: Boolean,
    onCambiarTema: (Boolean) -> Unit,
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel = koinViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route
    val prestamosActivos by mainViewModel.prestamosActivosCount.collectAsState()


    val mostrarBottomBar = rutaActual in listOf(
        AppRoute.Inicio.route,
        AppRoute.Catalogo.route,
        AppRoute.Prestamos.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (rutaActual) {
                            AppRoute.Inicio.route -> "BiblioAndes"
                            AppRoute.Catalogo.route -> "Catálogo"
                            AppRoute.Prestamos.route -> "Mis Préstamos"
                            AppRoute.Perfil.route -> "Perfil"
                            else -> {
                                if (rutaActual?.startsWith("detalle_libro") == true) {
                                    "Detalle del Libro"
                                } else {
                                    "BiblioAndes"
                                }
                            }
                        }
                    )
                },
                actions = {
                    if (rutaActual == AppRoute.Inicio.route) {
                        IconButton(
                            onClick = {
                                navController.navigate(AppRoute.Perfil.route)
                            }
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Perfil"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (mostrarBottomBar) {
                BottomNavigationBar(
                    rutaActual = rutaActual,
                    prestamosActivos = prestamosActivos,
                    onNavigate = { ruta ->
                        navController.navigate(ruta) {
                            popUpTo(AppRoute.Inicio.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Inicio.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(AppRoute.Inicio.route) {
                val viewModel = koinViewModel<InicioViewModel>()
                InicioScreen(
                    viewModel = viewModel,
                    onIrACatalogo = {
                        navController.navigate(AppRoute.Catalogo.route) {
                            popUpTo(AppRoute.Inicio.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onIrAPrestamos = {
                        navController.navigate(AppRoute.Prestamos.route) {
                            popUpTo(AppRoute.Inicio.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(AppRoute.Catalogo.route) {
                val viewModel = koinViewModel<CatalogoViewModel>()
                CatalogoScreen(
                    viewModel = viewModel,
                    onLibroClick = { libroId ->
                        navController.navigate(
                            AppRoute.DetalleLibro.crearRoute(libroId)
                        )
                    }
                )
            }

            composable(
                route = AppRoute.DetalleLibro.route,
                arguments = listOf(
                    navArgument("libroId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val libroId = backStackEntry.arguments?.read {
                    getInt("libroId")
                } ?: 0
                val viewModel = koinViewModel<DetalleLibroViewModel>(
                    parameters = { parametersOf(libroId) }
                )
                DetalleLibroScreen(
                    viewModel = viewModel,
                    onVolver = { navController.popBackStack() }
                )
            }

            composable(AppRoute.Prestamos.route) {
                val viewModel = koinViewModel<PrestamosViewModel>()
                PrestamosScreen(viewModel = viewModel)
            }

            composable(AppRoute.Perfil.route) {
                PerfilScreen(
                    temaOscuro = temaOscuro,
                    onCambiarTema = onCambiarTema,
                    onVolver = { navController.popBackStack() }
                )
            }
        }
    }
}
