-- Ejecutar CADA BLOQUE por separado con cuenta administradora en MySQL 8.4.
-- Las sentencias inválidas deben producir error. Siempre finalizar con ROLLBACK.
USE gestion_tecnica;

-- BD01: cliente referenciado. Esperado: error de clave foránea, no se elimina.
START TRANSACTION;
DELETE FROM cliente WHERE id_cliente = 1;
ROLLBACK;

-- BD02: solicitud huérfana. Esperado: error de clave foránea.
START TRANSACTION;
INSERT INTO solicitud (id_servicio, id_usuario_registro, descripcion)
VALUES (2147483647, 1, 'No debe insertarse');
ROLLBACK;

-- BD03: estado fuera del dominio. Esperado: rechazo por CHECK.
START TRANSACTION;
INSERT INTO solicitud (id_servicio, id_usuario_registro, descripcion, estado)
VALUES (1, 1, 'Estado inválido de prueba', 'DESCONOCIDO');
ROLLBACK;

-- BD04: cierre sin resolución. Esperado: rechazo por CHECK.
START TRANSACTION;
INSERT INTO solicitud (id_servicio, id_usuario_registro, descripcion, estado)
VALUES (1, 1, 'Cierre incompleto de prueba', 'CERRADA');
ROLLBACK;

-- BD05: login duplicado. Esperado: rechazo por UNIQUE.
START TRANSACTION;
INSERT INTO usuario (nombre, login, clave_hash, rol)
VALUES ('Duplicado temporal', 'atencion1', 'NoSeUsa', 'ATENCION');
ROLLBACK;
