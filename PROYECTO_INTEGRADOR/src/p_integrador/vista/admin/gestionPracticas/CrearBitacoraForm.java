package p_integrador.vista.admin.gestionPracticas;

import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import p_integrador.vista.admin.panelMenu;

/**
 * Creación de una bitácora (rediseñada para ser más clara).
 *
 *  Paso 1: Datos generales -> OBJETIVO GENERAL (qué se evalúa en la bitácora)
 *          + periodo (fecha de inicio y fecha límite).
 *  Paso 2: Visitas -> lista de visitas a la izquierda (agregar / quitar) y, para
 *          la visita seleccionada, sus preguntas a la derecha.
 *
 * Al guardar: se crea la bitácora con su objetivo, sus visitas base con preguntas,
 * y se inscribe automáticamente a TODOS los estudiantes del grupo de la práctica.
 *
 * Sin colores verde/amarillo y sin emojis.
 */
public class CrearBitacoraForm extends javax.swing.JFrame {

    private final String idPractica;

    // Visitas temporales: nombre visible -> lista de preguntas
    private final java.util.List<String> visitasTemp = new java.util.ArrayList<>();
    private final Map<String, java.util.List<String>> preguntas = new LinkedHashMap<>();

    private final Color AZUL_UDI   = new Color(0, 51, 102);
    private final Color FONDO      = new Color(240, 248, 255);
    private final Color BLANCO     = Color.WHITE;
    private final Color GRIS_BORDE = new Color(210, 220, 235);
    private final Color ROJO       = new Color(180, 50, 50);
    private final Color VERDE_OK   = new Color(0, 110, 60);

    private JTextArea       txtObjetivo;
    private JDateChooser    dateInicio, dateLimite;
    private JList<String>   listaVisitas;
    private DefaultListModel<String> modeloVisitas;
    private JTable          tblPreguntas;
    private JTextArea       txtPregunta;
    private JButton         btnAgregarVisita, btnEliminarVisita;
    private JButton         btnAgregarPregunta, btnEditarPregunta, btnEliminarPregunta;
    private JButton         btnGuardar;
    private JLabel          lblMensaje;

    public CrearBitacoraForm(String idPractica) {
        this.idPractica = idPractica;
        initComponents();
        buildUI();
        agregarVisita();                  // primera visita por defecto
        listaVisitas.setSelectedIndex(0);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Nueva Bitácora - " + idPractica);
        setSize(1250, 720);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        txtObjetivo = new JTextArea(3, 20);
        txtObjetivo.setLineWrap(true);
        txtObjetivo.setWrapStyleWord(true);
        dateInicio = new JDateChooser();
        dateLimite = new JDateChooser();
        modeloVisitas = new DefaultListModel<>();
        listaVisitas = new JList<>(modeloVisitas);
        tblPreguntas = new JTable();
        txtPregunta = new JTextArea(4, 20);
        txtPregunta.setLineWrap(true);
        txtPregunta.setWrapStyleWord(true);

        btnAgregarVisita    = new JButton("+ Agregar visita");
        btnEliminarVisita   = new JButton("Quitar visita");
        btnAgregarPregunta  = new JButton("Añadir pregunta");
        btnEditarPregunta   = new JButton("Editar selección");
        btnEliminarPregunta = new JButton("Quitar pregunta");
        btnGuardar          = new JButton("GUARDAR BITÁCORA");
        lblMensaje          = new JLabel(" ");

        listaVisitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaVisitas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) refrescarPreguntas();
        });

        btnAgregarVisita.addActionListener(e -> { agregarVisita(); listaVisitas.setSelectedIndex(visitasTemp.size()-1); });
        btnEliminarVisita.addActionListener(e -> eliminarVisita());
        btnAgregarPregunta.addActionListener(e -> agregarPregunta());
        btnEditarPregunta.addActionListener(e -> editarPregunta());
        btnEliminarPregunta.addActionListener(e -> eliminarPregunta());
        btnGuardar.addActionListener(e -> guardarBitacora());
    }

    private void buildUI() {
        getContentPane().setBackground(FONDO);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new panelMenu(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout(0, 16));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(26, 30, 22, 30));

        JLabel titulo = new JLabel("NUEVA BITÁCORA");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(AZUL_UDI);
        main.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setOpaque(false);
        centro.add(buildPanelGeneral(), BorderLayout.NORTH);
        centro.add(buildPanelVisitas(), BorderLayout.CENTER);
        main.add(centro, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        footer.add(lblMensaje, BorderLayout.WEST);
        estiloPrimario(btnGuardar);
        btnGuardar.setPreferredSize(new Dimension(260, 44));
        footer.add(btnGuardar, BorderLayout.EAST);
        main.add(footer, BorderLayout.SOUTH);

        getContentPane().add(main, BorderLayout.CENTER);
    }

    /** Paso 1: objetivo general + periodo. */
    private JPanel buildPanelGeneral() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BLANCO);
        card.setBorder(seccion("Paso 1: Datos generales de la bitácora"));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 12, 8, 12);
        g.anchor = GridBagConstraints.NORTHWEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0;
        card.add(etiquetaBold("Objetivo general (qué se evalúa en esta bitácora):"), g);

        g.gridy = 1; g.weightx = 1.0; g.gridwidth = 4;
        txtObjetivo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtObjetivo.setBorder(new CompoundBorder(new LineBorder(GRIS_BORDE), new EmptyBorder(6,8,6,8)));
        JScrollPane spObj = new JScrollPane(txtObjetivo);
        spObj.setPreferredSize(new Dimension(0, 70));
        card.add(spObj, g);

        g.gridwidth = 1; g.weightx = 0;
        JLabel ej = new JLabel("Ej.: Que los estudiantes aprendan a analizar los ambientes donde los niños"
                + " reciben clases e identificar si son aptos o no.");
        ej.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        ej.setForeground(new Color(120, 120, 120));
        g.gridy = 2; g.gridwidth = 4;
        card.add(ej, g);

        g.gridwidth = 1;
        g.gridy = 3; g.gridx = 0;
        card.add(etiquetaBold("Fecha de inicio:"), g);
        dateInicio.setPreferredSize(new Dimension(160, 32));
        g.gridx = 1; card.add(dateInicio, g);
        g.gridx = 2; card.add(etiquetaBold("Fecha límite:"), g);
        dateLimite.setPreferredSize(new Dimension(160, 32));
        g.gridx = 3; card.add(dateLimite, g);

        return card;
    }

    /** Paso 2: visitas (lista) + preguntas de la visita seleccionada. */
    private JPanel buildPanelVisitas() {
        JPanel card = new JPanel(new BorderLayout(16, 0));
        card.setBackground(BLANCO);
        card.setBorder(seccion("Paso 2: Visitas y sus preguntas"));

        // Izquierda: lista de visitas
        JPanel izq = new JPanel(new BorderLayout(0, 8));
        izq.setOpaque(false);
        izq.setPreferredSize(new Dimension(230, 0));
        JLabel lblV = etiquetaBold("Visitas de la bitácora");
        izq.add(lblV, BorderLayout.NORTH);

        listaVisitas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listaVisitas.setFixedCellHeight(34);
        listaVisitas.setBorder(new LineBorder(GRIS_BORDE));
        izq.add(new JScrollPane(listaVisitas), BorderLayout.CENTER);

        JPanel botV = new JPanel(new GridLayout(1, 2, 8, 0));
        botV.setOpaque(false);
        estiloSecundario(btnAgregarVisita);
        estiloPeligro(btnEliminarVisita);
        botV.add(btnAgregarVisita);
        botV.add(btnEliminarVisita);

        izq.add(botV, BorderLayout.SOUTH);
        card.add(izq, BorderLayout.WEST);

        // Centro: tabla de preguntas
        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setOpaque(false);
        centro.add(etiquetaBold("Preguntas de la visita seleccionada"), BorderLayout.NORTH);
        tblPreguntas.setRowHeight(26);
        tblPreguntas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblPreguntas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        centro.add(new JScrollPane(tblPreguntas), BorderLayout.CENTER);
        card.add(centro, BorderLayout.CENTER);

        // Derecha: editor de pregunta
        JPanel der = new JPanel();
        der.setOpaque(false);
        der.setLayout(new BoxLayout(der, BoxLayout.Y_AXIS));
        der.setPreferredSize(new Dimension(300, 0));
        JLabel lblEd = etiquetaBold("Redactar pregunta");
        lblEd.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPregunta.setBorder(new CompoundBorder(new LineBorder(GRIS_BORDE), new EmptyBorder(6,8,6,8)));
        JScrollPane spP = new JScrollPane(txtPregunta);
        spP.setAlignmentX(Component.LEFT_ALIGNMENT);
        spP.setMaximumSize(new Dimension(300, 120));
        der.add(lblEd);
        der.add(Box.createRigidArea(new Dimension(0, 8)));
        der.add(spP);
        der.add(Box.createRigidArea(new Dimension(0, 12)));
        estiloPrimario(btnAgregarPregunta);   anchoFull(btnAgregarPregunta);  der.add(btnAgregarPregunta);
        der.add(Box.createRigidArea(new Dimension(0, 8)));
        estiloSecundario(btnEditarPregunta);   anchoFull(btnEditarPregunta);  der.add(btnEditarPregunta);
        der.add(Box.createRigidArea(new Dimension(0, 8)));
        estiloPeligro(btnEliminarPregunta);     anchoFull(btnEliminarPregunta); der.add(btnEliminarPregunta);
        card.add(der, BorderLayout.EAST);

        return card;
    }

    // ── Manejo de visitas (en memoria) ───────────────────────────────

    private void agregarVisita() {
        int num = visitasTemp.size() + 1;
        String key = "Visita " + num;
        visitasTemp.add(key);
        preguntas.put(key, new java.util.ArrayList<>());
        modeloVisitas.addElement(key);
    }

    private void eliminarVisita() {
        int idx = listaVisitas.getSelectedIndex();
        if (idx < 0) { msgError("Selecciona la visita que deseas quitar."); return; }
        if (visitasTemp.size() <= 1) { msgError("La bitácora debe tener al menos una visita."); return; }
        String key = visitasTemp.get(idx);
        preguntas.remove(key);
        visitasTemp.remove(idx);
        modeloVisitas.remove(idx);
        renumerarVisitas();
        if (!visitasTemp.isEmpty()) listaVisitas.setSelectedIndex(0);
    }

    private void renumerarVisitas() {
        // Reconstruye nombres "Visita N" manteniendo preguntas
        Map<String, java.util.List<String>> nuevoPreg = new LinkedHashMap<>();
        modeloVisitas.clear();
        for (int i = 0; i < visitasTemp.size(); i++) {
            String viejo = visitasTemp.get(i);
            String key = "Visita " + (i + 1);
            nuevoPreg.put(key, preguntas.getOrDefault(viejo, new java.util.ArrayList<>()));
            visitasTemp.set(i, key);
            modeloVisitas.addElement(key);
        }
        preguntas.clear();
        preguntas.putAll(nuevoPreg);
    }

    private void agregarPregunta() {
        int idx = listaVisitas.getSelectedIndex();
        if (idx < 0) { msgError("Selecciona primero una visita."); return; }
        String texto = txtPregunta.getText().trim();
        if (texto.isEmpty()) { msgError("Escribe el texto de la pregunta."); return; }
        preguntas.get(visitasTemp.get(idx)).add(texto);
        txtPregunta.setText("");
        refrescarPreguntas();
    }

    private void editarPregunta() {
        int idx = listaVisitas.getSelectedIndex();
        int fila = tblPreguntas.getSelectedRow();
        if (idx < 0 || fila < 0) { msgError("Selecciona una pregunta de la tabla para editarla."); return; }
        String texto = preguntas.get(visitasTemp.get(idx)).remove(fila);
        txtPregunta.setText(texto);
        refrescarPreguntas();
    }

    private void eliminarPregunta() {
        int idx = listaVisitas.getSelectedIndex();
        int fila = tblPreguntas.getSelectedRow();
        if (idx < 0 || fila < 0) { msgError("Selecciona una pregunta de la tabla para quitarla."); return; }
        preguntas.get(visitasTemp.get(idx)).remove(fila);
        refrescarPreguntas();
    }

    private void refrescarPreguntas() {
        int idx = listaVisitas.getSelectedIndex();
        DefaultTableModel modelo = new DefaultTableModel(new String[]{"N°", "Pregunta"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        if (idx >= 0 && idx < visitasTemp.size()) {
            java.util.List<String> pList = preguntas.getOrDefault(visitasTemp.get(idx), new java.util.ArrayList<>());
            for (int i = 0; i < pList.size(); i++) modelo.addRow(new Object[]{i + 1, pList.get(i)});
        }
        tblPreguntas.setModel(modelo);
        if (tblPreguntas.getColumnModel().getColumnCount() > 0)
            tblPreguntas.getColumnModel().getColumn(0).setMaxWidth(45);
    }

    // ── Guardado ─────────────────────────────────────────────────────

    private void guardarBitacora() {
        if (txtObjetivo.getText().trim().isEmpty()) {
            msgError("Escribe el objetivo general de la bitácora."); return;
        }
        if (dateInicio.getDate() == null || dateLimite.getDate() == null) {
            msgError("Selecciona la fecha de inicio y la fecha límite."); return;
        }
        if (dateLimite.getDate().before(dateInicio.getDate())) {
            msgError("La fecha límite debe ser posterior a la fecha de inicio."); return;
        }

        p_integrador.dao.BitacoraDAO bitacoraDAO = new p_integrador.dao.BitacoraDAO();
        p_integrador.dao.VisitaDAO   visitaDAO   = new p_integrador.dao.VisitaDAO();
        p_integrador.dao.PreguntaDAO preguntaDAO = new p_integrador.dao.PreguntaDAO();

        // Lecturas previas (pocas y secuenciales)
        String codigo = p_integrador.dao.BitacoraDAO.generarCodigo();
        int numBit = bitacoraDAO.listarPorPractica(idPractica).size() + 1;
        String idBitacora = idPractica + "_BIT" + numBit;

        p_integrador.modelo.Practica practica = new p_integrador.dao.PracticaDAO().buscarPorId(idPractica);
        java.util.List<p_integrador.modelo.Usuario> estudiantes = new java.util.ArrayList<>();
        if (practica != null)
            estudiantes = new p_integrador.dao.UsuarioDAO().listarPorGrupo(practica.getCodigoGrupo());

        // TODA la escritura va en UNA sola conexión + transacción.
        // Así se evita abrir decenas de conexiones (causa del ORA-12519 en Oracle XE).
        try (java.sql.Connection con = p_integrador.conexion.ConexionDB.conectar()) {
            con.setAutoCommit(false);
            try {
                // 1. Bitácora madre con objetivo
                p_integrador.modelo.Bitacora b = new p_integrador.modelo.Bitacora(
                    idBitacora, codigo, idPractica, dateLimite.getDate(), "ACTIVA", null, null, null, 0, 0
                );
                b.setObjetivo(txtObjetivo.getText().trim());
                bitacoraDAO.crear(b, con);

                // 2. Visitas base + preguntas + copia por estudiante (inscripción automática)
                for (int i = 0; i < visitasTemp.size(); i++) {
                    String idVisita = idBitacora + "_V" + (i + 1);
                    p_integrador.modelo.Visita vBase = new p_integrador.modelo.Visita(
                        idVisita, idBitacora, 0L, i + 1,
                        dateInicio.getDate(), dateLimite.getDate(),
                        0, null, "PENDIENTE", null, 0, "PENDIENTE", null, 0
                    );
                    visitaDAO.crear(vBase, con);

                    java.util.List<String> pList = preguntas.getOrDefault(visitasTemp.get(i), new java.util.ArrayList<>());
                    for (int j = 0; j < pList.size(); j++) {
                        preguntaDAO.crear(new p_integrador.modelo.Pregunta(
                            idVisita + "_P" + (j + 1), idVisita, j + 1, pList.get(j)), con);
                    }
                    for (p_integrador.modelo.Usuario est : estudiantes) {
                        String idVEst = idVisita + "_" + est.getIdUsuario();
                        p_integrador.modelo.Visita vEst = new p_integrador.modelo.Visita(
                            idVEst, idBitacora, est.getIdUsuario(), i + 1,
                            dateInicio.getDate(), dateLimite.getDate(),
                            0, null, "PENDIENTE", null, 0, "PENDIENTE", null, 0
                        );
                        visitaDAO.crear(vEst, con);
                    }
                }

                con.commit();
                msgOk("Bitácora creada. " + estudiantes.size() + " estudiante(s) inscrito(s) automáticamente.");
                btnGuardar.setEnabled(false);

            } catch (java.sql.SQLException ex) {
                try { con.rollback(); } catch (java.sql.SQLException ignore) {}
                msgError("No se pudo crear la bitácora (se revirtieron los cambios): " + ex.getMessage());
            }
        } catch (java.sql.SQLException e) {
            msgError("Error de conexión con la base de datos: " + e.getMessage());
        }
    }

    // ── Estilo ───────────────────────────────────────────────────────

    private void msgOk(String m)    { lblMensaje.setText(m); lblMensaje.setForeground(VERDE_OK); }
    private void msgError(String m) { lblMensaje.setText(m); lblMensaje.setForeground(ROJO); }

    private TitledBorder seccion(String t) {
        TitledBorder tb = BorderFactory.createTitledBorder(new LineBorder(GRIS_BORDE), t);
        tb.setTitleFont(new Font("Segoe UI", Font.BOLD, 13));
        tb.setTitleColor(AZUL_UDI);
        return tb;
    }
    private JLabel etiquetaBold(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(AZUL_UDI);
        return l;
    }
    private void anchoFull(JButton b) {
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(300, 36));
    }
    private void base(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(140, 36));
    }
    private void estiloPrimario(JButton b)   { base(b); b.setBackground(AZUL_UDI); b.setForeground(BLANCO); b.setBorder(new EmptyBorder(6,10,6,10)); }
    private void estiloSecundario(JButton b) { base(b); b.setBackground(BLANCO); b.setForeground(AZUL_UDI); b.setBorder(new LineBorder(AZUL_UDI,1)); }
    private void estiloPeligro(JButton b)    { base(b); b.setBackground(BLANCO); b.setForeground(ROJO); b.setBorder(new LineBorder(ROJO,1)); }

    public static void main(String args[]) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        java.awt.EventQueue.invokeLater(() -> new CrearBitacoraForm("TEST_PRACTICA").setVisible(true));
    }
}
