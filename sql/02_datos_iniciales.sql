-- Datos exclusivamente ficticios. Ejecutar una sola vez, después de 01_estructura.sql.
-- Todas las cuentas de demostración usan DemoTP2!2026. No son claves de producción.
-- Cada hash PBKDF2 contiene iteraciones, sal y resultado; las sales aquí son públicas.
USE gestion_tecnica;
START TRANSACTION;
INSERT INTO cliente (id_cliente, nombre, telefono) VALUES
(1, 'Cliente de prueba A', '000-0001'),
(2, 'Cliente de prueba B', '000-0002');
INSERT INTO domicilio (id_domicilio, id_cliente, direccion, localidad) VALUES
(1, 1, 'Domicilio ficticio 100', 'Neuquén'),
(2, 2, 'Domicilio ficticio 200', 'Neuquén');
INSERT INTO servicio (id_servicio, id_domicilio, codigo, tipo, activo) VALUES
(1, 1, 'DEMO-INT-001', 'INTERNET', TRUE),
(2, 1, 'DEMO-TV-001', 'TELEVISION', TRUE),
(3, 2, 'DEMO-INT-002', 'INTERNET', TRUE);
INSERT INTO usuario (id_usuario, nombre, login, clave_hash, rol, activo) VALUES
(1, 'Operador de atención', 'atencion1', '600000:gMpmfBgUewjcv5LsEsh4hw==:ySrek/xFdk7AAJz/yL2ZyFalxe52cpEX0VaZxl0LDQI=', 'ATENCION', TRUE),
(2, 'Operador de soporte', 'soporte1', '600000:5W0DxdzCwZYM8vev17xBWQ==:FVMPZhhRL9jOF46Ta9Lop9k45hxbb1SNnur0z6fi7PA=', 'SOPORTE', TRUE),
(3, 'Coordinador técnico', 'coordinador1', '600000:lw+q7yp8xQoTVaaSf7XBeg==:g64rF6lIEQq3DDDQdwKbYBlU9fCKNufH/NhklOMjkrk=', 'COORDINADOR', TRUE),
(4, 'Técnico de prueba 1', 'tecnico1', '600000:yvLClvlic17Jo5vN4jhu0g==:JXfI95V/SGTYO7cOtuExwoxV2vMeAfHZ4Xglnpu4w4I=', 'TECNICO', TRUE),
(5, 'Técnico de prueba 2', 'tecnico2', '600000:sjAqVxbhOiUzEMjUBIkdjw==:OB/bKT9gOk7DQV7H3F/63qgtceOBtFr4XCIPpRvBW0Q=', 'TECNICO', TRUE),
(6, 'Administrador de prueba', 'admin1', '600000:Vxaf9cvvRtaJZ5boe3MtEQ==:vDe4spK9zPCjdD5vr52dfxWYTaOzm8qTddAUq/ux4Ks=', 'ADMINISTRADOR', TRUE);
COMMIT;
-- Las solicitudes se crean desde Swing o durante las pruebas de integración.
