package p_integrador.vista.admin;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.MatteBorder;

public class panelBotonesGestion extends javax.swing.JPanel {

    private final Color AZUL_UDI = new Color(0, 51, 102);
    private final Color FONDO_CLARO = new Color(219, 240, 255);
    private final Color AMARILLO_UDI = new Color(255, 204, 0);

    public panelBotonesGestion() {
        initComponents();
        estilizarPanel();
    }

    private void estilizarPanel() {
        this.setBackground(FONDO_CLARO);
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Margen para respetar el menú lateral
        gbc.insets = new Insets(0, 250, 0, 0); 
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;

        // Botón Agregar
        gbc.gridx = 0;
        estilizarBotonNavegacion(btnAgregar, "AGREGAR");
        this.add(btnAgregar, gbc);

        // Botón Modificar
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 0, 0);
        estilizarBotonNavegacion(btnModificar, "MODIFICAR");
        this.add(btnModificar, gbc);

        // Botón Lista
        gbc.gridx = 2;
        estilizarBotonNavegacion(btnLista, "LISTA USUARIOS");
        this.add(btnLista, gbc);
        
        // Espaciador para alinear a la izquierda
        gbc.gridx = 3;
        gbc.weightx = 1.0;
        this.add(new Box.Filler(new Dimension(0,0), new Dimension(0,0), new Dimension(32767,0)), gbc);
    }

    private void estilizarBotonNavegacion(JButton btn, String texto) {
        btn.setText(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(160, 60)); // Más altos para que parezcan pestañas
        btn.setForeground(AZUL_UDI);
        btn.setBackground(FONDO_CLARO);
        btn.setOpaque(true);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Borde inferior invisible por defecto
        btn.setBorder(new MatteBorder(0, 0, 4, 0, FONDO_CLARO));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Al pasar el mouse, se resalta con el amarillo de la UDI abajo
                btn.setBorder(new MatteBorder(0, 0, 4, 0, AMARILLO_UDI));
                btn.setForeground(AZUL_UDI);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBorder(new MatteBorder(0, 0, 4, 0, FONDO_CLARO));
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        btnAgregar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnLista = new javax.swing.JButton();

        btnAgregar.addActionListener(this::btnAgregarActionPerformed);
        btnModificar.addActionListener(this::btnModificarActionPerformed);
        btnLista.addActionListener(this::btnListaActionPerformed);
    }

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {
        new p_integrador.vista.admin.GestionUsuarios.AgregarUsuariosForm().setVisible(true);
        cerrarVentanaPadre();
    }

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {
        new p_integrador.vista.admin.GestionUsuarios.ModificarUsuariosForm().setVisible(true);
        cerrarVentanaPadre();
    }

    private void btnListaActionPerformed(java.awt.event.ActionEvent evt) {
        new p_integrador.vista.admin.GestionUsuarios.ListaUsuariosForm().setVisible(true);
        cerrarVentanaPadre();
    }

    private void cerrarVentanaPadre() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) window.dispose();
    }

    private javax.swing.JButton btnAgregar, btnLista, btnModificar;
}