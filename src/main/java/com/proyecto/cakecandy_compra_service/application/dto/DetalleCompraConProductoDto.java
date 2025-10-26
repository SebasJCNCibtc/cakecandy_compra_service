package com.proyecto.cakecandy_compra_service.application.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleCompraConProductoDto {
    private String nombreProducto;
    private int cantidad;
    private BigDecimal precioCosto;
    private BigDecimal subtotal;
}