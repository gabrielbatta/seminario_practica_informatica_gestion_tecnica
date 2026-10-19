# TP3 - Gestión y seguimiento de asistencias técnicas

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
| `src/test/java/` | Comprobaciones existentes: unitarias, integración y construcción de ventanas. |
| `sql/` | Estructura, datos iniciales, cuenta de conexión, operaciones y controles de integridad. |
| `config/db.properties.example` | Plantilla de conexión sin credenciales locales reales. |
| `pom.xml` | Compilación Java 21, Connector/J y ejecución con Maven. |

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

## Comprobaciones existentes

No se agregaron nuevas pruebas para esta revisión. Las credenciales de la comprobación de integración existente se actualizaron a `1234`.

Unitarias, sin MySQL:

```bash
mvn test
```

Este objetivo ejecuta `PruebasUnitarias`, un programa de 28 comprobaciones explícitas sin JUnit.

Integración, con una base de práctica preparada:

```bash
mvn test-compile exec:java "-Dexec.mainClass=ar.edu.gestiontecnica.PruebasIntegracion" "-Dexec.classpathScope=test" "-Dexec.args=--confirmar-base-pruebas"
```

La integración agrega solicitudes identificadas como `PRUEBA_TP2` y conserva sus historiales. Esa marca pertenece al programa existente. Ejecutarla únicamente en la base de práctica.

`sql/04_operaciones_y_consultas.sql` contiene Q01-Q10. Q01-Q03 insertan, modifican y eliminan solamente un cliente temporal sin relaciones; las demás consultas recuperan información del circuito. `sql/05_pruebas_integridad.sql` contiene cinco bloques negativos: ejecutarlos individualmente, incluyendo cada `ROLLBACK`.

`PruebasInterfaz` realiza tres comprobaciones de construcción de ventanas; requiere entorno gráfico y no sustituye los recorridos completos con MySQL.

**Verificación local registrada:** 28 comprobaciones unitarias, 17 de integración y cinco de integridad aprobadas; Q01-Q10 ejecutadas. Después del cambio de contraseña se verificó el acceso de las seis cuentas con `1234`. Los procedimientos PP01-PP03 siguen pendientes de ejecución íntegra desde los formularios; las consultas y validaciones gráficas parciales no equivalen a su aprobación.
