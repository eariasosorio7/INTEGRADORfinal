package p_integrador.vista.estudiante;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import p_integrador.dao.BitacoraDAO;
import p_integrador.dao.VisitaDAO;
import p_integrador.modelo.Bitacora;
import p_integrador.modelo.SesionActiva;

/**
 * Vista de bitácoras del estudiante.
 * Los estudiantes quedan asignados automáticamente al crear la bitácora,
 * por lo que ya no es necesario ingresar códigos manualmente.
 */
public class BitacorasEstudianteForm extends javax.swing.JFrame {

    private final Color AZUL_UDI    = new Color(0, 51, 102);
    private final Color FONDO_CLARO = new Color(219, 240, 255);
    private final Color BLANCO      = Color.WHITE;
    private final Color VERDE       = new Color(34, 139, 34);
    private final Color NARANJA     = new Color(200, 120, 0);

    private JTable tblBitacoras;
    private JLabel lblMensaje;
    private JButton btnVerVisitas;
    private List<Bitacora> bitacoras;

    public BitacorasEstudianteForm() {
        initComponents();
        buildUI();
        cargarBitacoras();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 675);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        tblBitacoras  = new JTable();
        lblMensaje    = new JLabel(" ");
        btnVerVisitas = new JButton("VER MIS AVANCES");
        btnVerVisitas.addActionListener(e -> verVisitas());
    }

    private void buildUI() {
        getContentPane().setBackground(FONDO_CLARO);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new panelMenuEstudiante(), BorderLayout.WEST);

        JPanel pnlMain = new JPanel(new BorderLayout(0, 18));
        pnlMain.setOpaque(false);
        pnlMain.setBorder(new EmptyBorder(28, 28, 20, 28));

        // Cabecera
        JPanel pnlHeader = new JPanel(new BorderLayout(0, 6));
        pnlHeader.setOpaque(false);

        JLabel lblTitulo = new JLabel("MIS PRÁCTICAS EN CURSO");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(AZUL_UDI);

        JLabel lblSub = new JLabel("Aquí aparecen las bitácoras a las que has sido asignado.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(90, 90, 90));

        pnlHeader.add(lblTitulo, BorderLayout.NORTH);
        pnlHeader.add(lblSub, BorderLayout.SOUTH);
        pnlMain.add(pnlHeader, BorderLayout.NORTH);

        // Tabla de bitácoras
        tblBitacoras.setRowHeight(36);
        tblBitacoras.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblBitacoras.getTableHeader().setBackground(new Color(235, 242, 252));
        tblBitacoras.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblBitacoras.setSelectionBackground(new Color(210, 228, 252));
        tblBitacoras.setGridColor(new Color(230, 235, 245));
        tblBitacoras.setShowGrid(true);

        JScrollPane scroll = new JScrollPane(tblBitacoras);
        scroll.setBorder(new LineBorder(new Color(210, 220, 235)));
        pnlMain.add(scroll, BorderLayout.CENTER);

        // Footer
        JPanel pnlFooter = new JPanel(new BorderLayout(15, 0));
        pnlFooter.setOpaque(false);

        btnVerVisitas.setBackground(AZUL_UDI);
        btnVerVisitas.setForeground(BLANCO);
        btnVerVisitas.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVerVisitas.setPreferredSize(new Dimension(200, 40));
        btnVerVisitas.setFocusPainted(false);
        btnVerVisitas.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblMensaje.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        pnlFooter.add(btnVerVisitas, BorderLayout.WEST);
        pnlFooter.add(lblMensaje, BorderLayout.CENTER);
        pnlMain.add(pnlFooter, BorderLayout.SOUTH);

        getContentPane().add(pnlMain, BorderLayout.CENTER);
    }

    private void cargarBitacoras() {
        long idEst = SesionActiva.getUsuario().getIdUsuario();
        VisitaDAO vDAO = new VisitaDAO();
        BitacoraDAO bDAO = new BitacoraDAO();

        List<String> ids = vDAO.listarBitacorasDeEstudiante(idEst);
        bitacoras = new java.util.ArrayList<>();

        String[] cols = {"Bitácora", "Práctica", "Fecha Límite", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (String id : ids) {
            Bitacora b = bDAO.buscarPorId(id);
            if (b != null) {
                bitacoras.add(b);
                modelo.addRow(new Object[]{
                    b.getIdBitacora(), b.getIdPractica(), b.getFechaLimite(), b.getEstado()
                });
            }
        }
        tblBitacoras.setModel(modelo);
        tblBitacoras.getTableHeader().setReorderingAllowed(false);

        // Renderer de color por estado
        tblBitacoras.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    String est = t.getValueAt(row, 3) != null ? t.getValueAt(row, 3).toString() : "";
                    setBackground("ACTIVA".equals(est) ? new Color(240, 252, 242) : new Color(252, 248, 235));
                }
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        if (ids.isEmpty()) {
            lblMensaje.setText("No tienes bitácoras asignadas aún.");
            lblMensaje.setForeground(NARANJA);
        } else {
            lblMensaje.setText("" + ids.size() + " bitácora(s) encontrada(s).");
            lblMensaje.setForeground(VERDE);
        }
    }

    private void verVisitas() {
        int fila = tblBitacoras.getSelectedRow();
        if (fila < 0) {
            lblMensaje.setText("Selecciona una bitácora primero.");
            lblMensaje.setForeground(NARANJA);
            return;
        }
        String idBit = bitacoras.get(fila).getIdBitacora();
        long idEst   = SesionActiva.getUsuario().getIdUsuario();
        new VisitasEstudianteForm(idBit, idEst).setVisible(true);
        this.dispose();
    }

    public static void main(String args[]) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        java.awt.EventQueue.invokeLater(() -> new BitacorasEstudianteForm().setVisible(true));
    }
}
