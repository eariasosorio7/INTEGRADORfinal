package p_integrador.vista.estudiante;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import p_integrador.dao.BitacoraDAO;
import p_integrador.dao.VisitaDAO;
import p_integrador.modelo.Bitacora;
import p_integrador.modelo.SesionActiva;
import p_integrador.modelo.Usuario;
import p_integrador.servicio.ReporteService;

/**
 * Reportes del estudiante: puede exportar a PDF sus PROPIAS bitácoras
 * (individualmente) y un reporte completo con todas sus prácticas/bitácoras.
 * Solo trabaja sobre el estudiante en sesión, con sus propias respuestas y notas.
 */
public class ReportesEstudianteForm extends javax.swing.JFrame {

    private final Color AZUL_UDI    = new Color(0, 51, 102);
    private final Color FONDO_CLARO = new Color(219, 240, 255);
    private final Color BLANCO      = Color.WHITE;
    private final Color VERDE       = new Color(0, 130, 0);
    private final Color ROJO        = new Color(180, 50, 50);

    private JTable tblBitacoras;
    private JLabel lblMensaje;
    private List<Bitacora> bitacoras = new ArrayList<>();
    private final Usuario estudiante = SesionActiva.getUsuario();

    public ReportesEstudianteForm() {
        buildUI();
        cargarBitacoras();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 675);
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        getContentPane().setBackground(FONDO_CLARO);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new panelMenuEstudiante(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout(0, 18));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(28, 28, 20, 28));

        JLabel titulo = new JLabel("MIS REPORTES");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(AZUL_UDI);
        main.add(titulo, BorderLayout.NORTH);

        tblBitacoras = new JTable();
        tblBitacoras.setRowHeight(32);
        tblBitacoras.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblBitacoras.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblBitacoras.getTableHeader().setReorderingAllowed(false);
        tblBitacoras.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(tblBitacoras);
        sp.setBorder(new LineBorder(new Color(210, 220, 235)));
        main.add(sp, BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout());
        sur.setOpaque(false);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        acciones.setOpaque(false);
        JButton btnBitacora = boton("Exportar bitácora seleccionada");
        btnBitacora.addActionListener(e -> exportarBitacoraSeleccionada());
        JButton btnTodo = boton("Exportar reporte completo");
        btnTodo.addActionListener(e -> exportarReporteCompleto());
        acciones.add(btnBitacora);
        acciones.add(btnTodo);
        JLabel ayuda = new JLabel("Los PDF incluyen tus respuestas, retroalimentación y notas.");
        ayuda.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        ayuda.setForeground(new Color(120, 120, 120));
        acciones.add(ayuda);
        sur.add(acciones, BorderLayout.NORTH);

        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sur.add(lblMensaje, BorderLayout.SOUTH);
        main.add(sur, BorderLayout.SOUTH);

        getContentPane().add(main, BorderLayout.CENTER);
    }

    private void cargarBitacoras() {
        bitacoras.clear();
        VisitaDAO vDAO = new VisitaDAO();
        BitacoraDAO bDAO = new BitacoraDAO();
        List<String> ids = vDAO.listarBitacorasDeEstudiante(estudiante.getIdUsuario());
        for (String id : ids) {
            Bitacora b = bDAO.buscarPorId(id);
            if (b != null) bitacoras.add(b);
        }

        String[] cols = {"Bitácora", "Práctica", "Objetivo general", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Bitacora b : bitacoras) {
            String obj = b.getObjetivo() == null || b.getObjetivo().isEmpty() ? "(sin objetivo)" : b.getObjetivo();
            modelo.addRow(new Object[]{ b.getIdBitacora(), b.getIdPractica(), obj, b.getEstado() });
        }
        tblBitacoras.setModel(modelo);
        if (tblBitacoras.getColumnModel().getColumnCount() > 0) {
            tblBitacoras.getColumnModel().getColumn(0).setPreferredWidth(180);
            tblBitacoras.getColumnModel().getColumn(1).setPreferredWidth(140);
            tblBitacoras.getColumnModel().getColumn(2).setPreferredWidth(420);
            tblBitacoras.getColumnModel().getColumn(3).setPreferredWidth(90);
        }
        if (bitacoras.isEmpty()) {
            lblMensaje.setText("Aún no tienes bitácoras asignadas.");
            lblMensaje.setForeground(new Color(120, 120, 120));
        }
    }

    private void exportarBitacoraSeleccionada() {
        int fila = tblBitacoras.getSelectedRow();
        if (fila < 0) { msg("Selecciona una bitácora de la tabla.", ROJO); return; }
        String ruta = ReporteService.generarReporteBitacora(bitacoras.get(fila), estudiante);
        abrir(ruta);
    }

    private void exportarReporteCompleto() {
        String ruta = ReporteService.generarReporteEstudiante(estudiante);
        abrir(ruta);
    }

    private void abrir(String ruta) {
        if (ruta != null) {
            try { Desktop.getDesktop().open(new File(ruta)); } catch (Exception ignore) {}
            msg("PDF generado en la carpeta 'reportes': " + new File(ruta).getName(), VERDE);
        } else {
            msg("No se pudo generar el PDF.", ROJO);
        }
    }

    private void msg(String m, Color c) { lblMensaje.setText(m); lblMensaje.setForeground(c); }

    private JButton boton(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(AZUL_UDI);
        b.setForeground(BLANCO);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(280, 38));
        return b;
    }

    public static void main(String args[]) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        java.awt.EventQueue.invokeLater(() -> new ReportesEstudianteForm().setVisible(true));
    }
}
