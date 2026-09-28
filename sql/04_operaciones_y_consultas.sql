-- Ejecutar con cuenta administradora en la base de práctica.
-- Q01 a Q03 demuestran INSERT, UPDATE y DELETE sin eliminar historiales.
USE gestion_tecnica;
START TRANSACTION;
-- Q01: insertar un cliente temporal SIN relaciones dependientes.
INSERT INTO cliente (nombre, telefono) VALUES ('Registro temporal para TP2', '000-TEMP');
SET @cliente_temporal = LAST_INSERT_ID();
SELECT id_cliente, nombre, telefono FROM cliente WHERE id_cliente = @cliente_temporal;
-- Q02: actualizar y verificar ese mismo registro.
UPDATE cliente SET telefono = '000-EDITADO' WHERE id_cliente = @cliente_temporal;
SELECT id_cliente, nombre, telefono FROM cliente WHERE id_cliente = @cliente_temporal;
-- Q03: borrar solo el registro recién creado y comprobar su ausencia.
DELETE FROM cliente WHERE id_cliente = @cliente_temporal;
SELECT COUNT(*) AS registros_restantes FROM cliente WHERE id_cliente = @cliente_temporal;
COMMIT;

-- Q04: solicitudes abiertas con cliente, domicilio y servicio.
SELECT s.id_solicitud, c.nombre AS cliente, d.direccion, v.codigo AS servicio,
       s.descripcion, s.estado, s.fecha_registro
FROM solicitud s
INNER JOIN servicio v ON v.id_servicio = s.id_servicio
INNER JOIN domicilio d ON d.id_domicilio = v.id_domicilio
INNER JOIN cliente c ON c.id_cliente = d.id_cliente
WHERE s.estado <> 'CERRADA'
ORDER BY s.fecha_registro, s.id_solicitud;

-- Q05: visitas activas asignadas al técnico de prueba 1.
SET @tecnico = 4;
SELECT a.id_asistencia, a.id_solicitud, a.fecha_programada, a.estado, a.motivo
FROM asistencia a
WHERE a.id_tecnico = @tecnico AND a.estado <> 'FINALIZADA'
ORDER BY a.fecha_programada, a.id_asistencia;

-- Q06: antecedentes remotos de una solicitud (usar un ID existente).
SET @solicitud = (SELECT MIN(id_solicitud) FROM solicitud);
SELECT r.fecha, u.nombre AS responsable, r.diagnostico, r.acciones, r.resultado, r.solucion
FROM atencion_remota r
INNER JOIN usuario u ON u.id_usuario = r.id_usuario
WHERE r.id_solicitud = @solicitud
ORDER BY r.fecha, r.id_atencion;

-- Q07: historial de solicitudes de un servicio, incluidas las cerradas.
SET @servicio = 1;
SELECT id_solicitud, descripcion, estado, fecha_registro,
       fecha_cierre, canal_cierre, solucion_cierre
FROM solicitud WHERE id_servicio = @servicio
ORDER BY fecha_registro, id_solicitud;

-- Q08: visitas finalizadas que NO resolvieron el inconveniente.
SELECT a.id_asistencia, a.id_solicitud, a.resultado, a.detalle_resultado,
       s.estado AS estado_actual_solicitud
FROM asistencia a
INNER JOIN solicitud s ON s.id_solicitud = a.id_solicitud
WHERE a.estado = 'FINALIZADA' AND a.resultado = 'PENDIENTE';

-- Q09: cantidades por estado. El agrupamiento no duplica datos persistentes.
SELECT estado, COUNT(*) AS cantidad FROM solicitud GROUP BY estado ORDER BY estado;

-- Q10: auditoría de la solicitud elegida.
SELECT r.fecha, u.login, r.operacion, r.detalle
FROM registro_actividad r
INNER JOIN usuario u ON u.id_usuario = r.id_usuario
WHERE r.id_solicitud = @solicitud
ORDER BY r.fecha, r.id_registro;
