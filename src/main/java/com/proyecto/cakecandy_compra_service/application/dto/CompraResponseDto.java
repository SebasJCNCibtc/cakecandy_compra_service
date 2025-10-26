package com.proyecto.cakecandy_compra_service.application.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate; // Importar LocalDate
import java.time.LocalDateTime;
import java.util.List;

// ... (El DTO anidado DetalleCompraResponseDto no cambia) ...

@Data
public class CompraResponseDto {
    private Integer idCompra;
    private Integer idProveedor;
    private LocalDateTime fechaCompra;
    private BigDecimal total;
    private List<DetalleCompraResponseDto> detalles;

    // --- AÑADIR ESTOS CAMPOS ---
    private String estadoPago;
    private LocalDate fechaVencimiento;
    private String metodoPago;
}