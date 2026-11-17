# INDUMOTOS - Proyecto final DAM

Autor: Roberto Samuel Valencia Saavedra.

## Contenido de esta publicación

- `movil/`: Kotlin Multiplatform, Compose Material 3, Koin, Ktor, repositorio, caso de uso y ViewModel; Android e integración compartida para iOS.
- `validacion/`: scripts y resultados de comprobaciones, analizadores sintácticos y pruebas de reglas de negocio.
- `Compilar-Movil.ps1`: compilación Android y copia del APK cuando el build pase.

El backend no se incluye en esta publicación. La app requiere la API ampliada de la carpeta local `APLICAICONES DISTRIBUIDAS/backend`; el backend original del Sprint 1, sin esas ampliaciones, no implementa las rutas `mobile/**`. Los scripts de BD y reglas necesitan esa carpeta hermana `backend/` para ejecutarse.

## Abrir en Android Studio

Abrir la carpeta completa `movil`, sincronizar Gradle y ejecutar `androidApp`. Requiere SDK Android 36 y un JDK compatible con Gradle 9.1.0. No se publica `local.properties`: Android Studio lo genera para la computadora local.

Con el backend de demostración iniciado en el puerto 8088:

- Emulador Android: `http://10.0.2.2:8088`.
- Celular físico en la misma red: `http://IP_DE_LA_PC:8088`.
- Usar las credenciales configuradas localmente en el backend; no están incluidas en Git.

## Alcance implementado en código

1. Acceso, cuentas, permisos, recepciones y consulta/registro de motocicletas.
2. Ubicaciones, solicitudes de ensamblaje, asignación, movimientos y anulación.
3. Disponibilidad, inconsistencias, corrección de fichas y consulta de historiales.

Compras, ventas y agenda de postventa no se incluyen.

## Estado verificable

Avance adicional del 17/11/2026: cliente HTTP único inyectado por Koin y jerarquía sellada de errores, con distinción entre validación, sesión vencida, permisos, conflicto y conexión. Cinco comprobaciones de la traducción de errores se compilaron y ejecutaron satisfactoriamente con Kotlin/JVM. Los tests completos de commonTest y el build Android todavía no se han aprobado.

- Migraciones V1-V10, scripts SQL de incrementos 2/3 y actualización V7-V10 con datos previos: aprobados en PostgreSQL 18 aislado. Ver `validacion/resultado-bd.json`.
- Reglas de negocio: 16/16 casos aprobados con Java.
- Revisión sintáctica: 43 archivos Java y 12 Kotlin sin errores. Es una comprobación sintáctica, no una compilación integral.
- **APK, compilación integral y prueba app-API pendientes**. El entorno automatizado bloqueó Gradle por comprobación de escritura y Maven por acceso a dependencias. No se afirma que el sistema esté completamente probado.
- iOS requiere macOS/Xcode y no se ha compilado.

Referencia de prácticas: PharmaMobile_Valencia (`af5990c87bfa2befd06b7c5673a30f745e0b187b`) y BackendPharmobile (`82793965b6e5339c4b56375beab0d4fbb671d5bd`), revisados el 17/11/2026.
