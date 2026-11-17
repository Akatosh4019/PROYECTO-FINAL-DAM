# Alcance de la entrega de avance

Responsable: Roberto Samuel Valencia Saavedra. Fecha: 17/11/2026.

Esta publicación es un avance de DAM, no la versión final ni la certificación de todos los requisitos del curso.

## Incluido

- Código del móvil de INDUMOTOS para los tres incrementos logísticos principales.
- Capas de dominio, datos y presentación; ViewModel con UiState/StateFlow, Koin y Ktor.
- Cliente HTTP único registrado en Koin.
- Errores tipados: validación con campos, 401, 403, conflicto, red y servidor.
- Cinco comprobaciones de traducción de errores ejecutadas con Kotlin/JVM y tests equivalentes preparados en commonTest.
- Evidencias previas de BD aislada y pruebas de reglas de negocio; no se presentan como pruebas integrales de la app.
- Instrucciones para abrir el proyecto y compilar; configuración privada excluida.

## Pendientes declarados según las guías del docente

| Requisito | Estado del avance |
|---|---|
| Build Android y recorrido app-API | Pendiente de compilación integral y ejecución |
| CRUD completo, paginación y error uniforme del servidor | Parcial; revisar contrato y completar |
| DTO separados del dominio | Pendiente: aún hay contratos con JsonObject |
| Token seguro y recuperación de sesión Android/iOS | Pendiente: la sesión permanece en memoria |
| Offline-first con SQLite observable | No implementado |
| Guardado local, IDs provisionales y sincronización de subida antes de bajada | No implementado |
| Reintentos acotados y estado visible de pendientes | No implementado |
| Capacidad nativa/Firebase y tres estados de permisos | No implementado |
| Pruebas en dos Android y un iOS reales | No ejecutadas |
| AAB/APK firmados e IPA/archivo Xcode | No generados |
| SonarQube y cobertura del curso | No ejecutado |
| Informe 18-30 páginas y formatos F-01 a F-13 | Pendientes |
| C4: contexto, contenedores y dos diagramas de componentes | Pendientes |
| Spring Boot 3 indicado por especificación | La copia del backend usa 4.0.7; resolver compatibilidad con el docente |
| Flujo develop, ramas personales, revisión cruzada y v1.0-final | No acreditado; no se crea una etiqueta final para este avance |

El backend ampliado permanece en la copia local y no está incluido en este repositorio de móvil y validación. Su publicación requiere un repositorio de servicio y conservar el contrato compartido.

No se incluyen compras, ventas, OCR ni agenda de postventa. No se registran pruebas en dispositivos ni revisiones de compañeros que no se hayan realizado.
