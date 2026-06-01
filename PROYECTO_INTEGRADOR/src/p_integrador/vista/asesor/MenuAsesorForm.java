package p_integrador.vista.asesor;

import java.awt.*;
import javax.swing.*;
import p_integrador.vista.asesor.panelMenuAsesor;

/**
 * Pantalla principal de bienvenida para el módulo de Asesor.
 * Actúa como el contenedor base que integra el menú de navegación lateral
 * y proyecta la identidad institucional de la universidad.
 */
public class MenuAsesorForm extends javax.swing.JFrame {

    // Paleta de colores institucional UDI
    private final Color FONDO_CLARO = new Color(219, 240, 255);

    /**
     * Constructor del formulario. Configura la ventana principal,
     * inicializa los componentes de navegación y establece el diseño
     * visual centrado para el panel de bienvenida.
     */
    public MenuAsesorForm() {
        initComponents();
        configurarEstetica();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 675);
        setLocationRelativeTo(null);
    }

    /**
     * Aplica la arquitectura de Layout Managers para la interfaz.
     * Utiliza BorderLayout para separar el menú lateral y GridBagLayout
     * para centrar el contenido principal de forma dinámica.
     */
    private void configurarEstetica() {
        getContentPane().setBackground(FONDO_CLARO);
        getContentPane().setLayout(new BorderLayout());

        // Inserción del menú de navegación lateral para asesores
        panelMenuAsesor menu = new panelMenuAsesor();
        getContentPane().add(menu, BorderLayout.WEST);

        // Panel central diseñado para contener elementos de identidad visual
        JPanel pnlBienvenida = new JPanel(new GridBagLayout());
        pnlBienvenida.setOpaque(false);

        try {
            // Carga y escalado técnico del logo institucional
            ImageIcon iconOriginal = new ImageIcon(getClass().getResource("/imagenes/UDI_LOGO.png"));
            Image imgEscalada = iconOriginal.getImage().getScaledInstance(500, 250, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(imgEscalada));
            
            // El logo se añade al centro del GridBagLayout por defecto
            pnlBienvenida.add(lblLogo);
        } catch (Exception e) {
            // Manejo de excepción en caso de que el recurso gráfico no se encuentre
            System.err.println("Error al cargar la imagen institucional: " + e.getMessage());
            pnlBienvenida.add(new JLabel("BIENVENIDO AL SISTEMA DE PRÁCTICAS - UDI"));
        }

        getContentPane().add(pnlBienvenida, BorderLayout.CENTER);
    }

    /**
     * Inicializa las propiedades básicas del marco de la ventana.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sistema de Gestión de Prácticas - Módulo Asesor");
    }

    /**
     * Método de entrada principal para ejecutar la interfaz del menú asesor.
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String args[]) {
        try {
            // Intenta aplicar el Look and Feel del sistema operativo para mayor coherencia visual
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel: " + e.getMessage());
        }
        
        java.awt.EventQueue.invokeLater(() -> new MenuAsesorForm().setVisible(true));
    }
}