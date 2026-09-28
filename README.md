# TP2 — Gestión y seguimiento de asistencias técnicas

Proyecto académico de **Gabriel Alejandro Battagini**, Seminario de Práctica de Informática, Universidad Siglo 21. Interfaz de escritorio **Java Swing**, lógica separada de los DAO y persistencia prevista en **MySQL 8.4**.

## 1. Qué contiene la entrega

| Carpeta o archivo | Contenido |
|---|---|
| `informe/BATTAGINI-GABRIEL-ALEJANDRO-AP2.md` | Informe: continuidad, PUD, análisis, diseño, base de datos, implementación, pruebas, comunicación y trazabilidad. |
| `diagramas/` | Once fuentes PlantUML y sus once PNG, renderizados nativamente. |
| `sql/` | Estructura completa, datos iniciales, usuario de conexión, operaciones, consultas y pruebas negativas. |
| `src/main/java/` | Código de la aplicación. |
| `src/test/java/` | Comprobaciones unitarias, pruebas con MySQL y comprobaciones básicas de Swing. |
| `evidencias/` | Resultados reales de lo ejecutado, capturas de construcción de ventanas y estado de verificación. |
| `config/db.properties.example` | Ejemplo de configuración, sin una contraseña real. |
| `pom.xml`, `.vscode/` | Dependencias y configuración para abrir el proyecto en VS Code. |

**Estado de verificación:** fuentes compiladas con Java 21; 28 comprobaciones unitarias y tres de construcción de interfaz aprobadas. **La integración con MySQL no fue ejecutada en el entorno de elaboración.** El código no se presenta como validado para producción. Consultar `evidencias/ESTADO_VERIFICACION.md`.

**Verificación local adicional (28/09/2026):** en macOS, con Java 21.0.8 y Maven 3.9.16, `mvn test` compiló el proyecto y aprobó las 28 comprobaciones. Se ejecutaron los scripts 01–03 sobre MySQL 9.6.0 y se verificaron la conexión JDBC cifrada y el ingreso con `atencion1`. El entorno previsto sigue siendo MySQL 8.4; las pruebas completas I01–I17 y los recorridos manuales con persistencia continúan pendientes.

## 2. Preparación del entorno

Utilizar un **JDK 21**, **MySQL Community Server 8.4**, VS Code y **Extension Pack for Java**. La carpeta `.vscode` contiene la recomendación de extensión. Abrir la **carpeta completa que contiene `pom.xml`**, no únicamente un archivo `.java`.

El proyecto declara `com.mysql:mysql-connector-j:8.4.0`. VS Code/Maven deben descargar esa dependencia. La primera preparación requiere acceso a los repositorios de dependencias; la aplicación no consume un servicio de Internet para gestionar las solicitudes.

Para usar los comandos de este README se necesita Maven 3.9.x instalado y disponible como `mvn`. Verificar en la terminal:

```bash
java -version
javac -version
mvn -version
```

Este paquete **no incluye** JDK, servidor MySQL, Maven, Connector/J ni el renderizador de PlantUML. Los PNG ya están generados y no requieren instalar PlantUML para leer el informe.

Los scripts utilizan características y la intercalación de MySQL 8.4. No sustituirlo silenciosamente por MariaDB; por ejemplo, una instalación de XAMPP puede incluir otro motor.

## 3. Crear la base de práctica

Trabajar sobre una instalación o base de práctica, no sobre datos de producción. Los scripts no ejecutan `DROP DATABASE` ni eliminan tablas. La estructura y la carga inicial están pensadas para ejecutarse **una vez sobre una base nueva**.

Desde la raíz del proyecto, abrir un cliente SQL con una cuenta administradora. Por ejemplo:

```bash
mysql -u root -p
```

En el cliente MySQL, ejecutar en este orden:

```sql
SOURCE sql/01_estructura.sql;
SOURCE sql/02_datos_iniciales.sql;
```

Antes del tercer script, **copiar** `sql/03_usuario_bd.sql` como `sql/03_usuario_bd.local.sql`. En esa copia, reemplazar `CAMBIAR_CLAVE_LOCAL` por una contraseña local propia. La copia `*.local.sql` está excluida de Git; mantener el archivo original con su marcador, sin una clave real. Utilizar una clave que no contenga una comilla simple para no alterar el literal SQL, o escaparla correctamente. Luego ejecutar:

```sql
SOURCE sql/03_usuario_bd.local.sql;
```

La cuenta creada es `gt_app` para conexiones locales, exige SSL y no tiene permiso de borrado ni de modificación de estructura. **No usar `root` como cuenta de conexión de la aplicación.** Si ya existe `gt_app`, revisar esa cuenta de forma explícita; el script no sobrescribe automáticamente usuarios existentes.

Comprobar estructura y datos iniciales:

```sql
USE gestion_tecnica;
SHOW TABLES;
SELECT id_servicio, codigo, tipo FROM servicio;
SELECT id_usuario, login, rol FROM usuario;
```

Debe haber ocho tablas, tres servicios y seis usuarios de aplicación. Si se produce un error durante la preparación, detenerse y revisar el mensaje antes de continuar; no ocultarlo ejecutando todos los scripts repetidamente.

## 4. Configurar la conexión

Copiar `config/db.properties.example` como **`config/db.properties`** y colocar la misma contraseña elegida para `gt_app`.

```properties
db.url=jdbc:mysql://127.0.0.1:3306/gestion_tecnica?sslMode=REQUIRED&connectTimeout=5000&socketTimeout=10000&connectionTimeZone=LOCAL
db.usuario=gt_app
db.clave=COLOCAR_AQUI_LA_CLAVE_LOCAL
```

La contraseña anterior es un marcador, no una credencial que se haya creado. `.gitignore` excluye `config/db.properties`. No publicar ese archivo, la copia `03_usuario_bd.local.sql` ni contraseñas locales.

La configuración de ejemplo es para aplicación y MySQL en el mismo equipo. `sslMode=REQUIRED` exige cifrado, pero no verifica la identidad del servidor; para red interna real se debe configurar `VERIFY_IDENTITY`, certificados y reglas de acceso. No habilitar el puerto de MySQL hacia Internet para esta práctica.

El programa lee este archivo desde la raíz del proyecto. Una ubicación alternativa puede indicarse con la propiedad Java `-Dgt.config=/ruta/db.properties`. Las fechas se interpretan como hora local del equipo; mantener la misma zona horaria en los puestos que compartan la base.

## 5. Ejecutar la aplicación

En VS Code, esperar a que termine la importación del proyecto Java/Maven. Abrir `src/main/java/ar/edu/gestiontecnica/Main.java` y usar **Run**, o elegir la configuración **Ejecutar Gestión Técnica** de `.vscode/launch.json`.

Desde una terminal ubicada en la raíz también se puede utilizar:

```bash
mvn compile exec:java "-Dexec.cleanupDaemonThreads=false"
```

Se muestra la ventana de acceso. Las siguientes son **cuentas ficticias de aplicación**, distintas de la cuenta de conexión `gt_app`:

| Usuario | Rol |
|---|---|
| `atencion1` | Operador de atención |
| `soporte1` | Soporte técnico |
| `coordinador1` | Coordinación |
| `tecnico1` | Técnico de campo 1 |
| `tecnico2` | Técnico de campo 2 |
| `admin1` | Administrador: consultas de revisión en este prototipo |

**Contraseña de demostración para las seis cuentas: `DemoTP2!2026`.** Está documentada deliberadamente porque son datos de prueba. Los hashes se encuentran en el script inicial; no usar estas credenciales ni esta carga en producción.

Si la aplicación informa un error de conexión, revisar servicio MySQL iniciado, configuración, contraseña, SSL y disponibilidad de Connector/J. El mensaje no equivale a una operación guardada. La consola conserva el detalle técnico del error.

## 6. Demostrar los dos recorridos

### A. Resolución remota

Con `atencion1`, usar **Nueva solicitud**, seleccionar un servicio y registrar un inconveniente. Anotar el identificador creado; no asumir que siempre será 1. Cerrar sesión e ingresar con `soporte1`. Seleccionar la solicitud, registrar diagnóstico, acciones y resultado `RESUELTO`, completar la solución y confirmar. Consultar **Historial**: la solicitud debe quedar cerrada, con sus antecedentes, sin generar una visita.

### B. Visita sin resolución y segunda intervención

Registrar otra solicitud con atención. Con soporte, usar **Derivar**, completar el motivo y confirmar. Con coordinación, abrir **Asistencias**, seleccionar la visita, programar fecha/hora y asignar `tecnico1`. Se pueden cargar ambos datos en cualquier orden; solo con ambos queda programada.

Para la demostración de una visita ya realizada, usar una fecha anterior o igual al momento de registro. No se permite finalizar una visita futura. Las fechas se ingresan como `dd/MM/aaaa HH:mm`.

Con `tecnico1`, guardar el diagnóstico y las tareas mediante sus botones separados. Usar **Finalizar**, elegir `PENDIENTE`, detallar qué falta y confirmar que la visita se realizó. La asistencia debe finalizar y la solicitud permanecer pendiente de continuidad.

Con soporte, derivar nuevamente **la misma solicitud**; con coordinación, programar y asignar la nueva visita; con el técnico, completar su intervención y finalizar como `RESUELTO`. Consultar el historial y comprobar que conserva ambas visitas. Esta diferencia entre visita y solicitud es el ajuste de RF17.

`tecnico2` no debe visualizar las asignaciones de `tecnico1`. La cuenta `admin1` no sustituye los permisos de operación de los demás roles: en esta iteración solo dispone de consultas. La administración completa de usuarios no está implementada.

## 7. Ejecutar y registrar las pruebas

### Unitarias, sin MySQL

```bash
mvn test
```

El objetivo `test` está configurado para ejecutar `PruebasUnitarias`, un programa de comprobaciones explícitas sin JUnit. Debe informar 28 aprobadas y cero fallidas; cualquier incumplimiento lanza un error. También puede ejecutarse esa clase desde VS Code. No requiere `db.properties`.

### Integración, con MySQL real

Después de preparar los scripts y la conexión:

```bash
mvn test-compile exec:java "-Dexec.mainClass=ar.edu.gestiontecnica.PruebasIntegracion" "-Dexec.classpathScope=test" "-Dexec.args=--confirmar-base-pruebas"
```

El argumento de confirmación es obligatorio. El programa comprueba el motor, utiliza las cuentas ficticias y crea solicitudes con la marca `PRUEBA_TP2`. **Agrega datos y conserva sus historiales**: no los borra después. Ejecutarlo únicamente en una base de práctica.

Comprobar el resultado real de I01–I17. Guardar la salida y la versión del motor en `evidencias/`, sin copiar un resultado esperado como si hubiera ocurrido. El programa termina con error si una comprobación falla; los datos creados antes del fallo pueden permanecer para su revisión.

### Integridad y consultas SQL

Ejecutar `sql/04_operaciones_y_consultas.sql` con una cuenta administradora de la base de práctica. Q01–Q03 insertan, modifican y borran **solo un cliente temporal sin relaciones**; las demás consultas leen la información del circuito. Algunas consultas devolverán cero filas hasta que se hayan creado solicitudes y visitas.

En `sql/05_pruebas_integridad.sql`, ejecutar **cada bloque por separado**, incluyendo su `ROLLBACK`. Cada bloque negativo debe producir el rechazo indicado; el error esperado no es una falla del prototipo. Registrar los resultados obtenidos de BD01–BD05.

Completar además PP01–PP03 del informe desde Swing, tomando capturas de datos realmente persistidos. Las capturas ya incluidas son solo evidencia de construcción de ventanas sin conexión.

### Comprobaciones visuales automatizadas

`PruebasInterfaz` construye ventanas, comprueba tres condiciones de componentes/permisos y genera capturas sin abrir la base. Puede ejecutarse desde VS Code como una clase Java de prueba; requiere un entorno gráfico. No es una prueba de uso completo ni sustituye las pruebas manuales con MySQL.

En el entorno de elaboración se ejecutó en una pantalla virtual Xvfb. La compilación y las comprobaciones entregadas se realizaron con `javac` y `java`; **no se ejecutó Maven en ese entorno**.

## 8. Regenerar los diagramas

Cada PNG se obtiene de su `.puml` homónimo, sin pasos manuales de dibujo. Todos son PlantUML real; no hay vistas previas de Graphviz usadas como sustituto. El renderizador puede utilizar Graphviz internamente para distribuir los elementos, lo cual forma parte del proceso normal.

Con un `plantuml.jar` instalado localmente, desde la raíz:

```bash
java -Djava.awt.headless=true -jar /ruta/plantuml.jar -charset UTF-8 -checkonly "diagramas/*.puml"
java -Djava.awt.headless=true -jar /ruta/plantuml.jar -charset UTF-8 -tpng "diagramas/*.puml"
```

El paquete registra la versión utilizada en `evidencias/ESTADO_VERIFICACION.md`. Algunas versiones del renderizador pueden cambiar ligeramente la distribución, sin modificar el modelo.

## 9. Preparar la entrega

El informe usa rutas relativas `../diagramas/` desde la carpeta `informe`. Mantener esa estructura al abrirlo o convertirlo; mover únicamente el MD a otra carpeta rompe sus imágenes.

Antes de entregar: ejecutar las pruebas pendientes, actualizar sus resultados reales en la sección 7.4, incorporar capturas de los recorridos y comprobar que el enlace permita acceder al código. La organización local incluye un `.gitignore` para excluir credenciales y archivos de compilación.

Repositorio del proyecto: **[seminario_practica_informatica_gestion_tecnica — rama master](https://github.com/gabrielbatta/seminario_practica_informatica_gestion_tecnica/tree/master)**. El proyecto se encuentra en la raíz de `master`, junto con el PDF del TP1 conservado del historial anterior. Para descargar esta versión:

```bash
git clone --branch master https://github.com/gabrielbatta/seminario_practica_informatica_gestion_tecnica.git
cd seminario_practica_informatica_gestion_tecnica
```

La configuración `config/db.properties` y el script `sql/03_usuario_bd.local.sql` son archivos locales excluidos de Git; cada instalación debe prepararlos según las secciones 3 y 4.

Convertir el informe a PDF con **A4, Calibri 11, espaciado simple y portada**. Ajustar los diagramas al ancho de página y revisar que sus textos sean legibles; cuando sea necesario dedicar una página al diagrama. Nombre final: **`BATTAGINI-GABRIEL-ALEJANDRO-AP2.PDF`**.

Las referencias académicas se utilizan para organización y continuidad; las decisiones propias del prototipo están diferenciadas. No se atribuyen entrevistas, pruebas de aceptación ni mediciones de rendimiento que no se hayan realizado.
