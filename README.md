# FabrikApp - Sistema de Gestión Industrial

## Descripción
FabrikApp es una aplicación Android moderna para la gestión integral de procesos industriales, desarrollada con las mejores prácticas de desarrollo móvil.

## Características Principales

### 📦 Gestión de Inventario
- Control de insumos y materias primas
- Seguimiento de stock en tiempo real
- Alertas de stock bajo
- Gestión de proveedores

### 🧪 Gestión de Fórmulas
- Creación y edición de fórmulas de producción
- Cálculo automático de costos
- Control de versiones de fórmulas
- Integración con inventario

### 🏭 Control de Producción
- Registro de lotes de producción
- Seguimiento de eficiencia
- Historial detallado de producción
- Cálculo de costos de producción

### 💼 Gestión Comercial
- Registro de ventas
- Control de stock de productos terminados
- Balance financiero
- Pedidos a proveedores

### 📊 Analíticas y Reportes
- Dashboard con métricas en tiempo real
- Historial de producción
- Reportes de ventas
- Análisis de costos

## Arquitectura Técnica

### Stack Tecnológico
- **Lenguaje**: Kotlin
- **UI Framework**: Jetpack Compose + Material 3
- **Arquitectura**: MVVM + Clean Architecture
- **Inyección de Dependencias**: Hilt
- **Base de Datos**: Room
- **Navegación**: Navigation Compose
- **Estado**: StateFlow + Coroutines

### Estructura del Proyecto
```
app/src/main/java/com/fjrh/FabrikApp/
├── data/                    # Capa de datos
│   ├── local/              # Base de datos local
│   │   ├── entity/         # Entidades de Room
│   │   ├── dao/            # Data Access Objects
│   │   ├── repository/     # Repositorios
│   │   └── service/        # Servicios locales
├── domain/                 # Capa de dominio
│   ├── model/              # Modelos de dominio
│   ├── usecase/            # Casos de uso
│   └── service/            # Servicios de dominio
├── ui/                     # Capa de presentación
│   ├── screens/            # Pantallas de la aplicación
│   ├── components/         # Componentes reutilizables
│   ├── viewmodel/          # ViewModels
│   ├── navigation/         # Navegación
│   └── theme/              # Temas y estilos
└── di/                     # Inyección de dependencias
```

## Configuración del Proyecto

### Requisitos
- Android Studio Hedgehog | 2023.1.1 o superior
- Kotlin 1.9.0 o superior
- Android SDK 35
- Gradle 8.0 o superior

### Instalación
1. Clona el repositorio
2. Abre el proyecto en Android Studio
3. Sincroniza el proyecto con Gradle
4. Ejecuta la aplicación en un dispositivo o emulador

### Dependencias Principales
```gradle
// Compose
implementation platform('androidx.compose:compose-bom:2024.05.00')
implementation 'androidx.compose.ui:ui'
implementation 'androidx.compose.material3:material3'

// Navigation
implementation 'androidx.navigation:navigation-compose:2.7.7'

// Hilt
implementation "com.google.dagger:hilt-android:2.48"
implementation "androidx.hilt:hilt-navigation-compose:1.2.0"

// Room
implementation "androidx.room:room-runtime:2.6.1"
implementation "androidx.room:room-ktx:2.6.1"

// DataStore
implementation "androidx.datastore:datastore-preferences:1.0.0"
```

## Funcionalidades Detalladas

### 1. Gestión de Inventario
- **Agregar Insumos**: Registro de nuevos insumos con información completa
- **Editar Stock**: Actualización de cantidades disponibles
- **Alertas**: Notificaciones automáticas de stock bajo
- **Proveedores**: Gestión de información de proveedores

### 2. Fórmulas de Producción
- **Crear Fórmulas**: Definición de recetas con ingredientes y proporciones
- **Editar Fórmulas**: Modificación de fórmulas existentes
- **Cálculo de Costos**: Estimación automática de costos de producción
- **Control de Versiones**: Seguimiento de cambios en fórmulas

### 3. Control de Producción
- **Iniciar Producción**: Creación de lotes de producción
- **Seguimiento**: Monitoreo en tiempo real del proceso
- **Historial**: Registro completo de todas las producciones
- **Eficiencia**: Análisis de rendimiento y costos

### 4. Gestión Comercial
- **Ventas**: Registro de transacciones comerciales
- **Stock de Productos**: Control de inventario de productos terminados
- **Balance**: Análisis financiero y de rentabilidad
- **Pedidos**: Gestión de compras a proveedores

## Paleta de Colores

La aplicación utiliza una paleta de colores industrial moderna:

- **Azul Principal**: `#1E3A8A` - Color principal de la marca
- **Azul Oscuro**: `#1E40AF` - Para elementos secundarios
- **Azul Claro**: `#3B82F6` - Para acentos y highlights
- **Naranja Industrial**: `#F97316` - Para alertas y acciones importantes
- **Verde Industrial**: `#059669` - Para indicadores positivos
- **Rojo Industrial**: `#DC2626` - Para errores y alertas críticas

## Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## Contacto

- **Desarrollador**: FJRH
- **Email**: [tu-email@ejemplo.com]
- **Proyecto**: [https://github.com/tu-usuario/FabrikApp]

---

**FabrikApp** - Transformando la gestión industrial con tecnología moderna.
