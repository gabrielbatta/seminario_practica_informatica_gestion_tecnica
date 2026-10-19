-- Datos exclusivamente ficticios. Ejecutar una sola vez, después de 01_estructura.sql.
-- Todas las cuentas de demostración usan 1234. No son claves de producción.
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
(1, 'Operador de atención', 'atencion1', '600000:OCh4HzfOCUApKN1qGYwUBQ==:Vuib4QLWiNquyCsDo4pBrFtpUwf441MBoZVnWvvpmQQ=', 'ATENCION', TRUE),
(2, 'Operador de soporte', 'soporte1', '600000:68BWizDMaGh7bhBehi2xOA==:Sr7/OfZsoCMeJXNENaOYVfTr+iLRGRBvkU2g6bHKc2s=', 'SOPORTE', TRUE),
(3, 'Coordinador técnico', 'coordinador1', '600000:EzfE4j+JKEZn8UmYyltPRw==:OgBmPW8kivoSGkqzIcAKyJNOsP4QBDs1crLQMlfUTpI=', 'COORDINADOR', TRUE),
(4, 'Técnico de prueba 1', 'tecnico1', '600000:yJ9u3yWzzDM8R5qElMHZEQ==:t9bQjWYH5DB3WIRgZ2YYm7MT9X096ugU04X3aLJHbZg=', 'TECNICO', TRUE),
(5, 'Técnico de prueba 2', 'tecnico2', '600000:TCcjMiRcYow1zS+acX3TUg==:7kd5UoM9786MS77b8Mtx5OuyWGwb1w3pWDj3mzQx6BM=', 'TECNICO', TRUE),
(6, 'Administrador de prueba', 'admin1', '600000:BVbRb7UspG8HwXkxCuRIiQ==:pdBgCUfvGRu9TRrGOsrvIrbXZNHJGGYp+21A0JGeAU0=', 'ADMINISTRADOR', TRUE);
COMMIT;
-- Las solicitudes se crean desde Swing o durante las pruebas de integración.
