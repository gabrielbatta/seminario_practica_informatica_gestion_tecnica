# Estado de verificación — versión TP2

Este registro distingue las comprobaciones ejecutadas de las verificaciones aún pendientes. No es una certificación de funcionamiento en producción.

## Entorno inicial

- Linux con OpenJDK **21.0.11** y `javac` con destino Java 21.
- Pantalla virtual Xvfb para construir y capturar las ventanas Swing.
- PlantUML **1.2026.9beta4**, commit **a5eb2c2**, compilación `2026-09-27 21:10:41 UTC`, obtenido del artefacto oficial `plantuml/plantuml`, ID `10942321195`.
- Graphviz **2.42.4**, utilizado internamente por PlantUML donde corresponde.

El renderizador y las herramientas del entorno no forman parte del código del prototipo. Se entregan fuentes editables y PNG. No se han distribuido archivos de fuentes tipográficas.

## Comprobaciones iniciales entregadas

| Comprobación | Evidencia | Resultado |
|---|---|---|
| Compilación de 20 fuentes de aplicación y tres fuentes de prueba | `compilacion.txt` | Sin errores. |
| 28 comprobaciones unitarias | `pruebas_unitarias.txt` | 28 aprobadas; cero fallidas. |
| Construcción y controles básicos de interfaz | `pruebas_interfaz.txt` | Tres aprobadas; cero fallidas. |
| Sintaxis y renderizado de once diagramas | `renderizado_plantuml.txt` y PNG en `diagramas/` | Sin errores de sintaxis; PNG nativos de PlantUML. |
| Coherencia de archivos | Revisión inicial de rutas e inventario | El informe inicial referenciaba once imágenes; se comprobaron ocho tablas coincidentes con las entidades del ER y archivos JSON/XML legibles. |

El informe compacto actual incorpora nueve diagramas: casos de uso, dominio, estados de solicitud y asistencia, clases de diseño, tres secuencias y entidad-relación. Las once fuentes y sus PNG permanecen en `diagramas/`; actividad y despliegue se conservan como material complementario. Esta selección documental no modifica las comprobaciones iniciales registradas.

### Capturas de interfaz

`01_login_swing.png`, `02_solicitudes_swing.png` y `03_asistencias_swing.png` muestran ventanas construidas por el código Swing real. **No hubo conexión con MySQL durante esas capturas.** Las tablas vacías no son datos operativos ni una evidencia de alta, cierre o consulta persistente.

La comprobación de permisos visuales verifica el estado habilitado de algunos botones según el usuario de prueba. No comprueba todos los controles de acceso ni sustituye las pruebas del servicio con una base real.

## Verificación local adicional — 28/09/2026

En macOS se ejecutó `mvn test` con Java 21.0.8 y Maven 3.9.16: compilación correcta, 28 comprobaciones aprobadas y `BUILD SUCCESS`. También se ejecutaron las tres comprobaciones de construcción de interfaz, con sus capturas en un directorio temporal; se conservaron las evidencias originales de esta carpeta.

Los scripts 01–03 se ejecutaron sobre la instancia local MySQL 9.6.0. Se verificaron ocho tablas InnoDB, dos clientes, dos domicilios, tres servicios y seis usuarios de demostración. Se creó `gt_app@localhost` con los permisos del script y TLS obligatorio. La conexión TCP y la conexión desde `ConexionBD.abrir` con Connector/J 8.4.0 negociaron `TLS_AES_128_GCM_SHA256`; `AutenticacionServicio` validó el ingreso de `atencion1` con rol `ATENCION`.

Estas comprobaciones verifican la preparación de la base, la conexión y un inicio de sesión. No equivalen a ejecutar I01–I17, Q01–Q10, BD01–BD05 ni los recorridos PP01–PP03. El entorno previsto del proyecto continúa siendo MySQL 8.4. Las credenciales se guardan únicamente en archivos locales excluidos de Git.

El código y la documentación se encuentran en la [rama master del repositorio](https://github.com/gabrielbatta/seminario_practica_informatica_gestion_tecnica/tree/master), conservando el historial y el PDF del TP1.

## Pendiente de ejecutar

| Verificación | Preparación entregada | Motivo del estado pendiente |
|---|---|---|
| Circuito completo de persistencia MySQL | Scripts 01–03, configuración y README. | La base local, la conexión y el login fueron comprobados; faltan los recorridos completos. |
| I01–I17 | `PruebasIntegracion.java`, compilado. | La base local está preparada; aún no se ejecutó el programa completo de integración. |
| Q01–Q10 | `04_operaciones_y_consultas.sql`. | No se ejecutó contra el motor MySQL. |
| BD01–BD05 | `05_pruebas_integridad.sql`. | No se ejecutó contra el motor MySQL. |
| PP01–PP03 desde Swing | [Sección 7.1](../informe/BATTAGINI-GABRIEL-ALEJANDRO-AP2.md#71-casos-y-procedimientos-de-prueba) del informe y recorridos del README. | Falta ejecución completa con base conectada. |
| Aceptación, carga, concurrencia y rendimiento | Criterios descritos en el informe. | No hubo usuarios validadores ni un entorno representativo. |

No se utilizaron SQLite, MariaDB, una base simulada ni resultados inventados para sustituir una prueba MySQL. La validación estática de nombres, tipos y scripts no equivale a ejecutar el esquema en el motor.

## Cómo completar el registro

Después de ejecutar cada prueba, conservar su salida, la versión del motor y los datos necesarios para reproducirla. Si falla, registrar el caso, los pasos, el resultado esperado, lo obtenido, la severidad y la corrección realizada. Repetir las pruebas después del ajuste y actualizar la [sección 7.2](../informe/BATTAGINI-GABRIEL-ALEJANDRO-AP2.md#72-registro-de-ejecución-de-esta-versión) del informe con esos resultados reales.

Las evidencias iniciales no deben reinterpretarse como una aprobación global del prototipo.
