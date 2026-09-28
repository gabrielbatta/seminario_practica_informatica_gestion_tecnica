-- Plantilla: copiar como 03_usuario_bd.local.sql y cambiar la clave en esa copia.
-- Ejecutar la copia con una cuenta administradora. *.local.sql está excluido de Git.
-- Si gt_app ya existe, NO reemplazarla automáticamente: revisar su configuración.
USE gestion_tecnica;
CREATE USER 'gt_app'@'localhost' IDENTIFIED BY 'CAMBIAR_CLAVE_LOCAL' REQUIRE SSL;

-- Lectura de catálogos. El prototipo no administra clientes ni usuarios desde Swing.
GRANT SELECT ON gestion_tecnica.cliente TO 'gt_app'@'localhost';
GRANT SELECT ON gestion_tecnica.domicilio TO 'gt_app'@'localhost';
GRANT SELECT ON gestion_tecnica.servicio TO 'gt_app'@'localhost';
GRANT SELECT ON gestion_tecnica.usuario TO 'gt_app'@'localhost';

-- Operaciones necesarias: no otorgar DELETE ni permisos de modificación de estructura.
GRANT SELECT, INSERT, UPDATE ON gestion_tecnica.solicitud TO 'gt_app'@'localhost';
GRANT SELECT, INSERT ON gestion_tecnica.atencion_remota TO 'gt_app'@'localhost';
GRANT SELECT, INSERT, UPDATE ON gestion_tecnica.asistencia TO 'gt_app'@'localhost';
GRANT SELECT, INSERT ON gestion_tecnica.registro_actividad TO 'gt_app'@'localhost';
