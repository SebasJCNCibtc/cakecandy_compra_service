package com.proyecto.cakecandy_compra_service.application.service.impl;

import com.proyecto.cakecandy_compra_service.application.dto.*;
import com.proyecto.cakecandy_compra_service.application.service.CompraService;
import com.proyecto.cakecandy_compra_service.domain.model.Compra;
import com.proyecto.cakecandy_compra_service.domain.model.DetalleCompra;
import com.proyecto.cakecandy_compra_service.domain.model.EstadoPago;
import com.proyecto.cakecandy_compra_service.domain.repository.CompraRepository;
import com.proyecto.cakecandy_compra_service.infraestructure.client.ProductoFeignClient;
import com.proyecto.cakecandy_compra_service.infraestructure.dto.ProductoDto;
import com.proyecto.cakecandy_compra_service.infraestructure.dto.ProveedorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompraServiceImpl implements CompraService {

    private final CompraRepository compraRepository;
    private final ProductoFeignClient productoFeignClient;
    private final PdfReportServiceImpl pdfReportService;

    @Override
    @Transactional
    public CompraResponseDto createCompra(CompraRequestDto requestDto) {
        // 1. Preparamos los datos de la compra
        BigDecimal totalCompra = BigDecimal.ZERO;
        List<DetalleCompra> detallesParaGuardar = new ArrayList<>();

        for (ItemCompraDto item : requestDto.getItems()) {
            BigDecimal subtotal = item.getPrecioCosto().multiply(new BigDecimal(item.getCantidad()));
            totalCompra = totalCompra.add(subtotal);

            DetalleCompra detalle = new DetalleCompra();
            detalle.setIdProducto(item.getIdProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioCosto(item.getPrecioCosto());
            detalle.setSubtotal(subtotal);
            detallesParaGuardar.add(detalle);
        }

        // 2. Creamos y guardamos la entidad Compra principal
        Compra nuevaCompra = new Compra();
        nuevaCompra.setIdProveedor(requestDto.getIdProveedor());
        nuevaCompra.setTotal(totalCompra);
        nuevaCompra.setFechaVencimiento(requestDto.getFechaVencimiento()); // <-- AÑADIMOS LA ASIGNACIÓN

        // 3. Vinculamos los detalles con la compra principal
        for (DetalleCompra detalle : detallesParaGuardar) {
            detalle.setCompra(nuevaCompra);
        }
        nuevaCompra.setDetalles(detallesParaGuardar);

        Compra compraGuardada = compraRepository.save(nuevaCompra);

        // 4. Actualizamos el stock en producto-service (orquestación)
        // Esto se hace después de guardar, para asegurar que la compra se registró primero.
        for (ItemCompraDto item : requestDto.getItems()) {
            productoFeignClient.addStock(item.getIdProducto(), item.getCantidad());
        }

        return buildResponseDto(compraGuardada);
    }

    // Método de ayuda para construir la respuesta a partir de la entidad
    private CompraResponseDto buildResponseDto(Compra compra) {
        List<DetalleCompraResponseDto> detallesDto = compra.getDetalles().stream().map(detalle -> {
            DetalleCompraResponseDto dto = new DetalleCompraResponseDto();
            dto.setIdProducto(detalle.getIdProducto());
            dto.setCantidad(detalle.getCantidad());
            dto.setPrecioCosto(detalle.getPrecioCosto());
            dto.setSubtotal(detalle.getSubtotal());
            return dto;
        }).collect(Collectors.toList());

        CompraResponseDto response = new CompraResponseDto();
        response.setIdCompra(compra.getIdCompra());
        response.setIdProveedor(compra.getIdProveedor());
        response.setFechaCompra(compra.getFechaCompra());
        response.setTotal(compra.getTotal());
        response.setDetalles(detallesDto);
        // --- AÑADIR ESTOS CAMPOS ---
        response.setEstadoPago(compra.getEstadoPago().name());
        response.setFechaVencimiento(compra.getFechaVencimiento());
        response.setMetodoPago(compra.getMetodoPago());
        return response;
    }
    // --- NUEVOS MÉTODOS ---
    @Override
    @Transactional
    public CompraResponseDto marcarComoPagada(Integer idCompra, String metodoPago) {
        Compra compra = compraRepository.findById(idCompra)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada con ID: " + idCompra));

        if (compra.getEstadoPago() == EstadoPago.PAGADA) {
            throw new IllegalStateException("Esta compra ya ha sido pagada.");
        }

        compra.setEstadoPago(EstadoPago.PAGADA);
        compra.setMetodoPago(metodoPago);

        return buildResponseDto(compraRepository.save(compra));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompraResponseDto> findByEstadoPago(EstadoPago estado) {
        return compraRepository.findByEstadoPago(estado).stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompraResponseDto> findVencidas() {
        return compraRepository.findByEstadoPagoAndFechaVencimientoBefore(EstadoPago.PENDIENTE, LocalDate.now()).stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public List<CompraResponseDto> findAll() {
        return compraRepository.findAll().stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public CompraDetalleDto findCompraConDetalles(Integer idCompra) {
        // 1. Busca la compra principal en la base de datos de este servicio
        Compra compra = compraRepository.findById(idCompra)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada con ID: " + idCompra));

        // 2. Orquestación: Llama al producto-service para obtener el nombre del proveedor
        ProveedorDto proveedor = productoFeignClient.findProveedorById(compra.getIdProveedor());

        // 3. Orquestación: Llama al producto-service por cada item para obtener su nombre
        List<DetalleCompraConProductoDto> detallesEnriquecidos = compra.getDetalles().stream().map(detalle -> {
            // Llama al endpoint GET /api/productos/{id}
            ProductoDto producto = productoFeignClient.findProductoById(detalle.getIdProducto());

            DetalleCompraConProductoDto detalleDto = new DetalleCompraConProductoDto();
            detalleDto.setNombreProducto(producto.getNombreProducto());
            detalleDto.setCantidad(detalle.getCantidad());
            detalleDto.setPrecioCosto(detalle.getPrecioCosto());
            detalleDto.setSubtotal(detalle.getSubtotal());
            return detalleDto;
        }).collect(Collectors.toList());

        // 4. Construye el objeto de respuesta final con toda la información recolectada
        CompraDetalleDto dtoFinal = new CompraDetalleDto();
        dtoFinal.setIdCompra(compra.getIdCompra());
        dtoFinal.setNombreProveedor(proveedor.getNombreProveedor());
        dtoFinal.setFechaCompra(compra.getFechaCompra());
        dtoFinal.setTotal(compra.getTotal());
        dtoFinal.setEstadoPago(compra.getEstadoPago().name());
        dtoFinal.setFechaVencimiento(compra.getFechaVencimiento());
        dtoFinal.setDetalles(detallesEnriquecidos);

        return dtoFinal;
    }

    @Override
    public byte[] exportComprasToPdf() throws IOException {
        List<CompraResponseDto> compras = this.findAll();
        return pdfReportService.generateComprasReport(compras);
    }
    @Override
    public byte[] exportCompraDetailToPdf(Integer idCompra) throws IOException {
        // Reutilizamos el método de orquestación que ya creamos
        CompraDetalleDto compraDetalle = this.findCompraConDetalles(idCompra);
        return pdfReportService.generateCompraDetailReport(compraDetalle);
    }
}