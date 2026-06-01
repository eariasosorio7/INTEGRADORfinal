package p_integrador.vista.admin;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.*;
import java.util.ArrayList;
import java.util.List;
import p_integrador.dao.BitacoraDAO;
import p_integrador.dao.UsuarioDAO;
import p_integrador.dao.VisitaDAO;
import p_integrador.modelo.Bitacora;
import p_integrador.modelo.Usuario;
import p_integrador.servicio.ReporteService;

/**
 * Reportes mejorados: tabla con lista de reportes generados + vista previa de info.
 */
public class ReportesAdminForm extends javax.swing.JFrame {

    private List<Bitacora> bitacoras = new ArrayList<>();
    private List<Usuario>  estudiantes = new ArrayList<>();

    private final Color AZUL_UDI    = new Color(0, 51, 102);
    private final Color FONDO_CLARO = new Color(219, 240, 255);
    private final Color BLANCO      = Color.WHITE;

    public ReportesAdminForm() {
        initComponents();
        configurarEstetica();
        cargarBitacoras();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 675);
        setLocationRelativeTo(null);
    }

    private void configurarEstetica() {
        getContentPane().setBackground(FONDO_CLARO);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new panelMenu(), BorderLayout.WEST);

        JPanel pnlTrabajo = new JPanel(new BorderLayout(25, 0));
        pnlTrabajo.setOpaque(false);
        pnlTrabajo.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Panel izquierdo: filtros y acciones
        JPanel pnlFiltros = new JPanel(new GridBagLayout());
        pnlFiltros.setBackground(BLANCO);
        pnlFiltros.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 210, 225), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));
        pnlFiltros.setPreferredSize(new Dimension(420, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0;

        JLabel lblTitulo = new JLabel("GENERAR REPORTE PDF");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(AZUL_UDI);
        gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(0, 5, 20, 5);
        pnlFiltros.add(lblTitulo, gbc);

        gbc.gridwidth = 1; gbc.insets = new Insets(8, 5, 4, 5);
        gbc.gridy = 1;
        JLabel lblB = new JLabel("Bitácora:");
        lblB.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlFiltros.add(lblB, gbc);
        gbc.gridy = 2; gbc.weightx = 1.0;
        cmbBitacora.setPreferredSize(new Dimension(350, 33));
        pnlFiltros.add(cmbBitacora, gbc);

        gbc.gridy = 3; gbc.weightx = 0;
        JLabel lblE = new JLabel("Estudiante:");
        lblE.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlFiltros.add(lblE, gbc);
        gbc.gridy = 4; gbc.weightx = 1.0;
        cmbEstudiante.setPreferredSize(new Dimension(350, 33));
        pnlFiltros.add(cmbEstudiante, gbc);

        gbc.gridy = 5; gbc.insets = new Insets(25, 5, 8, 5);
        btnExportar.setBackground(AZUL_UDI);
        btnExportar.setForeground(BLANCO);
        btnExportar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExportar.setPreferredSize(new Dimension(0, 44));
        btnExportar.setFocusPainted(false);
        btnExportar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlFiltros.add(btnExportar, gbc);

        gbc.gridy = 6; gbc.insets = new Insets(12, 5, 0, 5);
        lblMensaje.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblMensaje.setForeground(new Color(80,80,80));
        pnlFiltros.add(lblMensaje, gbc);

        // Relleno inferior
        gbc.gridy = 7; gbc.weighty = 1.0;
        pnlFiltros.add(new JLabel(), gbc);

        pnlTrabajo.add(pnlFiltros, BorderLayout.WEST);

        // Panel derecho: info de bitácora seleccionada
        JPanel pnlInfo = new JPanel(new BorderLayout(0, 15));
        pnlInfo.setOpaque(false);

        JLabel lblInfoTit = new JLabel("VISTA PREVIA DEL REPORTE");
        lblInfoTit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblInfoTit.setForeground(AZUL_UDI);
        pnlInfo.add(lblInfoTit, BorderLayout.NORTH);

        previewReporte.setEditable(false);
        previewReporte.setContentType("text/html");
        previewReporte.setBackground(BLANCO);
        previewReporte.setBorder(new CompoundBorder(
            new LineBorder(new Color(210, 220, 230)),
            new EmptyBorder(4, 4, 4, 4)
        ));
        previewReporte.setText("<html><body style='font-family:Segoe UI; padding:16px; color:#777;'>"
            + "Selecciona una bitácora para ver la vista previa del reporte.</body></html>");
        JScrollPane spPrev = new JScrollPane(previewReporte);
        spPrev.getVerticalScrollBar().setUnitIncrement(16);
        pnlInfo.add(spPrev, BorderLayout.CENTER);

        pnlTrabajo.add(pnlInfo, BorderLayout.CENTER);
        getContentPane().add(pnlTrabajo, BorderLayout.CENTER);
    }

    private void cargarBitacoras() {
        bitacoras = new BitacoraDAO().listarTodas();
        cmbBitacora.removeAllItems();
        for (Bitacora b : bitacoras) {
            cmbBitacora.addItem(b.getIdBitacora() + " · " + b.getIdPractica() + " [" + b.getEstado() + "]");
        }
    }

    private void cargarEstudiantes() {
        int idx = cmbBitacora.getSelectedIndex();
        if (idx < 0) return;

        Bitacora b = bitacoras.get(idx);
        List<String> ids = new VisitaDAO().listarEstudiantesDebitacora(b.getIdBitacora());
        UsuarioDAO uDAO = new UsuarioDAO();

        estudiantes.clear();
        cmbEstudiante.removeAllItems();
        cmbEstudiante.addItem("— Todos los estudiantes —");

        for (String idEst : ids) {
            try {
                Usuario u = uDAO.buscarPorId(Long.parseLong(idEst));
                if (u != null) {
                    estudiantes.add(u);
                    cmbEstudiante.addItem(u.getNombre1() + " " + u.getApellido1() + "  (" + u.getIdUsuario() + ")");
                }
            } catch (Exception ignored) {}
        }

        // Vista previa que refleja el PDF
        actualizarVistaPrevia();
    }

    /** Refresca la vista previa HTML según la bitácora y el estudiante elegidos. */
    private void actualizarVistaPrevia() {
        int idxBit = cmbBitacora.getSelectedIndex();
        if (idxBit < 0) return;
        Bitacora b = bitacoras.get(idxBit);
        int idxEst = cmbEstudiante.getSelectedIndex();
        Usuario estudiante = idxEst > 0 ? estudiantes.get(idxEst - 1) : null;
        String html = ReporteService.generarVistaPreviaHTML(b, estudiante);
        previewReporte.setText(html);
        previewReporte.setCaretPosition(0);
    }

    private void exportar() {
        int idxBit = cmbBitacora.getSelectedIndex();
        if (idxBit < 0) { lblMensaje.setText("Selecciona una bitácora válida."); return; }

        Bitacora b = bitacoras.get(idxBit);
        int idxEst = cmbEstudiante.getSelectedIndex();
        Usuario estudiante = idxEst > 0 ? estudiantes.get(idxEst - 1) : null;

        lblMensaje.setText("Generando documento...");
        String ruta = ReporteService.generarReporteBitacora(b, estudiante);

        if (ruta != null) {
            try {
                java.awt.Desktop.getDesktop().open(new File(ruta));
                lblMensaje.setText("PDF generado: " + new File(ruta).getName());
                lblMensaje.setForeground(new Color(0, 130, 0));
            } catch (Exception ex) {
                lblMensaje.setText("PDF generado (no se pudo abrir automáticamente).");
                lblMensaje.setForeground(new Color(0, 130, 0));
            }
        } else {
            lblMensaje.setText("Error al construir el reporte PDF.");
            lblMensaje.setForeground(new Color(180, 50, 50));
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        cmbBitacora    = new JComboBox<>();
        cmbEstudiante  = new JComboBox<>();
        btnExportar    = new JButton("EXPORTAR DOCUMENTO PDF");
        lblMensaje     = new JLabel(" ");
        previewReporte = new JEditorPane();

        cmbBitacora.addActionListener(e -> cargarEstudiantes());
        cmbEstudiante.addActionListener(e -> actualizarVistaPrevia());
        btnExportar.addActionListener(e -> exportar());
    }

    public static void main(String args[]) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        java.awt.EventQueue.invokeLater(() -> new ReportesAdminForm().setVisible(true));
    }

    private javax.swing.JComboBox<String> cmbBitacora, cmbEstudiante;
    private javax.swing.JButton btnExportar;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JEditorPane previewReporte;
}
