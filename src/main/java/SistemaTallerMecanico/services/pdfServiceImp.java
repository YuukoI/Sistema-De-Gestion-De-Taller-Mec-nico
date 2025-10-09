package SistemaTallerMecanico.services;

import SistemaTallerMecanico.entities.Presupuesto;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class pdfServiceImp implements pdfService {

    public byte[] generarReciboPdf(Presupuesto presupuesto) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Font subtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font normal = FontFactory.getFont(FontFactory.HELVETICA, 11);

            Paragraph reciboTitulo = new Paragraph("RECIBO DE PAGO", titulo);
            reciboTitulo.setAlignment(Element.ALIGN_CENTER);
            document.add(reciboTitulo);
            document.add(new Paragraph("\n"));

            try {
                Image logo = Image.getInstance("src/main/resources/static/images/logo.jpg");
                logo.scaleToFit(250, 250);
                logo.setAlignment(Element.ALIGN_CENTER);
                document.add(logo);
            } catch (Exception e) {
            }

            document.add(new Paragraph("\n"));

            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            PdfPTable reciboTable = new PdfPTable(1);
            reciboTable.setWidthPercentage(100);

            PdfPCell reciboCell = new PdfPCell();
            Paragraph reciboPara = new Paragraph();
            reciboPara.add(new Chunk("Fecha: " + presupuesto.getFecha().format(formatoFecha) + "\n"));
            reciboPara.add(new Chunk("Recibo Nº: " + presupuesto.getId()));
            reciboPara.setAlignment(Element.ALIGN_RIGHT);
            reciboCell.addElement(reciboPara);
            reciboCell.setBorder(Rectangle.NO_BORDER);
            reciboCell.setPaddingRight(10f);

            reciboTable.addCell(reciboCell);
            document.add(reciboTable);

            document.add(new Paragraph("\n"));

            Paragraph clienteInfo = new Paragraph();
            clienteInfo.add(new Paragraph("Cliente: " + presupuesto.getNombrePropietario(), normal));
            clienteInfo.add(new Paragraph("Patente: " + presupuesto.getPatente(), normal));
            clienteInfo.setAlignment(Element.ALIGN_LEFT);
            document.add(clienteInfo);

            document.add(new Paragraph("\n"));

            LineSeparator separator = new LineSeparator();
            separator.setLineColor(BaseColor.LIGHT_GRAY);
            document.add(new Chunk(separator));

            document.add(new Paragraph("\n"));

            PdfPTable trabajoTable = new PdfPTable(1);
            trabajoTable.setWidthPercentage(100);

            PdfPCell th = new PdfPCell(new Phrase("Trabajo realizado",
                    new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE)));
            th.setHorizontalAlignment(Element.ALIGN_CENTER);
            th.setBackgroundColor(BaseColor.DARK_GRAY);
            th.setVerticalAlignment(Element.ALIGN_MIDDLE);
            th.setPadding(10f);
            trabajoTable.addCell(th);

            PdfPCell descripcion = new PdfPCell(new Phrase(
                    presupuesto.getDescripcion() != null ? presupuesto.getDescripcion() : "Sin descripción",
                    normal));
            descripcion.setPadding(12f);
            trabajoTable.addCell(descripcion);

            document.add(trabajoTable);
            document.add(new Paragraph("\n"));

            double total = presupuesto.getTotal() != null ? presupuesto.getTotal() : 0;
            double subtotal = total / 1.21;
            double iva = total - subtotal;

            Paragraph totales = new Paragraph(
                    String.format("Subtotal: $%.2f\nIVA (21%%): $%.2f\nTotal: $%.2f", subtotal, iva, total),
                    subtitulo
            );
            totales.setAlignment(Element.ALIGN_CENTER);
            document.add(totales);
            document.add(new Paragraph("\n\n"));

            Paragraph cierre = new Paragraph("¡Gracias por elegirnos!", subtitulo);
            cierre.setAlignment(Element.ALIGN_CENTER);
            document.add(cierre);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF", e);
        }
    }
}
