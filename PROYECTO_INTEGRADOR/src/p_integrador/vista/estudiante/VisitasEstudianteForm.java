package p_integrador.vista.estudiante;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import p_integrador.dao.PreguntaDAO;
import p_integrador.dao.RespuestaDAO;
import p_integrador.dao.VisitaDAO;
import p_integrador.modelo.Pregunta;
import p_integrador.modelo.Respuesta;
import p_integrador.modelo.Visita;

/**
 * Vista mejorada de respuesta de visitas.
 * - Panel lateral con lista de visitas + indicador de estado
 * - Área derecha con formulario limpio de preguntas
 * - Retroalimentación del asesor visible al final
 */
public class VisitasEstudianteForm extends javax.swing.JFrame {

    private final String idBitacora;
    private final long   idEstudiante;
    private List<Visita> visitas = new ArrayList<>();
    private List<JTextArea> camposRespuesta = new ArrayList<>();
    private Visita visitaActual;
    private JTextArea campoUbicacion;

    private final Color AZUL_UDI    = new Color(0, 51, 102);
    private final Color FONDO_CLARO = new Color(219, 240, 255);
    private final Color BLANCO      = Color.WHITE;
    private final Color VERDE       = new Color(34, 139, 34);
    private final Color ROJO        = new Color(200, 40, 40);
    private final Color NARANJA     = new Color(200, 120, 0);

    // Componentes
    private JList<String>           listaVisitas;
    private DefaultListModel<String> modeloLista;
    private JPanel                  panelFormulario;
    private JScrollPane             scrollFormulario;
    private JLabel                  lblBitacora, lblMensaje;
    private JButton                 btnGuardar;

    public VisitasEstudianteForm(String idBitacora, long idEstudiante) {
        this.idBitacora   = idBitacora;
        this.idEstudiante = idEstudiante;
        initComponents();
        buildUI();
        cargarVisitas();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 675);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        modeloLista   = new DefaultListModel<>();
        listaVisitas  = new JList<>(modeloLista);
        panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBackground(FONDO_CLARO);
        scrollFormulario = new JScrollPane(panelFormulario);
        lblBitacora  = new JLabel();
        lblMensaje   = new JLabel(" ");
        btnGuardar   = new JButton("GUARDAR RESPUESTAS");

        listaVisitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaVisitas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarVisita();
        });
        btnGuardar.addActionListener(e -> guardarRespuestasActuales());
    }

    private void buildUI() {
        getContentPane().setBackground(FONDO_CLARO);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new panelMenuEstudiante(), BorderLayout.WEST);

        JPanel pnlMain = new JPanel(new BorderLayout(0, 12));
        pnlMain.setOpaque(false);
        pnlMain.setBorder(new EmptyBorder(25, 25, 18, 25));

        // Cabecera
        lblBitacora.setText("BITÁCORA: " + idBitacora);
        lblBitacora.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblBitacora.setForeground(AZUL_UDI);
        pnlMain.add(lblBitacora, BorderLayout.NORTH);

        // Centro: lista izquierda + formulario derecho
        JPanel pnlCentro = new JPanel(new BorderLayout(15, 0));
        pnlCentro.setOpaque(false);

        // ── Lista de visitas ────────────────────────────────────────
        JPanel pnlIzq = new JPanel(new BorderLayout(0, 8));
        pnlIzq.setOpaque(false);
        pnlIzq.setPreferredSize(new Dimension(210, 0));

        JLabel lblTitLista = new JLabel("VISITAS");
        lblTitLista.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitLista.setForeground(AZUL_UDI);

        listaVisitas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listaVisitas.setFixedCellHeight(40);
        listaVisitas.setBackground(BLANCO);
        listaVisitas.setCellRenderer(new VisitaCellRenderer());

        JScrollPane scrollLista = new JScrollPane(listaVisitas);
        scrollLista.setBorder(new LineBorder(new Color(210, 220, 235)));

        pnlIzq.add(lblTitLista, BorderLayout.NORTH);
        pnlIzq.add(scrollLista, BorderLayout.CENTER);
        pnlCentro.add(pnlIzq, BorderLayout.WEST);

        // ── Formulario de preguntas ─────────────────────────────────
        scrollFormulario.setBorder(new LineBorder(new Color(210, 220, 235)));
        scrollFormulario.getVerticalScrollBar().setUnitIncrement(16);
        pnlCentro.add(scrollFormulario, BorderLayout.CENTER);

        pnlMain.add(pnlCentro, BorderLayout.CENTER);

        // Footer
        JPanel pnlFooter = new JPanel(new BorderLayout(15, 0));
        pnlFooter.setOpaque(false);

        btnGuardar.setBackground(AZUL_UDI);
        btnGuardar.setForeground(BLANCO);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setPreferredSize(new Dimension(220, 40));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setVisible(false);

        lblMensaje.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        pnlFooter.add(btnGuardar, BorderLayout.WEST);
        pnlFooter.add(lblMensaje, BorderLayout.CENTER);
        pnlMain.add(pnlFooter, BorderLayout.SOUTH);

        getContentPane().add(pnlMain, BorderLayout.CENTER);
    }

    private void cargarVisitas() {
        visitas = new VisitaDAO().listarPorBitacora(idBitacora, idEstudiante);
        modeloLista.clear();
        for (Visita v : visitas) {
            modeloLista.addElement("Visita " + v.getNumeroVisita());
        }
        if (!visitas.isEmpty()) listaVisitas.setSelectedIndex(0);
    }

    private void mostrarVisita() {
        int idx = listaVisitas.getSelectedIndex();
        if (idx < 0 || idx >= visitas.size()) return;

        Visita v = visitas.get(idx);
        visitaActual = v;
        panelFormulario.removeAll();
        camposRespuesta.clear();
        campoUbicacion = null;
        panelFormulario.setBorder(new EmptyBorder(20, 22, 20, 22));

        boolean vencida = v.getFechaLimite() != null && new Date().after(v.getFechaLimite());

        // Banner de vencida
        if (vencida) {
            JPanel banner = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            banner.setBackground(new Color(255, 243, 243));
            banner.setBorder(new LineBorder(new Color(220, 150, 150)));
            banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            banner.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel lbl = new JLabel("Plazo vencido - solo lectura");
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(ROJO);
            banner.add(lbl);
            panelFormulario.add(banner);
            panelFormulario.add(Box.createRigidArea(new Dimension(0, 14)));
        }

        // Info de la visita
        JLabel lblInfo = new JLabel("Visita N° " + v.getNumeroVisita()
            + "   -   Fecha límite: " + (v.getFechaLimite() != null ? v.getFechaLimite() : "—")
            + "   -   Estado: " + v.getEstado());
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfo.setForeground(new Color(100, 100, 100));
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelFormulario.add(lblInfo);
        panelFormulario.add(Box.createRigidArea(new Dimension(0, 16)));

        // Ubicación de la visita (dónde se realizó)
        panelFormulario.add(crearSeccionUbicacion(v, vencida));
        panelFormulario.add(Box.createRigidArea(new Dimension(0, 12)));

        // Preguntas
        PreguntaDAO pregDAO = new PreguntaDAO();
        RespuestaDAO respDAO = new RespuestaDAO();
        String idVisitaBase = v.getIdVisita().replace("_" + idEstudiante, "");
        List<Pregunta> preguntas = pregDAO.listarPorVisita(idVisitaBase);

        if (preguntas.isEmpty()) {
            JLabel lbl = new JLabel("Esta visita no tiene preguntas configuradas.");
            lbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lbl.setForeground(new Color(120, 120, 120));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelFormulario.add(lbl);
        } else {
            for (int i = 0; i < preguntas.size(); i++) {
                Pregunta p = preguntas.get(i);
                panelFormulario.add(crearCardPregunta(p, i + 1, respDAO, vencida));
                panelFormulario.add(Box.createRigidArea(new Dimension(0, 12)));
            }
        }

        // Sección de evidencia
        panelFormulario.add(crearSeccionEvidencia(v, vencida));
        panelFormulario.add(Box.createRigidArea(new Dimension(0, 12)));

        // Retroalimentación del asesor
        if (v.getRetroalimentacion() != null && !v.getRetroalimentacion().isEmpty()) {
            panelFormulario.add(crearSeccionRetro(v));
        }

        btnGuardar.setVisible(!vencida);

        // Guardar referencia a la visita actual para el botón
        btnGuardar.setActionCommand(v.getIdVisita());

        panelFormulario.revalidate();
        panelFormulario.repaint();
        scrollFormulario.getVerticalScrollBar().setValue(0);
    }

    private JPanel crearSeccionUbicacion(Visita v, boolean bloqueado) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BLANCO);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(210, 225, 245), 1),
            new EmptyBorder(14, 18, 14, 18)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        JLabel lblTit = new JLabel("Ubicación de la visita");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTit.setForeground(AZUL_UDI);
        lblTit.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Indica dónde realizaste esta visita (institución, sede, dirección, salón, etc.)");
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblSub.setForeground(new Color(120, 120, 120));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        campoUbicacion = new JTextArea(v.getUbicacion() != null ? v.getUbicacion() : "");
        campoUbicacion.setLineWrap(true);
        campoUbicacion.setWrapStyleWord(true);
        campoUbicacion.setEditable(!bloqueado);
        campoUbicacion.setBackground(bloqueado ? new Color(248, 248, 248) : new Color(250, 252, 255));
        campoUbicacion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        campoUbicacion.setBorder(new CompoundBorder(
            new LineBorder(new Color(210, 220, 235)),
            new EmptyBorder(8, 10, 8, 10)
        ));
        campoUbicacion.setAlignmentX(Component.LEFT_ALIGNMENT);
        campoUbicacion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        card.add(lblTit);
        card.add(Box.createRigidArea(new Dimension(0, 3)));
        card.add(lblSub);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(campoUbicacion);
        return card;
    }

    private JPanel crearCardPregunta(Pregunta p, int num, RespuestaDAO dao, boolean bloqueado) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BLANCO);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(225, 232, 245), 1),
            new EmptyBorder(14, 18, 14, 18)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel lblNum = new JLabel("Pregunta " + num);
        lblNum.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblNum.setForeground(new Color(120, 140, 170));

        JLabel lblTexto = new JLabel("<html>" + p.getTexto() + "</html>");
        lblTexto.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTexto.setForeground(AZUL_UDI);

        List<Respuesta> rList = dao.listarPorPreguntaYEstudiante(p.getIdPregunta(), idEstudiante);
        String textoResp = rList.isEmpty() ? "" : rList.get(0).getRespuesta();

        JTextArea area = new JTextArea(textoResp);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(!bloqueado);
        area.setBackground(bloqueado ? new Color(248, 248, 248) : new Color(250, 252, 255));
        area.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        area.setBorder(new CompoundBorder(
            new LineBorder(new Color(210, 220, 235)),
            new EmptyBorder(8, 10, 8, 10)
        ));
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
        area.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        area.putClientProperty("idPregunta", p.getIdPregunta());
        area.putClientProperty("idRespuesta", rList.isEmpty() ? null : rList.get(0).getIdRespuesta());

        card.add(lblNum);
        card.add(Box.createRigidArea(new Dimension(0, 3)));
        card.add(lblTexto);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(area);
        camposRespuesta.add(area);
        return card;
    }

    private JPanel crearSeccionEvidencia(Visita v, boolean bloqueado) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(new Color(248, 251, 255));
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(210, 225, 245), 1),
            new EmptyBorder(14, 18, 14, 18)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTit = new JLabel("Evidencia adjunta");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTit.setForeground(AZUL_UDI);
        card.add(lblTit, BorderLayout.WEST);

        JPanel pnlDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlDer.setOpaque(false);

        String nombre = v.getRutaPlantilla() != null
            ? new File(v.getRutaPlantilla()).getName() : "Sin archivo";
        JLabel lblArch = new JLabel(nombre);
        lblArch.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblArch.setForeground(new Color(100, 100, 100));
        pnlDer.add(lblArch);

        if (v.getRutaPlantilla() != null) {
            JButton btnVer = miniBtn("Ver");
            btnVer.addActionListener(e -> {
                try { Desktop.getDesktop().open(new File(v.getRutaPlantilla())); }
                catch (Exception ex) { mostrarMsg("No se pudo abrir el archivo.", ROJO); }
            });
            pnlDer.add(btnVer);
        }

        if (!bloqueado) {
            JButton btnAdj = miniBtn("+ Adjuntar");
            btnAdj.setBackground(AZUL_UDI);
            btnAdj.setForeground(BLANCO);
            btnAdj.addActionListener(e -> adjuntarArchivo(v, lblArch));
            pnlDer.add(btnAdj);
        }
        card.add(pnlDer, BorderLayout.EAST);
        return card;
    }

    private JPanel crearSeccionRetro(Visita v) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(240, 255, 245));
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(180, 220, 195), 1),
            new EmptyBorder(14, 18, 14, 18)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTit = new JLabel("Retroalimentación del asesor");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTit.setForeground(VERDE);
        lblTit.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea area = new JTextArea(v.getRetroalimentacion());
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(new Color(240, 255, 245));
        area.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        area.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNota = new JLabel("Calificación: " + (v.getNota() > 0 ? v.getNota() : "Pendiente"));
        lblNota.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNota.setForeground(AZUL_UDI);
        lblNota.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTit);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(area);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(lblNota);
        return card;
    }

    private JButton miniBtn(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(BLANCO);
        btn.setForeground(AZUL_UDI);
        btn.setBorder(new LineBorder(AZUL_UDI, 1));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 26));
        return btn;
    }

    private void adjuntarArchivo(Visita v, JLabel lblArch) {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Evidencias (PDF, Imágenes)", "pdf", "png", "jpg", "jpeg"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File fuente  = fc.getSelectedFile();
        File destino = new File("plantillas/" + v.getIdVisita() + "_" + fuente.getName());
        destino.getParentFile().mkdirs();
        try {
            Files.copy(fuente.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            if (new VisitaDAO().actualizarPlantilla(v.getIdVisita(), destino.getAbsolutePath())) {
                lblArch.setText(fuente.getName());
                mostrarMsg("Evidencia adjuntada correctamente.", VERDE);
            }
        } catch (Exception ex) {
            mostrarMsg("Error al adjuntar: " + ex.getMessage(), ROJO);
        }
    }

    private void guardarRespuestasActuales() {
        // Guardar ubicación de la visita
        if (visitaActual != null && campoUbicacion != null) {
            String ubic = campoUbicacion.getText().trim();
            boolean okUbic = new VisitaDAO().actualizarUbicacion(visitaActual.getIdVisita(), ubic);
            if (okUbic) {
                // Actualizar el objeto en memoria para que no se pierda al cambiar de visita
                visitaActual.setUbicacion(ubic);
            } else {
                mostrarMsg("No se pudo guardar la ubicación. Revisa la conexión.", new Color(180, 50, 50));
                return;
            }
        }
        RespuestaDAO dao = new RespuestaDAO();
        int ok = 0;
        for (JTextArea campo : camposRespuesta) {
            String idPreg = (String) campo.getClientProperty("idPregunta");
            String idResp = (String) campo.getClientProperty("idRespuesta");
            String texto  = campo.getText().trim();
            if (texto.isEmpty()) continue;
            if (idResp != null) {
                if (dao.actualizar(new Respuesta(idResp, idPreg, idEstudiante, texto))) ok++;
            } else {
                String nuevoId = idPreg + "_" + idEstudiante;
                if (dao.crear(new Respuesta(nuevoId, idPreg, idEstudiante, texto))) {
                    campo.putClientProperty("idRespuesta", nuevoId);
                    ok++;
                }
            }
        }
        mostrarMsg("Cambios guardados. " + ok + " respuesta(s) registrada(s).", VERDE);
    }

    private void mostrarMsg(String msg, Color color) {
        lblMensaje.setText(msg);
        lblMensaje.setForeground(color);
    }

    /** Renderer para la lista de visitas con colores según estado */
    private class VisitaCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                       boolean sel, boolean focus) {
            super.getListCellRendererComponent(list, value, index, sel, focus);
            if (!sel && index < visitas.size()) {
                Visita v = visitas.get(index);
                boolean venc = v.getFechaLimite() != null && new Date().after(v.getFechaLimite());
                if ("COMPLETADA".equals(v.getEstado())) setForeground(VERDE);
                else if (venc)                          setForeground(ROJO);
                else                                    setForeground(NARANJA);
            }
            setBorder(new EmptyBorder(6, 10, 6, 10));
            return this;
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new VisitasEstudianteForm("BIT-001", 1005).setVisible(true));
    }
}
