# Verificación local del TP4

Fecha: 08/11/2026. Rama local: `tp4`. Punto de partida: `6e84a3c3485616592e52ae296a52d44ea88f89d1`.

La copia se creó desde el repositorio GitHub indicado. La copia anterior de `Developer/universidad/seminario`, sus archivos sin seguimiento y su base de datos no se modificaron. La rama remota `master` se verificó mediante una lectura y conserva el mismo identificador de partida. No se realizó push ni commit.

## Resultados

| Verificación | Evidencia | Resultado |
|---|---|---|
| Compilación y unitarias | Salida conservada como validación local externa | Ejecución histórica del 08/11/2026 con `mvn clean test`: 47 aprobadas, cero fallidas, BUILD SUCCESS. |
| Integración | Salida conservada como validación local externa | 24 aprobadas; MySQL 9.6.0, Connector/J 8.4.0. |
| Construcción de ventanas | Salida conservada como validación local externa | UI01-UI03 aprobadas; tablas vacías, sin MySQL. |
| Consultas gráficas | Salida conservada como validación local externa | UI04-UI07 aprobadas sobre MySQL; carga, búsqueda, historial y visitas. |
| Integridad | Salida conservada como validación local externa | Cinco errores esperados: 1451, 1452, 3819, 3819 y 1062. |
| Consultas | `consultas_sql.txt` | Q01-Q10 ejecutadas sin errores. |
| Cambios | `git diff --check` | Sin errores de espacios. |

Las pruebas de integración se ejecutaron durante el desarrollo y conservaron sus solicitudes ficticias. La salida final identifica la resolución remota #5 y la solicitud domiciliaria #6, con sus dos visitas. Las capturas finales se generaron después de esa ejecución. Las consultas SQL se registraron en una ejecución anterior sobre la misma instancia aislada; por eso sus cantidades no necesariamente coinciden con las capturas finales.

## Aislamiento y repetición

Se inicializó una instancia independiente en `/tmp/seminario-tp4-mysql-01a1bcf6/data`, con socket propio y puerto TCP `33307`. Se cargaron los scripts 01 y 02 y una cuenta de aplicación exclusiva, con los permisos de lectura y escritura requeridos y SSL obligatorio. No se ejecutaron scripts de creación ni pruebas sobre la base anterior.

La instancia temporal se detiene al finalizar la revisión y se retira el archivo local de conexión utilizado para probar. Para repetir la integración, preparar una base de práctica según el README y completar un nuevo `config/db.properties`. La configuración real continúa excluida de Git. No reutilizar automáticamente una base con información relevante para ejecutar pruebas que agregan solicitudes.

Los registros finales de integración e interfaz se obtuvieron iniciando los ejecutores directamente con `java`. Después de separar las pruebas, sus fuentes, instrucciones de ejecución y salidas se conservan en la carpeta de validación local, fuera del repositorio. El README del proyecto entregable describe la compilación y los recorridos de la aplicación.

## Alcance y pendientes

El 09/11/2026 se separaron todas las pruebas automáticas, los controles SQL de validación y sus salidas en una carpeta local ajena al repositorio. El proyecto entregable ya no depende de esos ejecutores. La cantidad de comprobaciones registradas se conserva como evidencia de validación interna; los informes previos describen la ejecución realizada antes de esta separación.

Las capturas `04` a `07` muestran consultas reales autenticadas. Los datos se crearon desde los servicios de integración; no se declara que los formularios hayan completado los circuitos de escritura. PP01-PP03 siguen pendientes de ejecución íntegra desde Swing. También permanecen pendientes aceptación con usuarios, pruebas de carga y mediciones de rendimiento.

Se preparó el informe integrador y el guion de tres minutos. El PDF es un borrador local: falta revisar la entrega con el alumno, grabar el video y, únicamente después de una autorización expresa, publicar la versión TP4 y actualizar su enlace. La participación académica en foros y encuentros debe ser revisada por el alumno.
