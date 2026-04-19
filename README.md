# RetrofitLab — Unidad 5

App Android que consume la API pública [JSONPlaceholder](https://jsonplaceholder.typicode.com/)
usando Retrofit, OkHttp y Jetpack Compose.

---

## Setup

### Requisitos
- Android Studio Hedgehog o superior
- SDK mínimo: 26
- Kotlin 1.9.22

### Pasos
1. Clonar el repositorio.
2. Abrir el proyecto en Android Studio.
3. Esperar sincronización de Gradle.
4. Ejecutar en emulador o dispositivo físico con Android 8.0+.

No se requiere API key. La app usa `android.permission.INTERNET` declarado en el `AndroidManifest.xml`.

---

## Arquitectura

El proyecto sigue una separación en capas:

data/
remote/api/       → PostApi (interfaz Retrofit)
remote/dto/       → PostDto + mapper toDomain()
repository/       → PostRepositoryImpl
di/
NetworkModule     → configuración OkHttp + Retrofit
domain/
model/            → Post (modelo de dominio)
error/            → AppError (sealed class) + mappers
repository/       → PostRepository (interfaz)
presentation/
ui/               → PostsScreen, PostCard
viewmodel/        → PostsViewModel, PostsUiState

---

## Estados de la UI

| Estado | Descripción |
|--------|-------------|
| `Loading` | Muestra `CircularProgressIndicator` mientras se obtienen los datos |
| `Success` | Muestra la lista de posts en un `LazyColumn` con paginación |
| `Error` | Muestra el mensaje de error y botón "Reintentar" |
| `Empty` | Muestra mensaje cuando la API no retorna posts |

---

## Decisiones de diseño

### Interceptor de OkHttp
Se configuraron dos interceptores en `NetworkModule`:

- **`HttpLoggingInterceptor`** en nivel `BODY`: registra en Logcat cada request
  y response completos, incluyendo headers y cuerpo JSON. Útil para depuración
  sin necesidad de herramientas externas.
- **Interceptor de headers**: agrega automáticamente `Accept: application/json`
  y `X-App-Version: 1.0.0` a cada request, simulando un cliente autenticado
  sin duplicar lógica en cada llamada de la API.

### Mapeo de errores
`AppError` es una `sealed class` que representa cada falla posible de forma
explícita y tipada:

- `Network` — falla de conectividad (`IOException`), sin acceso a internet.
- `Unauthorized` — códigos 401/403, sesión inválida.
- `NotFound` — código 404, recurso inexistente.
- `Server` — códigos 500–599, falla del servidor.
- `Unknown` — cualquier otro error no contemplado.

La extensión `Throwable.toAppError()` centraliza la conversión desde excepciones
crudas de Retrofit/OkHttp hacia errores de dominio, desacoplando la capa de datos
de la presentación. `AppError.toMessage()` traduce cada caso a un mensaje legible
para el usuario.

### Mapper DTO → Dominio
`PostDto.toDomain()` aísla el modelo de red del modelo de dominio. El campo
`excerpt` trunca el `body` a 100 caracteres, evitando exponer datos crudos
del backend directamente en la UI.

### Paginación
`PostsViewModel` acumula los posts en `allPosts` y actualiza el `StateFlow`
con la lista completa en cada página, garantizando que "Cargar más" añada
sin reemplazar el contenido previo.