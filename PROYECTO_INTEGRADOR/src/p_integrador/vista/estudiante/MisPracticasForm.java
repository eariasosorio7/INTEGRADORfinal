package p_integrador.vista.estudiante;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import p_integrador.dao.DocumentoDAO;
import p_integrador.dao.PracticaDAO;
import p_integrador.modelo.Practica;
import p_integrador.modelo.SesionActiva;

/**
 * Vista de prácticas del estudiante.
 * Condición: documentos aprobados requeridos para acceder.
 */
public class MisPracticasForm extends javax.swing.JFrame {

    private final Color AZUL_UDI    = new Color(0, 51, 102);
    private final Color FONDO_CLARO = new Color(219, 240, 255);
    private final Color BLANCO      = Color.WHITE;
    private final Color VERDE       = new Color(34, 139, 34);
    private final Color ROJO        = new Color(200, 40, 40);
    private final Color NARANJA     = new Color(200, 120, 0);

    // Contenido central intercambiable
    private JPanel pnlContenido;
    private JLabel lblMensaje;

    public MisPracticasForm() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 675);
        setLocationRelativeTo(null);
        buildUI();
        verificarYCargar();
    }

    private void buildUI() {
        getContentPane().setBackground(FONDO_CLARO);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new panelMenuEstudiante(), BorderLayout.WEST);

        JPanel pnlMain = new JPanel(new BorderLayout(0, 18));
        pnlMain.setOpaque(false);
        pnlMain.setBorder(new EmptyBorder(28, 28, 20, 28));

        JLabel lblTitulo = new JLabel("MIS PRÁCTICAS ACADÉMICAS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(AZUL_UDI);
        pnlMain.add(lblTitulo, BorderLayout.NORTH);

        pnlContenido = new JPanel(new BorderLayout());
        pnlContenido.setOpaque(false);
        pnlMain.add(pnlContenido, BorderLayout.CENTER);

        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlMain.add(lblMensaje, BorderLayout.SOUTH);

        getContentPane().add(pnlMain, BorderLayout.CENTER);
    }

    private void verificarYCargar() {
        long idEst = SesionActiva.getUsuario().getIdUsuario();
        DocumentoDAO docDAO = new DocumentoDAO();

        if (!docDAO.tieneDocumentosAprobados(idEst)) {
            mostrarBloqueo();
        } else {
            mostrarPracticas();
        }
    }

    private void mostrarBloqueo() {
        pnlContenido.removeAll();

        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(new Color(255, 248, 248));
        tarjeta.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 170, 170), 1),
            new EmptyBorder(40, 50, 40, 50)
        ));
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));

        JLabel ico = new JLabel("", SwingConstants.CENTER);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        ico.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("Documentos pendientes de aprobación");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(ROJO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel desc = new JLabel(
            "<html><center>Para gestionar tus prácticas, todos tus documentos<br>"
            + "deben ser <b>aprobados</b> por el administrador.<br><br>"
            + "Ve a <b>Documentación</b> para subir o revisar el estado.</center></html>",
            SwingConstants.CENTER
        );
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setForeground(new Color(100, 50, 50));
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btn = new JButton("Ir a Documentación");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(AZUL_UDI);
        btn.setForeground(BLANCO);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> {
            new DocumentacionEstudianteForm().setVisible(true);
            dispose();
        });

        tarjeta.add(Box.createRigidArea(new Dimension(0, 10)));
        tarjeta.add(ico);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 15)));
        tarjeta.add(titulo);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 12)));
        tarjeta.add(desc);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 20)));
        tarjeta.add(btn);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(tarjeta);
        pnlContenido.add(wrapper, BorderLayout.CENTER);

        lblMensaje.setText("Completa y espera la aprobación de tus documentos para continuar.");
        lblMensaje.setForeground(NARANJA);

        pnlContenido.revalidate();
        pnlContenido.repaint();
    }

    private void mostrarPracticas() {
        pnlContenido.removeAll();

        try {
            String grupo = SesionActiva.getUsuario().getGrupo();
            if (grupo == null || "NINGUNO".equals(grupo)) {
                lblMensaje.setText("No tienes un grupo asignado. Contacta al administrador.");
                lblMensaje.setForeground(NARANJA);
                return;
            }

            List<Practica> lista = new PracticaDAO().listarFinalizadasPorGrupo(grupo);

            String[] cols = {"Práctica", "Nivel", "Horas Requeridas", "Estado"};
            DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            for (Practica p : lista) {
                modelo.addRow(new Object[]{
                    p.getIdPractica(), "Nivel " + p.getNivel(),
                    p.getHorasRequeridas() + " hrs", p.getEstado()
                });
            }

            JTable tabla = new JTable(modelo);
            tabla.setRowHeight(36);
            tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
            tabla.getTableHeader().setBackground(new Color(235, 242, 252));
            tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            tabla.setSelectionBackground(new Color(210, 228, 252));
            tabla.setGridColor(new Color(230, 235, 245));
            tabla.getTableHeader().setReorderingAllowed(false);
            tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JScrollPane scroll = new JScrollPane(tabla);
            scroll.setBorder(new LineBorder(new Color(210, 220, 235)));
            pnlContenido.add(scroll, BorderLayout.CENTER);

            // Barra inferior con botón de exportar PDF individual
            JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            acciones.setOpaque(false);
            JButton btnExportar = new JButton("Exportar práctica seleccionada a PDF");
            btnExportar.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnExportar.setBackground(AZUL_UDI);
            btnExportar.setForeground(BLANCO);
            btnExportar.setFocusPainted(false);
            btnExportar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnExportar.setPreferredSize(new Dimension(320, 38));
            btnExportar.addActionListener(e -> {
                int fila = tabla.getSelectedRow();
                if (fila < 0) {
                    lblMensaje.setText("Selecciona una práctica de la tabla para exportarla.");
                    lblMensaje.setForeground(NARANJA);
                    return;
                }
                exportarPractica(lista.get(fila));
            });
            acciones.add(btnExportar);
            JLabel ayuda = new JLabel("Se genera un PDF individual con tus bitácoras, visitas, respuestas y notas de la práctica.");
            ayuda.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            ayuda.setForeground(new Color(120, 120, 120));
            acciones.add(ayuda);
            pnlContenido.add(acciones, BorderLayout.SOUTH);

            if (lista.isEmpty()) {
                lblMensaje.setText("Aún no tienes prácticas finalizadas en el grupo " + grupo + ".");
                lblMensaje.setForeground(NARANJA);
            } else {
                lblMensaje.setText(lista.size() + " práctica(s) finalizada(s) en el grupo " + grupo);
                lblMensaje.setForeground(VERDE);
            }
        } catch (Exception e) {
            lblMensaje.setText("Error al cargar prácticas.");
            lblMensaje.setForeground(ROJO);
        }

        pnlContenido.revalidate();
        pnlContenido.repaint();
    }

    /** Exporta a PDF, para el estudiante en sesión, todas las bitácoras de una práctica finalizada. */
    private void exportarPractica(Practica practica) {
        try {
            p_integrador.modelo.Usuario est = SesionActiva.getUsuario();
            java.util.List<p_integrador.modelo.Bitacora> bitacoras =
                new p_integrador.dao.BitacoraDAO().listarPorPractica(practica.getIdPractica());

            if (bitacoras.isEmpty()) {
                lblMensaje.setText("Esta práctica no tiene bitácoras para exportar.");
                lblMensaje.setForeground(NARANJA);
                return;
            }

            String ultimaRuta = null;
            for (p_integrador.modelo.Bitacora b : bitacoras) {
                ultimaRuta = p_integrador.servicio.ReporteService.generarReporteBitacora(b, est);
            }

            if (ultimaRuta != null) {
                try { java.awt.Desktop.getDesktop().open(new java.io.File(ultimaRuta)); } catch (Exception ignore) {}
                lblMensaje.setText("PDF generado en la carpeta 'reportes' (" + bitacoras.size() + " bitácora/s).");
                lblMensaje.setForeground(VERDE);
            } else {
                lblMensaje.setText("No se pudo generar el PDF.");
                lblMensaje.setForeground(ROJO);
            }
        } catch (Exception e) {
            lblMensaje.setText("Error al exportar: " + e.getMessage());
            lblMensaje.setForeground(ROJO);
        }
    }

    public static void main(String args[]) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        java.awt.EventQueue.invokeLater(() -> new MisPracticasForm().setVisible(true));
    }
}
