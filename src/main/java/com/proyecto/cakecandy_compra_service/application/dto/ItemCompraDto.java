package com.proyecto.cakecandy_compra_service.application.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemCompraDto {
    private Integer idProducto;
    private Integer cantidad;
    private BigDecimal precioCosto;
}