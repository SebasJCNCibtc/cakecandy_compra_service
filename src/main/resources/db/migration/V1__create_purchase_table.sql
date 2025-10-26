CREATE TABLE compra (
                        id_compra INT PRIMARY KEY AUTO_INCREMENT,
                        id_proveedor INT NOT NULL,
                        fecha_compra DATETIME DEFAULT CURRENT_TIMESTAMP,
                        total DECIMAL(10, 2) NOT NULL
);

CREATE TABLE detalle_compra (
                                id_detalle_compra INT PRIMARY KEY AUTO_INCREMENT,
                                id_compra INT NOT NULL,
                                id_producto INT NOT NULL,
                                cantidad INT NOT NULL,
                                precio_costo DECIMAL(10, 2) NOT NULL,
                                subtotal DECIMAL(10, 2) NOT NULL,
                                FOREIGN KEY (id_compra) REFERENCES compra(id_compra) ON DELETE CASCADE
);

ALTER TABLE compra
    ADD COLUMN estado_pago VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
ADD COLUMN fecha_vencimiento DATE,
ADD COLUMN metodo_pago VARCHAR(50);