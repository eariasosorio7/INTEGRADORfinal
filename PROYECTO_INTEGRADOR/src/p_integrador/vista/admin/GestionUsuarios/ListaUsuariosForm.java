package p_integrador.vista.admin.GestionUsuarios;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import p_integrador.vista.admin.*;

public class ListaUsuariosForm extends javax.swing.JFrame {

    private final Color FONDO_CLARO = new Color(219, 240, 255);

    public ListaUsuariosForm() {
        initComponents();
        configurarDiseno();
        cargarTabla();
        
        setSize(1220, 675);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void configurarDiseno() {
        getContentPane().setBackground(FONDO_CLARO);
        getContentPane().setLayout(new BorderLayout());

        // Sidebar
        getContentPane().add(new panelMenu(), BorderLayout.WEST);

        // Contenedor Derecho
        JPanel pnlContenido = new JPanel(new BorderLayout());
        pnlContenido.setOpaque(false);
        pnlContenido.setBorder(new EmptyBorder(0, 20, 30, 30));

        // Navbar superior
        pnlContenido.add(new panelBotonesGestion(), BorderLayout.NORTH);

        // Tabla Central
        JPanel pnlTabla = new JPanel(new BorderLayout());
        pnlTabla.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("LISTADO GENERAL DE USUARIOS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setBorder(new EmptyBorder(10, 0, 10, 0));
        pnlTabla.add(lblTitulo, BorderLayout.NORTH);

        jScrollPane1.setViewportView(jTable1);
        pnlTabla.add(jScrollPane1, BorderLayout.CENTER);

        pnlContenido.add(pnlTabla, BorderLayout.CENTER);
        getContentPane().add(pnlContenido, BorderLayout.CENTER);
    }

    private void cargarTabla() {
        p_integrador.dao.UsuarioDAO dao = new p_integrador.dao.UsuarioDAO();
        java.util.List<p_integrador.modelo.Usuario> lista = dao.listarTodos();

        String[] columnas = {"Cédula","Nombre Completo","Correo","Rol","Grupo","Estado"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (p_integrador.modelo.Usuario u : lista) {
            modelo.addRow(new Object[]{
                u.getIdUsuario(),
                u.getNombre1() + " " + u.getApellido1(),
                u.getCorreo(), u.getRol(), u.getGrupo(), u.getEstado()
            });
        }
        jTable1.setModel(modelo);
        jTable1.setRowHeight(28);
        jTable1.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jScrollPane1 = new JScrollPane();
        jTable1 = new JTable();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String args[]) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        java.awt.EventQueue.invokeLater(() -> new ListaUsuariosForm().setVisible(true));
    }

    private JScrollPane jScrollPane1;
    private JTable jTable1;
}