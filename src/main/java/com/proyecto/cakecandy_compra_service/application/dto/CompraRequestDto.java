package com.proyecto.cakecandy_compra_service.application.dto;

import lombok.Data;
import java.time.LocalDate; // Importar LocalDate
import java.util.List;

@Data
public class CompraRequestDto {
    private Integer idProveedor;
    private LocalDate fechaVencimiento;
    private List<ItemCompraDto> items;
}