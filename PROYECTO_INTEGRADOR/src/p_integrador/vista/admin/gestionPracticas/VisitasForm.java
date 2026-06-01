package p_integrador.vista.admin.gestionPracticas;

import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import p_integrador.dao.VisitaDAO;
import p_integrador.modelo.Visita;
import p_integrador.vista.admin.panelMenu;

/**
 * Visitas de una bitácora (vista de administración / docente).
 *
 *  - Muestra el objetivo general de la bitácora.
 *  - Lista las visitas base (plantilla del grupo) y permite crear, mover fecha
 *    y eliminar visitas de forma clara.
 *  - Permite elegir un estudiante para revisar SU ubicación de la visita, sus
 *    respuestas y la retroalimentación, ya que las bitácoras son de todo el grupo.
 *
 * Sin colores verde/amarillo y sin emojis.
 */
public class VisitasForm extends javax.swing.JFrame {

    private final String idBitacora;
    private String idPractica = "";
    private String objetivo = "";
    private List<Visita> visitas;
    private List<p_integrador.modelo.Usuario> estudiantes = new java.util.ArrayList<>();

    private final Color AZUL_UDI    = new Color(0, 51, 102);
    private final Color FONDO_CLARO = new Color(219, 240, 255);
    private final Color BLANCO      = Color.WHITE;
    private final Color GRIS_BORDE  = new Color(210, 220, 235);
    private final Color ROJO        = new Color(180, 50, 50);
    private final Color VERDE_OK    = new Color(0, 110, 60);

    private JTable          tblVisitas;
    private JComboBox<String> cmbEstudiante;
    private JPanel          panelDetalle;
    private JScrollPane     scrollDetalle;
    private JLabel          lblBitacora, lblObjetivo, lblMensaje;
    private JButton         btnCrearVisita, btnModificarFecha, btnEliminarVisita, btnVolver;
    private JDateChooser    dateFecha;

    public VisitasForm(String idBitacora) {
        this.idBitacora = idBitacora;
        cargarContexto();
        initComponents();
        buildUI();
        cargarVisitas();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Visitas - Bitácora " + idBitacora);
        setSize(1240, 700);
        setLocationRelativeTo(null);
    }

    /** Compatibilidad: el segundo parámetro (estudiante) se ignora. */
    public VisitasForm(String idBitacora, long idEstudiante) {
        this(idBitacora);
    }

    private void cargarContexto() {
        p_integrador.modelo.Bitacora b = new p_integrador.dao.BitacoraDAO().buscarPorId(idBitacora);
        if (b != null) {
            idPractica = b.getIdPractica();
            objetivo = b.getObjetivo() != null ? b.getObjetivo() : "(sin objetivo registrado)";
            p_integrador.modelo.Practica p = new p_integrador.dao.PracticaDAO().buscarPorId(idPractica);
            if (p != null)
                estudiantes = new p_integrador.dao.UsuarioDAO().listarPorGrupo(p.getCodigoGrupo());
        }
    }

    private void initComponents() {
        lblBitacora  = new JLabel("VISITAS - BITÁCORA: " + idBitacora);
        lblObjetivo  = new JLabel();
        lblMensaje   = new JLabel(" ");
        tblVisitas   = new JTable();
        panelDetalle = new JPanel();
        panelDetalle.setLayout(new BoxLayout(panelDetalle, BoxLayout.Y_AXIS));
        panelDetalle.setBackground(BLANCO);
        scrollDetalle = new JScrollPane(panelDetalle);

        cmbEstudiante = new JComboBox<>();
        cmbEstudiante.addItem("— Plantilla (sin estudiante) —");
        for (p_integrador.modelo.Usuario u : estudiantes)
            cmbEstudiante.addItem(u.getIdUsuario() + " - " + u.getNombre1() + " " + u.getApellido1());
        cmbEstudiante.addActionListener(e -> mostrarDetalleSeleccionada());

        dateFecha = new JDateChooser();
        dateFecha.setPreferredSize(new Dimension(150, 30));

        btnCrearVisita    = new JButton("+ Nueva visita");
        btnModificarFecha = new JButton("Mover fecha límite");
        btnEliminarVisita = new JButton("Eliminar visita");
        btnVolver         = new JButton("Volver a bitácoras");

        tblVisitas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarDetalleSeleccionada();
        });
        btnCrearVisita.addActionListener(e -> crearVisita());
        btnModificarFecha.addActionListener(e -> modificarFecha());
        btnEliminarVisita.addActionListener(e -> eliminarVisita());
        btnVolver.addActionListener(e -> {
            new BitacorasForm(idPractica).setVisible(true);
            this.dispose();
        });
    }

    private void buildUI() {
        getContentPane().setBackground(FONDO_CLARO);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new panelMenu(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(24, 26, 22, 26));

        // Cabecera + objetivo
        JPanel header = new JPanel(new BorderLayout(0, 6));
        header.setOpaque(false);
        lblBitacora.setFont(new Font("Segoe UI", Font.BOLD, 21));
        lblBitacora.setForeground(AZUL_UDI);

        lblObjetivo.setText("<html><b>Objetivo general:</b> " + objetivo + "</html>");
        lblObjetivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblObjetivo.setForeground(new Color(60, 60, 60));
        lblObjetivo.setBorder(new CompoundBorder(new LineBorder(GRIS_BORDE), new EmptyBorder(8, 10, 8, 10)));
        lblObjetivo.setOpaque(true);
        lblObjetivo.setBackground(BLANCO);

        JPanel headTop = new JPanel(new BorderLayout());
        headTop.setOpaque(false);
        headTop.add(lblBitacora, BorderLayout.WEST);
        estiloSecundario(btnVolver);
        btnVolver.setPreferredSize(new Dimension(190, 32));
        JPanel hr = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        hr.setOpaque(false); hr.add(btnVolver);
        headTop.add(hr, BorderLayout.EAST);

        header.add(headTop, BorderLayout.NORTH);
        header.add(lblObjetivo, BorderLayout.SOUTH);
        main.add(header, BorderLayout.NORTH);

        // Centro: visitas (izq) + detalle (der)
        JPanel centro = new JPanel(new GridLayout(1, 2, 15, 0));
        centro.setOpaque(false);

        // Izquierda: lista + creación
        JPanel izq = new JPanel(new BorderLayout(0, 8));
        izq.setOpaque(false);
        JLabel lblTbl = etiquetaBold("LISTADO DE VISITAS");
        tblVisitas.setRowHeight(28);
        tblVisitas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblVisitas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblVisitas.getTableHeader().setReorderingAllowed(false);
        tblVisitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane spT = new JScrollPane(tblVisitas);
        spT.setBorder(new LineBorder(GRIS_BORDE));
        izq.add(lblTbl, BorderLayout.NORTH);
        izq.add(spT, BorderLayout.CENTER);

        // Caja de creación de visita, clara y guiada
        JPanel crear = new JPanel(new GridBagLayout());
        crear.setBackground(BLANCO);
        TitledBorder tbCrear = BorderFactory.createTitledBorder(new LineBorder(GRIS_BORDE), "Programar o reprogramar una visita");
        tbCrear.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        tbCrear.setTitleColor(AZUL_UDI);
        crear.setBorder(new CompoundBorder(tbCrear, new EmptyBorder(8, 10, 10, 10)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblFecha = new JLabel("Fecha límite:");
        lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFecha.setForeground(AZUL_UDI);
        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        crear.add(lblFecha, g);

        dateFecha.setPreferredSize(new Dimension(180, 32));
        g.gridx = 1; g.gridy = 0; g.weightx = 1.0; g.gridwidth = 2;
        crear.add(dateFecha, g);
        g.gridwidth = 1;

        estiloPrimario(btnCrearVisita);
        btnCrearVisita.setPreferredSize(new Dimension(160, 36));
        g.gridx = 0; g.gridy = 1; g.weightx = 1.0;
        crear.add(btnCrearVisita, g);

        estiloSecundario(btnModificarFecha);
        btnModificarFecha.setPreferredSize(new Dimension(160, 36));
        g.gridx = 1; g.gridy = 1;
        crear.add(btnModificarFecha, g);

        estiloPeligro(btnEliminarVisita);
        btnEliminarVisita.setPreferredSize(new Dimension(160, 36));
        g.gridx = 2; g.gridy = 1;
        crear.add(btnEliminarVisita, g);

        JLabel ayuda = new JLabel("<html><i>Para una nueva visita, elige la fecha y pulsa "
                + "\"+ Nueva visita\". Para mover la fecha o eliminar, primero selecciona "
                + "una visita en la tabla de arriba.</i></html>");
        ayuda.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        ayuda.setForeground(new Color(120, 120, 120));
        g.gridx = 0; g.gridy = 2; g.gridwidth = 3; g.insets = new Insets(8, 6, 2, 6);
        crear.add(ayuda, g);

        izq.add(crear, BorderLayout.SOUTH);
        centro.add(izq);

        // Derecha: selector estudiante + detalle
        JPanel der = new JPanel(new BorderLayout(0, 8));
        der.setOpaque(false);
        JPanel derTop = new JPanel(new BorderLayout(8, 0));
        derTop.setOpaque(false);
        derTop.add(etiquetaBold("DETALLE DE LA VISITA"), BorderLayout.NORTH);
        JPanel selPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        selPanel.setOpaque(false);
        JLabel lblSel = new JLabel("Ver como:");
        lblSel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cmbEstudiante.setPreferredSize(new Dimension(300, 30));
        selPanel.add(lblSel);
        selPanel.add(cmbEstudiante);
        derTop.add(selPanel, BorderLayout.SOUTH);
        der.add(derTop, BorderLayout.NORTH);

        scrollDetalle.setBorder(new LineBorder(GRIS_BORDE));
        scrollDetalle.getVerticalScrollBar().setUnitIncrement(16);
        der.add(scrollDetalle, BorderLayout.CENTER);
        centro.add(der);

        main.add(centro, BorderLayout.CENTER);

        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        main.add(lblMensaje, BorderLayout.SOUTH);

        getContentPane().add(main, BorderLayout.CENTER);
    }

    private void cargarVisitas() {
        visitas = new VisitaDAO().listarVisitasBase(idBitacora);
        String[] cols = {"ID Visita", "Visita", "Fecha límite", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Visita v : visitas) {
            modelo.addRow(new Object[]{
                v.getIdVisita(), "Visita " + v.getNumeroVisita(), v.getFechaLimite(), v.getEstado()
            });
        }
        tblVisitas.setModel(modelo);
        if (tblVisitas.getColumnModel().getColumnCount() > 0) {
            tblVisitas.getColumnModel().getColumn(0).setPreferredWidth(180);
            tblVisitas.getColumnModel().getColumn(1).setPreferredWidth(70);
            tblVisitas.getColumnModel().getColumn(2).setPreferredWidth(110);
            tblVisitas.getColumnModel().getColumn(3).setPreferredWidth(90);
        }
        panelDetalle.removeAll();
        panelDetalle.add(msgLabel("Selecciona una visita para ver su detalle."));
        panelDetalle.revalidate(); panelDetalle.repaint();
    }

    private void mostrarDetalleSeleccionada() {
        int fila = tblVisitas.getSelectedRow();
        if (fila < 0 || fila >= visitas.size()) return;
        mostrarDetalle(visitas.get(fila));
    }

    private void mostrarDetalle(Visita v) {
        panelDetalle.removeAll();
        panelDetalle.setBorder(new EmptyBorder(15, 15, 15, 15));

        long idEst = estudianteSeleccionado();   // 0 = plantilla

        seccionLabel("VISITA N° " + v.getNumeroVisita());
        infoRow("Fecha límite:", v.getFechaLimite() != null ? v.getFechaLimite().toString() : "—");
        infoRow("Estado:", v.getEstado());

        // Ubicación de la visita (la registra el estudiante)
        panelDetalle.add(Box.createRigidArea(new Dimension(0, 8)));
        seccionLabel("UBICACIÓN DE LA VISITA");
        String ubic = "(disponible al elegir un estudiante)";
        if (idEst > 0) {
            ubic = ubicacionDeEstudiante(v.getNumeroVisita(), idEst);
            if (ubic == null || ubic.isEmpty()) ubic = "(el estudiante aún no registra dónde hizo la visita)";
        }
        cajaTexto(ubic);

        // Preguntas (+ respuestas si hay estudiante)
        panelDetalle.add(Box.createRigidArea(new Dimension(0, 10)));
        seccionLabel("PREGUNTAS");
        p_integrador.dao.PreguntaDAO pregDAO = new p_integrador.dao.PreguntaDAO();
        p_integrador.dao.RespuestaDAO respDAO = new p_integrador.dao.RespuestaDAO();
        List<p_integrador.modelo.Pregunta> preguntas = pregDAO.listarPorVisita(v.getIdVisita());

        if (preguntas.isEmpty()) {
            panelDetalle.add(msgLabel("Sin preguntas registradas."));
        } else {
            for (p_integrador.modelo.Pregunta p : preguntas) {
                infoRow("P" + p.getNumero() + ":", p.getTexto());
                if (idEst > 0) {
                    List<p_integrador.modelo.Respuesta> resps =
                        respDAO.listarPorPreguntaYEstudiante(p.getIdPregunta(), idEst);
                    String txt = resps.isEmpty() ? "(sin respuesta)" : resps.get(0).getRespuesta();
                    cajaTexto(txt);
                }
            }
        }

        // Retroalimentación
        panelDetalle.add(Box.createRigidArea(new Dimension(0, 10)));
        seccionLabel("RETROALIMENTACIÓN");
        cajaTexto(v.getRetroalimentacion() != null ? v.getRetroalimentacion() : "Sin retroalimentación");

        // Validar horas
        panelDetalle.add(Box.createRigidArea(new Dimension(0, 10)));
        JPanel pnlHoras = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlHoras.setOpaque(false);
        pnlHoras.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pnlHoras.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblH = new JLabel("Horas validadas:");
        lblH.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JTextField txtH = new JTextField(v.getHorasValidadas() > 0 ? String.valueOf(v.getHorasValidadas()) : "", 5);
        JButton btnG = new JButton("Guardar");
        estiloPrimario(btnG);
        btnG.setPreferredSize(new Dimension(100, 30));
        btnG.addActionListener(e -> {
            try {
                int h = Integer.parseInt(txtH.getText().trim());
                if (new VisitaDAO().validarHoras(v.getIdVisita(), h)) msgOk("Horas guardadas para " + v.getIdVisita());
            } catch (NumberFormatException ex) { msgError("Ingresa un número válido."); }
        });
        pnlHoras.add(lblH); pnlHoras.add(txtH); pnlHoras.add(btnG);
        panelDetalle.add(pnlHoras);

        panelDetalle.revalidate(); panelDetalle.repaint();
        scrollDetalle.getVerticalScrollBar().setValue(0);
    }

    private long estudianteSeleccionado() {
        int idx = cmbEstudiante.getSelectedIndex();
        if (idx <= 0) return 0L;
        String item = cmbEstudiante.getSelectedItem().toString();
        try { return Long.parseLong(item.split(" - ")[0].trim()); }
        catch (Exception e) { return 0L; }
    }

    private String ubicacionDeEstudiante(int numeroVisita, long idEst) {
        List<Visita> lista = new VisitaDAO().listarPorBitacoraYNumero(idBitacora, idEst, numeroVisita);
        return lista.isEmpty() ? null : lista.get(0).getUbicacion();
    }

    // ── Acciones de visita ───────────────────────────────────────────

    private void crearVisita() {
        if (dateFecha.getDate() == null) { msgError("Selecciona una fecha límite para la nueva visita."); return; }
        int num = visitas.size() + 1;
        String idVisita = idBitacora + "_V" + num;
        Visita v = new Visita(idVisita, idBitacora, 0L, num, null, dateFecha.getDate(),
            0, null, "PENDIENTE", null, 0, "PENDIENTE", null, 0);
        if (new VisitaDAO().crear(v)) { msgOk("Visita " + num + " creada."); cargarVisitas(); }
        else msgError("Error al crear la visita.");
    }

    private void modificarFecha() {
        int fila = tblVisitas.getSelectedRow();
        if (fila < 0) { msgError("Selecciona una visita en la tabla."); return; }
        if (dateFecha.getDate() == null) { msgError("Selecciona la nueva fecha límite."); return; }
        Visita v = visitas.get(fila);
        if (new VisitaDAO().actualizarFechaLimite(v.getIdVisita(), dateFecha.getDate())) {
            msgOk("Fecha actualizada."); cargarVisitas();
        } else msgError("Error al actualizar la fecha.");
    }

    private void eliminarVisita() {
        int fila = tblVisitas.getSelectedRow();
        if (fila < 0) { msgError("Selecciona una visita en la tabla."); return; }
        Visita v = visitas.get(fila);
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Eliminar la visita " + v.getIdVisita() + " con sus preguntas y respuestas?",
            "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (new VisitaDAO().eliminar(v.getIdVisita())) { msgOk("Visita eliminada."); cargarVisitas(); }
            else msgError("Error al eliminar la visita.");
        }
    }

    // ── Estilo / helpers ─────────────────────────────────────────────

    private void msgOk(String m)    { lblMensaje.setText(m); lblMensaje.setForeground(VERDE_OK); }
    private void msgError(String m) { lblMensaje.setText(m); lblMensaje.setForeground(ROJO); }

    private JLabel etiquetaBold(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(AZUL_UDI);
        return l;
    }
    private void seccionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(AZUL_UDI);
        l.setBorder(new EmptyBorder(4, 0, 4, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalle.add(l);
    }
    private void infoRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel val = new JLabel("<html>" + value + "</html>");
        val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        row.add(lbl); row.add(val);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalle.add(row);
    }
    private void cajaTexto(String texto) {
        JTextArea area = new JTextArea(texto);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        area.setBackground(new Color(245, 249, 255));
        area.setBorder(new CompoundBorder(new LineBorder(new Color(200, 215, 235)), new EmptyBorder(6, 8, 6, 8)));
        area.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalle.add(area);
        panelDetalle.add(Box.createRigidArea(new Dimension(0, 6)));
    }
    private JLabel msgLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        l.setForeground(new Color(100, 100, 100));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
    private void base(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(150, 32));
    }
    private void estiloPrimario(JButton b)   { base(b); b.setBackground(AZUL_UDI); b.setForeground(BLANCO); b.setBorder(new EmptyBorder(6,10,6,10)); }
    private void estiloSecundario(JButton b) { base(b); b.setBackground(BLANCO); b.setForeground(AZUL_UDI); b.setBorder(new LineBorder(AZUL_UDI,1)); }
    private void estiloPeligro(JButton b)    { base(b); b.setBackground(BLANCO); b.setForeground(ROJO); b.setBorder(new LineBorder(ROJO,1)); }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new VisitasForm("TEST").setVisible(true));
    }
}
