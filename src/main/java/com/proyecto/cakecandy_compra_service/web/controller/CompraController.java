package com.proyecto.cakecandy_compra_service.web.controller;

import com.proyecto.cakecandy_compra_service.application.dto.CompraDetalleDto;
import com.proyecto.cakecandy_compra_service.application.dto.CompraRequestDto;
import com.proyecto.cakecandy_compra_service.application.dto.CompraResponseDto;
import com.proyecto.cakecandy_compra_service.application.service.CompraService;
import com.proyecto.cakecandy_compra_service.domain.model.EstadoPago;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    /**
     * Registra una nueva compra de productos.
     */
    @PostMapping
    public ResponseEntity<CompraResponseDto> createCompra(@RequestBody CompraRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compraService.createCompra(requestDto));
    }

    /**
     * Marca una compra específica como PAGADA.
     * @param id El ID de la compra a modificar.
     * @param metodoPago El método de pago utilizado (ej. "Transferencia", "Efectivo").
     */
    @PatchMapping("/{id}/pagar")
    public ResponseEntity<CompraResponseDto> marcarComoPagada(
            @PathVariable Integer id,
            @RequestParam String metodoPago) {
        return ResponseEntity.ok(compraService.marcarComoPagada(id, metodoPago));
    }

    /**
     * Obtiene una lista de todas las compras con estado PENDIENTE.
     */
    @GetMapping("/pendientes")
    public ResponseEntity<List<CompraResponseDto>> getComprasPendientes() {
        return ResponseEntity.ok(compraService.findByEstadoPago(EstadoPago.PENDIENTE));
    }

    /**
     * Obtiene una lista de todas las compras con estado PAGADA.
     */
    @GetMapping("/pagadas")
    public ResponseEntity<List<CompraResponseDto>> getComprasPagadas() {
        return ResponseEntity.ok(compraService.findByEstadoPago(EstadoPago.PAGADA));
    }

    /**
     * Obtiene una lista de todas las compras pendientes cuya fecha de vencimiento ya pasó.
     */
    @GetMapping("/vencidas")
    public ResponseEntity<List<CompraResponseDto>> getComprasVencidas() {
        return ResponseEntity.ok(compraService.findVencidas());
    }
    @GetMapping
    public ResponseEntity<List<CompraResponseDto>> getAllCompras() {
        return ResponseEntity.ok(compraService.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<CompraDetalleDto> getCompraById(@PathVariable Integer id) {
        return ResponseEntity.ok(compraService.findCompraConDetalles(id));
    }
    @GetMapping("/reporte/pdf")
    public ResponseEntity<byte[]> exportPdf() {
        try {
            byte[] pdfReport = compraService.exportComprasToPdf();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "reporte_compras.pdf");
            // ... (resto de headers)
            return new ResponseEntity<>(pdfReport, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{id}/reporte/pdf")
    public ResponseEntity<byte[]> exportDetailPdf(@PathVariable Integer id) {
        try {
            byte[] pdfReport = compraService.exportCompraDetailToPdf(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "detalle_compra_" + id + ".pdf");
            // ... (resto de headers)
            return new ResponseEntity<>(pdfReport, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/resumen/vencidas")
    public ResponseEntity<Long> getCountComprasVencidas() {
        // Reutilizamos el servicio que ya teníamos y solo devolvemos el tamaño de la lista
        long count = compraService.findVencidas().size();
        return ResponseEntity.ok(count);
    }
}