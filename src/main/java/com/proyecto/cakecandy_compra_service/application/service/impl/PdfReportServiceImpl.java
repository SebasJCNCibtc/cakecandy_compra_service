package com.proyecto.cakecandy_compra_service.application.service.impl;

import com.proyecto.cakecandy_compra_service.application.dto.CompraDetalleDto;
import com.proyecto.cakecandy_compra_service.application.dto.CompraResponseDto;
import com.proyecto.cakecandy_compra_service.application.dto.DetalleCompraConProductoDto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfReportServiceImpl {

    public byte[] generateComprasReport(List<CompraResponseDto> compras) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Reporte de Historial de Compras");
                contentStream.endText();

                drawTable(contentStream, compras);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void drawTable(PDPageContentStream contentStream, List<CompraResponseDto> compras) throws IOException {
        float margin = 50;
        float y = 700;
        float rowHeight = 20.0f;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Cabeceras
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
        writeText(contentStream, margin + 5, y - 15, "ID");
        writeText(contentStream, margin + 40, y - 15, "ID Prov.");
        writeText(contentStream, margin + 100, y - 15, "Fecha Compra");
        writeText(contentStream, margin + 200, y - 15, "Fecha Venc.");
        writeText(contentStream, margin + 300, y - 15, "Estado");
        writeText(contentStream, margin + 380, y - 15, "Total");
        y -= rowHeight;

        // Filas
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
        for (CompraResponseDto compra : compras) {
            writeText(contentStream, margin + 5, y - 15, String.valueOf(compra.getIdCompra()));
            writeText(contentStream, margin + 40, y - 15, String.valueOf(compra.getIdProveedor()));
            writeText(contentStream, margin + 100, y - 15, compra.getFechaCompra().format(formatter));
            writeText(contentStream, margin + 200, y - 15, compra.getFechaVencimiento() != null ? compra.getFechaVencimiento().format(formatter) : "N/A");
            writeText(contentStream, margin + 300, y - 15, compra.getEstadoPago());
            writeText(contentStream, margin + 380, y - 15, "S/ " + compra.getTotal().toString());
            y -= rowHeight;
        }
    }

    private void writeText(PDPageContentStream stream, float x, float y, String text) throws IOException {
        stream.beginText();
        stream.newLineAtOffset(x, y);
        stream.showText(text);
        stream.endText();
    }

    public byte[] generateCompraDetailReport(CompraDetalleDto compra) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Detalle de Compra #" + compra.getIdCompra());
                contentStream.endText();

                // Información del proveedor y la compra
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                writeText(contentStream, 50, 720, "Proveedor: " + compra.getNombreProveedor());
                writeText(contentStream, 50, 700, "Fecha: " + compra.getFechaCompra().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                writeText(contentStream, 50, 680, "Vencimiento: " + compra.getFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                writeText(contentStream, 50, 660, "Estado: " + compra.getEstadoPago());
                writeText(contentStream, 50, 640, "Total: S/ " + compra.getTotal().toString());

                // Tabla de productos
                drawDetailTable(contentStream, compra.getDetalles());
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void drawDetailTable(PDPageContentStream contentStream, List<DetalleCompraConProductoDto> detalles) throws IOException {
        float margin = 50;
        float y = 600;
        float rowHeight = 20.0f;

        // Cabeceras
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
        writeText(contentStream, margin + 5, y - 15, "Producto");
        writeText(contentStream, margin + 250, y - 15, "Cantidad");
        writeText(contentStream, margin + 350, y - 15, "Costo Unit.");
        writeText(contentStream, margin + 450, y - 15, "Subtotal");
        y -= rowHeight;

        // Filas
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        for (DetalleCompraConProductoDto detalle : detalles) {
            writeText(contentStream, margin + 5, y - 15, detalle.getNombreProducto());
            writeText(contentStream, margin + 250, y - 15, String.valueOf(detalle.getCantidad()));
            writeText(contentStream, margin + 350, y - 15, "S/ " + detalle.getPrecioCosto().toString());
            writeText(contentStream, margin + 450, y - 15, "S/ " + detalle.getSubtotal().toString());
            y -= rowHeight;
        }
    }
}