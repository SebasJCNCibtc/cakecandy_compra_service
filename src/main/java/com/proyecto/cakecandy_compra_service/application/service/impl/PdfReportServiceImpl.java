package com.proyecto.cakecandy_compra_service.application.service.impl;

import com.proyecto.cakecandy_compra_service.application.dto.CompraDetalleDto;
import com.proyecto.cakecandy_compra_service.application.dto.CompraResponseDto;
import com.proyecto.cakecandy_compra_service.application.dto.DetalleCompraConProductoDto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfReportServiceImpl {

    public byte[] generateComprasReport(List<CompraResponseDto> compras) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                addWatermark(contentStream, page);
                addHeader(contentStream, page);

                // Ajustar posición del título principal hacia abajo
                writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16, 50, 650, "Reporte de Historial de Compras");
                // Ajustar posición de la tabla hacia abajo
                drawTable(contentStream, compras, 630);
                addFooter(contentStream, 1);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] generateCompraDetailReport(CompraDetalleDto compra) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                addWatermark(contentStream, page);
                addHeader(contentStream, page);

                // Ajustar posiciones para el detalle de compra
                drawInfoCard(contentStream, compra);
                drawDetailTable(contentStream, compra.getDetalles());
                addFooter(contentStream, 1);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void drawTable(PDPageContentStream contentStream, List<CompraResponseDto> compras, float y) throws IOException {
        float margin = 50;
        float rowHeight = 20.0f;
        float tableWidth = 500f;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        float[] colWidths = {30, 50, 90, 90, 70, 70}; // Anchos de columna

        // Dibuja el fondo de la cabecera
        contentStream.setNonStrokingColor(Color.DARK_GRAY);
        contentStream.addRect(margin, y - rowHeight, tableWidth, rowHeight);
        contentStream.fill();
        contentStream.setNonStrokingColor(Color.WHITE);

        // Cabeceras
        String[] headers = {"ID", "ID Prov.", "Fecha Compra", "Fecha Venc.", "Estado", "Total"};
        float textX = margin + 5;
        float textY = y - 15;
        for(int i = 0; i < headers.length; i++) {
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10, textX, textY, headers[i]);
            textX += colWidths[i];
        }

        // Filas
        contentStream.setNonStrokingColor(Color.BLACK);
        textY -= rowHeight;
        for (CompraResponseDto compra : compras) {
            textX = margin + 5;
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9, textX, textY, String.valueOf(compra.getIdCompra()));
            textX += colWidths[0];
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9, textX, textY, String.valueOf(compra.getIdProveedor()));
            textX += colWidths[1];
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9, textX, textY, compra.getFechaCompra().format(formatter));
            textX += colWidths[2];
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9, textX, textY, compra.getFechaVencimiento() != null ? compra.getFechaVencimiento().format(formatter) : "N/A");
            textX += colWidths[3];
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9, textX, textY, compra.getEstadoPago());
            textX += colWidths[4];
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 9, textX, textY, "S/ " + compra.getTotal().toString());
            textY -= rowHeight;
        }
    }

    private void drawInfoCard(PDPageContentStream contentStream, CompraDetalleDto compra) throws IOException {
        float margin = 50;
        float cardWidth = 500;
        // Ajustar posición de la tarjeta de información hacia abajo
        float yStart = 650;

        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18, margin, yStart, "Detalle de Compra #" + compra.getIdCompra());

        // Información de la compra
        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12, margin, yStart - 30, "Proveedor:");
        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12, margin + 80, yStart - 30, compra.getNombreProveedor());

        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12, margin, yStart - 50, "Fecha Compra:");
        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12, margin + 100, yStart - 50, compra.getFechaCompra().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12, margin, yStart - 70, "Fecha Vencimiento:");
        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12, margin + 130, yStart - 70, compra.getFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12, margin, yStart - 90, "Estado:");
        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12, margin + 60, yStart - 90, compra.getEstadoPago());

        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12, margin + 350, yStart - 90, "TOTAL:");
        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 20, margin + 350, yStart - 115, "S/ " + compra.getTotal().toString());

        // Línea separadora
        contentStream.setStrokingColor(Color.LIGHT_GRAY);
        contentStream.moveTo(margin, yStart - 125);
        contentStream.lineTo(margin + cardWidth, yStart - 125);
        contentStream.stroke();
    }

    private void drawDetailTable(PDPageContentStream contentStream, List<DetalleCompraConProductoDto> detalles) throws IOException {
        float margin = 50;
        // Ajustar posición de la tabla de detalles hacia abajo
        float y = 480;
        float rowHeight = 20.0f;
        float tableWidth = 500f;
        float[] colWidths = {250, 80, 80, 90};

        // Dibuja el fondo de la cabecera
        contentStream.setNonStrokingColor(Color.decode("#F3F4F6"));
        contentStream.addRect(margin, y, tableWidth, rowHeight);
        contentStream.fill();
        contentStream.setNonStrokingColor(Color.BLACK);

        // Cabeceras
        String[] headers = {"Producto", "Cantidad", "Costo Unit.", "Subtotal"};
        float textX = margin + 5;
        float textY = y + 5;
        for (int i = 0; i < headers.length; i++) {
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10, textX, textY, headers[i]);
            textX += colWidths[i];
        }

        y -= rowHeight;

        // Filas
        for (DetalleCompraConProductoDto detalle : detalles) {
            contentStream.setStrokingColor(Color.LIGHT_GRAY);
            contentStream.moveTo(margin, y);
            contentStream.lineTo(margin + tableWidth, y);
            contentStream.stroke();

            textX = margin + 5;
            textY = y + 5;

            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10, textX, textY, detalle.getNombreProducto());
            textX += colWidths[0];
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10, textX, textY, String.valueOf(detalle.getCantidad()));
            textX += colWidths[1];
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10, textX, textY, "S/ " + detalle.getPrecioCosto().toString());
            textX += colWidths[2];
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10, textX, textY, "S/ " + detalle.getSubtotal().toString());

            y -= rowHeight;
        }
    }

    private void addHeader(PDPageContentStream contentStream, PDPage page) throws IOException {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        // Ajustar posición del header hacia abajo
        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18, 50, 750, "Cake Candy");
        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10, 450, 750, "Fecha: " + fecha);
        contentStream.setStrokingColor(Color.DARK_GRAY);
        contentStream.setLineWidth(1.5f);
        contentStream.moveTo(50, 740);
        contentStream.lineTo(page.getMediaBox().getWidth() - 50, 740);
        contentStream.stroke();
    }

    private void addWatermark(PDPageContentStream contentStream, PDPage page) throws IOException {
        PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
        gs.setNonStrokingAlphaConstant(0.08f);
        contentStream.setGraphicsStateParameters(gs);

        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 100);
        contentStream.setNonStrokingColor(Color.GRAY);

        contentStream.saveGraphicsState();
        // Ajustar posición del watermark para mejor centrado
        contentStream.transform(new org.apache.pdfbox.util.Matrix(
                (float) Math.cos(Math.toRadians(45)), (float) Math.sin(Math.toRadians(45)),
                -(float) Math.sin(Math.toRadians(45)), (float) Math.cos(Math.toRadians(45)),
                page.getMediaBox().getWidth() / 3, page.getMediaBox().getHeight() / 3));

        contentStream.beginText();
        contentStream.showText("Cake Candy");
        contentStream.endText();

        contentStream.restoreGraphicsState();

        // Restaurar estado gráfico
        gs.setNonStrokingAlphaConstant(1.0f);
        contentStream.setGraphicsStateParameters(gs);
        contentStream.setNonStrokingColor(Color.BLACK);
    }

    private void addFooter(PDPageContentStream contentStream, int pageNum) throws IOException {
        // Ajustar posición del footer
        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10, 270, 50, "Página " + pageNum);
    }

    private void writeText(PDPageContentStream stream, PDType1Font font, int fontSize, float x, float y, String text) throws IOException {
        stream.setFont(font, fontSize);
        stream.beginText();
        stream.newLineAtOffset(x, y);
        stream.showText(text != null ? text : "");
        stream.endText();
    }
}