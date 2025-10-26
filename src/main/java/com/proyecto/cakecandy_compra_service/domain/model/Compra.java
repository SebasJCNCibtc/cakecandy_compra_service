package com.proyecto.cakecandy_compra_service.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate; // Cambiado a LocalDate para la fecha de vencimiento
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "compra")
@Data
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Integer idCompra;

    // ... (campos existentes: idProveedor, fechaCompra, total) ...
    @Column(name = "id_proveedor", nullable = false)
    private Integer idProveedor;

    @Column(name = "fecha_compra")
    private LocalDateTime fechaCompra;

    @Column(nullable = false)
    private BigDecimal total;

    // --- NUEVOS CAMPOS ---
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", nullable = false)
    private EstadoPago estadoPago;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento; // Usamos LocalDate porque no necesitamos la hora

    @Column(name = "metodo_pago")
    private String metodoPago;
    // --- FIN DE NUEVOS CAMPOS ---

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DetalleCompra> detalles;

    @PrePersist
    public void prePersist() {
        fechaCompra = LocalDateTime.now();
        // Asignamos PENDIENTE como estado por defecto al crear una nueva compra
        if (estadoPago == null) {
            estadoPago = EstadoPago.PENDIENTE;
        }
    }
}