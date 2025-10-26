package com.proyecto.cakecandy_compra_service.application.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CompraDetalleDto {
    private Integer idCompra;
    private String nombreProveedor;
    private LocalDateTime fechaCompra;
    private BigDecimal total;
    private String estadoPago;
    private LocalDate fechaVencimiento;
    private List<DetalleCompraConProductoDto> detalles;
}