-- =====================================================
-- Script SQL: Sistema de Alquiler de Vehículos
-- Base de Datos: MySQL 8.x
-- =====================================================

-- Crear base de datos
DROP DATABASE IF EXISTS db_alquilape;
CREATE DATABASE db_alquilape CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_alquilape;

-- =====================================================
-- CREACIÓN DE TABLAS
-- =====================================================

-- Tabla: usuarios
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    nombre_completo VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_rol (rol)
) ENGINE=InnoDB;

-- Tabla: clientes
CREATE TABLE clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100),
    dni_ruc VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(20) NOT NULL,
    direccion VARCHAR(200),
    tipo VARCHAR(20) NOT NULL,
    licencia_numero VARCHAR(20),
    licencia_vencimiento DATE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    INDEX idx_dni_ruc (dni_ruc),
    INDEX idx_email (email),
    INDEX idx_nombre_apellido (nombre, apellido),
    INDEX idx_tipo (tipo)
) ENGINE=InnoDB;

-- Tabla: vehiculos
CREATE TABLE vehiculos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    anio INT NOT NULL,
    placa VARCHAR(10) NOT NULL UNIQUE,
    color VARCHAR(30),
    tipo VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
    precio_por_dia DECIMAL(10,2) NOT NULL,
    kilometraje INT NOT NULL DEFAULT 0,
    capacidad_pasajeros INT NOT NULL,
    caracteristicas_adicionales JSON,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    INDEX idx_placa (placa),
    INDEX idx_marca_modelo (marca, modelo),
    INDEX idx_tipo_estado (tipo, estado),
    INDEX idx_precio (precio_por_dia)
) ENGINE=InnoDB;

-- Tabla: alquileres
CREATE TABLE alquileres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    vehiculo_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    fecha_inicio DATETIME NOT NULL,
    fecha_fin_estimada DATETIME NOT NULL,
    fecha_devolucion_real DATETIME,
    precio_total DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    kilometraje_inicio INT NOT NULL,
    kilometraje_fin INT,
    observaciones TEXT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    FOREIGN KEY (vehiculo_id) REFERENCES vehiculos(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    INDEX idx_cliente (cliente_id),
    INDEX idx_vehiculo (vehiculo_id),
    INDEX idx_usuario (usuario_id),
    INDEX idx_estado (estado),
    INDEX idx_fechas (fecha_inicio, fecha_fin_estimada)
) ENGINE=InnoDB;

-- Tabla: pagos
CREATE TABLE pagos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alquiler_id BIGINT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    fecha_pago DATETIME NOT NULL,
    metodo_pago VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    numero_transaccion VARCHAR(100),
    observaciones TEXT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    FOREIGN KEY (alquiler_id) REFERENCES alquileres(id),
    INDEX idx_alquiler (alquiler_id),
    INDEX idx_estado (estado),
    INDEX idx_fecha (fecha_pago)
) ENGINE=InnoDB;

-- =====================================================
-- INSERCIÓN DE DATOS DE PRUEBA
-- =====================================================

-- Usuarios (contraseña: password123 - encriptada con BCrypt)
INSERT INTO usuarios (username, password, email, nombre_completo, rol, activo, created_by) VALUES
('admin', '$2a$10$3yWIv23IID9tOkUS8tXdJ.J8uob.D8nHUi2D2nHDp8JswJRpLlmkS', 'admin@empresa.com', 'Administrador del Sistema', 'ADMIN', TRUE, 'SYSTEM'),
('supervisor1', '$2a$10$hEAeU5TUB.zLPU76otay9uQkk1xee.RbV7OOpfAYo5xq7i7J0KAw.', 'supervisor@empresa.com', 'Juan Pérez Supervisor', 'SUPERVISOR', TRUE, 'SYSTEM'),
('asistente1', '$2a$10$QnT41dECiaYJ6HBAd3Vxv.CcGNxIZdQa3bEe/6xb/3wM2r3Nt/a5q', 'asistente@empresa.com', 'María González Asistente', 'ASISTENTE', TRUE, 'SYSTEM');

-- Clientes
INSERT INTO clientes (nombre, apellido, dni_ruc, email, telefono, direccion, tipo, licencia_numero, licencia_vencimiento, created_by) VALUES
('Carlos', 'Rodríguez', '12345678', 'carlos.rodriguez@email.com', '987654321', 'Av. Principal 123, Lima', 'PERSONA', 'L12345678', '2026-12-31', 'admin'),
('Ana', 'Martínez', '87654321', 'ana.martinez@email.com', '987123456', 'Jr. Comercio 456, Lima', 'PERSONA', 'L87654321', '2025-06-30', 'admin'),
('Tech Solutions SAC', NULL, '20123456789', 'contacto@techsolutions.com', '014567890', 'Av. Tecnología 789, San Isidro', 'EMPRESA', 'L99887766', '2027-03-15', 'admin'),
('Luis', 'García', '45678912', 'luis.garcia@email.com', '956781234', 'Calle Los Olivos 321, Miraflores', 'PERSONA', 'L45678912', '2026-08-20', 'admin'),
('Patricia', 'Torres', '78945612', 'patricia.torres@email.com', '945612378', 'Av. Arequipa 654, Lince', 'PERSONA', 'L78945612', '2025-12-10', 'admin');

-- Vehículos
INSERT INTO vehiculos (marca, modelo, anio, placa, color, tipo, estado, precio_por_dia, kilometraje, capacidad_pasajeros, caracteristicas_adicionales, created_by) VALUES
('Toyota', 'Corolla', 2023, 'ABC123', 'Blanco', 'AUTO', 'DISPONIBLE', 150.00, 15000, 5, '{"aire_acondicionado": true, "bluetooth": true, "camara_reversa": true}', 'admin'),
('Honda', 'Civic', 2024, 'DEF456', 'Negro', 'AUTO', 'DISPONIBLE', 180.00, 5000, 5, '{"aire_acondicionado": true, "bluetooth": true, "sensores_parking": true}', 'admin'),
('Nissan', 'Sentra', 2022, 'GHI789', 'Gris', 'AUTO', 'DISPONIBLE', 140.00, 25000, 5, '{"aire_acondicionado": true, "usb": true}', 'admin'),
('Hyundai', 'Tucson', 2023, 'JKL012', 'Rojo', 'CAMIONETA', 'DISPONIBLE', 220.00, 12000, 7, '{"aire_acondicionado": true, "4x4": true, "techo_panoramico": true}', 'admin'),
('Ford', 'Ranger', 2024, 'MNO345', 'Azul', 'CAMIONETA', 'DISPONIBLE', 250.00, 8000, 5, '{"aire_acondicionado": true, "4x4": true, "caja_carga": true}', 'admin'),
('Chevrolet', 'Tracker', 2023, 'PQR678', 'Plata', 'CAMIONETA', 'DISPONIBLE', 200.00, 18000, 5, '{"aire_acondicionado": true, "pantalla_tactil": true}', 'admin'),
('Kia', 'Sportage', 2022, 'STU901', 'Blanco', 'CAMIONETA', 'DISPONIBLE', 210.00, 22000, 7, '{"aire_acondicionado": true, "asientos_cuero": true}', 'admin'),
('Mazda', '3', 2024, 'VWX234', 'Negro', 'AUTO', 'DISPONIBLE', 170.00, 3000, 5, '{"aire_acondicionado": true, "carplay": true, "sistema_bose": true}', 'admin'),
('Toyota', 'Hilux', 2023, 'YZA567', 'Gris', 'CAMIONETA', 'MANTENIMIENTO', 280.00, 30000, 5, '{"aire_acondicionado": true, "4x4": true, "traccion_diferencial": true}', 'admin'),
('Honda', 'CR-V', 2024, 'BCD890', 'Rojo', 'CAMIONETA', 'DISPONIBLE', 240.00, 6000, 7, '{"aire_acondicionado": true, "honda_sensing": true, "sunroof": true}', 'admin');

-- Alquileres (algunos completados, algunos activos)
INSERT INTO alquileres (cliente_id, vehiculo_id, usuario_id, fecha_inicio, fecha_fin_estimada, fecha_devolucion_real, precio_total, estado, kilometraje_inicio, kilometraje_fin, observaciones, created_by) VALUES
-- Alquileres completados
(1, 1, 1, '2024-12-01 09:00:00', '2024-12-05 09:00:00', '2024-12-05 10:30:00', 600.00, 'COMPLETADO', 15000, 15320, 'Devolución puntual. Vehículo en buen estado.', 'supervisor1'),
(2, 3, 2, '2024-12-03 14:00:00', '2024-12-07 14:00:00', '2024-12-07 13:45:00', 560.00, 'COMPLETADO', 25000, 25180, 'Cliente satisfecho con el servicio.', 'supervisor1'),
(3, 4, 1, '2024-12-05 08:00:00', '2024-12-10 08:00:00', '2024-12-10 09:00:00', 1100.00, 'COMPLETADO', 12000, 12450, 'Usado para viaje corporativo. Sin novedades.', 'admin'),
(4, 6, 2, '2024-12-08 10:00:00', '2024-12-11 10:00:00', '2024-12-11 11:30:00', 600.00, 'COMPLETADO', 18000, 18220, 'Excelente estado del vehículo.', 'supervisor1'),

-- Alquileres activos
(1, 2, 1, '2024-12-15 09:00:00', '2024-12-22 09:00:00', NULL, 1260.00, 'ACTIVO', 5000, NULL, 'Viaje familiar programado.', 'admin'),
(5, 5, 2, '2024-12-16 08:00:00', '2024-12-20 08:00:00', NULL, 1000.00, 'ACTIVO', 8000, NULL, 'Requiere vehículo con capacidad de carga.', 'supervisor1'),
(2, 7, 1, '2024-12-17 14:00:00', '2024-12-24 14:00:00', NULL, 1470.00, 'ACTIVO', 22000, NULL, 'Viaje de vacaciones a la playa.', 'admin'),
(3, 10, 2, '2024-12-18 10:00:00', '2024-12-25 10:00:00', NULL, 1680.00, 'ACTIVO', 6000, NULL, 'Transporte ejecutivo.', 'supervisor1'),

-- Alquiler cancelado
(4, 8, 1, '2024-12-12 09:00:00', '2024-12-16 09:00:00', NULL, 680.00, 'CANCELADO', 3000, NULL, 'Cliente canceló por cambio de planes.', 'admin'),

-- Alquiler antiguo completado
(5, 1, 2, '2024-11-20 09:00:00', '2024-11-25 09:00:00', '2024-11-25 10:00:00', 750.00, 'COMPLETADO', 14500, 14820, 'Sin novedades.', 'supervisor1');

-- Actualizar estado de vehículos alquilados
UPDATE vehiculos SET estado = 'ALQUILADO' WHERE id IN (2, 5, 7, 10);

-- Pagos
INSERT INTO pagos (alquiler_id, monto, fecha_pago, metodo_pago, estado, numero_transaccion, created_by) VALUES
-- Pagos completados
(1, 600.00, '2024-12-01 09:30:00', 'TARJETA', 'PAGADO', 'TXN001234567', 'supervisor1'),
(2, 560.00, '2024-12-03 14:30:00', 'EFECTIVO', 'PAGADO', NULL, 'supervisor1'),
(3, 1100.00, '2024-12-05 08:30:00', 'TRANSFERENCIA', 'PAGADO', 'TXN001234568', 'admin'),
(4, 600.00, '2024-12-08 10:30:00', 'YAPE', 'PAGADO', 'YPE987654321', 'supervisor1'),
(10, 750.00, '2024-11-20 09:30:00', 'TARJETA', 'PAGADO', 'TXN001234566', 'supervisor1'),

-- Pagos pendientes (alquileres activos)
(5, 1260.00, '2024-12-15 09:30:00', 'TARJETA', 'PENDIENTE', NULL, 'admin'),
(6, 1000.00, '2024-12-16 08:30:00', 'TRANSFERENCIA', 'PENDIENTE', NULL, 'supervisor1'),
(7, 1470.00, '2024-12-17 14:30:00', 'EFECTIVO', 'PENDIENTE', NULL, 'admin'),
(8, 1680.00, '2024-12-18 10:30:00', 'TARJETA', 'PENDIENTE', NULL, 'supervisor1');

-- =====================================================
-- CONSULTAS DE VERIFICACIÓN
-- =====================================================

-- Verificar usuarios
SELECT id, username, email, nombre_completo, rol FROM usuarios;

-- Verificar clientes
SELECT id, CONCAT(nombre, ' ', COALESCE(apellido, '')) as nombre_completo, dni_ruc, tipo FROM clientes;

-- Verificar vehículos disponibles
SELECT id, marca, modelo, placa, tipo, estado, precio_por_dia FROM vehiculos WHERE estado = 'DISPONIBLE';

-- Verificar alquileres activos
SELECT a.id, c.nombre as cliente, v.placa, v.marca, v.modelo, a.fecha_inicio, a.fecha_fin_estimada, a.precio_total
FROM alquileres a
JOIN clientes c ON a.cliente_id = c.id
JOIN vehiculos v ON a.vehiculo_id = v.id
WHERE a.estado = 'ACTIVO';

-- Verificar pagos pendientes
SELECT p.id, a.id as alquiler_id, p.monto, p.metodo_pago, p.estado
FROM pagos p
JOIN alquileres a ON p.alquiler_id = a.id
WHERE p.estado = 'PENDIENTE';

-- Estadísticas generales
SELECT
    'Total Usuarios' as concepto,
    COUNT(*) as cantidad
FROM usuarios
UNION ALL
SELECT
    'Total Clientes',
    COUNT(*)
FROM clientes
UNION ALL
SELECT
    'Total Vehículos',
    COUNT(*)
FROM vehiculos
UNION ALL
SELECT
    'Vehículos Disponibles',
    COUNT(*)
FROM vehiculos
WHERE estado = 'DISPONIBLE'
UNION ALL
SELECT
    'Alquileres Activos',
    COUNT(*)
FROM alquileres
WHERE estado = 'ACTIVO'
UNION ALL
SELECT
    'Ingresos Totales',
    COALESCE(SUM(monto), 0)
FROM pagos
WHERE estado = 'PAGADO';

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================