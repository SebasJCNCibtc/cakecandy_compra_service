package com.proyecto.cakecandy_compra_service.application.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate; // Importar LocalDate
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CompraResponseDto {
    private Integer idCompra;
    private Integer idProveedor;
    private LocalDateTime fechaCompra;
    private BigDecimal total;
    private List<DetalleCompraResponseDto> detalles;

    private String estadoPago;
    private LocalDate fechaVencimiento;
    private String metodoPago;
}