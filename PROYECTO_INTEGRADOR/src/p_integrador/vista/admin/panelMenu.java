package p_integrador.vista.admin;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class panelMenu extends javax.swing.JPanel {

    private final Color AZUL_UDI = new Color(0, 51, 102);
    private final Color AZUL_MUY_CLARO = new Color(240, 248, 255);
    private final Color AMARILLO_UDI = new Color(255, 204, 0);
    private final Color GRIS_TEXTO = new Color(100, 100, 100);

    public panelMenu() {
        initComponents();
        estilizarPanel();
        lblNombre.setText(p_integrador.modelo.SesionActiva.getNombre().toUpperCase());
        lblRol.setText(p_integrador.modelo.SesionActiva.getRol());
    }

    private void estilizarPanel() {
        this.setBackground(AZUL_MUY_CLARO);
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(240, 675));

        JPanel panelPerfil = new JPanel();
        panelPerfil.setOpaque(false);
        panelPerfil.setLayout(new BoxLayout(panelPerfil, BoxLayout.Y_AXIS));
        panelPerfil.setBorder(new EmptyBorder(50, 20, 40, 20));

        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombre.setForeground(AZUL_UDI);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblRol.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRol.setForeground(GRIS_TEXTO);
        lblRol.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel linea = new JPanel();
        linea.setMaximumSize(new Dimension(40, 2));
        linea.setBackground(AMARILLO_UDI);
        linea.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelPerfil.add(lblNombre);
        panelPerfil.add(Box.createRigidArea(new Dimension(0, 5)));
        panelPerfil.add(lblRol);
        panelPerfil.add(Box.createRigidArea(new Dimension(0, 15)));
        panelPerfil.add(linea);

        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));

        estilizarBotonMenu(btnGestionUsuarios);
        estilizarBotonMenu(btnGestionPracticas);
        estilizarBotonMenu(btnDocumentacion);
        estilizarBotonMenu(btnReportes);
        // btnConfiguracion ELIMINADO

        panelBotones.add(btnGestionUsuarios);
        panelBotones.add(btnGestionPracticas);
        panelBotones.add(btnDocumentacion);
        panelBotones.add(btnReportes);

        JPanel panelFooter = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelFooter.setOpaque(false);
        panelFooter.setBorder(new EmptyBorder(0, 0, 40, 0));

        btnCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCerrarSesion.setForeground(new Color(180, 50, 50));
        btnCerrarSesion.setContentAreaFilled(false);
        btnCerrarSesion.setBorder(BorderFactory.createLineBorder(new Color(180, 50, 50), 1));
        btnCerrarSesion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrarSesion.setPreferredSize(new Dimension(160, 35));
        panelFooter.add(btnCerrarSesion);

        this.add(panelPerfil, BorderLayout.NORTH);
        this.add(panelBotones, BorderLayout.CENTER);
        this.add(panelFooter, BorderLayout.SOUTH);
    }

    private void estilizarBotonMenu(JButton btn) {
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(AZUL_UDI);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(AZUL_UDI);
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(AZUL_UDI);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        lblNombre = new javax.swing.JLabel();
        lblRol = new javax.swing.JLabel();
        btnGestionUsuarios = new javax.swing.JButton("Gestión Usuarios");
        btnGestionPracticas = new javax.swing.JButton("Gestión Prácticas");
        btnDocumentacion = new javax.swing.JButton("Documentación");
        btnReportes = new javax.swing.JButton("Reportes");
        btnCerrarSesion = new javax.swing.JButton("Cerrar Sesión");

        btnGestionUsuarios.addActionListener(this::btnGestionUsuariosActionPerformed);
        btnGestionPracticas.addActionListener(this::btnGestionPracticasActionPerformed);
        btnDocumentacion.addActionListener(this::btnDocumentacionActionPerformed);
        btnReportes.addActionListener(this::btnReportesActionPerformed);
        btnCerrarSesion.addActionListener(this::btnCerrarSesionActionPerformed);
    }

    private void btnGestionUsuariosActionPerformed(java.awt.event.ActionEvent evt) {
        new p_integrador.vista.admin.GestionUsuarios.GestionUsuariosForm().setVisible(true);
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private void btnGestionPracticasActionPerformed(java.awt.event.ActionEvent evt) {
        new p_integrador.vista.admin.gestionPracticas.GestionPracticasForm().setVisible(true);
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private void btnDocumentacionActionPerformed(java.awt.event.ActionEvent evt) {
        new p_integrador.vista.admin.DocumentacionAdminForm().setVisible(true);
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private void btnReportesActionPerformed(java.awt.event.ActionEvent evt) {
        new p_integrador.vista.admin.ReportesAdminForm().setVisible(true);
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {
        new p_integrador.vista.LoginForm().setVisible(true);
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private javax.swing.JButton btnCerrarSesion, btnDocumentacion, btnGestionPracticas, btnGestionUsuarios, btnReportes;
    private javax.swing.JLabel lblNombre, lblRol;
}
