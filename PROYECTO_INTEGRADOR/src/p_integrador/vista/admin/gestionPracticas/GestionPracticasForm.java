package p_integrador.vista.admin.gestionPracticas;

import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import p_integrador.dao.GrupoDAO;
import p_integrador.modelo.*;
import p_integrador.servicio.*;
import p_integrador.vista.admin.panelMenu;

/**
 * Gestión de Prácticas (rediseñada).
 *
 * Flujo nuevo y más intuitivo:
 *   1. Se elige un grupo.
 *   2. Se crea una práctica para ese grupo. Al crearla, TODOS los estudiantes
 *      del grupo quedan inscritos automáticamente en ella.
 *   3. Se selecciona una práctica de la tabla y con "VER BITÁCORAS" se entra a
 *      gestionar las bitácoras de la práctica (que son de todo el grupo).
 *
 * Ya no es necesario seleccionar un estudiante para ver las bitácoras: las
 * bitácoras pertenecen a la práctica completa (todo el grupo).
 *
 * Paleta: solo azul UDI, blanco, gris neutro y rojo (para eliminar).
 * Se retiraron los botones verde y amarillo de la versión anterior.
 */
public class GestionPracticasForm extends javax.swing.JFrame {

    private static final Color AZUL_UDI    = new Color(0, 51, 102);
    private static final Color FONDO_CLARO = new Color(219, 240, 255);
    private static final Color BLANCO      = Color.WHITE;
    private static final Color GRIS_BORDE  = new Color(210, 220, 235);
    private static final Color GRIS_TXT    = new Color(90, 90, 90);
    private static final Color VERDE_OK    = new Color(0, 110, 60);   // solo para texto de mensaje
    private static final Color ROJO_ERR    = new Color(180, 50, 50);

    private final PracticaService practicaService = new PracticaService();
    private final UsuarioService  usuarioService  = new UsuarioService();

    private JComboBox<String> cmbGrupo;
    private JComboBox<String> cmbNivel;
    private JDateChooser      dateFin;
    private JTable tblPracticas;
    private JTable tblEstudiantes;
    private JLabel lblMensaje;
    private JLabel lblContadorEst;
    private JButton btnCrear, btnEliminar, btnReabrir, btnFinalizar, btnVerBitacoras;

    public GestionPracticasForm() {
        setTitle("Gestión de Prácticas - UDI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 700);
        setLocationRelativeTo(null);
        initComponents();
        buildUI();
        cargarGrupos();
    }

    private void initComponents() {
        cmbGrupo = new JComboBox<>();
        cmbGrupo.setPreferredSize(new Dimension(220, 34));
        cmbGrupo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbNivel = new JComboBox<>(new String[]{"I", "II", "III"});
        cmbNivel.setPreferredSize(new Dimension(220, 34));
        cmbNivel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateFin = new JDateChooser();
        dateFin.setPreferredSize(new Dimension(220, 32));
        dateFin.setToolTipText("Opcional. Al pasar esta fecha, la práctica se marca como finalizada automáticamente.");

        tblPracticas   = new JTable();
        tblEstudiantes = new JTable();
        lblMensaje     = new JLabel(" ");
        lblContadorEst = new JLabel(" ");

        btnCrear        = new JButton("CREAR PRÁCTICA");
        btnEliminar     = new JButton("Eliminar");
        btnReabrir      = new JButton("Reabrir");
        btnFinalizar    = new JButton("Finalizar práctica");
        btnVerBitacoras = new JButton("VER BITÁCORAS");

        cmbGrupo.addActionListener(e -> onGrupoChanged());
        btnCrear.addActionListener(e -> crearPractica());
        btnEliminar.addActionListener(e -> eliminarPractica());
        btnReabrir.addActionListener(e -> cambiarEstadoPractica("ACTIVA"));
        btnFinalizar.addActionListener(e -> cambiarEstadoPractica("CERRADA"));
        btnVerBitacoras.addActionListener(e -> verBitacoras());
    }

    private void buildUI() {
        getContentPane().setBackground(FONDO_CLARO);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new panelMenu(), BorderLayout.WEST);

        JPanel pnlDerecho = new JPanel(new BorderLayout(0, 18));
        pnlDerecho.setOpaque(false);
        pnlDerecho.setBorder(new EmptyBorder(26, 28, 26, 28));

        // Cabecera
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        JLabel lblTitulo = new JLabel("GESTIÓN DE PRÁCTICAS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lblTitulo.setForeground(AZUL_UDI);
        JLabel lblSub = new JLabel("Crea prácticas por grupo, gestiona su estado y entra a sus bitácoras.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(GRIS_TXT);
        pnlHeader.add(lblTitulo, BorderLayout.NORTH);
        pnlHeader.add(lblSub, BorderLayout.SOUTH);
        pnlDerecho.add(pnlHeader, BorderLayout.NORTH);

        // Cuerpo: izquierda (controles) | centro (prácticas) | derecha (estudiantes)
        JPanel pnlCuerpo = new JPanel(new BorderLayout(18, 0));
        pnlCuerpo.setOpaque(false);
        pnlCuerpo.add(buildPanelControles(), BorderLayout.WEST);
        pnlCuerpo.add(buildPanelPracticas(), BorderLayout.CENTER);
        pnlCuerpo.add(buildPanelEstudiantes(), BorderLayout.EAST);
        pnlDerecho.add(pnlCuerpo, BorderLayout.CENTER);

        // Mensaje inferior
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlDerecho.add(lblMensaje, BorderLayout.SOUTH);

        getContentPane().add(pnlDerecho, BorderLayout.CENTER);
    }

    /** Tarjeta izquierda: seleccionar grupo + crear práctica. */
    private JPanel buildPanelControles() {
        JPanel card = tarjeta();
        card.setPreferredSize(new Dimension(300, 0));
        card.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.weightx = 1.0;

        g.gridy = 0; g.insets = new Insets(18, 22, 4, 22);
        card.add(tituloSeccion("1.  GRUPO"), g);
        g.gridy = 1; g.insets = new Insets(2, 22, 6, 22);
        card.add(etiqueta("Selecciona el grupo:"), g);
        g.gridy = 2; g.insets = new Insets(0, 22, 4, 22);
        card.add(cmbGrupo, g);
        g.gridy = 3; g.insets = new Insets(2, 22, 10, 22);
        card.add(lblContadorEst, g);

        g.gridy = 4; g.insets = new Insets(6, 22, 8, 22);
        card.add(separador(), g);

        g.gridy = 5; g.insets = new Insets(8, 22, 4, 22);
        card.add(tituloSeccion("2.  NUEVA PRÁCTICA"), g);
        g.gridy = 6; g.insets = new Insets(2, 22, 4, 22);
        card.add(etiqueta("Nivel:"), g);
        g.gridy = 7; g.insets = new Insets(0, 22, 4, 22);
        card.add(cmbNivel, g);
        g.gridy = 8; g.insets = new Insets(0, 22, 10, 22);
        JLabel horasInfo = new JLabel("<html><span style='color:#5a5a5a;'>Las horas se asignan según el nivel: "
                + "I = 20 h, II = 30 h, III = 40 h.</span></html>");
        horasInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        card.add(horasInfo, g);

        g.gridy = 9; g.insets = new Insets(2, 22, 4, 22);
        card.add(etiqueta("Fecha de fin (opcional):"), g);
        g.gridy = 10; g.insets = new Insets(0, 22, 10, 22);
        card.add(dateFin, g);

        g.gridy = 11; g.insets = new Insets(4, 22, 6, 22);
        estiloPrimario(btnCrear);
        card.add(btnCrear, g);

        JLabel hint = new JLabel("<html><span style='color:#5a5a5a;'>Al crear la práctica, todos los "
                + "estudiantes del grupo quedan inscritos automáticamente.</span></html>");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        g.gridy = 12; g.insets = new Insets(0, 22, 12, 22);
        card.add(hint, g);

        g.gridy = 13; g.weighty = 1.0;
        card.add(Box.createGlue(), g);
        return card;
    }

    /** Centro: tabla de prácticas + barra de acciones. */
    private JPanel buildPanelPracticas() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setOpaque(false);

        JLabel lbl = tituloSeccion("PRÁCTICAS DEL GRUPO");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnl.add(lbl, BorderLayout.NORTH);

        estilizarTabla(tblPracticas);
        JScrollPane sp = new JScrollPane(tblPracticas);
        sp.setBorder(new LineBorder(GRIS_BORDE));
        pnl.add(sp, BorderLayout.CENTER);

        // Barra de acciones
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        acciones.setOpaque(false);
        estiloPrimario(btnVerBitacoras);
        btnVerBitacoras.setPreferredSize(new Dimension(170, 38));
        estiloSecundario(btnFinalizar);
        estiloSecundario(btnReabrir);
        estiloPeligro(btnEliminar);
        acciones.add(btnVerBitacoras);
        acciones.add(btnFinalizar);
        acciones.add(btnReabrir);
        acciones.add(btnEliminar);
        pnl.add(acciones, BorderLayout.SOUTH);

        return pnl;
    }

    /** Derecha: roster de estudiantes del grupo (solo informativo). */
    private JPanel buildPanelEstudiantes() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setOpaque(false);
        pnl.setPreferredSize(new Dimension(300, 0));

        JLabel lbl = tituloSeccion("ESTUDIANTES DEL GRUPO");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnl.add(lbl, BorderLayout.NORTH);

        estilizarTabla(tblEstudiantes);
        JScrollPane sp = new JScrollPane(tblEstudiantes);
        sp.setBorder(new LineBorder(GRIS_BORDE));
        pnl.add(sp, BorderLayout.CENTER);
        return pnl;
    }

    // ── Lógica de negocio ────────────────────────────────────────────

    private void cargarGrupos() {
        cmbGrupo.removeAllItems();
        new GrupoDAO().listarTodos().stream()
            .filter(g -> !"NINGUNO".equals(g.getCodigo()))
            .forEach(g -> cmbGrupo.addItem(g.getCodigo()));
        onGrupoChanged();
    }

    private void onGrupoChanged() {
        if (cmbGrupo.getSelectedItem() == null) {
            lblContadorEst.setText(" ");
            return;
        }
        String grupo = cmbGrupo.getSelectedItem().toString();
        actualizarTablaPracticas(grupo);
        int n = actualizarTablaEstudiantes(grupo);
        lblContadorEst.setText("<html><b style='color:#003366;'>" + n
                + "</b> estudiante(s) en este grupo</html>");
        limpiarMensaje();
    }

    private void actualizarTablaPracticas(String grupo) {
        List<Practica> lista = practicaService.listarPorGrupo(grupo);
        int totalEst = usuarioService.listarPorGrupo(grupo).size();

        String[] cols = {"Práctica", "Nivel", "Horas", "Estudiantes", "Fecha fin", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Practica p : lista) {
            String fFin = p.getFechaFin() != null ? p.getFechaFin().toString() : "Sin fecha";
            modelo.addRow(new Object[]{
                p.getIdPractica(), "Nivel " + p.getNivel(),
                p.getHorasRequeridas() + " h", totalEst, fFin, p.getEstado()
            });
        }
        tblPracticas.setModel(modelo);
        tblPracticas.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                String estado = t.getValueAt(row, 5) != null ? t.getValueAt(row, 5).toString() : "";
                if (!sel) setBackground("CERRADA".equals(estado) ? new Color(242, 242, 242) : BLANCO);
                setForeground("CERRADA".equals(estado) ? Color.GRAY : AZUL_UDI);
                return this;
            }
        });
        if (tblPracticas.getColumnModel().getColumnCount() > 0) {
            tblPracticas.getColumnModel().getColumn(0).setPreferredWidth(150);
            tblPracticas.getColumnModel().getColumn(1).setPreferredWidth(60);
            tblPracticas.getColumnModel().getColumn(2).setPreferredWidth(50);
            tblPracticas.getColumnModel().getColumn(3).setPreferredWidth(70);
            tblPracticas.getColumnModel().getColumn(4).setPreferredWidth(90);
            tblPracticas.getColumnModel().getColumn(5).setPreferredWidth(70);
        }
    }

    private int actualizarTablaEstudiantes(String grupo) {
        List<Usuario> lista = usuarioService.listarPorGrupo(grupo);
        String[] cols = {"Cédula", "Nombre", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Usuario u : lista) {
            modelo.addRow(new Object[]{
                u.getIdUsuario(), u.getNombre1() + " " + u.getApellido1(), u.getEstado()
            });
        }
        tblEstudiantes.setModel(modelo);
        return lista.size();
    }

    private void crearPractica() {
        if (cmbGrupo.getSelectedItem() == null) { msgError("Selecciona un grupo."); return; }
        String grupo = cmbGrupo.getSelectedItem().toString();
        String nivel = cmbNivel.getSelectedItem().toString();

        String error = practicaService.crearPractica(grupo, nivel, dateFin.getDate());
        if (error != null) { msgError(error); return; }

        int inscritos = usuarioService.listarPorGrupo(grupo).size();
        dateFin.setDate(null);
        actualizarTablaPracticas(grupo);
        msgOk("Práctica Nivel " + nivel + " creada para " + grupo + ". "
                + inscritos + " estudiante(s) inscrito(s) automáticamente.");
    }

    private void eliminarPractica() {
        String idPrac = practicaSeleccionada();
        if (idPrac == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Eliminar la práctica " + idPrac + " y todas sus bitácoras y visitas?\n"
            + "Esta acción no se puede deshacer.",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            String error = practicaService.eliminarPractica(idPrac);
            if (error == null) {
                actualizarTablaPracticas(cmbGrupo.getSelectedItem().toString());
                msgOk("Práctica eliminada.");
            } else msgError(error);
        }
    }

    private void cambiarEstadoPractica(String estado) {
        String idPrac = practicaSeleccionada();
        if (idPrac == null) return;
        String error = practicaService.actualizarEstado(idPrac, estado);
        if (error == null) {
            actualizarTablaPracticas(cmbGrupo.getSelectedItem().toString());
            msgOk("Práctica " + ("ACTIVA".equals(estado) ? "reabierta" : "finalizada") + ".");
        } else msgError(error);
    }

    private void verBitacoras() {
        String idPrac = practicaSeleccionada();
        if (idPrac == null) return;
        new BitacorasForm(idPrac).setVisible(true);
        this.dispose();
    }

    private String practicaSeleccionada() {
        int fila = tblPracticas.getSelectedRow();
        if (fila == -1) { msgError("Selecciona una práctica en la tabla."); return null; }
        return tblPracticas.getValueAt(fila, 0).toString();
    }

    // ── Utilidades de estilo ─────────────────────────────────────────

    private void msgOk(String m)    { lblMensaje.setText(m); lblMensaje.setForeground(VERDE_OK); }
    private void msgError(String m) { lblMensaje.setText(m); lblMensaje.setForeground(ROJO_ERR); }
    private void limpiarMensaje()   { lblMensaje.setText(" "); }

    private JPanel tarjeta() {
        JPanel card = new JPanel();
        card.setBackground(BLANCO);
        card.setBorder(new LineBorder(GRIS_BORDE, 1));
        return card;
    }

    private JLabel tituloSeccion(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(AZUL_UDI);
        return l;
    }

    private JLabel etiqueta(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(new Color(60, 60, 60));
        return l;
    }

    private JSeparator separador() {
        JSeparator s = new JSeparator();
        s.setForeground(GRIS_BORDE);
        return s;
    }

    private void estilizarTabla(JTable t) {
        t.setRowHeight(28);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.getTableHeader().setReorderingAllowed(false);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setSelectionBackground(new Color(210, 228, 248));
        t.setSelectionForeground(AZUL_UDI);
    }

    private void base(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(130, 36));
    }

    private void estiloPrimario(JButton b) {
        base(b);
        b.setBackground(AZUL_UDI);
        b.setForeground(BLANCO);
        b.setBorder(new EmptyBorder(6, 10, 6, 10));
    }

    private void estiloSecundario(JButton b) {
        base(b);
        b.setBackground(BLANCO);
        b.setForeground(AZUL_UDI);
        b.setBorder(new LineBorder(AZUL_UDI, 1));
    }

    private void estiloPeligro(JButton b) {
        base(b);
        b.setBackground(BLANCO);
        b.setForeground(ROJO_ERR);
        b.setBorder(new LineBorder(ROJO_ERR, 1));
    }

    public static void main(String args[]) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        java.awt.EventQueue.invokeLater(() -> new GestionPracticasForm().setVisible(true));
    }
}
