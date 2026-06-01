package p_integrador.vista.estudiante;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Componente de navegación lateral (Sidebar) diseñado para el perfil de Estudiante.
 * Proporciona acceso directo a los módulos de prácticas, bitácoras y gestión documental.
 * Implementa una interfaz responsiva basada en los lineamientos visuales de la UDI.
 */
public class panelMenuEstudiante extends javax.swing.JPanel {

    // Constantes de color institucional
    private final Color AZUL_UDI = new Color(0, 51, 102);
    private final Color AZUL_MENU = new Color(219, 240, 255);
    private final Color BLANCO = Color.WHITE;
    private final Color ROJO_UDI = new Color(180, 50, 50);

    /**
     * Constructor del componente. Configura la estética institucional,
     * inicializa los eventos de navegación y sincroniza los datos del usuario en sesión.
     */
    public panelMenuEstudiante() {
        initComponents();
        estilizarPanel();
        cargarDatosUsuario();
    }

    /**
     * Recupera y despliega la información del estudiante autenticado 
     * desde el modelo de gestión de sesiones {@link p_integrador.modelo.SesionActiva}.
     */
    private void cargarDatosUsuario() {
        lblNombre.setText(p_integrador.modelo.SesionActiva.getNombre().toUpperCase());
        lblRol.setText(p_integrador.modelo.SesionActiva.getRol());
    }

    /**
     * Configura la arquitectura visual del sidebar.
     * Utiliza una combinación de BorderLayout y BoxLayout para garantizar la 
     * alineación vertical perfecta de los botones de navegación.
     */
    private void estilizarPanel() {
        this.setBackground(AZUL_MENU);
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(220, 675));
        
        // Panel superior: Identificación del Estudiante
        JPanel pnlUsuario = new JPanel();
        pnlUsuario.setOpaque(false);
        pnlUsuario.setLayout(new BoxLayout(pnlUsuario, BoxLayout.Y_AXIS));
        pnlUsuario.setBorder(new EmptyBorder(50, 10, 30, 10));

        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombre.setForeground(AZUL_UDI);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblRol.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRol.setForeground(new Color(100, 100, 100));
        lblRol.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlUsuario.add(lblNombre);
        pnlUsuario.add(Box.createRigidArea(new Dimension(0, 5)));
        pnlUsuario.add(lblRol);
        
        // Panel central: Botones de Acción
        JPanel pnlBotones = new JPanel();
        pnlBotones.setOpaque(false);
        pnlBotones.setLayout(new BoxLayout(pnlBotones, BoxLayout.Y_AXIS));
        pnlBotones.setBorder(new EmptyBorder(20, 20, 0, 20));

        estilizarBotonMenu(btnMisPracticas);
        estilizarBotonMenu(btnBitacoras);
        estilizarBotonMenu(btnDocumentacion);
        estilizarBotonMenu(btnReportes);

        pnlBotones.add(btnMisPracticas);
        pnlBotones.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlBotones.add(btnBitacoras);
        pnlBotones.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlBotones.add(btnDocumentacion);
        pnlBotones.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlBotones.add(btnReportes);

        // Panel inferior: Finalización de Sesión
        JPanel pnlSur = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 40));
        pnlSur.setOpaque(false);
        estilizarBotonSalir(btnCerrarSesion);
        pnlSur.add(btnCerrarSesion);

        this.add(pnlUsuario, BorderLayout.NORTH);
        this.add(pnlBotones, BorderLayout.CENTER);
        this.add(pnlSur, BorderLayout.SOUTH);
    }

    /**
     * Aplica estilos estandarizados y animaciones de color (Hover) a los botones.
     * 
     * @param btn El componente JButton a estilizar.
     */
    private void estilizarBotonMenu(JButton btn) {
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setBackground(BLANCO);
        btn.setForeground(AZUL_UDI);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(new Color(200, 220, 240)));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(AZUL_UDI);
                btn.setForeground(BLANCO);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(BLANCO);
                btn.setForeground(AZUL_UDI);
            }
        });
    }

    /**
     * Configura el botón de salida con una identidad visual de advertencia.
     */
    private void estilizarBotonSalir(JButton btn) {
        btn.setPreferredSize(new Dimension(150, 35));
        btn.setBackground(new Color(255, 255, 255, 150));
        btn.setForeground(ROJO_UDI);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createLineBorder(ROJO_UDI));
        btn.setFocusPainted(false);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        lblNombre = new JLabel();
        lblRol = new JLabel();
        btnMisPracticas = new JButton("Mis Prácticas");
        btnBitacoras = new JButton("Bitácoras");
        btnDocumentacion = new JButton("Documentación");
        btnReportes = new JButton("Reportes");
        btnCerrarSesion = new JButton("Cerrar Sesión");

        btnMisPracticas.addActionListener(this::btnMisPracticasActionPerformed);
        btnBitacoras.addActionListener(this::btnBitacorasActionPerformed);
        btnDocumentacion.addActionListener(this::btnDocumentacionActionPerformed);
        btnReportes.addActionListener(this::btnReportesActionPerformed);
        btnCerrarSesion.addActionListener(this::btnCerrarSesionActionPerformed);
    }

    private void btnReportesActionPerformed(java.awt.event.ActionEvent evt) {
        new p_integrador.vista.estudiante.ReportesEstudianteForm().setVisible(true);
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private void btnMisPracticasActionPerformed(java.awt.event.ActionEvent evt) {
        new p_integrador.vista.estudiante.MisPracticasForm().setVisible(true);
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private void btnBitacorasActionPerformed(java.awt.event.ActionEvent evt) {
        // Bloquear el acceso a bitácoras si la documentación NO está completa
        long idEst = p_integrador.modelo.SesionActiva.getUsuario().getIdUsuario();
        if (!new p_integrador.dao.DocumentoDAO().documentacionCompleta(idEst)) {
            JOptionPane.showMessageDialog(this,
                "No puedes acceder a las bitácoras todavía.\n\n"
                + "Tu documentación debe estar COMPLETA: todos tus documentos\n"
                + "deben estar aprobados por el administrador.\n\n"
                + "Revisa el estado en la sección Documentación.",
                "Documentación incompleta", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new p_integrador.vista.estudiante.BitacorasEstudianteForm().setVisible(true);
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private void btnDocumentacionActionPerformed(java.awt.event.ActionEvent evt) {
        new p_integrador.vista.estudiante.DocumentacionEstudianteForm().setVisible(true);
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {
        new p_integrador.vista.LoginForm().setVisible(true);
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private javax.swing.JButton btnMisPracticas, btnBitacoras, btnDocumentacion, btnReportes, btnCerrarSesion;
    private javax.swing.JLabel lblNombre, lblRol;
}