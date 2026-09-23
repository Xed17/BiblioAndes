package pe.edu.upeu.bilbioandes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.koin.compose.KoinApplication
import pe.edu.upeu.bilbioandes.di.appModule
import pe.edu.upeu.bilbioandes.presentation.navigation.AppNavHost
import pe.edu.upeu.bilbioandes.presentation.theme.BiblioAndesTheme

@Composable
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        var temaOscuro by rememberSaveable { mutableStateOf(false) }

        BiblioAndesTheme(darkTheme = temaOscuro) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AppNavHost(
                    temaOscuro = temaOscuro,
                    onCambiarTema = { temaOscuro = it }
                )
            }
        }
    }
}