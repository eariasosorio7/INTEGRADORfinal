package p_integrador.vista.admin;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import p_integrador.dao.DocumentoDAO;
import p_integrador.dao.UsuarioDAO;
import p_integrador.modelo.Documento;
import p_integrador.modelo.Usuario;

/**
 * Vista admin de documentación: lista de estudiantes a la izquierda,
 * documentos del estudiante seleccionado a la derecha con estado visual.
 */
public class DocumentacionAdminForm extends javax.swing.JFrame {

    private final Color AZUL_UDI     = new Color(0, 51, 102);
    private final Color FONDO_CLARO  = new Color(219, 240, 255);
    private final Color BLANCO       = Color.WHITE;
    private final Color VERDE        = new Color(34, 139, 34);
    private final Color ROJO         = new Color(200, 40, 40);
    private final Color NARANJA      = new Color(200, 120, 0);

    private JList<String>      listaEstudiantes;
    private DefaultListModel<String> modeloLista;
    private JPanel             panelDocumentos;
    private JScrollPane        scrollDocs;
    private JLabel             lblNombreEstudiante;
    private JLabel             lblMensaje;
    private JComboBox<String>  cmbGrupoFiltro;
    private JComboBox<String>  cmbEstadoFiltro;
    private JTextField         txtBuscar;

    private java.util.List<Usuario> todosEstudiantes = new java.util.ArrayList<>();
    private java.util.List<Usuario> estudiantes = new java.util.ArrayList<>();

    public DocumentacionAdminForm() {
        initComponents();
        buildUI();
        cargarEstudiantes();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 675);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        modeloLista        = new DefaultListModel<>();
        listaEstudiantes   = new JList<>(modeloLista);
        panelDocumentos    = new JPanel();
        panelDocumentos.setLayout(new BoxLayout(panelDocumentos, BoxLayout.Y_AXIS));
        panelDocumentos.setBackground(FONDO_CLARO);
        scrollDocs         = new JScrollPane(panelDocumentos);
        lblNombreEstudiante= new JLabel("Selecciona un estudiante");
        lblMensaje         = new JLabel(" ");
        cmbGrupoFiltro     = new JComboBox<>();
        cmbEstadoFiltro    = new JComboBox<>(new String[]{
            "Todos los estados", "Completo", "Pendiente", "Con rechazo", "Sin docs"
        });
        txtBuscar          = new JTextField();

        listaEstudiantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaEstudiantes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarDocumentosEstudiante();
        });

        cmbGrupoFiltro.addActionListener(e -> aplicarFiltros());
        cmbEstadoFiltro.addActionListener(e -> aplicarFiltros());
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
        });
    }

    private void buildUI() {
        getContentPane().setBackground(FONDO_CLARO);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new panelMenu(), BorderLayout.WEST);

        JPanel pnlMain = new JPanel(new BorderLayout(20, 15));
        pnlMain.setOpaque(false);
        pnlMain.setBorder(new EmptyBorder(28, 28, 20, 28));

        // Cabecera
        JLabel lblTitulo = new JLabel("CONTROL DE DOCUMENTACIÓN");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(AZUL_UDI);
        pnlMain.add(lblTitulo, BorderLayout.NORTH);

        // Centro: split
        JPanel pnlCentro = new JPanel(new BorderLayout(15, 0));
        pnlCentro.setOpaque(false);

        // ── Panel izquierdo: lista de estudiantes ──────────────────
        JPanel pnlIzq = new JPanel(new BorderLayout(0, 8));
        pnlIzq.setOpaque(false);
        pnlIzq.setPreferredSize(new Dimension(290, 0));

        JLabel lblLista = new JLabel("ESTUDIANTES");
        lblLista.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLista.setForeground(AZUL_UDI);

        // Panel de filtros
        JPanel pnlFiltros = new JPanel();
        pnlFiltros.setLayout(new BoxLayout(pnlFiltros, BoxLayout.Y_AXIS));
        pnlFiltros.setBackground(BLANCO);
        pnlFiltros.setBorder(new CompoundBorder(
            new LineBorder(new Color(210, 220, 235)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblBuscar = new JLabel("Buscar por nombre o cédula:");
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblBuscar.setForeground(new Color(80, 80, 80));
        lblBuscar.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtBuscar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        txtBuscar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblGrupo = new JLabel("Grupo:");
        lblGrupo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblGrupo.setForeground(new Color(80, 80, 80));
        lblGrupo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbGrupoFiltro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        cmbGrupoFiltro.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblEstado = new JLabel("Estado de documentación:");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEstado.setForeground(new Color(80, 80, 80));
        lblEstado.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbEstadoFiltro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        cmbEstadoFiltro.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlFiltros.add(lblBuscar);
        pnlFiltros.add(Box.createRigidArea(new Dimension(0, 3)));
        pnlFiltros.add(txtBuscar);
        pnlFiltros.add(Box.createRigidArea(new Dimension(0, 8)));
        pnlFiltros.add(lblGrupo);
        pnlFiltros.add(Box.createRigidArea(new Dimension(0, 3)));
        pnlFiltros.add(cmbGrupoFiltro);
        pnlFiltros.add(Box.createRigidArea(new Dimension(0, 8)));
        pnlFiltros.add(lblEstado);
        pnlFiltros.add(Box.createRigidArea(new Dimension(0, 3)));
        pnlFiltros.add(cmbEstadoFiltro);

        JPanel pnlTop = new JPanel(new BorderLayout(0, 8));
        pnlTop.setOpaque(false);
        pnlTop.add(lblLista, BorderLayout.NORTH);
        pnlTop.add(pnlFiltros, BorderLayout.CENTER);

        listaEstudiantes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listaEstudiantes.setFixedCellHeight(36);
        listaEstudiantes.setBorder(new EmptyBorder(4, 8, 4, 8));
        listaEstudiantes.setBackground(BLANCO);
        listaEstudiantes.setCellRenderer(new EstudianteCellRenderer());

        JScrollPane scrollLista = new JScrollPane(listaEstudiantes);
        scrollLista.setBorder(new LineBorder(new Color(210, 220, 235)));

        pnlIzq.add(pnlTop, BorderLayout.NORTH);
        pnlIzq.add(scrollLista, BorderLayout.CENTER);
        pnlCentro.add(pnlIzq, BorderLayout.WEST);

        // ── Panel derecho: documentos del estudiante ───────────────
        JPanel pnlDer = new JPanel(new BorderLayout(0, 10));
        pnlDer.setOpaque(false);

        lblNombreEstudiante.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNombreEstudiante.setForeground(AZUL_UDI);

        scrollDocs.setBorder(new LineBorder(new Color(210, 220, 235)));
        scrollDocs.getVerticalScrollBar().setUnitIncrement(16);

        pnlDer.add(lblNombreEstudiante, BorderLayout.NORTH);
        pnlDer.add(scrollDocs, BorderLayout.CENTER);
        pnlCentro.add(pnlDer, BorderLayout.CENTER);

        pnlMain.add(pnlCentro, BorderLayout.CENTER);

        lblMensaje.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        pnlMain.add(lblMensaje, BorderLayout.SOUTH);

        getContentPane().add(pnlMain, BorderLayout.CENTER);
    }

    private void cargarEstudiantes() {
        todosEstudiantes = new UsuarioDAO().listarTodos();
        todosEstudiantes.removeIf(u -> !"ESTUDIANTE".equals(u.getRol()));

        // Poblar combo de grupos (sin disparar filtro a medias)
        java.util.Set<String> grupos = new java.util.TreeSet<>();
        for (Usuario u : todosEstudiantes)
            if (u.getGrupo() != null && !u.getGrupo().isEmpty()) grupos.add(u.getGrupo());
        cmbGrupoFiltro.removeAllItems();
        cmbGrupoFiltro.addItem("Todos los grupos");
        for (String g : grupos) cmbGrupoFiltro.addItem(g);

        aplicarFiltros();
    }

    private void aplicarFiltros() {
        if (todosEstudiantes == null) return;
        String grupoSel  = cmbGrupoFiltro.getSelectedItem()  != null ? cmbGrupoFiltro.getSelectedItem().toString()  : "Todos los grupos";
        String estadoSel = cmbEstadoFiltro.getSelectedItem() != null ? cmbEstadoFiltro.getSelectedItem().toString() : "Todos los estados";
        String texto     = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";

        DocumentoDAO dDAO = new DocumentoDAO();
        modeloLista.clear();
        estudiantes.clear();

        for (Usuario u : todosEstudiantes) {
            // Filtro por grupo
            if (!"Todos los grupos".equals(grupoSel) && !grupoSel.equals(u.getGrupo())) continue;

            // Filtro por texto (nombre completo o cédula)
            String nombreCompleto = (u.getNombre1() + " " + u.getApellido1()).toLowerCase();
            if (!texto.isEmpty()
                && !nombreCompleto.contains(texto)
                && !String.valueOf(u.getIdUsuario()).contains(texto)) continue;

            List<Documento> docs = dDAO.listarPorEstudiante(u.getIdUsuario());
            String estadoGlobal = calcularEstadoGlobal(docs);

            // Filtro por estado de documentación
            if (!"Todos los estados".equals(estadoSel)) {
                if ("Completo".equals(estadoSel)    && !"Completo".equals(estadoGlobal)) continue;
                if ("Pendiente".equals(estadoSel)   && !"Pendiente".equals(estadoGlobal)) continue;
                if ("Con rechazo".equals(estadoSel) && !"Con rechazo".equals(estadoGlobal)) continue;
                if ("Sin docs".equals(estadoSel)    && !"Sin docs".equals(estadoGlobal)) continue;
            }

            estudiantes.add(u);
            modeloLista.addElement(u.getNombre1() + " " + u.getApellido1() + "  [" + estadoGlobal + "]");
        }

        lblMensaje.setText(estudiantes.size() + " estudiante(s) en el filtro actual.");
        lblMensaje.setForeground(new Color(80, 80, 80));

        // Limpiar panel de documentos al refiltrar
        lblNombreEstudiante.setText("Selecciona un estudiante");
        panelDocumentos.removeAll();
        panelDocumentos.revalidate();
        panelDocumentos.repaint();
    }

    private String calcularEstadoGlobal(List<Documento> docs) {
        if (docs.isEmpty()) return "Sin docs";
        long aprobados = docs.stream().filter(d -> "APROBADO".equals(d.getEstado())).count();
        long rechazados = docs.stream().filter(d -> "RECHAZADO".equals(d.getEstado())).count();
        long pendientes = docs.stream().filter(d -> "PENDIENTE".equals(d.getEstado())).count();
        if (rechazados > 0) return "Con rechazo";
        if (pendientes > 0) return "Pendiente";
        if (aprobados == docs.size()) return "Completo";
        return "Parcial";
    }

    private void mostrarDocumentosEstudiante() {
        int idx = listaEstudiantes.getSelectedIndex();
        if (idx < 0 || idx >= estudiantes.size()) return;

        Usuario u = estudiantes.get(idx);
        lblNombreEstudiante.setText("Documentos de: " + u.getNombre1() + " " + u.getNombre2() + " " + u.getApellido1());

        List<Documento> docs = new DocumentoDAO().listarPorEstudiante(u.getIdUsuario());
        panelDocumentos.removeAll();
        panelDocumentos.setBorder(new EmptyBorder(15, 15, 15, 15));

        if (docs.isEmpty()) {
            JLabel lbl = new JLabel("Este estudiante no tiene documentos registrados.");
            lbl.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            lbl.setForeground(new Color(120, 120, 120));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelDocumentos.add(lbl);
        } else {
            for (Documento d : docs) {
                panelDocumentos.add(crearTarjetaDocumento(d));
                panelDocumentos.add(Box.createRigidArea(new Dimension(0, 12)));
            }
        }

        panelDocumentos.revalidate();
        panelDocumentos.repaint();
    }

    private JPanel crearTarjetaDocumento(Documento d) {
        Color colorEstado;
        String iconoEstado;
        switch (d.getEstado()) {
            case "APROBADO":  colorEstado = VERDE;   iconoEstado = ""; break;
            case "RECHAZADO": colorEstado = ROJO;    iconoEstado = ""; break;
            default:          colorEstado = NARANJA; iconoEstado = "●"; break;
        }

        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(BLANCO);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 228, 240), 1),
            new EmptyBorder(14, 18, 14, 18)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Indicador de color lateral
        JPanel barraColor = new JPanel();
        barraColor.setBackground(colorEstado);
        barraColor.setPreferredSize(new Dimension(5, 0));
        card.add(barraColor, BorderLayout.WEST);

        // Información del documento
        JPanel pnlInfo = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlInfo.setOpaque(false);

        JLabel lblTipo = new JLabel(iconoEstado + "  " + d.getTipoDocumento());
        lblTipo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTipo.setForeground(AZUL_UDI);

        String nombreArchivo = new java.io.File(d.getRutaArchivo()).getName();
        JLabel lblArchivo = new JLabel(nombreArchivo + "  ·  " + d.getFechaSubida());
        lblArchivo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblArchivo.setForeground(new Color(100, 100, 100));

        pnlInfo.add(lblTipo);
        pnlInfo.add(lblArchivo);
        card.add(pnlInfo, BorderLayout.CENTER);

        // Badge de estado + botones
        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlAcciones.setOpaque(false);

        JLabel badge = new JLabel(d.getEstado());
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(BLANCO);
        badge.setBackground(colorEstado);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(3, 10, 3, 10));

        JButton btnAprobar = botonAccion("Aprobar", VERDE);
        JButton btnRechazar = botonAccion("Rechazar", ROJO);
        JButton btnVer = botonAccion("Ver", AZUL_UDI);

        btnAprobar.addActionListener(e -> accionEstado(d.getIdDocumento(), "APROBADO"));
        btnRechazar.addActionListener(e -> accionEstado(d.getIdDocumento(), "RECHAZADO"));
        btnVer.addActionListener(e -> abrirArchivo(d.getRutaArchivo()));

        pnlAcciones.add(badge);
        pnlAcciones.add(btnVer);
        pnlAcciones.add(btnAprobar);
        pnlAcciones.add(btnRechazar);
        card.add(pnlAcciones, BorderLayout.EAST);

        return card;
    }

    private JButton botonAccion(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(BLANCO);
        btn.setForeground(color);
        btn.setBorder(new LineBorder(color, 1));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(80, 28));
        return btn;
    }

    private void accionEstado(String idDoc, String estado) {
        if (new DocumentoDAO().actualizarEstado(idDoc, estado)) {
            // Recordar el estudiante seleccionado para volver a él tras refiltrar
            int idxSel = listaEstudiantes.getSelectedIndex();
            long idSel = (idxSel >= 0 && idxSel < estudiantes.size())
                ? estudiantes.get(idxSel).getIdUsuario() : -1;

            aplicarFiltros();

            if (idSel != -1) {
                for (int i = 0; i < estudiantes.size(); i++) {
                    if (estudiantes.get(i).getIdUsuario() == idSel) {
                        listaEstudiantes.setSelectedIndex(i);
                        mostrarDocumentosEstudiante();
                        break;
                    }
                }
            }
            lblMensaje.setText("Documento " + estado.toLowerCase() + " correctamente.");
            lblMensaje.setForeground(VERDE);
        } else {
            lblMensaje.setText("Error al actualizar estado.");
            lblMensaje.setForeground(ROJO);
        }
    }

    private void abrirArchivo(String ruta) {
        try {
            Desktop.getDesktop().open(new java.io.File(ruta));
        } catch (Exception e) {
            lblMensaje.setText("No se pudo abrir el archivo.");
            lblMensaje.setForeground(ROJO);
        }
    }

    /** Renderer de la lista con color según estado */
    private class EstudianteCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                       boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String txt = value.toString();
            if (!isSelected) {
                if (txt.contains("[Completo]"))     setForeground(VERDE);
                else if (txt.contains("[Con rechazo]")) setForeground(ROJO);
                else if (txt.contains("[Pendiente]"))   setForeground(NARANJA);
                else setForeground(new Color(80, 80, 80));
            }
            setBorder(new EmptyBorder(4, 8, 4, 8));
            return this;
        }
    }

    public static void main(String args[]) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        java.awt.EventQueue.invokeLater(() -> new DocumentacionAdminForm().setVisible(true));
    }
}
