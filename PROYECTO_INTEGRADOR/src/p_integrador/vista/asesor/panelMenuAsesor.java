package p_integrador.vista.asesor;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Panel de navegación lateral diseñado específicamente para el rol de Asesor.
 * Proporciona acceso centralizado a las bitácoras asignadas y gestión de sesión.
 * Utiliza una arquitectura de diseño basada en la identidad visual de la UDI.
 */
public class panelMenuAsesor extends javax.swing.JPanel {

    // Paleta de colores institucional
    private final Color AZUL_UDI = new Color(0, 51, 102);
    private final Color AZUL_MENU = new Color(219, 240, 255);
    private final Color BLANCO = Color.WHITE;
    private final Color ROJO_UDI = new Color(180, 50, 50);

    /**
     * Constructor del componente. Inicializa la interfaz, aplica estilos 
     * institucionales y carga la información de la sesión activa.
     */
    public panelMenuAsesor() {
        initComponents();
        estilizarPanel();
        cargarDatosUsuario();
    }

    /**
     * Recupera y despliega la información del usuario autenticado 
     * desde el modelo de gestión de sesiones.
     */
    private void cargarDatosUsuario() {
        lblNombre.setText(p_integrador.modelo.SesionActiva.getNombre().toUpperCase());
        lblRol.setText(p_integrador.modelo.SesionActiva.getRol());
    }

    /**
     * Configura la arquitectura visual del panel.
     * Implementa un diseño de caja (BoxLayout) para la alineación vertical 
     * de los elementos de navegación.
     */
    private void estilizarPanel() {
        this.setBackground(AZUL_MENU);
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(220, 675));
        
        // Panel superior para información del usuario
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
        
        // Panel central para botones de navegación
        JPanel pnlBotones = new JPanel();
        pnlBotones.setOpaque(false);
        pnlBotones.setLayout(new BoxLayout(pnlBotones, BoxLayout.Y_AXIS));
        pnlBotones.setBorder(new EmptyBorder(20, 20, 0, 20));

        estilizarBotonMenu(btnMisBitacoras);
        pnlBotones.add(btnMisBitacoras);

        // Panel inferior para cerrar sesión
        JPanel pnlSur = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 40));
        pnlSur.setOpaque(false);
        estilizarBotonSalir(btnCerrarSesion);
        pnlSur.add(btnCerrarSesion);

        this.add(pnlUsuario, BorderLayout.NORTH);
        this.add(pnlBotones, BorderLayout.CENTER);
        this.add(pnlSur, BorderLayout.SOUTH);
    }

    /**
     * Aplica estilos estéticos y eventos de interacción a los botones del menú.
     * Incluye lógica de Hover para retroalimentación visual al usuario.
     * 
     * @param btn El JButton a configurar.
     */
    private void estilizarBotonMenu(JButton btn) {
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setBackground(BLANCO);
        btn.setForeground(AZUL_UDI);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
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
     * Configura el botón de finalización de sesión con colores de advertencia.
     * 
     * @param btn El JButton de salida.
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
        btnMisBitacoras = new JButton("Mis Bitácoras");
        btnCerrarSesion = new JButton("Cerrar Sesión");

        btnMisBitacoras.addActionListener(this::btnMisBitacorasActionPerformed);
        btnCerrarSesion.addActionListener(this::btnCerrarSesionActionPerformed);
    }

    private void btnMisBitacorasActionPerformed(java.awt.event.ActionEvent evt) {
        new p_integrador.vista.asesor.BitacorasAsesorForm().setVisible(true);
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {
        new p_integrador.vista.LoginForm().setVisible(true);
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private javax.swing.JButton btnMisBitacoras;
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblRol;
}