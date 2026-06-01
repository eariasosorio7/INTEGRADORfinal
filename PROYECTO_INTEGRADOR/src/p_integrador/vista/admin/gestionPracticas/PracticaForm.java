package p_integrador.vista.admin.gestionPracticas;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import p_integrador.vista.admin.panelMenu;

/**
 * Formulario administrativo para la gestión de niveles de práctica.
 * Permite filtrar estudiantes por grupo y habilitar nuevas instancias de práctica.
 */
public class PracticaForm extends javax.swing.JFrame {

    private String nivel;
    private final Color AZUL_UDI = new Color(0, 51, 102);
    private final Color FONDO_CLARO = new Color(219, 240, 255);
    private final Color BLANCO = Color.WHITE;

    public PracticaForm(String nivel) {
        this.nivel = nivel;
        initComponents();
        configurarEstetica();
        cargarGrupos();
        
        lblNivelPractica.setText("GESTIÓN DE PRÁCTICA " + nivel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 675);
        setLocationRelativeTo(null);
    }

    /**
     * Configura la arquitectura visual del formulario.
     * Se ha corregido la alineación vertical mediante GridBagConstraints.
     */
    private void configurarEstetica() {
        getContentPane().setBackground(FONDO_CLARO);
        getContentPane().setLayout(new BorderLayout());

        // 1. Sidebar
        getContentPane().add(new panelMenu(), BorderLayout.WEST);

        // 2. Contenedor Principal
        JPanel pnlDerecho = new JPanel(new BorderLayout(25, 25));
        pnlDerecho.setOpaque(false);
        pnlDerecho.setBorder(new EmptyBorder(35, 35, 35, 35));

        // Cabecera
        lblNivelPractica.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblNivelPractica.setForeground(AZUL_UDI);
        pnlDerecho.add(lblNivelPractica, BorderLayout.NORTH);

        // Centro: Split entre Formulario de Control y Tabla
        JPanel pnlCentro = new JPanel(new GridBagLayout());
        pnlCentro.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        
        // --- Panel Izquierdo: Controles de Grupo ---
        JPanel pnlControles = new JPanel();
        pnlControles.setBackground(BLANCO);
        pnlControles.setBorder(new LineBorder(new Color(210, 220, 230), 1));
        pnlControles.setLayout(new GridBagLayout()); // Usamos GridBagLayout interno para centrar botones
        pnlControles.setPreferredSize(new Dimension(300, 0));

        GridBagConstraints gbcInt = new GridBagConstraints();
        gbcInt.insets = new Insets(10, 20, 10, 20);
        gbcInt.gridx = 0;

        JLabel lblSel = new JLabel("SELECCIONAR GRUPO:");
        lblSel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbcInt.gridy = 0; pnlControles.add(lblSel, gbcInt);
        
        gbcInt.gridy = 1; pnlControles.add(cmbGrupo, gbcInt);
        gbcInt.gridy = 2; gbcInt.insets = new Insets(40, 20, 10, 20);
        pnlControles.add(estilizarBotonAccion(btnVerBitacoras, false), gbcInt);
        
        gbcInt.gridy = 3; gbcInt.insets = new Insets(10, 20, 10, 20);
        pnlControles.add(estilizarBotonAccion(btnCrearPractica, true), gbcInt);
        
        gbcInt.gridy = 4; gbcInt.weighty = 1.0; // Espaciador inferior
        pnlControles.add(lblMensaje, gbcInt);

        // Ajuste de alineación para que no se vea torcido
        gbc.gridx = 0; 
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0; // Forzamos que se estire a toda la altura disponible
        gbc.fill = GridBagConstraints.BOTH; 
        gbc.insets = new Insets(0, 0, 0, 25);
        pnlCentro.add(pnlControles, gbc);

        // --- Panel Derecho: Tabla de Estudiantes ---
        JPanel pnlTabla = new JPanel(new BorderLayout(0, 10));
        pnlTabla.setOpaque(false);
        
        JLabel lblEst = new JLabel("ESTUDIANTES REGISTRADOS EN EL GRUPO");
        lblEst.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEst.setForeground(AZUL_UDI);
        
        jScrollPane1.setViewportView(jTable1);
        jTable1.setRowHeight(30);
        
        pnlTabla.add(lblEst, BorderLayout.NORTH);
        pnlTabla.add(jScrollPane1, BorderLayout.CENTER);

        gbc.gridx = 1; 
        gbc.weightx = 1.0; 
        gbc.insets = new Insets(0, 0, 0, 0);
        pnlCentro.add(pnlTabla, gbc);

        pnlDerecho.add(pnlCentro, BorderLayout.CENTER);
        getContentPane().add(pnlDerecho, BorderLayout.CENTER);
    }

    private JButton estilizarBotonAccion(JButton btn, boolean esPrincipal) {
        btn.setPreferredSize(new Dimension(220, 42));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (esPrincipal) {
            btn.setBackground(AZUL_UDI);
            btn.setForeground(BLANCO);
            btn.setBorder(null);
        } else {
            btn.setBackground(BLANCO);
            btn.setForeground(AZUL_UDI);
            btn.setBorder(new LineBorder(AZUL_UDI, 1));
        }
        return btn;
    }

    /**
     * Inicializa los componentes básicos.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {
        lblNivelPractica = new JLabel();
        cmbGrupo = new JComboBox<>();
        cmbGrupo.setPreferredSize(new Dimension(220, 35));
        jScrollPane1 = new JScrollPane();
        jTable1 = new JTable();
        btnVerBitacoras = new JButton("VER BITÁCORAS");
        btnCrearPractica = new JButton("CREAR PRÁCTICA");
        lblMensaje = new JLabel(" ");

        cmbGrupo.addActionListener(e -> cargarEstudiantes());
        btnVerBitacoras.addActionListener(this::btnVerBitacorasActionPerformed);
        btnCrearPractica.addActionListener(this::btnCrearPracticaActionPerformed);
    }

    private void btnVerBitacorasActionPerformed(java.awt.event.ActionEvent evt) {
        int fila = jTable1.getSelectedRow();
        if (fila == -1) {
            lblMensaje.setText("Selecciona un estudiante.");
            return;
        }
        long idEst = Long.parseLong(jTable1.getValueAt(fila, 0).toString());
        String idPrac = cmbGrupo.getSelectedItem().toString() + "_" + nivel;
        new BitacorasForm(idPrac, idEst).setVisible(true);
        this.dispose();
    }

    private void btnCrearPracticaActionPerformed(java.awt.event.ActionEvent evt) {
        if (cmbGrupo.getSelectedItem() == null || cmbGrupo.getSelectedItem().toString().equals("NINGUNO")) {
            lblMensaje.setText("Selección inválida.");
            return;
        }

        String grupo = cmbGrupo.getSelectedItem().toString();
        String idPrac = grupo + "_" + nivel;
        p_integrador.dao.PracticaDAO dao = new p_integrador.dao.PracticaDAO();

        if (dao.existePractica(idPrac)) {
            lblMensaje.setText("La práctica ya existe.");
            return;
        }

        int horas = nivel.equals("I") ? 20 : nivel.equals("II") ? 30 : 40;
        p_integrador.modelo.Practica p = new p_integrador.modelo.Practica(idPrac, nivel, horas, "ACTIVA", grupo);

        if (dao.crear(p)) {
            lblMensaje.setText("Creada con éxito.");
        } else {
            lblMensaje.setText("Error de servidor.");
        }
    }

    private void cargarGrupos() {
        p_integrador.dao.GrupoDAO dao = new p_integrador.dao.GrupoDAO();
        cmbGrupo.removeAllItems();
        for (p_integrador.modelo.Grupo g : dao.listarTodos()) {
            cmbGrupo.addItem(g.getCodigo());
        }
        cargarEstudiantes();
    }

    private void cargarEstudiantes() {
        if (cmbGrupo.getSelectedItem() == null) return;
        String grupo = cmbGrupo.getSelectedItem().toString();
        p_integrador.dao.UsuarioDAO dao = new p_integrador.dao.UsuarioDAO();
        java.util.List<p_integrador.modelo.Usuario> lista = dao.listarPorGrupo(grupo);

        String[] columnas = {"Cédula", "Nombre Completo", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (p_integrador.modelo.Usuario u : lista) {
            modelo.addRow(new Object[]{ u.getIdUsuario(), u.getNombre1() + " " + u.getApellido1(), u.getEstado() });
        }
        jTable1.setModel(modelo);
    }

    public static void main(String args[]) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        java.awt.EventQueue.invokeLater(() -> new PracticaForm("I").setVisible(true));
    }

    private javax.swing.JButton btnCrearPractica, btnVerBitacoras;
    private javax.swing.JComboBox<String> cmbGrupo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblMensaje, lblNivelPractica;
}