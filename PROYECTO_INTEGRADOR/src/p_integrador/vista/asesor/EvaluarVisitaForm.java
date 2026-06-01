package p_integrador.vista.asesor;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.List;
import p_integrador.modelo.Visita;
import p_integrador.modelo.Pregunta;
import p_integrador.modelo.Respuesta;
import p_integrador.modelo.Usuario;
import p_integrador.dao.VisitaDAO;
import p_integrador.dao.PreguntaDAO;
import p_integrador.dao.RespuestaDAO;
import p_integrador.dao.UsuarioDAO;

public class EvaluarVisitaForm extends javax.swing.JFrame {

    private String idBitacora;
    private List<Visita> visitasBase;
    private List<Usuario> estudiantes;

    private final Color AZUL_UDI = new Color(0, 51, 102);
    private final Color FONDO_CLARO = new Color(219, 240, 255);
    private final Color BLANCO = Color.WHITE;

    public EvaluarVisitaForm(String idBitacora) {
        this.idBitacora = idBitacora;
        initComponents();
        configurarEstetica();
        cargarEstudiantes();
        cargarVisitas();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 720);
        setLocationRelativeTo(null);
    }

    private void configurarEstetica() {
        getContentPane().setBackground(FONDO_CLARO);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new panelMenuAsesor(), BorderLayout.WEST);

        JPanel pnlMain = new JPanel(new BorderLayout(20, 20));
        pnlMain.setOpaque(false);
        pnlMain.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel pnlHeader = new JPanel(new GridBagLayout());
        pnlHeader.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblTitulo = new JLabel("MÓDULO DE EVALUACIÓN - BITÁCORA: " + idBitacora);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(AZUL_UDI);
        gbc.gridx = 0; gbc.gridy = 0;
        pnlHeader.add(lblTitulo, gbc);

        JPanel pnlSelector = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlSelector.setOpaque(false);

        JLabel lblEst = new JLabel("Estudiante:");
        lblEst.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cmbEstudiante.setPreferredSize(new Dimension(280, 30));
        pnlSelector.add(lblEst);
        pnlSelector.add(cmbEstudiante);

        JLabel lblSel = new JLabel("Visita:");
        lblSel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cmbVisita.setPreferredSize(new Dimension(180, 30));
        pnlSelector.add(lblSel);
        pnlSelector.add(cmbVisita);

        gbc.gridy = 1;
        pnlHeader.add(pnlSelector, gbc);
        pnlMain.add(pnlHeader, BorderLayout.NORTH);

        scrollPanel.setBorder(new LineBorder(new Color(200, 210, 225), 1));
        scrollPanel.getVerticalScrollBar().setUnitIncrement(16);
        pnlMain.add(scrollPanel, BorderLayout.CENTER);

        lblMensaje.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        pnlMain.add(lblMensaje, BorderLayout.SOUTH);

        getContentPane().add(pnlMain, BorderLayout.CENTER);
    }

    private void cargarEstudiantes() {

        VisitaDAO dao = new VisitaDAO();
        List<String> ids = dao.listarEstudiantesDebitacora(idBitacora);

        System.out.println("idBitacora: " + idBitacora);
        System.out.println("Estudiantes encontrados: " + ids.size());
        
        for (String id : ids) System.out.println("ID: " + id);

        UsuarioDAO uDAO = new UsuarioDAO();
        estudiantes = new java.util.ArrayList<>();
        cmbEstudiante.removeAllItems();
        cmbEstudiante.addItem("-- Seleccionar Estudiante --");
        for (String idEst : ids) {
            try {
                Usuario u = uDAO.buscarPorId(Long.parseLong(idEst));
                if (u != null) {
                    estudiantes.add(u);
                    cmbEstudiante.addItem(u.getNombre1() + " " + u.getApellido1() + " - CC: " + u.getIdUsuario());
                }
            } catch (NumberFormatException e) {}
        }
    }

    private void cargarVisitas() {
        VisitaDAO dao = new VisitaDAO();
        visitasBase = dao.listarVisitasBase(idBitacora);
        cmbVisita.removeAllItems();
        cmbVisita.addItem("-- Seleccionar Visita --");
        for (Visita v : visitasBase) {
            cmbVisita.addItem("Visita N° " + v.getNumeroVisita());
        }
    }

    private void mostrarVisita() {
        int idxEst = cmbEstudiante.getSelectedIndex();
        int idxVis = cmbVisita.getSelectedIndex();
        if (idxEst <= 0 || idxVis <= 0 || visitasBase == null || estudiantes == null) return;

        Usuario estudiante = estudiantes.get(idxEst - 1);
        Visita vBase = visitasBase.get(idxVis - 1);

        // Buscar visita del estudiante
        VisitaDAO vDAO = new VisitaDAO();
        List<Visita> visitasEst = vDAO.listarPorBitacora(idBitacora, estudiante.getIdUsuario());
        Visita v = vBase;
        for (Visita ve : visitasEst) {
            if (ve.getNumeroVisita() == vBase.getNumeroVisita()) { v = ve; break; }
        }

        final Visita visitaFinal = v;
        panelDetalle.removeAll();
        panelDetalle.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel lblEncabezado = new JLabel("VISITA N° " + v.getNumeroVisita() +
            " — " + estudiante.getNombre1() + " " + estudiante.getApellido1());
        lblEncabezado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblEncabezado.setForeground(AZUL_UDI);
        lblEncabezado.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalle.add(lblEncabezado);
        panelDetalle.add(Box.createRigidArea(new Dimension(0, 20)));

        PreguntaDAO pregDAO = new PreguntaDAO();
        RespuestaDAO respDAO = new RespuestaDAO();
        List<Pregunta> preguntas = pregDAO.listarPorVisita(vBase.getIdVisita());

        for (Pregunta p : preguntas) {
            panelDetalle.add(crearTarjetaPregunta(p, respDAO, estudiante.getIdUsuario()));
            panelDetalle.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        // Ubicación de la visita (registrada por el estudiante)
        JPanel pnlUbic = new JPanel(new BorderLayout(0, 6));
        pnlUbic.setBackground(BLANCO);
        pnlUbic.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1), new EmptyBorder(12, 20, 12, 20)));
        pnlUbic.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlUbic.setMaximumSize(new Dimension(950, 120));
        JLabel lblUbicTit = new JLabel("Lugar donde se realizó la visita:");
        lblUbicTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUbicTit.setForeground(AZUL_UDI);
        String ubicTxt = (visitaFinal.getUbicacion() != null && !visitaFinal.getUbicacion().trim().isEmpty())
            ? visitaFinal.getUbicacion() : "El estudiante no registró la ubicación de esta visita.";
        JTextArea areaUbic = new JTextArea(ubicTxt);
        areaUbic.setEditable(false);
        areaUbic.setLineWrap(true);
        areaUbic.setWrapStyleWord(true);
        areaUbic.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        areaUbic.setBackground(new Color(250, 252, 255));
        areaUbic.setBorder(new EmptyBorder(8, 10, 8, 10));
        pnlUbic.add(lblUbicTit, BorderLayout.NORTH);
        pnlUbic.add(areaUbic, BorderLayout.CENTER);
        panelDetalle.add(pnlUbic);
        panelDetalle.add(Box.createRigidArea(new Dimension(0, 15)));

        // Documento / evidencia adjuntada por el estudiante
        JPanel pnlDoc = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlDoc.setBackground(BLANCO);
        pnlDoc.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1), new EmptyBorder(8, 20, 8, 20)));
        pnlDoc.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlDoc.setMaximumSize(new Dimension(950, 60));
        JLabel lblDocTit = new JLabel("Evidencia del estudiante:");
        lblDocTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDocTit.setForeground(AZUL_UDI);
        pnlDoc.add(lblDocTit);
        final String rutaDoc = visitaFinal.getRutaPlantilla();
        if (rutaDoc != null && !rutaDoc.trim().isEmpty()) {
            JButton btnVerDoc = new JButton("Abrir documento adjunto");
            btnVerDoc.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnVerDoc.setBackground(AZUL_UDI);
            btnVerDoc.setForeground(BLANCO);
            btnVerDoc.setFocusPainted(false);
            btnVerDoc.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnVerDoc.addActionListener(ev -> {
                try {
                    java.io.File f = new java.io.File(rutaDoc);
                    if (f.exists()) java.awt.Desktop.getDesktop().open(f);
                    else lblMensaje.setText("El archivo adjunto no se encuentra en: " + rutaDoc);
                } catch (Exception ex) {
                    lblMensaje.setText("No se pudo abrir el documento: " + ex.getMessage());
                }
            });
            pnlDoc.add(btnVerDoc);
            pnlDoc.add(new JLabel(new java.io.File(rutaDoc).getName()));
        } else {
            JLabel lblSin = new JLabel("El estudiante no adjuntó ningún documento.");
            lblSin.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblSin.setForeground(new Color(120, 120, 120));
            pnlDoc.add(lblSin);
        }
        panelDetalle.add(pnlDoc);
        panelDetalle.add(Box.createRigidArea(new Dimension(0, 15)));

        panelDetalle.add(new JSeparator());
        panelDetalle.add(Box.createRigidArea(new Dimension(0, 20)));
        panelDetalle.add(crearBloqueCalificacion(visitaFinal));

        panelDetalle.revalidate();
        panelDetalle.repaint();
    }

    private JPanel crearTarjetaPregunta(Pregunta p, RespuestaDAO respDAO, long idEstudiante) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BLANCO);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(950, 150));

        JLabel lblP = new JLabel(p.getNumero() + ". " + p.getTexto());
        lblP.setFont(new Font("Segoe UI", Font.BOLD, 12));

        List<Respuesta> rList = respDAO.listarPorPreguntaYEstudiante(p.getIdPregunta(), idEstudiante);
        String tResp = rList.isEmpty() ? "Sin respuesta registrada." : rList.get(0).getRespuesta();

        JTextArea areaR = new JTextArea(tResp);
        areaR.setEditable(false);
        areaR.setLineWrap(true);
        areaR.setWrapStyleWord(true);
        areaR.setBackground(new Color(250, 252, 255));
        areaR.setBorder(new EmptyBorder(10, 10, 10, 10));

        card.add(lblP);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(areaR);
        return card;
    }

    private JPanel crearBloqueCalificacion(Visita v) {
        JPanel pnlEval = new JPanel(new GridBagLayout());
        pnlEval.setBackground(BLANCO);
        pnlEval.setBorder(BorderFactory.createTitledBorder(new LineBorder(AZUL_UDI),
            " PANEL DE CALIFICACIÓN ",
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
            new Font("Segoe UI", Font.BOLD, 12), AZUL_UDI));
        pnlEval.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlEval.setMaximumSize(new Dimension(950, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        pnlEval.add(new JLabel("Retroalimentación Pedagógica:"), gbc);

        txtRetroalimentacion = new JTextArea(v.getRetroalimentacion() != null ? v.getRetroalimentacion() : "");
        txtRetroalimentacion.setLineWrap(true);
        txtRetroalimentacion.setWrapStyleWord(true);
        txtRetroalimentacion.setPreferredSize(new Dimension(0, 80));
        JScrollPane spRetro = new JScrollPane(txtRetroalimentacion);

        gbc.gridy = 1;
        pnlEval.add(spRetro, gbc);

        JPanel pnlNota = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlNota.setOpaque(false);
        pnlNota.add(new JLabel("Calificación (0.0 - 5.0): "));
        txtNota = new JTextField(v.getNota() > 0 ? String.valueOf(v.getNota()) : "", 5);
        pnlNota.add(txtNota);
        pnlNota.add(new JLabel("     Horas cumplidas en esta visita: "));
        txtHoras = new JTextField(v.getHorasValidadas() > 0 ? String.valueOf(v.getHorasValidadas()) : "", 5);
        pnlNota.add(txtHoras);

        gbc.gridy = 2;
        pnlEval.add(pnlNota, gbc);

        JButton btnGuardar = new JButton("REGISTRAR EVALUACIÓN");
        btnGuardar.setBackground(AZUL_UDI);
        btnGuardar.setForeground(BLANCO);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setPreferredSize(new Dimension(0, 40));
        btnGuardar.addActionListener(e -> guardarEvaluacion(v.getIdVisita(), idBitacora + "_V" + v.getNumeroVisita()));

        gbc.gridy = 3; gbc.insets = new Insets(10, 150, 20, 150);
        pnlEval.add(btnGuardar, gbc);

        return pnlEval;
    }

    private void guardarEvaluacion(String idVisita, String idVisitaBase) {
        String retro = txtRetroalimentacion.getText().trim();
        String notaStr = txtNota.getText().trim();

        if (notaStr.isEmpty()) { lblMensaje.setText("Error: Debe asignar una calificación."); return; }

        try {
            double nota = Double.parseDouble(notaStr);
            if (nota < 0 || nota > 5) { lblMensaje.setText("Error: La nota debe estar entre 0.0 y 5.0"); return; }

            if (new VisitaDAO().evaluarVisita(idVisita, retro, nota)) {
                // Registrar horas cumplidas en la visita base (id_estudiante NULL),
                // de donde se suman las horas de la bitácora.
                String horasStr = txtHoras.getText().trim();
                if (!horasStr.isEmpty()) {
                    try {
                        int horas = Integer.parseInt(horasStr);
                        if (horas < 0) throw new NumberFormatException();
                        new VisitaDAO().validarHoras(idVisitaBase, horas);
                    } catch (NumberFormatException ex) {
                        lblMensaje.setText("Evaluación guardada, pero las horas deben ser un entero válido.");
                        return;
                    }
                }
                lblMensaje.setText("Evaluación guardada correctamente.");
                JOptionPane.showMessageDialog(this, "Evaluación guardada.");
            } else {
                lblMensaje.setText("Error al guardar.");
            }
        } catch (NumberFormatException e) {
            lblMensaje.setText("Formato de nota inválido. Use punto para decimales.");
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        cmbEstudiante = new JComboBox<>();
        cmbVisita = new JComboBox<>();
        panelDetalle = new JPanel();
        panelDetalle.setLayout(new BoxLayout(panelDetalle, BoxLayout.Y_AXIS));
        panelDetalle.setBackground(FONDO_CLARO);
        scrollPanel = new JScrollPane(panelDetalle);
        lblMensaje = new JLabel(" ");

        cmbEstudiante.addActionListener(e -> mostrarVisita());
        cmbVisita.addActionListener(e -> mostrarVisita());
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new EvaluarVisitaForm("TEST").setVisible(true));
    }

    private JComboBox<String> cmbEstudiante;
    private JComboBox<String> cmbVisita;
    private JPanel panelDetalle;
    private JScrollPane scrollPanel;
    private JLabel lblMensaje;
    private JTextArea txtRetroalimentacion;
    private JTextField txtNota;
    private JTextField txtHoras;
}