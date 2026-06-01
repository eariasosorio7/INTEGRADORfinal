package p_integrador.vista.admin.GestionUsuarios;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class AgregarUsuariosForm extends javax.swing.JFrame {

    private final Color AZUL_UDI = new Color(0, 51, 102);
    private final Color FONDO_CLARO = new Color(219, 240, 255);
    private final Color BLANCO = Color.WHITE;

    public AgregarUsuariosForm() {
        initComponents();
        configurarDiseno();
        cargarGrupos();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 675);
        setLocationRelativeTo(null);
    }

    private void configurarDiseno() {
        getContentPane().setBackground(FONDO_CLARO);
        getContentPane().setLayout(new BorderLayout());

        // 1. Sidebar y Navbar Superior
        p_integrador.vista.admin.panelMenu menu = new p_integrador.vista.admin.panelMenu();
        getContentPane().add(menu, BorderLayout.WEST);

        JPanel pnlDerecho = new JPanel(new BorderLayout());
        pnlDerecho.setOpaque(false);

        p_integrador.vista.admin.panelBotonesGestion botones = new p_integrador.vista.admin.panelBotonesGestion();
        pnlDerecho.add(botones, BorderLayout.NORTH);

        // 2. Contenedor del Formulario (Tarjeta)
        JPanel pnlCentro = new JPanel(new GridBagLayout());
        pnlCentro.setOpaque(false);
        
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BLANCO);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título de la tarjeta
        JLabel lblTitulo = new JLabel("REGISTRO DE NUEVO USUARIO");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(AZUL_UDI);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 20, 10);
        card.add(lblTitulo, gbc);

        // --- COLUMNA 1 ---
        gbc.gridwidth = 1; gbc.insets = new Insets(5, 10, 5, 10);
        
        gbc.gridy = 1; gbc.gridx = 0; card.add(new JLabel("Primer Nombre:"), gbc);
        gbc.gridy = 2; card.add(txtNombre1, gbc);
        
        gbc.gridy = 3; card.add(new JLabel("Segundo Nombre:"), gbc);
        gbc.gridy = 4; card.add(txtNombre2, gbc);
        
        gbc.gridy = 5; card.add(new JLabel("Primer Apellido:"), gbc);
        gbc.gridy = 6; card.add(txtApellido1, gbc);
        
        gbc.gridy = 7; card.add(new JLabel("Segundo Apellido:"), gbc);
        gbc.gridy = 8; card.add(txtApellido2, gbc);

        // --- COLUMNA 2 ---
        gbc.gridx = 1;
        
        gbc.gridy = 1; card.add(new JLabel("Cédula:"), gbc);
        gbc.gridy = 2; card.add(txtCedula, gbc);
        
        gbc.gridy = 3; card.add(new JLabel("Rol:"), gbc);
        gbc.gridy = 4; card.add(cmbRol, gbc);
        
        gbc.gridy = 5; card.add(lblGrupoTag, gbc);
        gbc.gridy = 6; 
        JPanel pnlGrupo = new JPanel(new BorderLayout(5, 0));
        pnlGrupo.setOpaque(false);
        pnlGrupo.add(cmbGrupo, BorderLayout.CENTER);
        pnlGrupo.add(btnAgregarGrupo, BorderLayout.EAST);
        card.add(pnlGrupo, gbc);
        
        gbc.gridy = 7; card.add(new JLabel("Correo Institucional:"), gbc);
        gbc.gridy = 8; card.add(txtCorreoGenerado, gbc);

        // --- BOTÓN GUARDAR ---
        gbc.gridy = 9; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 10, 0, 10);
        btnGuardar.setBackground(AZUL_UDI);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setPreferredSize(new Dimension(0, 40));
        card.add(btnGuardar, gbc);

        gbc.gridy = 10;
        lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblMensaje, gbc);

        pnlCentro.add(card);
        pnlDerecho.add(pnlCentro, BorderLayout.CENTER);
        getContentPane().add(pnlDerecho, BorderLayout.CENTER);
        
        // Configuración inicial de visibilidad
        lblGrupoTag.setVisible(false);
        cmbGrupo.setVisible(false);
        btnAgregarGrupo.setVisible(false);
    }

    private void cargarGrupos() {
        p_integrador.dao.GrupoDAO dao = new p_integrador.dao.GrupoDAO();
        cmbGrupo.removeAllItems();
        for (p_integrador.modelo.Grupo g : dao.listarTodos()) {
            cmbGrupo.addItem(g.getCodigo());
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        txtCedula = new JTextField();
        txtNombre1 = new JTextField();
        txtNombre2 = new JTextField();
        txtApellido1 = new JTextField();
        txtApellido2 = new JTextField();
        txtCorreoGenerado = new JTextField();
        txtCorreoGenerado.setEditable(false);
        txtCorreoGenerado.setBackground(new Color(245, 245, 245));
        
        cmbRol = new JComboBox<>(new String[] { "ESTUDIANTE", "DOCENTE", "ASESOR", "ADMIN" });
        cmbGrupo = new JComboBox<>();
        lblGrupoTag = new JLabel("Grupo:");
        lblMensaje = new JLabel(" ");
        
        btnGuardar = new JButton("GUARDAR USUARIO");
        btnAgregarGrupo = new JButton("+");
        btnAgregarGrupo.setToolTipText("Gestionar Grupos");

        // Listeners para generar correo automáticamente
        java.awt.event.KeyAdapter keyRel = new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { generarCorreoPreview(); }
        };
        txtCedula.addKeyListener(keyRel);
        txtNombre1.addKeyListener(keyRel);
        txtApellido1.addKeyListener(keyRel);

        cmbRol.addActionListener(e -> {
            boolean esEstudiante = cmbRol.getSelectedItem().toString().equals("ESTUDIANTE");
            lblGrupoTag.setVisible(esEstudiante);
            cmbGrupo.setVisible(esEstudiante);
            btnAgregarGrupo.setVisible(esEstudiante);
        });

        btnAgregarGrupo.addActionListener(e -> {
            new p_integrador.vista.admin.GestionUsuarios.GestionGruposForm().setVisible(true);
            this.dispose();
        });

        btnGuardar.addActionListener(this::btnGuardarActionPerformed);
    }

    private void generarCorreoPreview() {
        String n1 = txtNombre1.getText().trim();
        String a1 = txtApellido1.getText().trim();
        String ced = txtCedula.getText().trim();
        if (!n1.isEmpty() && !a1.isEmpty() && ced.length() >= 2) {
            try {
                long c = Long.parseLong(ced);
                txtCorreoGenerado.setText(p_integrador.modelo.Usuario.generarCorreo(n1, a1, c));
            } catch (Exception e) { txtCorreoGenerado.setText(""); }
        } else { txtCorreoGenerado.setText(""); }
    }

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {
        String cedStr = txtCedula.getText().trim();
        String n1 = txtNombre1.getText().trim();
        String a1 = txtApellido1.getText().trim();
        
        if (cedStr.isEmpty() || n1.isEmpty() || a1.isEmpty()) {
            lblMensaje.setText("Cédula, Nombre y Apellido obligatorios.");
            lblMensaje.setForeground(Color.RED);
            return;
        }

        try {
            long cedula = Long.parseLong(cedStr);
            String rol = cmbRol.getSelectedItem().toString();
            String grupo = rol.equals("ESTUDIANTE") ? cmbGrupo.getSelectedItem().toString() : "NINGUNO";
            
            p_integrador.modelo.Usuario u = new p_integrador.modelo.Usuario(
                cedula, n1, txtNombre2.getText().trim(), a1, txtApellido2.getText().trim(),
                txtCorreoGenerado.getText(), cedStr, rol, "ACTIVO", grupo
            );

            if (new p_integrador.dao.UsuarioDAO().crear(u)) {
                lblMensaje.setText("¡Usuario creado con éxito!");
                lblMensaje.setForeground(new Color(0, 150, 0));
                limpiarCampos();
            } else {
                lblMensaje.setText("Error al guardar en la base de datos.");
            }
        } catch (Exception e) {
            lblMensaje.setText("Cédula inválida.");
        }
    }

    private void limpiarCampos() {
        txtCedula.setText(""); txtNombre1.setText(""); txtNombre2.setText("");
        txtApellido1.setText(""); txtApellido2.setText(""); txtCorreoGenerado.setText("");
        cmbRol.setSelectedIndex(0);
    }

    public static void main(String args[]) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        java.awt.EventQueue.invokeLater(() -> new AgregarUsuariosForm().setVisible(true));
    }

    private JTextField txtApellido1, txtApellido2, txtCedula, txtCorreoGenerado, txtNombre1, txtNombre2;
    private JComboBox<String> cmbGrupo, cmbRol;
    private JButton btnAgregarGrupo, btnGuardar;
    private JLabel lblMensaje, lblGrupoTag;
}