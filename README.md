# BiblioAndes - Sistema de Gestión de Biblioteca Universitaria

Aplicación móvil desarrollada con **Kotlin Multiplatform (KMP)** y **Compose Multiplatform**, orientada a la consulta de catálogos y gestión de préstamos de libros para estudiantes universitarios.

El proyecto implementa una arquitectura limpia (**Clean Architecture**) con el patrón **MVVM** (Model-View-ViewModel), inyección de dependencias con **Koin**, flujo unidireccional de datos (**UDF**) y persistencia simulada en memoria sin dependencias de red o bases de datos externas.

---

## 🏛️ Decisiones de Arquitectura

El proyecto adopta los principios de **Clean Architecture** organizados en capas estrictamente desacopladas dentro del módulo compartido (`shared/commonMain`):

```
┌────────────────────────────────────────────────────────┐
│                   PRESENTATION                         │
│  (Compose Multiplatform, ViewModels, UiState, Theme)   │
└───────────────────────────┬────────────────────────────┘
                            │ usa
┌───────────────────────────▼────────────────────────────┐
│                      DOMAIN                            │
│    (Models, Use Cases, Repository Interfaces, RN)      │
│            * Núcleo puro sin frameworks *             │
└───────────────────────────▲────────────────────────────┘
                            │ implementa
┌───────────────────────────┴────────────────────────────┐
│                       DATA                             │
│   (In-Memory Fake Repository, DatosSimulados)          │
└────────────────────────────────────────────────────────┘
```

### 1. Capa de Dominio (`domain`) - El Núcleo del Negocio
* **Independencia absoluta:** No depende de librerías de UI, frameworks de Android/iOS ni detalles de infraestructura.
* **Casos de Uso (Interactors):** Cada caso de uso representa una única acción de negocio (`invoke()`), facilitando pruebas unitarias y legibilidad.
* **Reglas de Negocio Centralizadas:** Toda la lógica de validación de préstamos (RN-01 a RN-04) vive estrictamente en esta capa.
* **Modelos Fuertemente Tipados:** Empleo de `sealed class` para representar estados del dominio (`EstadoPrestamo`, `ResultadoPrestamo`), evitando el uso de cadenas de texto mágicas.

### 2. Capa de Presentación (`presentation`) - Interfaz y Estado
* **Compose Multiplatform:** UI compartida 100% declarativa para Android e iOS.
* **Patrón MVVM + UDF:** Los `ViewModels` exponen un único flujo inmutable `StateFlow<ScreenState>`. La UI emite eventos y reacciona a los cambios de estado.
* **Manejo de Estados de UI (`UiState<T>`):** Modelo sellado que representa `Loading`, `Success<T>` y `Error(message)` en todas las pantallas.
* **Simulación de Carga Asíncrona:** Se implementó un retraso controlado de 800 ms mediante corrutinas (`delay(800)`) dentro del `viewModelScope`, sin bloquear el hilo principal.
* **Navegación:** `navigation-compose` oficial de Jetpack / Compose Multiplatform con barra de navegación inferior (`NavigationBar`) y paso de argumentos tipados.
* **Diseño y Temas:** Cumplimiento de **Material Design 3**, con soporte para alternar entre **Tema Claro** y **Tema Oscuro** dinámicamente.

### 3. Capa de Datos (`data`) - Simulación en Memoria
* **Repositorio Simulado (`BibliotecaRepositoryFake`):** Implementa la interfaz `BibliotecaRepository` del dominio utilizando colecciones mutables en memoria (`mutableListOf`), sin dependencias de base de datos (Room, SQLDelight) ni servicios web (Ktor, Retrofit).
* **Catálogo Inicial:** 12 libros predefinidos en 5 categorías, de los cuales 2 no cuentan con ejemplares disponibles para probar validaciones.
* **Historial de Préstamos:** 5 préstamos iniciales (2 activos, 2 devueltos y 1 vencido).

### 4. Inyección de Dependencias (`di`)
* Implementado con **Koin 4.0** (`koin-core`, `koin-compose`, `koin-compose-viewmodel`).
* Módulo unificado `appModule` que provee el repositorio como `single`, los casos de uso como `factory`, y los `ViewModel` como `viewModel`.

---

## ⚖️ Reglas de Negocio (Implementación en Dominio)

Durante la defensa del proyecto se auditará la ubicación de las cuatro reglas de negocio. Todas viven en la capa de **Dominio**:

| Código | Regla de Negocio | Ubicación en Dominio | Función / Modelo Responsable |
| :---: | :--- | :--- | :--- |
| **RN-01** | Un estudiante no puede tener más de tres préstamos en estado Activo de forma simultánea. | `domain/usecase/SolicitarPrestamoUseCase.kt` | `validarLimitePrestamos(prestamos)` |
| **RN-02** | No se puede solicitar un libro cuyo número de ejemplares disponibles sea cero. | `domain/usecase/SolicitarPrestamoUseCase.kt` | `validarDisponibilidad(libro)` |
| **RN-03** | Todo préstamo dura siete días; si la fecha de devolución ya pasó, el préstamo se muestra como Vencido con sus días de atraso. | `domain/model/EstadoPrestamo.kt` | `sealed class EstadoPrestamo` (`Activo(diasRestantes=7)`, `Vencido(diasAtraso)`) |
| **RN-04** | Un estudiante con al menos un préstamo Vencido no puede solicitar un libro nuevo hasta regularizarlo. | `domain/usecase/SolicitarPrestamoUseCase.kt` | `validarPrestamoVencido(prestamos)` |

---

## 📁 Estructura de Paquetes del Proyecto

El código base se distribuye bajo el paquete raíz `pe.edu.upeu.bilbioandes`:

```text
BilbioAndes/
├── androidApp/                                      # Punto de entrada para Android
│   └── src/main/
│       ├── AndroidManifest.xml                      # Declaración de MainActivity y tema
│       └── kotlin/pe/edu/upeu/bilbioandes/
│           └── MainActivity.kt                      # ComponentActivity que invoca App()
│
├── iosApp/                                          # Punto de entrada para iOS
│   └── iosApp/
│       └── iOSApp.swift                             # Entrada SwiftUI que aloja ComposeUIViewController
│
├── shared/                                          # Módulo Multiplataforma compartido
│   └── src/
│       ├── androidMain/                             # Implementaciones específicas de Android
│       ├── iosMain/                                 # Entrypoint ComposeUIViewController para iOS
│       │   └── kotlin/pe/edu/upeu/bilbioandes/
│       │       └── MainViewController.kt
│       └── commonMain/kotlin/pe/edu/upeu/bilbioandes/
│           │
│           ├── App.kt                               # Composable raíz (KoinApplication + Surface)
│           │
│           ├── domain/                              # CAPA DE DOMINIO (Reglas puras)
│           │   ├── model/
│           │   │   ├── EstadoPrestamo.kt            # Sealed class: Activo, Devuelto, Vencido
│           │   │   ├── Estudiante.kt                # Entidad del estudiante
│           │   │   ├── Libro.kt                     # Entidad de libro y stock
│           │   │   ├── Prestamo.kt                  # Entidad de préstamo
│           │   │   └── ResultadoPrestamo.kt         # Sealed class: Exitoso, Rechazado(mensaje)
│           │   ├── repository/
│           │   │   └── BibliotecaRepository.kt      # Contrato/interfaz del repositorio
│           │   └── usecase/
│           │       ├── ObtenerCategoriasUseCase.kt  # Extracción de categorías únicas
│           │       ├── ObtenerEstudianteUseCase.kt  # Consulta de datos del alumno
│           │       ├── ObtenerLibroUseCase.kt       # Consulta individual por ID
│           │       ├── ObtenerLibrosUseCase.kt      # Listado completo de libros
│           │       ├── ObtenerPrestamosUseCase.kt   # Préstamos ordenados por fecha límite
│           │       └── SolicitarPrestamoUseCase.kt  # Orquestador con validaciones RN-01 a RN-04
│           │
│           ├── data/                                # CAPA DE DATOS (Simulación en memoria)
│           │   ├── local/
│           │   │   └── DatosSimulados.kt            # Semilla inicial (Estudiante, 12 libros, 5 préstamos)
│           │   └── repository/
│           │       └── BibliotecaRepositoryFake.kt  # Implementación en memoria del repositorio
│           │
│           ├── presentation/                        # CAPA DE PRESENTACIÓN (Compose UI & MVVM)
│           │   ├── common/
│           │   │   ├── FiltroPrestamo.kt            # Enum de filtros de historial (Todos, Activos...)
│           │   │   ├── StringUtils.kt               # Normalizador de búsqueda insensible a tildes
│           │   │   └── UiState.kt                   # Sealed interface: Loading, Success, Error
│           │   ├── theme/
│           │   │   ├── Theme.kt                     # BiblioAndesTheme (soporte Claro/Oscuro)
│           │   │   └── Type.kt                      # Tipografías Material 3
│           │   ├── navigation/
│           │   │   ├── AppNavHost.kt                # Scaffold principal y grafo NavHost
│           │   │   ├── AppRoute.kt                  # Definición de rutas y parámetros
│           │   │   └── BottomNavigationBar.kt       # Barra inferior de 3 destinos
│           │   ├── inicio/                          # RF-01: Pantalla de Inicio
│           │   │   ├── InicioScreen.kt
│           │   │   ├── InicioState.kt
│           │   │   └── InicioViewModel.kt
│           │   ├── catalogo/                        # RF-02: Catálogo con filtros y búsqueda
│           │   │   ├── CatalogoScreen.kt
│           │   │   ├── CatalogoState.kt
│           │   │   └── CatalogoViewModel.kt
│           │   ├── detalle/                         # RF-03: Ficha técnica y solicitud
│           │   │   ├── DetalleLibroScreen.kt
│           │   │   ├── DetalleLibroState.kt
│           │   │   └── DetalleLibroViewModel.kt
│           │   ├── prestamos/                       # RF-04: Historial con filtros por estado
│           │   │   ├── PrestamosScreen.kt
│           │   │   ├── PrestamosState.kt
│           │   │   └── PrestamosViewModel.kt
│           │   └── perfil/                          # RF-05: Perfil y cambio de tema
│           │       └── PerfilScreen.kt
│           │
│           └── di/
│               └── AppModule.kt                     # Definición de módulos de inyección Koin
│
└── gradle/
    └── libs.versions.toml                           # Catálogo de versiones y dependencias
```

---

## 🚀 Instrucciones de Ejecución

### Requisitos Previos
* **Java Development Kit (JDK):** Versión 17 o superior.
* **Android Studio:** Ladybug / Meerkat o superior con plugin de Kotlin y soporte Compose Multiplatform.
* **Android SDK:** Compile SDK 37, Min SDK 28.
* *(Opcional para iOS)*: macOS con **Xcode 15+** y CocoaPods instalado.

---

### Ejecución en Android

#### Opción A: Desde Android Studio (Recomendada)
1. Abre el proyecto en Android Studio (`File > Open...` y selecciona la carpeta raíz `BilbioAndes`).
2. Espera a que finalice la sincronización de Gradle (*Gradle Sync*).
3. Inicia un dispositivo virtual Android (AVD) o conecta un dispositivo físico con depuración USB activada.
4. En el selector de configuraciones de la barra superior, asegúrate de que esté seleccionado **`androidApp`**.
5. Presiona el botón verde de ejecución **Run (▶)** (o presiona `Shift + F10`).

#### Opción B: Desde la Línea de Comandos (Terminal)
Para compilar e instalar directamente en el dispositivo/emulador conectado:

* **En Windows (PowerShell / CMD):**
  ```powershell
  # Compilar el APK (en modo debug)
  .\gradlew.bat :androidApp:assembleDebug

  # Instalar y ejecutar directamente en el dispositivo conectado
  .\gradlew.bat :androidApp:installDebug
  ```

* **En macOS / Linux:**
  ```bash
  ./gradlew :androidApp:assembleDebug
  ./gradlew :androidApp:installDebug
  ```

---

### Ejecución en iOS

1. Asegúrate de compilar primero el framework compartido desde Gradle:
   ```bash
   ./gradlew :shared:embedAndSignAppleFrameworkForXcode
   ```
2. Abre la carpeta `iosApp` en Xcode:
   ```bash
   open iosApp/iosApp.xcodeproj
   ```
3. Selecciona un simulador de iPhone (por ejemplo, *iPhone 16*) y presiona **Cmd + R** para compilar y ejecutar.

---

## 👤 Datos de Prueba Preconfigurados

* **Estudiante activo:** Eduard Chambilla (Código: `202612345`, Carrera: `Ingeniería de Software`).
* **Catálogo:** 12 libros divididos en Programación, Matemática, Redes, Gestión y Literatura.
  * Libros sin ejemplares para probar **RN-02**: *Kotlin Multiplatform* (Sede Norte) y *Scrum Práctico* (Sede Central).
* **Préstamos predefinidos:**
  * 2 Activos (*Clean Code*, *Estructuras de Datos*).
  * 2 Devueltos (*Álgebra Lineal*, *Cálculo de una Variable*).
  * 1 Vencido con 11 días de atraso (*Redes de Computadoras* - permite validar el bloqueo de **RN-04**).