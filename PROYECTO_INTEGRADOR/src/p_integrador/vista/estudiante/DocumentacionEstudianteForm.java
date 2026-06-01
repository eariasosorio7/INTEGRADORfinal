package p_integrador.vista.estudiante;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import p_integrador.dao.DocumentoDAO;
import p_integrador.modelo.Documento;
import p_integrador.modelo.SesionActiva;

/**
 * Vista mejorada de documentación para el estudiante.
 * - Tarjetas por tipo de documento con estado visual
 * - Subida intuitiva por tipo
 * - Eliminar documentos propios
 */
public class DocumentacionEstudianteForm extends javax.swing.JFrame {

    private static final String[] TIPOS = {"Cédula", "Carné Estudiantil", "EPS", "ARL", "Hoja de Vida"};

    private final Color AZUL_UDI    = new Color(0, 51, 102);
    private final Color FONDO_CLARO = new Color(219, 240, 255);
    private final Color BLANCO      = Color.WHITE;
    private final Color VERDE       = new Color(34, 139, 34);
    private final Color ROJO        = new Color(200, 40, 40);
    private final Color NARANJA     = new Color(200, 120, 0);

    private JPanel  panelTarjetas;
    private JLabel  lblMensaje;
    private JLabel  lblEstado;

    public DocumentacionEstudianteForm() {
        initComponents();
        buildUI();
        actualizarVista();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 675);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        panelTarjetas = new JPanel();
        panelTarjetas.setLayout(new BoxLayout(panelTarjetas, BoxLayout.Y_AXIS));
        panelTarjetas.setBackground(FONDO_CLARO);
        lblMensaje    = new JLabel(" ");
        lblEstado     = new JLabel(" ");
    }

    private void buildUI() {
        getContentPane().setBackground(FONDO_CLARO);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new panelMenuEstudiante(), BorderLayout.WEST);

        JPanel pnlMain = new JPanel(new BorderLayout(0, 18));
        pnlMain.setOpaque(false);
        pnlMain.setBorder(new EmptyBorder(28, 28, 20, 28));

        // ── Cabecera ────────────────────────────────────────────────
        JPanel pnlHeader = new JPanel(new BorderLayout(0, 6));
        pnlHeader.setOpaque(false);

        JLabel lblTitulo = new JLabel("MIS DOCUMENTOS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(AZUL_UDI);

        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(new Color(90, 90, 90));

        pnlHeader.add(lblTitulo, BorderLayout.NORTH);
        pnlHeader.add(lblEstado, BorderLayout.SOUTH);
        pnlMain.add(pnlHeader, BorderLayout.NORTH);

        // ── Tarjetas de documentos (scroll) ─────────────────────────
        JScrollPane scroll = new JScrollPane(panelTarjetas);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        pnlMain.add(scroll, BorderLayout.CENTER);

        // ── Footer ──────────────────────────────────────────────────
        lblMensaje.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlMain.add(lblMensaje, BorderLayout.SOUTH);

        getContentPane().add(pnlMain, BorderLayout.CENTER);
    }

    private void actualizarVista() {
        long idEst = SesionActiva.getUsuario().getIdUsuario();
        List<Documento> docs = new DocumentoDAO().listarPorEstudiante(idEst);

        panelTarjetas.removeAll();
        panelTarjetas.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Resumen de estado global
        long aprobados = docs.stream().filter(d -> "APROBADO".equals(d.getEstado())).count();
        long total = docs.size();
        if (total == 0) {
            lblEstado.setText("Sube los documentos requeridos para poder gestionar tus prácticas.");
        } else if (aprobados == TIPOS.length) {
            lblEstado.setText("Todos tus documentos están aprobados. Ya puedes gestionar tus prácticas.");
            lblEstado.setForeground(VERDE);
        } else {
            lblEstado.setText("Documentos aprobados: " + aprobados + " / " + TIPOS.length +
                              " — Espera la revisión del administrador.");
            lblEstado.setForeground(NARANJA);
        }

        // Una tarjeta por cada tipo requerido
        for (String tipo : TIPOS) {
            Documento doc = docs.stream()
                .filter(d -> tipo.equals(d.getTipoDocumento()))
                .findFirst().orElse(null);
            panelTarjetas.add(crearTarjeta(tipo, doc));
            panelTarjetas.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        panelTarjetas.revalidate();
        panelTarjetas.repaint();
    }

    private JPanel crearTarjeta(String tipo, Documento doc) {
        boolean existe  = doc != null;
        String  estado  = existe ? doc.getEstado() : "SIN SUBIR";
        Color   color;
        String  icono;

        switch (estado) {
            case "APROBADO":  color = VERDE;   icono = ""; break;
            case "RECHAZADO": color = ROJO;    icono = ""; break;
            case "PENDIENTE": color = NARANJA; icono = "◷"; break;
            default:          color = new Color(170, 170, 170); icono = "○"; break;
        }

        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(BLANCO);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 228, 240), 1),
            new EmptyBorder(16, 20, 16, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Barra de color lateral
        JPanel barra = new JPanel();
        barra.setBackground(color);
        barra.setPreferredSize(new Dimension(5, 0));
        card.add(barra, BorderLayout.WEST);

        // Info central
        JPanel pnlInfo = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlInfo.setOpaque(false);

        JLabel lblTipo = new JLabel(icono + "  " + tipo);
        lblTipo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTipo.setForeground(AZUL_UDI);

        String sub = existe
            ? new File(doc.getRutaArchivo()).getName() + "  ·  " + doc.getFechaSubida()
            : "No subido aún";
        JLabel lblSub = new JLabel(sub);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(110, 110, 110));

        pnlInfo.add(lblTipo);
        pnlInfo.add(lblSub);
        card.add(pnlInfo, BorderLayout.CENTER);

        // Acciones
        JPanel pnlAcc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlAcc.setOpaque(false);

        // Badge estado
        JLabel badge = new JLabel(estado);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(BLANCO);
        badge.setBackground(color);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(3, 9, 3, 9));
        pnlAcc.add(badge);

        if (existe) {
            JButton btnVer = miniBoton("Ver", AZUL_UDI);
            btnVer.addActionListener(e -> abrirArchivo(doc.getRutaArchivo()));
            pnlAcc.add(btnVer);

            // Solo puede eliminar si está PENDIENTE o RECHAZADO (no aprobado)
            if (!"APROBADO".equals(estado)) {
                JButton btnEliminar = miniBoton("Eliminar", ROJO);
                btnEliminar.addActionListener(e -> eliminarDocumento(doc.getIdDocumento(), tipo));
                pnlAcc.add(btnEliminar);
            }

            // Si está rechazado puede volver a subir
            if ("RECHAZADO".equals(estado)) {
                JButton btnResubir = miniBoton("Resubir", NARANJA);
                btnResubir.addActionListener(e -> subirDocumento(tipo, doc.getIdDocumento()));
                pnlAcc.add(btnResubir);
            }
        } else {
            JButton btnSubir = miniBoton("+ Subir", AZUL_UDI);
            btnSubir.setBackground(AZUL_UDI);
            btnSubir.setForeground(BLANCO);
            btnSubir.addActionListener(e -> subirDocumento(tipo, null));
            pnlAcc.add(btnSubir);
        }

        card.add(pnlAcc, BorderLayout.EAST);
        return card;
    }

    private JButton miniBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(BLANCO);
        btn.setForeground(color);
        btn.setBorder(new LineBorder(color, 1));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(80, 26));
        return btn;
    }

    private void subirDocumento(String tipo, String idDocExistente) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar " + tipo);
        fc.setFileFilter(new FileNameExtensionFilter("Documentos (PDF, PNG, JPG)", "pdf", "png", "jpg", "jpeg"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File fuente = fc.getSelectedFile();
        long idEst  = SesionActiva.getUsuario().getIdUsuario();
        String idDoc = idEst + "_" + tipo.replace(" ", "_");

        File dir     = new File("documentos/");
        if (!dir.exists()) dir.mkdirs();
        File destino = new File("documentos/" + idDoc + "_" + fuente.getName());

        try {
            Files.copy(fuente.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);

            DocumentoDAO dao = new DocumentoDAO();
            if (idDocExistente != null) {
                // Actualizar ruta y resetear a PENDIENTE
                dao.eliminar(idDocExistente);
            }
            Documento d = new Documento(idDoc, idEst, tipo, destino.getAbsolutePath(), new Date(), "PENDIENTE");
            if (dao.crear(d)) {
                mostrarMsg("" + tipo + " subido correctamente. Pendiente de revisión.", VERDE);
                actualizarVista();
            }
        } catch (Exception ex) {
            mostrarMsg("Error al copiar el archivo: " + ex.getMessage(), ROJO);
        }
    }

    private void eliminarDocumento(String idDoc, String tipo) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Eliminar el documento \"" + tipo + "\"?",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (new DocumentoDAO().eliminar(idDoc)) {
                mostrarMsg("Documento eliminado.", NARANJA);
                actualizarVista();
            } else {
                mostrarMsg("Error al eliminar.", ROJO);
            }
        }
    }

    private void abrirArchivo(String ruta) {
        try { Desktop.getDesktop().open(new File(ruta)); }
        catch (Exception e) { mostrarMsg("No se pudo abrir el archivo.", ROJO); }
    }

    private void mostrarMsg(String msg, Color color) {
        lblMensaje.setText(msg);
        lblMensaje.setForeground(color);
    }

    public static void main(String args[]) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        java.awt.EventQueue.invokeLater(() -> new DocumentacionEstudianteForm().setVisible(true));
    }
}
