-- TP2 - Estructura para MySQL 8.4. Ejecutar con una cuenta administradora.
-- No elimina bases ni tablas existentes. Ejecutar una sola vez en una base nueva.
CREATE DATABASE IF NOT EXISTS gestion_tecnica
    CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE gestion_tecnica;

-- 1. Datos básicos. Un domicilio pertenece a un cliente.
CREATE TABLE cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(30) NOT NULL,
    CONSTRAINT ck_cliente_nombre CHECK (CHAR_LENGTH(TRIM(nombre)) > 0),
    CONSTRAINT ck_cliente_telefono CHECK (CHAR_LENGTH(TRIM(telefono)) > 0)
) ENGINE=InnoDB;

CREATE TABLE domicilio (
    id_domicilio INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    direccion VARCHAR(150) NOT NULL,
    localidad VARCHAR(80) NOT NULL,
    CONSTRAINT fk_domicilio_cliente FOREIGN KEY (id_cliente)
        REFERENCES cliente(id_cliente) ON DELETE RESTRICT,
    CONSTRAINT ck_domicilio_direccion CHECK (CHAR_LENGTH(TRIM(direccion)) > 0),
    CONSTRAINT ck_domicilio_localidad CHECK (CHAR_LENGTH(TRIM(localidad)) > 0)
) ENGINE=InnoDB;

CREATE TABLE servicio (
    id_servicio INT AUTO_INCREMENT PRIMARY KEY,
    id_domicilio INT NOT NULL,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    tipo VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_servicio_domicilio FOREIGN KEY (id_domicilio)
        REFERENCES domicilio(id_domicilio) ON DELETE RESTRICT,
    CONSTRAINT ck_servicio_tipo CHECK (tipo IN ('INTERNET', 'TELEVISION'))
) ENGINE=InnoDB;

-- 2. Los roles son un conjunto fijo del prototipo. No se guardan contraseñas en claro.
CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    login VARCHAR(40) NOT NULL UNIQUE,
    clave_hash VARCHAR(200) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_usuario_rol CHECK
        (rol IN ('ATENCION', 'SOPORTE', 'COORDINADOR', 'TECNICO', 'ADMINISTRADOR'))
) ENGINE=InnoDB;

-- 3. La solicitud identifica el inconveniente, no una visita.
CREATE TABLE solicitud (
    id_solicitud INT AUTO_INCREMENT PRIMARY KEY,
    id_servicio INT NOT NULL,
    id_usuario_registro INT NOT NULL,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descripcion VARCHAR(1000) NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'REGISTRADA',
    fecha_cierre DATETIME NULL,
    canal_cierre VARCHAR(20) NULL,
    solucion_cierre VARCHAR(2000) NULL,
    CONSTRAINT fk_solicitud_servicio FOREIGN KEY (id_servicio)
        REFERENCES servicio(id_servicio) ON DELETE RESTRICT,
    CONSTRAINT fk_solicitud_usuario FOREIGN KEY (id_usuario_registro)
        REFERENCES usuario(id_usuario) ON DELETE RESTRICT,
    CONSTRAINT ck_solicitud_descripcion CHECK (CHAR_LENGTH(TRIM(descripcion)) > 0),
    CONSTRAINT ck_solicitud_estado CHECK (estado IN
        ('REGISTRADA', 'EN_ATENCION_REMOTA', 'DERIVADA', 'PENDIENTE_CONTINUIDAD', 'CERRADA')),
    CONSTRAINT ck_solicitud_cierre CHECK (
        (estado = 'CERRADA' AND fecha_cierre IS NOT NULL
         AND canal_cierre IS NOT NULL AND canal_cierre IN ('REMOTA', 'DOMICILIARIA')
         AND solucion_cierre IS NOT NULL AND CHAR_LENGTH(TRIM(solucion_cierre)) > 0)
        OR
        (estado <> 'CERRADA' AND fecha_cierre IS NULL
         AND canal_cierre IS NULL AND solucion_cierre IS NULL)
    ),
    CONSTRAINT ck_solicitud_fechas CHECK
        (fecha_cierre IS NULL OR fecha_cierre >= fecha_registro),
    INDEX ix_solicitud_estado_fecha (estado, fecha_registro),
    INDEX ix_solicitud_servicio_fecha (id_servicio, fecha_registro)
) ENGINE=InnoDB;

CREATE TABLE atencion_remota (
    id_atencion INT AUTO_INCREMENT PRIMARY KEY,
    id_solicitud INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    diagnostico VARCHAR(2000) NOT NULL,
    acciones VARCHAR(2000) NOT NULL,
    resultado VARCHAR(15) NOT NULL,
    solucion VARCHAR(2000) NULL,
    CONSTRAINT fk_remota_solicitud FOREIGN KEY (id_solicitud)
        REFERENCES solicitud(id_solicitud) ON DELETE RESTRICT,
    CONSTRAINT fk_remota_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario) ON DELETE RESTRICT,
    CONSTRAINT ck_remota_diagnostico CHECK (CHAR_LENGTH(TRIM(diagnostico)) > 0),
    CONSTRAINT ck_remota_acciones CHECK (CHAR_LENGTH(TRIM(acciones)) > 0),
    CONSTRAINT ck_remota_resultado CHECK (resultado IN ('RESUELTO', 'PENDIENTE')),
    CONSTRAINT ck_remota_solucion CHECK (
        (resultado = 'RESUELTO' AND solucion IS NOT NULL AND CHAR_LENGTH(TRIM(solucion)) > 0)
        OR (resultado = 'PENDIENTE' AND solucion IS NULL)
    ),
    INDEX ix_remota_solicitud_fecha (id_solicitud, fecha)
) ENGINE=InnoDB;

CREATE TABLE asistencia (
    id_asistencia INT AUTO_INCREMENT PRIMARY KEY,
    id_solicitud INT NOT NULL,
    id_tecnico INT NULL,
    motivo VARCHAR(1000) NOT NULL,
    fecha_programada DATETIME NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    diagnostico VARCHAR(2000) NULL,
    tareas VARCHAR(2000) NULL,
    resultado VARCHAR(15) NULL,
    detalle_resultado VARCHAR(2000) NULL,
    fecha_finalizacion DATETIME NULL,
    CONSTRAINT fk_asistencia_solicitud FOREIGN KEY (id_solicitud)
        REFERENCES solicitud(id_solicitud) ON DELETE RESTRICT,
    -- Se omite una acción referencial explícita: se conserva el comportamiento
    -- restrictivo predeterminado, y la columna participa en el CHECK siguiente.
    CONSTRAINT fk_asistencia_tecnico FOREIGN KEY (id_tecnico)
        REFERENCES usuario(id_usuario),
    CONSTRAINT ck_asistencia_motivo CHECK (CHAR_LENGTH(TRIM(motivo)) > 0),
    CONSTRAINT ck_asistencia_estado CHECK (estado IN ('PENDIENTE', 'PROGRAMADA', 'FINALIZADA')),
    CONSTRAINT ck_asistencia_programacion CHECK (
        (estado = 'PENDIENTE' AND (id_tecnico IS NULL OR fecha_programada IS NULL))
        OR (estado IN ('PROGRAMADA', 'FINALIZADA') AND id_tecnico IS NOT NULL
            AND fecha_programada IS NOT NULL)
    ),
    CONSTRAINT ck_asistencia_finalizacion CHECK (
        (estado <> 'FINALIZADA' AND resultado IS NULL AND detalle_resultado IS NULL
            AND fecha_finalizacion IS NULL)
        OR (estado = 'FINALIZADA' AND resultado IS NOT NULL
            AND resultado IN ('RESUELTO', 'PENDIENTE')
            AND diagnostico IS NOT NULL AND CHAR_LENGTH(TRIM(diagnostico)) > 0
            AND tareas IS NOT NULL AND CHAR_LENGTH(TRIM(tareas)) > 0
            AND detalle_resultado IS NOT NULL AND CHAR_LENGTH(TRIM(detalle_resultado)) > 0
            AND fecha_finalizacion IS NOT NULL AND fecha_finalizacion >= fecha_programada)
    ),
    INDEX ix_asistencia_tecnico_estado_fecha (id_tecnico, estado, fecha_programada),
    INDEX ix_asistencia_solicitud_estado (id_solicitud, estado)
) ENGINE=InnoDB;

-- 4. Registro independiente: deja rastro de altas, estados, asignación y cierre.
CREATE TABLE registro_actividad (
    id_registro INT AUTO_INCREMENT PRIMARY KEY,
    id_solicitud INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    operacion VARCHAR(50) NOT NULL,
    detalle VARCHAR(2000) NOT NULL,
    CONSTRAINT fk_actividad_solicitud FOREIGN KEY (id_solicitud)
        REFERENCES solicitud(id_solicitud) ON DELETE RESTRICT,
    CONSTRAINT fk_actividad_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario) ON DELETE RESTRICT,
    INDEX ix_actividad_solicitud_fecha (id_solicitud, fecha)
) ENGINE=InnoDB;
