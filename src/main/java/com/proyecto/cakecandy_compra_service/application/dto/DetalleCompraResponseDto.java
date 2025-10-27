package com.proyecto.cakecandy_compra_service.application.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleCompraResponseDto {
    private Integer idProducto;
    private Integer cantidad;
    private BigDecimal precioCosto;
    private BigDecimal subtotal;
}