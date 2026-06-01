package p_integrador.vista.admin.GestionUsuarios;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import p_integrador.vista.admin.*;

public class ModificarUsuariosForm extends javax.swing.JFrame {

    private final Color AZUL_UDI = new Color(0, 51, 102);
    private final Color FONDO_CLARO = new Color(219, 240, 255);

    public ModificarUsuariosForm() {
        initComponents();
        configurarDiseno();
        cargarTabla(null);
        
        setSize(1220, 675);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void configurarDiseno() {
        getContentPane().setBackground(FONDO_CLARO);
        getContentPane().setLayout(new BorderLayout());

        // Sidebar
        getContentPane().add(new panelMenu(), BorderLayout.WEST);

        // Contenedor Principal (Derecha)
        JPanel pnlContenido = new JPanel(new BorderLayout());
        pnlContenido.setOpaque(false);
        pnlContenido.setBorder(new EmptyBorder(0, 20, 20, 20));

        // Navbar Superior (Botones Agregar/Modificar/Lista)
        pnlContenido.add(new panelBotonesGestion(), BorderLayout.NORTH);

        // Centro: Búsqueda y Tabla
        JPanel pnlCentro = new JPanel(new BorderLayout(0, 15));
        pnlCentro.setOpaque(false);

        // Barra de búsqueda
        JPanel pnlBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlBusqueda.setOpaque(false);
        txtBuscarCedula.setPreferredSize(new Dimension(250, 30));
        pnlBusqueda.add(new JLabel("Filtrar por Cédula:"));
        pnlBusqueda.add(txtBuscarCedula);
        pnlBusqueda.add(estilizarBotonSecundario(btnBuscar, "Buscar"));
        pnlCentro.add(pnlBusqueda, BorderLayout.NORTH);

        // Tabla con scroll
        tblScroll.setViewportView(jTable1);
        pnlCentro.add(tblScroll, BorderLayout.CENTER);
        
        pnlContenido.add(pnlCentro, BorderLayout.CENTER);

        // Sur: Botones de Acción
        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        pnlAcciones.setOpaque(false);
        pnlAcciones.add(estilizarBotonPrincipal(btnGuardar, "GUARDAR CAMBIOS"));
        
        JButton btnEliminar = new JButton("ELIMINAR");
        btnEliminar.addActionListener(e -> btnEliminarActionPerformed());
        pnlAcciones.add(estilizarBotonPeligro(btnEliminar));
        
        pnlAcciones.add(lblMensaje);
        pnlContenido.add(pnlAcciones, BorderLayout.SOUTH);

        getContentPane().add(pnlContenido, BorderLayout.CENTER);
    }

    private JButton estilizarBotonPrincipal(JButton btn, String texto) {
        btn.setText(texto);
        btn.setBackground(AZUL_UDI);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(180, 35));
        btn.setFocusPainted(false);
        return btn;
    }

    private JButton estilizarBotonSecundario(JButton btn, String texto) {
        btn.setText(texto);
        btn.setBackground(Color.WHITE);
        btn.setBorder(new LineBorder(AZUL_UDI));
        btn.setPreferredSize(new Dimension(100, 30));
        btn.setFocusPainted(false);
        return btn;
    }

    private JButton estilizarBotonPeligro(JButton btn) {
        btn.setBackground(new Color(180, 50, 50));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(150, 35));
        btn.setFocusPainted(false);
        return btn;
    }

    private void cargarTabla(String cedulaFiltro) {
        p_integrador.dao.UsuarioDAO dao = new p_integrador.dao.UsuarioDAO();
        java.util.List<p_integrador.modelo.Usuario> lista;

        if (cedulaFiltro != null && !cedulaFiltro.isEmpty()) {
            try {
                p_integrador.modelo.Usuario u = dao.buscarPorId(Long.parseLong(cedulaFiltro));
                lista = new java.util.ArrayList<>();
                if (u != null) lista.add(u);
            } catch (Exception e) { lista = dao.listarTodos(); }
        } else { lista = dao.listarTodos(); }

        String[] columnas = {"Cédula","Nombre Completo","Correo","Rol","Grupo","Estado"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return c >= 3; }
        };

        for (p_integrador.modelo.Usuario u : lista) {
            modelo.addRow(new Object[]{
                u.getIdUsuario(),
                u.getNombre1() + " " + u.getApellido1(),
                u.getCorreo(), u.getRol(), u.getGrupo(), u.getEstado()
            });
        }
        jTable1.setModel(modelo);
        jTable1.setRowHeight(25);
        
        // Configurar ComboBoxes en celdas (Rol, Grupo, Estado)
        configurarEditoresTabla();
    }

    private void configurarEditoresTabla() {
        TableColumn colRol = jTable1.getColumnModel().getColumn(3);
        colRol.setCellEditor(new DefaultCellEditor(new JComboBox<>(new String[]{"ESTUDIANTE", "DOCENTE", "ASESOR", "ADMIN"})));

        TableColumn colGrupo = jTable1.getColumnModel().getColumn(4);
        JComboBox<String> cmbGrupo = new JComboBox<>();
        new p_integrador.dao.GrupoDAO().listarTodos().forEach(g -> cmbGrupo.addItem(g.getCodigo()));
        colGrupo.setCellEditor(new DefaultCellEditor(cmbGrupo));

        TableColumn colEstado = jTable1.getColumnModel().getColumn(5);
        colEstado.setCellEditor(new DefaultCellEditor(new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"})));
    }

    private void btnEliminarActionPerformed() {
        int fila = jTable1.getSelectedRow();
        if (fila == -1) { lblMensaje.setText("Selecciona un usuario."); return; }
        long cedula = Long.parseLong(jTable1.getValueAt(fila, 0).toString());
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar usuario?") == JOptionPane.YES_OPTION) {
            if (new p_integrador.dao.UsuarioDAO().eliminar(cedula)) {
                lblMensaje.setText("Usuario eliminado.");
                cargarTabla(null);
            }
        }
    }

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) { cargarTabla(txtBuscarCedula.getText().trim()); }

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {
        if (JOptionPane.showConfirmDialog(this, "¿Guardar cambios?") != JOptionPane.YES_OPTION) return;
        p_integrador.dao.UsuarioDAO dao = new p_integrador.dao.UsuarioDAO();
        int filas = jTable1.getRowCount();
        int exitosos = 0;
        for (int i = 0; i < filas; i++) {
            long cedula = Long.parseLong(jTable1.getValueAt(i, 0).toString());
            p_integrador.modelo.Usuario u = dao.buscarPorId(cedula);
            if (u != null) {
                u.setRol(jTable1.getValueAt(i, 3).toString());
                u.setGrupo(jTable1.getValueAt(i, 4).toString());
                u.setEstado(jTable1.getValueAt(i, 5).toString());
                if (dao.actualizar(u)) exitosos++;
            }
        }
        lblMensaje.setText(exitosos + " actualizado(s).");
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        txtBuscarCedula = new JTextField();
        btnBuscar = new JButton();
        tblScroll = new JScrollPane();
        jTable1 = new JTable();
        btnGuardar = new JButton();
        lblMensaje = new JLabel(" ");
        btnBuscar.addActionListener(this::btnBuscarActionPerformed);
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);
    }

    private JButton btnBuscar, btnGuardar;
    private JTable jTable1;
    private JLabel lblMensaje;
    private JScrollPane tblScroll;
    private JTextField txtBuscarCedula;
}