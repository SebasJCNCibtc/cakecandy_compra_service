package com.proyecto.cakecandy_compra_service.application.service;

import com.proyecto.cakecandy_compra_service.application.dto.CompraDetalleDto;
import com.proyecto.cakecandy_compra_service.application.dto.CompraRequestDto;
import com.proyecto.cakecandy_compra_service.application.dto.CompraResponseDto;
import com.proyecto.cakecandy_compra_service.domain.model.EstadoPago;

import java.io.IOException;
import java.util.List;

public interface CompraService {
    /**
     * Registra una nueva compra, guarda los detalles y actualiza
     * el stock de los productos correspondientes.
     *
     * @param requestDto DTO con los detalles de la compra.
     * @return DTO con la información de la compra creada.
     */
    CompraResponseDto createCompra(CompraRequestDto requestDto);
    // --- NUEVOS MÉTODOS ---
    CompraResponseDto marcarComoPagada(Integer idCompra, String metodoPago);

    List<CompraResponseDto> findByEstadoPago(EstadoPago estado);

    List<CompraResponseDto> findVencidas();
    List<CompraResponseDto> findAll();
    CompraDetalleDto findCompraConDetalles(Integer idCompra);
    byte[] exportComprasToPdf() throws IOException;
    byte[] exportCompraDetailToPdf(Integer idCompra) throws IOException;
}