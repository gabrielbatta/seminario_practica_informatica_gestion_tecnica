# TP4 - Gestión y seguimiento de asistencias técnicas

Prototipo académico de **Gabriel Alejandro Battagini**, Seminario de Práctica de Informática, Universidad Siglo 21. Aplicación de escritorio en **Java 21 y Swing**, con reglas de negocio separadas de los DAO y persistencia en **MySQL**.

## Funciones del prototipo

- Ingreso con usuario y contraseña; permisos según el rol.
- Registro de solicitudes para clientes y servicios precargados.
- Atención remota, con diagnóstico, acciones y resolución o continuidad.
- Derivación a domicilio, programación y asignación de técnico.
- Registro de diagnóstico y tareas, y finalización de cada visita.
- Consulta de solicitudes, asistencias, historial y actividad por responsable.

**Finalizar una visita como PENDIENTE mantiene la solicitud abierta.** Una segunda intervención puede resolverla y cerrarla, conservando las dos visitas en el historial.

La administración gráfica de usuarios, clientes y servicios queda fuera de esta iteración. Esos datos se cargan mediante SQL. El administrador dispone de consultas; cada técnico consulta e interviene únicamente dentro de los permisos establecidos.

## Estructura

| Ruta | Contenido |
|---|---|
| `src/main/java/` | Aplicación: interfaz, servicios, DAO, modelos y conexión. |
| `sql/` | Estructura, datos iniciales, cuenta de conexión, operaciones y consultas. |
| `config/db.properties.example` | Plantilla de conexión sin credenciales locales reales. |
| `pom.xml` | Compilación Java 21, Connector/J y ejecución con Maven. |
| `diagramas/` | Imágenes de los diagramas y sus fuentes editables. |
| `evidencias/tp4/` | Resumen de verificación y capturas con datos persistidos. |

## Incremento TP4

Se conserva la organización en capas y se adopta MVC simplificado: las ventanas Swing presentan los datos y reciben los eventos; los modelos y servicios representan los datos y las reglas. Los DAO separan complementariamente la persistencia SQL.

`Solicitud` y `Asistencia` heredan de la clase abstracta `RegistroAtencion`. Ambas redefinen `getReferencia()`, utilizado polimórficamente al consultar el historial. `ReglaNegocioException` comunica validaciones y `AccesoDenegadoException` especializa los rechazos de permisos y autenticación. Los errores JDBC conservan su tipo `SQLException` y provocan rollback cuando hay una transacción activa.

`ConsultaSolicitudes` implementa búsqueda secuencial por cliente, servicio o número y ordenación por inserción, con ID descendente. Emplea `ArrayList` para reunir coincidencias y un arreglo `Solicitud[]` para ordenar. El DAO limita primero los datos según el usuario; la búsqueda Java solo procesa los registros autorizados. La búsqueda ignora mayúsculas y tildes; `%` y `_` se interpretan literalmente. Se mantiene la ordenación adicional de las columnas de Swing.

Los algoritmos están previstos para el volumen acotado del prototipo: la consulta carga la lista autorizada completa y la inserción tiene costo cuadrático en el peor caso. No se afirma rendimiento de producción.

La revisión se prepara en la rama **local `tp4`**, partiendo de `6e84a3c`. El enlace remoto de `master` conserva la entrega anterior; el incremento TP4 no está publicado.

El contenido versionable se limita al código Java y SQL, la configuración de ejemplo, los archivos necesarios para abrir y compilar el proyecto, los diagramas y las evidencias. Los informes académicos, sus PDF y documentos Word, el guion y los archivos de los TP se conservan únicamente como material local y están excluidos de Git.

## Requisitos

- JDK 21.
- Maven 3.9.x.
- Servidor MySQL. El entorno previsto es MySQL 8.4 y la verificación local se realizó en MySQL 9.6.0.
- Entorno gráfico para abrir Swing. Puede desarrollarse en VS Code con Extension Pack for Java.

El proyecto utiliza `mysql-connector-j:8.4.0`, descargado por Maven. Abrir la carpeta completa que contiene `pom.xml`.

## Preparación inicial

Clonar el repositorio y entrar en su carpeta:

```bash
git clone --branch master https://github.com/gabrielbatta/seminario_practica_informatica_gestion_tecnica.git
cd seminario_practica_informatica_gestion_tecnica
```

Los siguientes pasos corresponden a una **base de práctica nueva**. Los scripts 01 y 02 se ejecutan una sola vez; no deben repetirse sobre la base ya preparada.

Abrir MySQL con una cuenta administradora:

```bash
mysql -u root -p
```

Ejecutar desde la raíz del proyecto:

```sql
SOURCE sql/01_estructura.sql;
SOURCE sql/02_datos_iniciales.sql;
```

Copiar `sql/03_usuario_bd.sql` como `sql/03_usuario_bd.local.sql`. En la copia, reemplazar `CAMBIAR_CLAVE_LOCAL` por una contraseña propia para la cuenta de conexión `gt_app`; respetar el escape de literales SQL si contiene comillas. Ejecutar esa copia:

```sql
SOURCE sql/03_usuario_bd.local.sql;
```

El script crea una cuenta local con SSL y permisos limitados. Si `gt_app` ya existe, revisar su configuración antes de ejecutar el script.

Copiar `config/db.properties.example` como `config/db.properties` y completar la misma contraseña elegida para `gt_app`:

```properties
db.url=jdbc:mysql://127.0.0.1:3306/gestion_tecnica?sslMode=REQUIRED&connectTimeout=5000&socketTimeout=10000&connectionTimeZone=LOCAL
db.usuario=gt_app
db.clave=COLOCAR_AQUI_LA_CLAVE_LOCAL
```

La cuenta de conexión `gt_app` es diferente de los usuarios de la aplicación. `config/db.properties` y los scripts `*.local.sql` están excluidos de Git. La configuración mostrada corresponde a MySQL y la aplicación en el mismo equipo; `sslMode=REQUIRED` cifra la conexión sin verificar la identidad del servidor.

## Levantar la aplicación

Con MySQL iniciado, ejecutar desde la raíz del proyecto:

```bash
mvn compile exec:java "-Dexec.cleanupDaemonThreads=false"
```

Se abre la ventana de acceso. Para detener la ejecución desde la terminal, usar **Ctrl+C**. También puede iniciarse `ar.edu.gestiontecnica.Main` desde el IDE.

La ventana de acceso se centra en el monitor donde está el cursor al iniciarse. Para abrirla en la pantalla de la terminal, dejar el cursor sobre ese monitor hasta que aparezca. Al ingresar o cerrar sesión, la siguiente ventana conserva el monitor de la anterior.

La aplicación lee `config/db.properties` desde el directorio de trabajo. Una ubicación alternativa puede indicarse con `-Dgt.config=/ruta/db.properties`.

## Cuentas de demostración

**Contraseña de las seis cuentas: `1234`.** Son datos ficticios de práctica. El script inicial conserva hashes PBKDF2 con una sal diferente por cuenta.

| Usuario | Rol |
|---|---|
| `atencion1` | Registro de solicitudes. |
| `soporte1` | Atención remota y derivación. |
| `coordinador1` | Programación y asignación de visitas. |
| `tecnico1` | Diagnóstico, tareas y finalización de sus visitas. |
| `tecnico2` | Segundo técnico para verificar el aislamiento de asignaciones. |
| `admin1` | Consultas de revisión. |

## Recorridos manuales

1. **Resolución remota:** registrar una solicitud con atención; ingresar con soporte, cargar diagnóstico y acciones, elegir RESUELTO y completar la solución. Comprobar el cierre sin visita domiciliaria.
2. **Continuidad domiciliaria:** registrar otra solicitud, derivarla, programar una visita y asignar técnico. Guardar diagnóstico y tareas y finalizar como PENDIENTE. Derivar nuevamente la misma solicitud y resolverla en otra visita. Comprobar que el historial conserva ambas.
3. **Permisos y validaciones:** comprobar campos obligatorios, restricciones por rol y que `tecnico2` no pueda intervenir sobre las visitas de `tecnico1`.

Las fechas se ingresan como `dd/MM/aaaa HH:mm`. Para registrar una visita realizada, utilizar una fecha anterior o igual al momento actual; no se permite finalizar una visita futura. Diagnóstico y tareas se guardan mediante sus acciones separadas.

## Verificación del prototipo

Compilar y generar el archivo de la aplicación:

```bash
mvn clean package
```

El repositorio conserva capturas del programa conectado con MySQL, con datos ficticios, búsqueda de solicitudes e historial de las asistencias.

`sql/04_operaciones_y_consultas.sql` contiene Q01-Q10. Q01-Q03 insertan, modifican y eliminan solamente un cliente temporal sin relaciones; las demás consultas recuperan información del circuito.

Las capturas de `evidencias/tp4/` muestran consultas, búsqueda e historial. Los recorridos manuales completos desde los formularios, la aceptación con usuarios y las mediciones de carga permanecen pendientes.
