package p_integrador.servicio;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.itextpdf.kernel.font.*;
import com.itextpdf.io.font.constants.StandardFonts;

public class ReporteService {

    public static String generarReporteBitacora(p_integrador.modelo.Bitacora bitacora, p_integrador.modelo.Usuario estudiante) {
        String ruta = "reportes/bitacora_" + bitacora.getIdBitacora() + 
            (estudiante != null ? "_" + estudiante.getIdUsuario() : "") + ".pdf";
        new java.io.File("reportes").mkdirs();

        try {
            PdfWriter writer = new PdfWriter(ruta);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont fontNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            doc.add(new Paragraph("SISTEMA BITÁCORA DIGITAL - UDI")
                .setFont(fontBold).setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new com.itextpdf.kernel.colors.DeviceRgb(0, 51, 102)));

            doc.add(new Paragraph("REPORTE DE BITÁCORA")
                .setFont(fontBold).setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph("\n"));

            doc.add(new Paragraph("Bitácora: " + bitacora.getIdBitacora()).setFont(fontBold));
            doc.add(new Paragraph("Práctica: " + bitacora.getIdPractica()).setFont(fontNormal));
            doc.add(new Paragraph("Fecha límite: " + bitacora.getFechaLimite()).setFont(fontNormal));
            doc.add(new Paragraph("Estado: " + bitacora.getEstado()).setFont(fontNormal));
            if (bitacora.getObjetivo() != null && !bitacora.getObjetivo().isEmpty()) {
                doc.add(new Paragraph("Objetivo general: " + bitacora.getObjetivo()).setFont(fontNormal));
            }

            if (bitacora.getIdAsesor() > 0) {
                p_integrador.dao.UsuarioDAO uDAO = new p_integrador.dao.UsuarioDAO();
                p_integrador.modelo.Usuario asesor = uDAO.buscarPorId(bitacora.getIdAsesor());
                if (asesor != null) {
                    doc.add(new Paragraph("Asesor: " + asesor.getNombre1() + " " +
                        asesor.getApellido1()).setFont(fontNormal));
                }
            }

            if (estudiante != null) {
                doc.add(new Paragraph("Estudiante: " + estudiante.getNombre1() + " " +
                    estudiante.getApellido1() + " - CC: " + estudiante.getIdUsuario())
                    .setFont(fontNormal));
            }

            doc.add(new Paragraph("\n"));

            p_integrador.dao.VisitaDAO vDAO = new p_integrador.dao.VisitaDAO();
            java.util.List<p_integrador.modelo.Visita> visitas = vDAO.listarVisitasBase(bitacora.getIdBitacora());

            for (p_integrador.modelo.Visita v : visitas) {
                doc.add(new Paragraph("VISITA N° " + v.getNumeroVisita()
                        + (v.getHorasObjetivo() > 0 ? "  -  " + v.getHorasObjetivo() + " horas" : ""))
                    .setFont(fontBold).setFontSize(12)
                    .setFontColor(new com.itextpdf.kernel.colors.DeviceRgb(0, 51, 102)));
                doc.add(new LineSeparator(new com.itextpdf.kernel.pdf.canvas.draw.SolidLine()));

                p_integrador.dao.PreguntaDAO pregDAO = new p_integrador.dao.PreguntaDAO();
                p_integrador.dao.RespuestaDAO respDAO = new p_integrador.dao.RespuestaDAO();
                java.util.List<p_integrador.modelo.Pregunta> preguntas = pregDAO.listarPorVisita(v.getIdVisita());

                for (p_integrador.modelo.Pregunta p : preguntas) {
                    doc.add(new Paragraph(p.getNumero() + ". " + p.getTexto())
                        .setFont(fontBold).setFontSize(11));
                    // En el reporte GENERAL (sin estudiante) no se incluyen respuestas.
                    if (estudiante != null) {
                        java.util.List<p_integrador.modelo.Respuesta> respuestas =
                            respDAO.listarPorPreguntaYEstudiante(p.getIdPregunta(), estudiante.getIdUsuario());
                        String resp = respuestas.isEmpty() ? "Sin respuesta" : respuestas.get(0).getRespuesta();
                        doc.add(new Paragraph(resp).setFont(fontNormal).setFontSize(10).setMarginLeft(20));
                    }
                }

                // Retroalimentación y nota SOLO en reportes individuales (con estudiante).
                // El reporte general de la bitácora no las incluye.
                if (estudiante != null) {
                    java.util.List<p_integrador.modelo.Visita> visitasEst = vDAO.listarPorBitacora(
                        bitacora.getIdBitacora(), estudiante.getIdUsuario());
                    for (p_integrador.modelo.Visita ve : visitasEst) {
                        if (ve.getNumeroVisita() == v.getNumeroVisita()) {
                            doc.add(new Paragraph("Retroalimentación: " +
                                (ve.getRetroalimentacion() != null ? ve.getRetroalimentacion() : "Sin retroalimentación"))
                                .setFont(fontNormal));
                            doc.add(new Paragraph("Nota: " + (ve.getNota() > 0 ? ve.getNota() : "Sin calificar"))
                                .setFont(fontBold));
                            break;
                        }
                    }
                }
                doc.add(new Paragraph("\n"));
            }

            doc.close();
            return ruta;

        } catch (Exception e) {
            System.err.println("Error al generar reporte: " + e.getMessage());
            return null;
        }
    }

    public static String generarReporteEstudiante(p_integrador.modelo.Usuario estudiante) {
        String ruta = "reportes/estudiante_" + estudiante.getIdUsuario() + ".pdf";
        new java.io.File("reportes").mkdirs();

        try {
            PdfWriter writer = new PdfWriter(ruta);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont fontNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            doc.add(new Paragraph("SISTEMA BITÁCORA DIGITAL - UDI")
                .setFont(fontBold).setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new com.itextpdf.kernel.colors.DeviceRgb(0, 51, 102)));

            doc.add(new Paragraph("REPORTE DE ESTUDIANTE")
                .setFont(fontBold).setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph("\n"));

            doc.add(new Paragraph("Estudiante: " + estudiante.getNombre1() + " " +
                estudiante.getApellido1()).setFont(fontBold));
            doc.add(new Paragraph("Cédula: " + estudiante.getIdUsuario()).setFont(fontNormal));
            doc.add(new Paragraph("Correo: " + estudiante.getCorreo()).setFont(fontNormal));
            doc.add(new Paragraph("Grupo: " + estudiante.getGrupo()).setFont(fontNormal));
            doc.add(new Paragraph("\n"));

            p_integrador.dao.VisitaDAO vDAO = new p_integrador.dao.VisitaDAO();
            java.util.List<String> idsBitacoras = vDAO.listarBitacorasDeEstudiante(estudiante.getIdUsuario());
            p_integrador.dao.BitacoraDAO bDAO = new p_integrador.dao.BitacoraDAO();

            for (String idBit : idsBitacoras) {
                p_integrador.modelo.Bitacora b = bDAO.buscarPorId(idBit);
                if (b == null) continue;

                doc.add(new Paragraph("BITÁCORA: " + b.getIdBitacora())
                    .setFont(fontBold).setFontSize(12)
                    .setFontColor(new com.itextpdf.kernel.colors.DeviceRgb(0, 51, 102)));
                doc.add(new LineSeparator(new com.itextpdf.kernel.pdf.canvas.draw.SolidLine()));
                doc.add(new Paragraph("Práctica: " + b.getIdPractica()).setFont(fontNormal));
                doc.add(new Paragraph("Estado: " + b.getEstado()).setFont(fontNormal));

                java.util.List<p_integrador.modelo.Visita> visitas = vDAO.listarPorBitacora(idBit, estudiante.getIdUsuario());
                for (p_integrador.modelo.Visita v : visitas) {
                    doc.add(new Paragraph("Visita " + v.getNumeroVisita() +
                        " - Nota: " + (v.getNota() > 0 ? v.getNota() : "Sin calificar"))
                        .setFont(fontNormal).setMarginLeft(10));
                }
                doc.add(new Paragraph("\n"));
            }

            doc.close();
            return ruta;

        } catch (Exception e) {
            System.err.println("Error al generar reporte: " + e.getMessage());
            return null;
        }
    }

    /**
     * Genera una vista previa en HTML que refleja el MISMO contenido que el PDF
     * de la bitácora. Si estudiante es null, es el reporte general (sin respuestas,
     * sin retroalimentación y sin nota), igual que el PDF general.
     */
    public static String generarVistaPreviaHTML(p_integrador.modelo.Bitacora bitacora,
                                                p_integrador.modelo.Usuario estudiante) {
        StringBuilder h = new StringBuilder();
        h.append("<html><body style='font-family:Segoe UI,Arial,sans-serif; margin:0; padding:16px; color:#222;'>");
        h.append("<div style='text-align:center; color:#003366; font-size:18px; font-weight:bold;'>SISTEMA BITÁCORA DIGITAL - UDI</div>");
        h.append("<div style='text-align:center; font-size:14px; font-weight:bold; margin-bottom:12px;'>REPORTE DE BITÁCORA</div>");
        h.append("<hr style='border:none; border-top:2px solid #003366;'>");

        boolean general = (estudiante == null);
        h.append("<p style='margin:4px 0;'><b>Tipo de reporte:</b> ")
         .append(general ? "General de la bitácora (todo el grupo)" : "Individual del estudiante").append("</p>");
        h.append("<p style='margin:4px 0;'><b>Bitácora:</b> ").append(esc(bitacora.getIdBitacora())).append("</p>");
        h.append("<p style='margin:4px 0;'><b>Práctica:</b> ").append(esc(bitacora.getIdPractica())).append("</p>");
        h.append("<p style='margin:4px 0;'><b>Fecha límite:</b> ").append(String.valueOf(bitacora.getFechaLimite())).append("</p>");
        h.append("<p style='margin:4px 0;'><b>Estado:</b> ").append(esc(bitacora.getEstado())).append("</p>");
        if (bitacora.getObjetivo() != null && !bitacora.getObjetivo().isEmpty())
            h.append("<p style='margin:4px 0;'><b>Objetivo general:</b> ").append(esc(bitacora.getObjetivo())).append("</p>");

        if (bitacora.getIdAsesor() > 0) {
            p_integrador.modelo.Usuario asesor = new p_integrador.dao.UsuarioDAO().buscarPorId(bitacora.getIdAsesor());
            if (asesor != null)
                h.append("<p style='margin:4px 0;'><b>Asesor:</b> ").append(esc(asesor.getNombre1() + " " + asesor.getApellido1())).append("</p>");
        }
        if (!general) {
            h.append("<p style='margin:4px 0;'><b>Estudiante:</b> ")
             .append(esc(estudiante.getNombre1() + " " + estudiante.getApellido1()))
             .append(" - CC: ").append(estudiante.getIdUsuario()).append("</p>");
        }

        p_integrador.dao.VisitaDAO vDAO = new p_integrador.dao.VisitaDAO();
        p_integrador.dao.PreguntaDAO pregDAO = new p_integrador.dao.PreguntaDAO();
        p_integrador.dao.RespuestaDAO respDAO = new p_integrador.dao.RespuestaDAO();
        java.util.List<p_integrador.modelo.Visita> visitas = vDAO.listarVisitasBase(bitacora.getIdBitacora());

        int totalHoras = 0;
        for (p_integrador.modelo.Visita v : visitas) totalHoras += v.getHorasObjetivo();
        h.append("<p style='margin:8px 0; background:#eef4ff; padding:6px; border-left:4px solid #003366;'>")
         .append("<b>Total de horas planeadas:</b> ").append(totalHoras).append(" h</p>");

        for (p_integrador.modelo.Visita v : visitas) {
            h.append("<div style='margin-top:14px; color:#003366; font-size:13px; font-weight:bold; border-bottom:1px solid #003366;'>")
             .append("VISITA N° ").append(v.getNumeroVisita())
             .append(v.getHorasObjetivo() > 0 ? " &nbsp;-&nbsp; " + v.getHorasObjetivo() + " horas" : "")
             .append("</div>");

            java.util.List<p_integrador.modelo.Pregunta> preguntas = pregDAO.listarPorVisita(v.getIdVisita());
            if (preguntas.isEmpty()) {
                h.append("<p style='margin:6px 0; color:#777;'><i>Sin preguntas registradas.</i></p>");
            }
            for (p_integrador.modelo.Pregunta p : preguntas) {
                h.append("<p style='margin:6px 0 2px 0;'><b>").append(p.getNumero()).append(". ")
                 .append(esc(p.getTexto())).append("</b></p>");
                if (!general) {
                    java.util.List<p_integrador.modelo.Respuesta> rs =
                        respDAO.listarPorPreguntaYEstudiante(p.getIdPregunta(), estudiante.getIdUsuario());
                    String resp = rs.isEmpty() ? "Sin respuesta" : rs.get(0).getRespuesta();
                    h.append("<p style='margin:0 0 6px 20px; color:#333;'>").append(esc(resp)).append("</p>");
                }
            }

            if (!general) {
                java.util.List<p_integrador.modelo.Visita> visitasEst =
                    vDAO.listarPorBitacora(bitacora.getIdBitacora(), estudiante.getIdUsuario());
                for (p_integrador.modelo.Visita ve : visitasEst) {
                    if (ve.getNumeroVisita() == v.getNumeroVisita()) {
                        h.append("<p style='margin:4px 0;'><b>Retroalimentación:</b> ")
                         .append(esc(ve.getRetroalimentacion() != null ? ve.getRetroalimentacion() : "Sin retroalimentación")).append("</p>");
                        h.append("<p style='margin:4px 0;'><b>Nota:</b> ")
                         .append(ve.getNota() > 0 ? String.valueOf(ve.getNota()) : "Sin calificar").append("</p>");
                        break;
                    }
                }
            }
        }

        if (general) {
            h.append("<p style='margin-top:14px; color:#777; font-size:11px;'><i>")
             .append("Este es el reporte general de la bitácora: no incluye respuestas, retroalimentación ni notas. ")
             .append("Para verlas, selecciona un estudiante.</i></p>");
        }

        h.append("</body></html>");
        return h.toString();
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}