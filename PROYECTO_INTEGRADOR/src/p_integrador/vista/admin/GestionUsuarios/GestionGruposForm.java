package p_integrador.vista.admin.GestionUsuarios;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import p_integrador.dao.GrupoDAO;
import p_integrador.modelo.Grupo;
import p_integrador.vista.admin.panelMenu;

/**
 * Interfaz administrativa para la creación y gestión de grupos académicos.
 * Permite la generación automatizada de códigos de grupo basados en semestre 
 * e identificador (ej. 6LEI-D) y controla la integridad referencial al eliminar.
 */
public class GestionGruposForm extends javax.swing.JFrame {

    // Paleta de colores institucional UDI
    private final Color AZUL_UDI = new Color(0, 51, 102);
    private final Color FONDO_CLARO = new Color(219, 240, 255);
    private final Color BLANCO = Color.WHITE;

    /**
     * Constructor del formulario. Configura la ventana principal,
     * inicializa la arquitectura de layouts y carga los datos existentes.
     */
    public GestionGruposForm() {
        initComponents();
        configurarEstetica();
        cargarTabla();
        actualizarCodigo();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 675);
        setLocationRelativeTo(null);
    }

    /**
     * Aplica la arquitectura de diseño responsivo.
     * Organiza el sidebar a la izquierda y un panel central dividido para 
     * optimizar el flujo de trabajo del administrador.
     */
    private void configurarEstetica() {
        getContentPane().setBackground(FONDO_CLARO);
        getContentPane().setLayout(new BorderLayout());

        // 1. Sidebar lateral
        getContentPane().add(new panelMenu(), BorderLayout.WEST);

        // 2. Contenedor de contenido con márgenes
        JPanel pnlContenido = new JPanel(new BorderLayout(25, 25));
        pnlContenido.setOpaque(false);
        pnlContenido.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Título de la vista
        JLabel lblTitulo = new JLabel("GESTIÓN DE GRUPOS ACADÉMICOS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(AZUL_UDI);
        pnlContenido.add(lblTitulo, BorderLayout.NORTH);

        // Panel Central dividido (Formulario | Tabla)
        JPanel pnlCentro = new JPanel(new GridBagLayout());
        pnlCentro.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // --- SECCIÓN IZQUIERDA: Formulario de Creación ---
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(BLANCO);
        pnlForm.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 220, 230), 1),
                new EmptyBorder(30, 25, 30, 25)
        ));
        pnlForm.setPreferredSize(new Dimension(350, 0));

        GridBagConstraints f = new GridBagConstraints();
        f.insets = new Insets(10, 0, 10, 0);
        f.fill = GridBagConstraints.HORIZONTAL;
        f.gridx = 0; f.weightx = 1.0;

        f.gridy = 0; pnlForm.add(new JLabel("SEMESTRE:"), f);
        f.gridy = 1; pnlForm.add(cmbSemestre, f);
        
        f.gridy = 2; pnlForm.add(new JLabel("IDENTIFICADOR (LEI-):"), f);
        f.gridy = 3; pnlForm.add(cmbIdentificador, f);
        
        f.gridy = 4; pnlForm.add(new JLabel("CÓDIGO GENERADO:"), f);
        f.gridy = 5; 
        txtCodigo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtCodigo.setForeground(AZUL_UDI);
        pnlForm.add(txtCodigo, f);

        f.gridy = 6; f.insets = new Insets(30, 0, 5, 0);
        pnlForm.add(estilizarBoton(btnGuardar, true), f);
        
        f.gridy = 7; f.insets = new Insets(10, 0, 5, 0);
        pnlForm.add(estilizarBoton(btnEliminar, false), f);
        
        f.gridy = 8; f.weighty = 1.0;
        lblMensaje.setVerticalAlignment(SwingConstants.TOP);
        pnlForm.add(lblMensaje, f);

        gbc.gridx = 0; gbc.weightx = 0.0; gbc.insets = new Insets(0, 0, 0, 30);
        pnlCentro.add(pnlForm, gbc);

        // --- SECCIÓN DERECHA: Tabla de registros ---
        JPanel pnlTabla = new JPanel(new BorderLayout(0, 10));
        pnlTabla.setOpaque(false);
        
        jScrollPane1.setViewportView(jTable1);
        jTable1.setRowHeight(30);
        jTable1.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        pnlTabla.add(new JLabel("GRUPOS REGISTRADOS EN EL SISTEMA"), BorderLayout.NORTH);
        pnlTabla.add(jScrollPane1, BorderLayout.CENTER);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.insets = new Insets(0, 0, 0, 0);
        pnlCentro.add(pnlTabla, gbc);

        pnlContenido.add(pnlCentro, BorderLayout.CENTER);
        getContentPane().add(pnlContenido, BorderLayout.CENTER);
    }

    /**
     * Aplica estilos estandarizados a los botones de acción.
     */
    private JButton estilizarBoton(JButton btn, boolean esPrimario) {
        btn.setPreferredSize(new Dimension(0, 40));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (esPrimario) {
            btn.setBackground(AZUL_UDI);
            btn.setForeground(BLANCO);
            btn.setBorder(null);
        } else {
            btn.setBackground(BLANCO);
            btn.setForeground(new Color(180, 50, 50));
            btn.setBorder(new LineBorder(new Color(180, 50, 50), 1));
        }
        return btn;
    }

    /**
     * Actualiza el campo de texto del código combinando las selecciones 
     * de los ComboBox de semestre e identificador.
     */
    private void actualizarCodigo() {
        String semestre = cmbSemestre.getSelectedItem().toString();
        String id = cmbIdentificador.getSelectedItem().toString();
        txtCodigo.setText(semestre + "LEI-" + id);
    }

    /**
     * Consulta al {@link GrupoDAO} para listar todos los grupos y 
     * refrescar la información desplegada en la tabla.
     */
    private void cargarTabla() {
        List<Grupo> lista = new GrupoDAO().listarTodos();
        String[] columnas = {"Código de Grupo", "Semestre Académico"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Grupo g : lista) {
            modelo.addRow(new Object[]{g.getCodigo(), g.getSemestre()});
        }
        jTable1.setModel(modelo);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        cmbSemestre = new JComboBox<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8" });
        cmbIdentificador = new JComboBox<>(new String[] { "A", "B", "C", "D" });
        txtCodigo = new JTextField();
        txtCodigo.setEditable(false);
        btnGuardar = new JButton("GUARDAR GRUPO");
        btnEliminar = new JButton("ELIMINAR GRUPO");
        jScrollPane1 = new JScrollPane();
        jTable1 = new JTable();
        lblMensaje = new JLabel(" ");

        cmbSemestre.addActionListener(e -> actualizarCodigo());
        cmbIdentificador.addActionListener(e -> actualizarCodigo());
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);
    }

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {
        String codigo = txtCodigo.getText().trim();
        int semestre = Integer.parseInt(cmbSemestre.getSelectedItem().toString());

        if (new GrupoDAO().crear(new Grupo(codigo, semestre))) {
            lblMensaje.setText("Grupo creado con éxito.");
            lblMensaje.setForeground(new Color(0, 150, 0));
            cargarTabla();
        } else {
            lblMensaje.setText("El código ya está registrado.");
            lblMensaje.setForeground(Color.RED);
        }
    }

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {
        int fila = jTable1.getSelectedRow();
        if (fila == -1) {
            lblMensaje.setText("Selecciona un grupo de la tabla.");
            return;
        }
        String codigo = jTable1.getValueAt(fila, 0).toString();
        GrupoDAO dao = new GrupoDAO();

        if (dao.tieneEstudiantes(codigo)) {
            JOptionPane.showMessageDialog(this, "No se puede eliminar: El grupo contiene estudiantes vinculados.", "Restricción de Integridad", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "¿Eliminar el grupo " + codigo + "?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (dao.eliminar(codigo)) {
                lblMensaje.setText("Registro eliminado.");
                cargarTabla();
            }
        }
    }

    public static void main(String args[]) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        java.awt.EventQueue.invokeLater(() -> new GestionGruposForm().setVisible(true));
    }

    private javax.swing.JButton btnGuardar, btnEliminar;
    private javax.swing.JComboBox<String> cmbIdentificador, cmbSemestre;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JTextField txtCodigo;
}