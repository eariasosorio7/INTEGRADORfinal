package p_integrador.vista.asesor;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import p_integrador.modelo.Bitacora;
import p_integrador.dao.BitacoraDAO;
import p_integrador.modelo.SesionActiva;

/**
 * Interfaz gráfica para la gestión de bitácoras asignadas a un Asesor.
 * Permite visualizar el listado de seguimientos pedagógicos y acceder 
 * al módulo de evaluación de visitas.
 */
public class BitacorasAsesorForm extends javax.swing.JFrame {

    // Paleta de colores institucional UDI
    private final Color AZUL_UDI = new Color(0, 51, 102);
    private final Color FONDO_CLARO = new Color(219, 240, 255);
    private final Color BLANCO = Color.WHITE;

    /**
     * Constructor del formulario. Inicializa la interfaz de usuario,
     * aplica la configuración estética y carga los registros de la base de datos.
     */
    public BitacorasAsesorForm() {
        initComponents();
        configurarEstetica();
        cargarBitacoras();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 675);
        setLocationRelativeTo(null);
    }

    /**
     * Configura el diseño visual utilizando Layout Managers para garantizar
     * la adaptabilidad de la ventana. Separa la navegación del contenido principal.
     */
    private void configurarEstetica() {
        getContentPane().setBackground(FONDO_CLARO);
        getContentPane().setLayout(new BorderLayout());

        // Sidebar de navegación del Asesor
        p_integrador.vista.asesor.panelMenuAsesor menu = new p_integrador.vista.asesor.panelMenuAsesor();
        getContentPane().add(menu, BorderLayout.WEST);

        // Contenedor de información central
        JPanel pnlContenido = new JPanel(new BorderLayout(20, 20));
        pnlContenido.setOpaque(false);
        pnlContenido.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Cabecera con título descriptivo
        JLabel lblTitulo = new JLabel("MIS BITÁCORAS ASIGNADAS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(AZUL_UDI);
        pnlContenido.add(lblTitulo, BorderLayout.NORTH);

        // Tabla de datos centralizada
        jScrollPane1.setViewportView(jTable1);
        jTable1.setRowHeight(25);
        pnlContenido.add(jScrollPane1, BorderLayout.CENTER);

        // Panel inferior de acciones y retroalimentación
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setOpaque(false);

        btnEvaluar.setBackground(AZUL_UDI);
        btnEvaluar.setForeground(BLANCO);
        btnEvaluar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEvaluar.setPreferredSize(new Dimension(200, 40));
        btnEvaluar.setFocusPainted(false);
        btnEvaluar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlFooter.add(btnEvaluar, BorderLayout.WEST);
        pnlFooter.add(lblMensaje, BorderLayout.CENTER);
        lblMensaje.setBorder(new EmptyBorder(0, 20, 0, 0));

        pnlContenido.add(pnlFooter, BorderLayout.SOUTH);
        getContentPane().add(pnlContenido, BorderLayout.CENTER);
    }

    /**
     * Inicializa los componentes básicos de la interfaz.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {
        jScrollPane1 = new JScrollPane();
        jTable1 = new JTable();
        btnEvaluar = new JButton("EVALUAR BITÁCORA");
        lblMensaje = new JLabel(" ");

        btnEvaluar.addActionListener(e -> evaluarBitacora());
    }

    /**
     * Recupera el identificador de la bitácora seleccionada y redirige 
     * al formulario de evaluación técnica de visitas.
     */
    private void evaluarBitacora() {
        int fila = jTable1.getSelectedRow();
        if (fila == -1) { 
            lblMensaje.setText("Selecciona una bitácora de la lista."); 
            return; 
        }
        String idBitacora = jTable1.getValueAt(fila, 0).toString();
        new p_integrador.vista.asesor.EvaluarVisitaForm(idBitacora).setVisible(true);
        this.dispose();
    }

    /**
     * Consulta al {@link BitacoraDAO} para obtener las bitácoras vinculadas
     * al ID del asesor en la sesión activa. Actualiza el modelo de la tabla.
     */
    private void cargarBitacoras() {
        try {
            long idAsesor = SesionActiva.getUsuario().getIdUsuario();
            BitacoraDAO dao = new BitacoraDAO();
            List<Bitacora> lista = dao.listarPorAsesor(idAsesor);

            String[] columnas = {"ID Bitácora", "Práctica", "Fecha Límite", "Estado"};
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
                @Override
                public boolean isCellEditable(int r, int c) { return false; }
            };

            for (Bitacora b : lista) {
                modelo.addRow(new Object[]{
                    b.getIdBitacora(), b.getIdPractica(),
                    b.getFechaLimite(), b.getEstado()
                });
            }
            jTable1.setModel(modelo);
            jTable1.getTableHeader().setReorderingAllowed(false);
        } catch (Exception e) {
            lblMensaje.setText("Error al cargar datos: " + e.getMessage());
        }
    }

    /**
     * Punto de entrada de la aplicación para pruebas del módulo de asesor.
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String args[]) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        java.awt.EventQueue.invokeLater(() -> new BitacorasAsesorForm().setVisible(true));
    }

    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JButton btnEvaluar;
}