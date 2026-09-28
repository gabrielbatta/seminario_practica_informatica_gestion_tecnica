# Seminario de Práctica de Informática

## Actividad práctica N.º 2

**Proyecto:** Sistema de Gestión y Seguimiento de Asistencias Técnicas para una empresa que presta servicios de Internet y televisión por cable.

| | |
|---|---|
| Universidad | Siglo 21 |
| Alumno | Battagini, Gabriel Alejandro |
| Profesor | Virgolini, Pablo Alejandro |
| Código de materia | INF275-11987 |
| Modalidad | Individual |
| Tecnologías del prototipo | Java, Swing y MySQL |
| Entorno de desarrollo previsto | Visual Studio Code |

## Índice general

- [1. Introducción](#1-introducción)
  - [1.1. Objetivos revisados](#11-objetivos-revisados)
  - [1.2. Ajuste de RF17](#12-ajuste-de-rf17)
  - [1.3. Alcance de la iteración](#13-alcance-de-la-iteración)
- [2. Aplicación del Proceso Unificado de Desarrollo](#2-aplicación-del-proceso-unificado-de-desarrollo)
- [3. Etapa de análisis](#3-etapa-de-análisis)
  - [3.1. Actores y casos de uso](#31-actores-y-casos-de-uso)
  - [3.2. Modelo de dominio y reglas](#32-modelo-de-dominio-y-reglas)
  - [3.3. Especificación de los casos priorizados](#33-especificación-de-los-casos-priorizados)
  - [3.4. Estados](#34-estados)
- [4. Etapa de diseño](#4-etapa-de-diseño)
  - [4.1. Arquitectura y responsabilidades](#41-arquitectura-y-responsabilidades)
  - [4.2. Interacciones principales](#42-interacciones-principales)
  - [4.3. Interfaz](#43-interfaz)
- [5. Definición de la base de datos](#5-definición-de-la-base-de-datos)
  - [5.1. Modelo relacional y normalización](#51-modelo-relacional-y-normalización)
  - [5.2. Diagrama entidad-relación](#52-diagrama-entidad-relación)
- [6. Etapa de implementación](#6-etapa-de-implementación)
  - [6.1. Organización del código](#61-organización-del-código)
  - [6.2. Creación y carga de MySQL](#62-creación-y-carga-de-mysql)
  - [6.3. Inserción, consulta y borrado](#63-inserción-consulta-y-borrado)
  - [6.4. Consultas funcionales SQL](#64-consultas-funcionales-sql)
- [7. Etapa de pruebas](#7-etapa-de-pruebas)
  - [7.1. Casos y procedimientos de prueba](#71-casos-y-procedimientos-de-prueba)
  - [7.2. Registro de ejecución de esta versión](#72-registro-de-ejecución-de-esta-versión)
- [8. Definiciones de comunicación](#8-definiciones-de-comunicación)
- [9. Conclusión y repositorio](#9-conclusión-y-repositorio)
- [Referencias](#referencias)

## 1. Introducción

Este trabajo continúa el proyecto iniciado en el Trabajo Practico 1, orientado a centralizar el registro y seguimiento de solicitudes técnicas. La problemática consiste en la dispersión de información entre planillas y comunicaciones separadas, que dificulta conocer antecedentes, responsables y resultados de la atención.

Se mantiene la distinción entre solicitud técnica, que representa el inconveniente del cliente, y asistencia domiciliaria, que representa una visita para atenderlo. El sistema documenta las acciones de soporte remoto; no ejecuta comandos ni configuraciones sobre los equipos del cliente.

Se incorporan las observaciones docentes: revisión de objetivos, precisión de requerimientos y desarrollo del modelo de dominio. Se utilizó como guía de referencia el TP4 de Análisis y Diseño de Software para organizar los artefactos UML, escenarios, procedimientos y resultados de prueba, sin trasladar su dominio ni sus funcionalidades.

### 1.1. Objetivos revisados

**Objetivo general del proyecto.** Desarrollar, para esta iteración, un prototipo operativo que centralice solicitudes técnicas y permita seguir su atención hasta el cierre, contemplando la resolución remota y las intervenciones domiciliarias.

**Objetivos específicos.** Analizar los requerimientos y reglas del circuito seleccionado; diseñar las clases, la interfaz y el modelo relacional; implementar ese circuito en Java con Swing y MySQL; y verificar su comportamiento mediante pruebas vinculadas con los requerimientos, registrando resultados y limitaciones.

**Objetivos del sistema.** Facilitar la consulta del estado de atención, la coordinación de visitas y la recuperación de antecedentes, manteniendo identificados los responsables de las operaciones. La reducción efectiva de demoras deberá evaluarse posteriormente con datos de uso; no se presenta como un resultado ya medido.

### 1.2. Ajuste de RF17

**RF17 — Redacción revisada.** El sistema deberá permitir finalizar una asistencia domiciliaria realizada una vez registrados el diagnóstico, las tareas efectuadas y el resultado de la intervención. El cierre de la solicitud correspondiente solo se permitirá cuando el inconveniente haya sido registrado como resuelto. Si continúa pendiente, la solicitud deberá permanecer abierta.

El ajuste conserva el identificador y elimina la ambigüedad respecto de RN06 y RN07. Finalizar una visita no equivale necesariamente a resolver el inconveniente. Se mantiene RN09: las solicitudes y asistencias finalizadas forman parte del historial.

### 1.3. Alcance de la iteración

Se implementan los casos CU01 a CU12 mediante dos recorridos: resolución remota y atención domiciliaria, incluida la continuidad después de una visita sin resolución. Se incluyen autenticación, permisos por rol y registro de responsables y momentos de operación.

Los clientes, domicilios, servicios y usuarios se precargan mediante SQL. RF01–RF03 y CU14 se cubren parcialmente con consulta y selección de datos básicos. Su administración completa, CU13 — Gestionar usuarios y RF20 quedan para otra iteración.

El prototipo es de escritorio: el técnico registra la visita desde un equipo autorizado, sin aplicación móvil ni funcionamiento desconectado.

## 2. Aplicación del Proceso Unificado de Desarrollo

Se adopta un desarrollo dirigido por casos de uso, con una arquitectura en capas y avances incrementales. Análisis, diseño, implementación y pruebas son flujos de trabajo relacionados.

| **Fase del PUD** | **Aplicación al proyecto** |
|---|---|
| Inicio | El TP1 establece la problemática, objetivos, alcance y casos de uso iniciales. |
| Elaboración | Se completan dominio y estados, y se define la arquitectura. Se atienden riesgos de cierres incorrectos, pérdida de historial y permisos inadecuados. |
| Construcción | Se organiza el incremento en atención remota y atención domiciliaria. Cada recorrido relaciona casos de uso, diseño, persistencia y pruebas. |
| Transición | Se preparan instrucciones de instalación, datos de demostración y procedimientos de verificación. La aceptación con usuarios y la ejecución integrada en el equipo de entrega permanecen pendientes. |

El estado de verificación, las evidencias y las pruebas pendientes se detallan en la [sección 7.2](#72-registro-de-ejecución-de-esta-versión).

## 3. Etapa de análisis

La organización presta servicios de Internet y televisión. El circuito identifica el servicio afectado, evalúa la resolución remota y organiza una visita cuando corresponde; puede derivarse directamente si existe una necesidad física. Una visita sin resolución conserva la solicitud para continuar la atención. Las decisiones de detalle son propuestas del prototipo: no se presentan como resultados de entrevistas u observaciones realizadas.

### 3.1. Actores y casos de uso

| **Actor** | **Operaciones del prototipo** |
|---|---|
| Operador de atención | Registrar y consultar solicitudes; seleccionar datos básicos; consultar historial. |
| Soporte técnico | Documentar atención remota, registrar resolución o derivar; consultar antecedentes. |
| Coordinador técnico | Consultar solicitudes y asistencias; programar, reprogramar y asignar técnicos. |
| Técnico de campo | Consultar sus asistencias; registrar diagnóstico, tareas y resultado; consultar antecedentes autorizados. |
| Administrador | Consultas de revisión en esta iteración. La gestión de usuarios permanece pendiente. |

El cliente participa del negocio, pero no opera directamente el sistema. La autenticación es una condición común de acceso, no una relación `include` repetida artificialmente en todos los casos. Como decisión de esta versión, coordinación y administración también pueden consultar historial; el técnico solo accede al historial de servicios sobre los que tiene alguna asistencia asignada.

![Casos de uso del proyecto e identificación del alcance del prototipo.](../diagramas/01_casos_de_uso.png)

*Casos de uso del proyecto e identificación del alcance del prototipo.*

Se conservan los identificadores del TP1. CU06 y CU07 siguen siendo operaciones diferenciadas, al igual que CU09, CU10 y CU11, aunque se acceda a ellas desde una misma pantalla.

### 3.2. Modelo de dominio y reglas

Un cliente puede tener varios domicilios; cada domicilio puede tener varios servicios. La solicitud corresponde a un servicio y, mediante este, al domicilio y cliente. Puede contar con varias atenciones remotas y asistencias domiciliarias. Los usuarios intervienen como responsables del registro, de las atenciones y de las visitas. Diagnóstico, acciones y resultado son información de la atención, no subsistemas independientes.

Además de RN01–RN09, se adoptan estas decisiones para acotar el prototipo: como máximo una asistencia no finalizada por solicitud; una nueva visita conserva las anteriores; una solicitud cerrada no admite nuevas intervenciones; y no se reprograma ni reasigna una asistencia con diagnóstico o tareas ya registrados. Estas restricciones deberán validarse con la organización antes de una implantación real.

![Conceptos del negocio, atributos relevantes y multiplicidades.](../diagramas/02_modelo_dominio.png)

*Conceptos del negocio, atributos relevantes y multiplicidades.*

### 3.3. Especificación de los casos priorizados

**CU01 — Registrar solicitud técnica.** Atención, autenticada, selecciona un servicio activo con cliente, domicilio y contacto registrados; ingresa el inconveniente y confirma. Tras validar, se guardan solicitud y actividad: estado REGISTRADA, identificador, responsable y fecha. Los datos incompletos o un servicio inválido impiden guardar; un error de persistencia revierte la operación.

**CU03 y CU04 — Registrar diagnóstico y resolución remota.** Soporte consulta antecedentes de una solicitud abierta sin visita activa, completa diagnóstico y acciones e indica el resultado; si está resuelto, agrega la solución y confirma. Se conserva la atención y la solicitud queda EN_ATENCION_REMOTA o CERRADA. Se rechazan campos obligatorios incompletos, solicitudes cerradas o con visita activa.

**CU05 — Derivar a asistencia domiciliaria.** Soporte evalúa la necesidad presencial de una solicitud abierta con contacto completo, consulta antecedentes, ingresa el motivo y confirma. Se verifica que no exista una visita activa, se crea la asistencia y se registra el cambio: solicitud DERIVADA y asistencia PENDIENTE. Una visita activa, datos incompletos o solicitud cerrada impiden derivar. No se exige un intento remoto previo.

**CU06 y CU07 — Programar o reprogramar asistencia y asignar técnico.** Coordinación selecciona una asistencia no finalizada y sin diagnóstico ni tareas, establece fecha y hora o técnico activo y confirma. Se registra el cambio y se recalcula el estado: PROGRAMADA con ambos datos; PENDIENTE si falta alguno. Se rechazan técnicos inválidos y modificaciones posteriores al registro de la intervención. Una visita no realizada se reprograma, sin finalizarla.

**CU09, CU10 y CU11 — Documentar y finalizar la visita.** El técnico asignado, con asistencia programada y solicitud abierta, guarda diagnóstico y tareas, indica resultado y detalle, confirma la realización y finaliza tras validar. La visita queda FINALIZADA; la solicitud, CERRADA si se resolvió o PENDIENTE_CONTINUIDAD en caso contrario. Se rechazan datos faltantes, otro técnico, la finalización de visitas programadas para el futuro o finalizaciones repetidas. Un fallo de escritura revierte los cambios de la operación.

**CU02, CU08 y CU12 — Consultar solicitud, asignaciones e historial.** El usuario autenticado y autorizado selecciona o filtra registros para consultar estados, antecedentes y responsables, sin modificar información. El listado del técnico contiene únicamente sus visitas; puede consultar los antecedentes completos de los servicios autorizados. Los errores de autorización o conexión se informan sin presentar la operación como confirmada.

### 3.4. Estados

![Ciclo de vida de la solicitud técnica.](../diagramas/04_estados_solicitud.png)

*Ciclo de vida de la solicitud técnica.*

`REGISTRADA` identifica una solicitud recibida; `EN_ATENCION_REMOTA`, una atención documentada aún no resuelta; `DERIVADA`, una solicitud con visita activa; `PENDIENTE_CONTINUIDAD`, una visita finalizada sin resolución; y `CERRADA`, una resolución documentada. Desde pendiente de continuidad puede retomarse el soporte remoto o generarse otra visita. Cerrar no significa borrar.

![Ciclo de vida de una asistencia domiciliaria.](../diagramas/05_estados_asistencia.png)

*Ciclo de vida de una asistencia domiciliaria.*

La asistencia es `PENDIENTE` mientras falta fecha o técnico, `PROGRAMADA` cuando tiene ambos y `FINALIZADA` cuando se documenta su realización. Su resultado, `RESUELTO` o `PENDIENTE`, es independiente del estado: una visita finalizada puede no haber resuelto el inconveniente.

## 4. Etapa de diseño

### 4.1. Arquitectura y responsabilidades

Se adopta una aplicación de escritorio con tres capas lógicas, conforme a RNF07. No se incorporan frameworks de persistencia ni un servidor web: el acceso se realiza mediante JDBC y SQL explícito.

| **Capa** | **Clases principales** | **Responsabilidad** |
|---|---|---|
| Presentación | `VentanaLogin`, `VentanaPrincipal`, `TareaInterfaz` | Obtener entradas, mostrar datos y mensajes y ejecutar las operaciones sin bloquear el hilo de eventos. |
| Negocio | `AutenticacionServicio`, `SolicitudServicio`, `AsistenciaServicio`, `ReglasAtencion` | Comprobar permisos y reglas, coordinar operaciones y delimitar transacciones. |
| Acceso a datos | `SolicitudDAO`, `AsistenciaDAO`, `AtencionRemotaDAO`, `ActividadDAO`, `UsuarioDAO`, `CatalogoDAO` | Ejecutar consultas y actualizaciones parametrizadas; convertir resultados en objetos. |
| Apoyo | `ConexionBD`, `SeguridadClave` y clases del paquete `modelo` | Configuración, comprobación de contraseñas y transporte de datos. |

![Vista de diseño del circuito principal.](../diagramas/06_clases_diseno.png)

*Vista de diseño del circuito principal.*

El diagrama de clases muestra nombres coincidentes con el código y omite accesores y detalles secundarios para mantener su legibilidad. No reemplaza el modelo de dominio: aquí aparecen clases técnicas, dependencias y operaciones de implementación.

Cada servicio comparte una conexión entre los DAO que participan de la misma operación. Primero valida, luego escribe los cambios y la actividad y finalmente confirma. Ante un error ejecuta `rollback`. Se bloquea primero la solicitud al decidir sobre una atención, evitando que dos operaciones del propio sistema creen visitas simultáneas sobre el mismo estado anterior. Este mecanismo no sustituye una prueba de carga concurrente.

### 4.2. Interacciones principales

![Secuencia de CU01.](../diagramas/07_secuencia_registrar_solicitud.png)

*Secuencia de CU01.*

La pantalla solicita el registro al servicio. Después de verificar permisos y datos del servicio, los DAO guardan solicitud y actividad dentro de una transacción. El identificador confirmado permite ubicar el registro desde las consultas.

![Secuencia de CU04.](../diagramas/08_secuencia_resolver_remotamente.png)

*Secuencia de CU04.*

Se verifica que la solicitud continúe abierta y sin visita activa. La atención remota, el cierre y la actividad se confirman conjuntamente; no debe quedar una resolución registrada con un estado de solicitud contradictorio.

![Secuencia de CU11 con las alternativas de RF17.](../diagramas/09_secuencia_finalizar_asistencia.png)

*Secuencia de CU11 con las alternativas de RF17.*

El técnico asignado debe haber registrado diagnóstico y tareas. La alternativa `RESUELTO` cierra la solicitud; `PENDIENTE` finaliza únicamente la visita y deja la solicitud disponible para continuar. El diagrama representa el orden lógico de llamadas; las operaciones de base de datos se ejecutan en segundo plano mediante `TareaInterfaz`.

### 4.3. Interfaz

La ventana principal muestra la pestaña Solicitudes y, para coordinación, técnicos y administración, también Asistencias domiciliarias. Las tablas no son editables y la consulta de solicitudes permite buscar por cliente, servicio o número. Los botones se habilitan conforme a los permisos del usuario. Los formularios muestran etiquetas y mensajes de validación; la selección del servicio presenta conjuntamente cliente y domicilio para reducir asociaciones incorrectas.

Los permisos no dependen solo de deshabilitar botones: los servicios vuelven a comprobar el rol y, cuando corresponde, el técnico asignado. Las operaciones de persistencia usan SwingWorker; las actualizaciones visuales se realizan en el hilo de eventos, siguiendo el modelo de concurrencia de Swing (Oracle, s. f.-b).

## 5. Definición de la base de datos

### 5.1. Modelo relacional y normalización

Se define la base gestion_tecnica para MySQL 8.4 como entorno previsto, con tablas InnoDB y codificación utf8mb4. La preparación y conexión local se comprobaron sobre MySQL 9.6.0, manteniendo Connector/J 8.4.0. Los identificadores son claves primarias enteras autoincrementales; las fechas se almacenan como DATETIME y los textos con longitudes explícitas.

**Primera forma normal:** cada columna contiene un valor del atributo y no se almacenan listas de visitas o usuarios dentro de una fila. Las múltiples atenciones se representan mediante registros relacionados.

**Segunda forma normal:** las claves primarias son simples y los atributos de cada tabla dependen de su identificador, sin dependencias parciales de una clave compuesta.

**Tercera forma normal:** los datos del cliente no se repiten en domicilio, servicio o solicitud; la información del usuario no se copia en cada intervención. Para conocer el cliente de una solicitud se recorre `solicitud → servicio → domicilio → cliente`. Los estados y roles son conjuntos de valores controlados, no atributos descriptivos de otras entidades que obliguen a crear catálogos.

El resumen de cierre de `solicitud` se conserva como dato del cierre. Cuando coincide con el detalle de la atención resolutiva existe una redundancia deliberada: ambos se escriben en la misma transacción y luego son inmutables desde la aplicación. No se utiliza esta copia para sustituir el historial.

Las claves foráneas impiden referencias inexistentes y restringen el borrado de registros referenciados. `NOT NULL`, `UNIQUE` y `CHECK` controlan obligatoriedad, unicidad y coherencia de los valores. Se comprueba, por ejemplo, que una solicitud cerrada tenga fecha y solución, y que una asistencia finalizada tenga diagnóstico, tareas y resultado (Oracle, s. f.-d, s. f.-e).

Otras reglas, como exigir un usuario con rol técnico o impedir dos visitas activas, necesitan considerar varias filas y se aplican en los servicios dentro de una transacción; no se atribuyen a una restricción `CHECK` inexistente.

Se incorporan índices por estado y fecha de solicitud, servicio y fecha, técnico/estado/fecha programada y solicitud/fecha de actividad. Las listas no tienen paginación; el cumplimiento de RNF08 exige medir consultas, revisar planes de ejecución y acordar el objetivo de rendimiento.

### 5.2. Diagrama entidad-relación

![Diagrama entidad-relación físico con claves y cardinalidades.](../diagramas/10_entidad_relacion.png)

*Diagrama entidad-relación físico con claves y cardinalidades.*

El diagrama contiene los atributos; sus tipos, longitudes, restricciones y nombres definitivos se encuentran en `sql/01_estructura.sql`. Los campos cliente, domicilio y servicio de los objetos de consulta Java se obtienen mediante uniones; no implican columnas repetidas en `solicitud`.

## 6. Etapa de implementación

### 6.1. Organización del código

El proyecto utiliza Java 21, Swing y Connector/J 8.4.0, con configuración Maven. La estructura permite abrir la carpeta que contiene `pom.xml` desde VS Code. El README detalla preparación y ejecución; la documentación de VS Code y Connector/J sustenta la configuración del proyecto y la dependencia (Microsoft, s. f.; Oracle, s. f.-c).

El código se organiza en ui, servicio, dao, modelo y config; las pruebas se ubican en src/test/java. Las ventanas invocan servicios y mantienen separadas las operaciones de diagnóstico, tareas y finalización.

Las consultas utilizan `PreparedStatement` con parámetros; las conexiones y resultados se cierran mediante `try-with-resources`. Las transacciones coordinan las escrituras relacionadas (Oracle, s. f.-f, s. f.-g). La configuración local se guarda fuera del código y se excluye de Git. Las contraseñas de aplicación se verifican mediante PBKDF2 con sal, no se comparan como texto almacenado.

La implementación de RF17 se reconoce en `AsistenciaServicio.finalizar`: después de las validaciones, actualiza la asistencia, cierra la solicitud únicamente ante `RESUELTO` o la deja en `PENDIENTE_CONTINUIDAD`, registra la actividad y confirma la transacción.

### 6.2. Creación y carga de MySQL

| **Script** | **Contenido** |
|---|---|
| `01_estructura.sql` | Creación de la base, ocho tablas, claves, restricciones e índices. |
| `02_datos_iniciales.sql` | Dos clientes ficticios, domicilios, tres servicios y seis usuarios de demostración. |
| `03_usuario_bd.sql` | Plantilla de cuenta local de conexión y permisos limitados. Se ejecuta una copia local con contraseña propia, excluida de Git. |
| `04_operaciones_y_consultas.sql` | Inserción, modificación, consulta y borrado controlado; consultas funcionales Q01–Q10. |
| `05_pruebas_integridad.sql` | Cinco pruebas negativas para ejecutar separadamente en una base de práctica. |

Los scripts se entregan completos. Como ejemplo de creación, la primera tabla establece su identificador y los datos mínimos de contacto:

```sql
CREATE TABLE cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(30) NOT NULL,
    CONSTRAINT ck_cliente_nombre CHECK (CHAR_LENGTH(TRIM(nombre)) > 0),
    CONSTRAINT ck_cliente_telefono CHECK (CHAR_LENGTH(TRIM(telefono)) > 0)
) ENGINE=InnoDB;
```

La carga inicial no simula solicitudes ya atendidas: estas se generan usando el prototipo o las pruebas de integración. De ese modo puede observarse el recorrido de los datos desde el registro hasta el historial.

### 6.3. Inserción, consulta y borrado

El siguiente ejemplo crea un registro de práctica, consulta su contenido y lo elimina sin afectar antecedentes técnicos. Debe ejecutarse con la cuenta de administración de la base de práctica; la cuenta usada por la aplicación no tiene permiso de borrado.

```sql
START TRANSACTION;
INSERT INTO cliente (nombre, telefono)
VALUES ('Registro temporal para TP2', '000-TEMP');
SET @cliente_temporal = LAST_INSERT_ID();

SELECT id_cliente, nombre, telefono
FROM cliente WHERE id_cliente = @cliente_temporal;

DELETE FROM cliente WHERE id_cliente = @cliente_temporal;
SELECT COUNT(*) AS registros_restantes
FROM cliente WHERE id_cliente = @cliente_temporal;
COMMIT;
```

El resultado esperado de la última consulta es cero. Se trata de un resultado **esperado**, no de una evidencia de ejecución en MySQL. No se ofrece un botón para borrar solicitudes ni visitas finalizadas, en concordancia con RN09.

### 6.4. Consultas funcionales SQL

| **Identificador** | **Consulta y utilidad** |
|---|---|
| Q04 | Solicitudes abiertas, con cliente, domicilio, servicio y estado. |
| Q05 | Asistencias activas de un técnico, ordenadas por fecha. |
| Q06 | Diagnósticos y acciones remotas de una solicitud con su responsable. |
| Q07 | Historial de solicitudes de un servicio, incluidas las cerradas. |
| Q08 | Visitas finalizadas cuyo resultado fue pendiente. |
| Q09 | Cantidad de solicitudes por estado. |
| Q10 | Operaciones, fechas y usuarios del registro de actividad. |

Una consulta directamente vinculada con RF17 es Q08:

```sql
SELECT a.id_asistencia, a.id_solicitud, a.resultado,
       a.detalle_resultado, s.estado AS estado_actual_solicitud
FROM asistencia a
INNER JOIN solicitud s ON s.id_solicitud = a.id_solicitud
WHERE a.estado = 'FINALIZADA' AND a.resultado = 'PENDIENTE';
```

La solicitud podría encontrarse cerrada al consultar posteriormente, si otra atención resolvió el inconveniente. La consulta conserva el resultado histórico de aquella visita y muestra, separadamente, el estado actual.

## 7. Etapa de pruebas

Se vinculan casos de uso y reglas con pruebas unitarias, de integración y recorridos desde Swing, incluyendo situaciones positivas y rechazos por permisos, estados o datos incompletos. Se definen entradas y resultados esperados, se registra lo obtenido y se evalúan diferencias (Battagini, 2026a). Las pruebas se repiten ante cambios; la aceptación requiere validar los recorridos con un usuario representativo.

### 7.1. Casos y procedimientos de prueba

| **Caso** | **Entrada o acción** | **Resultado esperado** | **Verificación preparada** |
|---|---|---|---|
| CP01 | Atención registra un inconveniente para un servicio activo. | Solicitud identificada y actividad registradas. | I03; recorrido Swing. |
| CP02 | Soporte resuelve con diagnóstico, acciones y solución. | Solicitud cerrada sin crear visita. | I04–I05, incluyendo rechazo de datos vacíos. |
| CP03 | Soporte deriva con motivo. Repite la operación. | Una visita activa; segunda derivación rechazada. | I07–I08. |
| CP04 | Coordinación carga fecha y asigna técnico. | Pendiente con un dato faltante; programada con ambos. | U13–U15; I09–I10. |
| CP05 | Un técnico intenta registrar una visita ajena. | Operación rechazada sin modificar el diagnóstico. | U16; I11. |
| CP06 | Finalizar sin diagnóstico, tareas o detalle. | La visita no se finaliza. | U17–U19; I12. |
| CP07 | Finalizar una visita realizada con resultado pendiente. | Visita finalizada y solicitud pendiente de continuidad. | U22; I13. |
| CP08 | Segunda visita resuelve el mismo inconveniente. | Solicitud cerrada y ambas visitas conservadas. | I14–I15. |
| CP09 | Modificar una solicitud cerrada o finalizar dos veces. | Rechazo y conservación del historial. | U08, U24; I06, I16. |
| CP10 | Acceso inválido, consulta de asignaciones ajenas o clave incorrecta. | Rechazo o filtrado según corresponda. | U05–U07, U26; I02, I17. |
| CP11 | Borrar un cliente referenciado o insertar valores inválidos. | Rechazo por integridad referencial, CHECK o unicidad. | BD01–BD05. |
| CP12 | Finalizar una visita programada para el futuro. | Se exige una visita efectivamente realizada. | U23; recorrido Swing. |

Los identificadores U, I y BD corresponden a las comprobaciones implementadas en los archivos entregados; CP identifica el escenario funcional que las agrupa.

**PP01 — Resolución remota (CP01–CP02).** Atención registra una solicitud; soporte documenta diagnóstico, acciones y solución y confirma RESUELTO. Verificar cierre remoto e historial con responsables, sin visita generada.

**PP02 — Visita sin resolución y continuidad (CP03–CP08).** Atención registra; soporte deriva; coordinación programa y asigna. El técnico documenta y finaliza PENDIENTE. Repetir la derivación sobre la misma solicitud y resolver en una segunda visita. Verificar cierre solo al resolver y conservación de ambas intervenciones.

**PP03 — Control de acceso y datos (CP05–CP06).** Con tecnico2, comprobar que una visita de tecnico1 no figure en sus asignaciones; con el asignado, verificar el rechazo al finalizar sin diagnóstico o tareas. La prueba de integración comprueba también el rechazo desde el servicio a un técnico ajeno.

### 7.2. Registro de ejecución de esta versión

| **Comprobación** | **Resultado obtenido** | **Estado** |
|---|---|---|
| Compilación de fuentes principales y de prueba | Compilación inicial con javac --release 21, sin errores. Verificación local adicional con Maven 3.9.16 y Java 21.0.8: BUILD SUCCESS. | Ejecutada. |
| U01–U28 | 28 comprobaciones aprobadas, ninguna fallida; resultado confirmado localmente mediante mvn test. | Ejecutadas. |
| UI01–UI03 | Construcción del login y controles básicos según rol. Tres comprobaciones aprobadas, sin recorrido completo con persistencia. | Ejecutadas, sin MySQL. |
| Preparación de la base local | Scripts 01–03 ejecutados sobre MySQL 9.6.0. Verificadas ocho tablas InnoDB, dos clientes, dos domicilios, tres servicios y seis usuarios. | Ejecutada. |
| Conexión y acceso local | Conexión JDBC cifrada mediante Connector/J 8.4.0 e inicio de sesión de atencion1 comprobados. | Ejecutados. |
| I01–I17 | Pruebas completas preparadas, aún no ejecutadas. La preparación de la base, la conexión y un inicio de sesión no sustituyen estas pruebas. | Pendientes. |
| BD01–BD05 y consultas Q01–Q10 | Scripts preparados; sin resultados de ejecución contra MySQL en esta versión. | Pendientes. |
| PP01–PP03 desde Swing | Procedimientos definidos; recorridos completos con persistencia aún no ejecutados. | Pendientes. |
| Aceptación, carga y rendimiento | No se realizó validación con usuarios ni medición representativa. | Pendientes. |

Las salidas de las comprobaciones iniciales se encuentran en [evidencias/pruebas_unitarias.txt](../evidencias/pruebas_unitarias.txt) y [evidencias/pruebas_interfaz.txt](../evidencias/pruebas_interfaz.txt). El registro [evidencias/ESTADO_VERIFICACION.md](../evidencias/ESTADO_VERIFICACION.md) documenta además la verificación local del 28/09/2026. Las tres capturas originales conservadas en esa carpeta muestran ventanas construidas sin conexión a la base y sin datos operativos; no son comprobantes de funcionamiento integrado.

No se registraron fallas en las comprobaciones ejecutadas. Esto no permite concluir que no existan defectos en la integración pendiente. Todo defecto posterior deberá registrarse indicando caso, pasos, diferencia observada, severidad y estado. Se considerará crítico un cierre incorrecto, pérdida de historial o acceso no autorizado.

**Criterio de aceptación del incremento:** ejecutar los recorridos principales con MySQL, verificar persistencia y permisos, resolver defectos críticos y registrar los resultados. Este criterio todavía no se declara cumplido. El README incluye los comandos para completar la verificación sin modificar el código fuente.

## 8. Definiciones de comunicación

| **Aspecto** | **Definición para el proyecto** |
|---|---|
| Entorno inmediato | Aplicación Swing y MySQL en el mismo equipo de práctica. Conexión a `127.0.0.1:3306`. |
| Acceso a datos | Java utiliza la API JDBC; Connector/J implementa la comunicación con el servidor MySQL. No se utiliza HTTP para esta conexión. |
| Transporte y red | Protocolo cliente-servidor de MySQL sobre TCP/IP. TCP proporciona un flujo ordenado y fiable, con mecanismos de retransmisión (Eddy, 2022). |
| Seguridad del enlace | Configuración local con `sslMode=REQUIRED` y cuenta que exige SSL. Cifra la conexión, pero no equivale a verificar la identidad del servidor. |
| Alternativa de infraestructura | Para varios puestos se propone un servidor interno, switch Ethernet, cableado de categoría 6 y enlaces de 1 Gb/s. Es una propuesta, no infraestructura relevada ni requisito mínimo medido. |
| Enlace de datos | En la alternativa cableada se emplearía Ethernet IEEE 802.3. La demostración por loopback no depende de un enlace Ethernet físico (IEEE 802.3 Working Group, s. f.). |
| Sistemas externos | No se implementan integraciones con facturación, monitoreo ni equipos del cliente. |

En una implantación por red se deberá configurar una dirección estable para el servidor, limitar el puerto de MySQL a equipos autorizados y verificar certificados mediante `VERIFY_IDENTITY`. La documentación de Connector/J diferencia este control de los modos que solo exigen cifrado (Oracle, s. f.-a).

La conexión requiere un archivo local de configuración excluido del repositorio. La cuenta `gt_app` no administra la base ni elimina historiales. Las fechas representan la hora local de la organización; la aplicación ajusta la sesión SQL al desplazamiento horario del equipo. Los puestos deben compartir configuración horaria y relojes sincronizados.

La arquitectura de escritorio con credenciales JDBC no protege por sí misma frente a un cliente modificado que use esas credenciales. Para un despliegue real con límites de seguridad más fuertes se deberá separar el acceso mediante un servicio de aplicación y reforzar autenticación, auditoría y operación. Esa infraestructura no forma parte del prototipo.

## 9. Conclusión y repositorio

La entrega integra análisis, diseño, persistencia e implementación del circuito seleccionado. La separación entre solicitud y asistencia permite conservar visitas sin resolución y continuar la atención; el historial identifica las intervenciones y sus responsables.

Las verificaciones realizadas y pendientes se detallan en la [sección 7.2](#72-registro-de-ejecución-de-esta-versión). La integración completa, los recorridos desde Swing y la aceptación deben completarse antes de considerar validado el prototipo.

**Repositorio del proyecto:** [seminario_practica_informatica_gestion_tecnica — rama master](https://github.com/gabrielbatta/seminario_practica_informatica_gestion_tecnica/tree/master).

## Referencias

Battagini, G. A. (2026a). *Análisis y diseño de software: Trabajo práctico 4* [Trabajo académico]. Universidad Siglo 21.

Battagini, G. A. (2026b). *Sistema de Gestión y Seguimiento de Asistencias Técnicas para una empresa que presta servicios de Internet y televisión por cable* [Trabajo práctico 1, Seminario de Práctica de Informática]. Universidad Siglo 21.

Eddy, W. (Ed.). (2022). *Transmission Control Protocol (TCP)* (RFC 9293). Internet Engineering Task Force. [https://www.rfc-editor.org/rfc/rfc9293.html](https://www.rfc-editor.org/rfc/rfc9293.html)

IEEE 802.3 Working Group. (s. f.). *IEEE 802.3 Ethernet*. [https://www.ieee802.org/3/](https://www.ieee802.org/3/)

Microsoft. (s. f.). *Managing Java projects in VS Code*. [https://code.visualstudio.com/docs/java/java-project](https://code.visualstudio.com/docs/java/java-project)

Oracle. (s. f.-a). *Connecting securely using SSL*. [https://dev.mysql.com/doc/connector-j/en/connector-j-reference-using-ssl.html](https://dev.mysql.com/doc/connector-j/en/connector-j-reference-using-ssl.html)

Oracle. (s. f.-b). *Concurrency in Swing*. [https://docs.oracle.com/javase/tutorial/uiswing/concurrency/index.html](https://docs.oracle.com/javase/tutorial/uiswing/concurrency/index.html)

Oracle. (s. f.-c). *Installing Connector/J using Maven*. [https://dev.mysql.com/doc/connector-j/en/connector-j-installing-maven.html](https://dev.mysql.com/doc/connector-j/en/connector-j-installing-maven.html)

Oracle. (s. f.-d). *MySQL 8.4 Reference Manual: CHECK constraints*. [https://dev.mysql.com/doc/refman/8.4/en/create-table-check-constraints.html](https://dev.mysql.com/doc/refman/8.4/en/create-table-check-constraints.html)

Oracle. (s. f.-e). *MySQL 8.4 Reference Manual: FOREIGN KEY constraints*. [https://dev.mysql.com/doc/refman/8.4/en/create-table-foreign-keys.html](https://dev.mysql.com/doc/refman/8.4/en/create-table-foreign-keys.html)

Oracle. (s. f.-f). *Using prepared statements*. [https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html](https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html)

Oracle. (s. f.-g). *Using transactions*. [https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html](https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html)
