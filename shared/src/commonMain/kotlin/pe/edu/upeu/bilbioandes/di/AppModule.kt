package pe.edu.upeu.bilbioandes.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import pe.edu.upeu.bilbioandes.data.repository.BibliotecaRepositoryFake
import pe.edu.upeu.bilbioandes.domain.repository.BibliotecaRepository
import pe.edu.upeu.bilbioandes.domain.usecase.ObservarPrestamosUseCase
import pe.edu.upeu.bilbioandes.domain.usecase.ObtenerCategoriasUseCase
import pe.edu.upeu.bilbioandes.domain.usecase.ObtenerEstudianteUseCase
import pe.edu.upeu.bilbioandes.domain.usecase.ObtenerLibroUseCase
import pe.edu.upeu.bilbioandes.domain.usecase.ObtenerLibrosUseCase
import pe.edu.upeu.bilbioandes.domain.usecase.ObtenerPrestamosUseCase
import pe.edu.upeu.bilbioandes.domain.usecase.SolicitarPrestamoUseCase
import pe.edu.upeu.bilbioandes.presentation.catalogo.CatalogoViewModel
import pe.edu.upeu.bilbioandes.presentation.detalle.DetalleLibroViewModel
import pe.edu.upeu.bilbioandes.presentation.inicio.InicioViewModel
import pe.edu.upeu.bilbioandes.presentation.main.MainViewModel
import pe.edu.upeu.bilbioandes.presentation.prestamos.PrestamosViewModel

val appModule = module {

    // Repository
    single<BibliotecaRepository> {
        BibliotecaRepositoryFake()
    }

    // Use Cases
    factory { ObtenerLibrosUseCase(get()) }
    factory { ObtenerLibroUseCase(get()) }
    factory { ObtenerCategoriasUseCase(get()) }
    factory { ObtenerPrestamosUseCase(get()) }
    factory { ObservarPrestamosUseCase(get()) }
    factory { ObtenerEstudianteUseCase(get()) }
    factory { SolicitarPrestamoUseCase(get()) }

    // ViewModels
    viewModel {
        MainViewModel(
            observarPrestamos = get(),
            solicitarPrestamoUseCase = get()
        )
    }

    viewModel {
        InicioViewModel(
            obtenerEstudiante = get(),
            obtenerPrestamos = get()
        )
    }

    viewModel {
        CatalogoViewModel(
            obtenerLibros = get(),
            obtenerCategorias = get()
        )
    }

    viewModel { (libroId: Int) ->
        DetalleLibroViewModel(
            libroId = libroId,
            obtenerLibro = get(),
            obtenerPrestamos = get(),
            solicitarPrestamo = get()
        )
    }

    viewModel {
        PrestamosViewModel(
            obtenerPrestamos = get()
        )
    }
}

