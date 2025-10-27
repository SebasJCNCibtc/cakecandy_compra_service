package com.proyecto.cakecandy_compra_service.application.service;

import com.proyecto.cakecandy_compra_service.application.dto.CompraDetalleDto;
import com.proyecto.cakecandy_compra_service.application.dto.CompraRequestDto;
import com.proyecto.cakecandy_compra_service.application.dto.CompraResponseDto;
import com.proyecto.cakecandy_compra_service.domain.model.EstadoPago;

import java.io.IOException;
import java.util.List;

public interface CompraService {

    CompraResponseDto createCompra(CompraRequestDto requestDto);

    CompraResponseDto marcarComoPagada(Integer idCompra, String metodoPago);

    List<CompraResponseDto> findByEstadoPago(EstadoPago estado);

    List<CompraResponseDto> findVencidas();
    List<CompraResponseDto> findAll();
    CompraDetalleDto findCompraConDetalles(Integer idCompra);
    byte[] exportComprasToPdf() throws IOException;
    byte[] exportCompraDetailToPdf(Integer idCompra) throws IOException;
}