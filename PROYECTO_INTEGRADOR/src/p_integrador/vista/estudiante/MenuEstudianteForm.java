package p_integrador.vista.estudiante;

import java.awt.*;
import javax.swing.*;
import p_integrador.vista.estudiante.panelMenuEstudiante;

/**
 * Pantalla de bienvenida principal para el módulo del Estudiante.
 * Actúa como el frame raíz que integra la navegación lateral y proyecta 
 * la identidad visual corporativa de la universidad.
 */
public class MenuEstudianteForm extends javax.swing.JFrame {

    // Paleta de colores institucional UDI
    private final Color FONDO_CLARO = new Color(219, 240, 255);

    /**
     * Constructor del formulario. Configura la arquitectura de la ventana,
     * inicializa el sidebar del estudiante y establece el diseño centrado
     * para el panel de bienvenida.
     */
    public MenuEstudianteForm() {
        initComponents();
        configurarEstetica();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 675);
        setLocationRelativeTo(null);
    }

    /**
     * Aplica la arquitectura de Layout Managers para la interfaz.
     * Utiliza BorderLayout para la estructura global y GridBagLayout 
     * para el posicionamiento dinámico del logo central.
     */
    private void configurarEstetica() {
        getContentPane().setBackground(FONDO_CLARO);
        getContentPane().setLayout(new BorderLayout());

        // Inserción del menú de navegación lateral (Sidebar del Estudiante)
        panelMenuEstudiante menu = new panelMenuEstudiante();
        getContentPane().add(menu, BorderLayout.WEST);

        // Panel central diseñado para la identidad institucional
        JPanel pnlBienvenida = new JPanel(new GridBagLayout());
        pnlBienvenida.setOpaque(false);

        try {
            // Carga y escalado técnico del logo UDI
            ImageIcon iconOriginal = new ImageIcon(getClass().getResource("/imagenes/UDI_LOGO.png"));
            Image imgEscalada = iconOriginal.getImage().getScaledInstance(500, 250, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(imgEscalada));
            
            // Posicionamiento automático en el centro del GridBagLayout
            pnlBienvenida.add(lblLogo);
        } catch (Exception e) {
            // Fallback en caso de ausencia de recursos gráficos
            JLabel lblFallback = new JLabel("BIENVENIDO AL SISTEMA DE PRÁCTICAS - UDI");
            lblFallback.setFont(new Font("Segoe UI", Font.BOLD, 24));
            pnlBienvenida.add(lblFallback);
        }

        getContentPane().add(pnlBienvenida, BorderLayout.CENTER);
    }

    /**
     * Inicializa las propiedades básicas del marco de la ventana definidas por la suite.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sistema de Gestión de Prácticas - Estudiante");
    }

    /**
     * Punto de entrada principal para el módulo del estudiante.
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String args[]) {
        try {
            // Sincronización con el Look and Feel nativo del sistema operativo
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Registro de advertencia silencioso
        }
        
        java.awt.EventQueue.invokeLater(() -> new MenuEstudianteForm().setVisible(true));
    }
}