package p_integrador.vista.admin.gestionPracticas;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import p_integrador.vista.admin.panelMenu;

/**
 * Bitácoras de una práctica (de todo el grupo).
 *
 * Cada bitácora tiene un OBJETIVO GENERAL que indica qué se evalúa en ella
 * (ej.: "Que los estudiantes aprendan a analizar los ambientes donde los niños
 * reciben clases e identificar si son aptos o no").
 *
 * Ya no se requiere seleccionar un estudiante: las bitácoras pertenecen a la
 * práctica completa. Desde aquí se entra a sus visitas.
 */
public class BitacorasForm extends javax.swing.JFrame {

    private final String idPractica;

    private final Color AZUL_UDI    = new Color(0, 51, 102);
    private final Color FONDO_CLARO = new Color(219, 240, 255);
    private final Color BLANCO      = Color.WHITE;
    private final Color GRIS_BORDE  = new Color(210, 220, 235);
    private final Color ROJO        = new Color(180, 50, 50);
    private final Color VERDE_OK    = new Color(0, 110, 60);
    private final Color NARANJA     = new Color(200, 120, 0);

    private int horasRequeridasPractica = 0;
    private JProgressBar barraHoras;
    private JLabel        lblHorasResumen;

    private JTable      tblBitacoras;
    private JLabel      lblPractica, lblMensaje;
    private JButton     btnCrearBitacora, btnVerVisitas, btnAsignarAsesor, btnEliminarBitacora;

    public BitacorasForm(String idPractica) {
        this.idPractica = idPractica;
        p_integrador.modelo.Practica p = new p_integrador.dao.PracticaDAO().buscarPorId(idPractica);
        if (p != null) horasRequeridasPractica = p.getHorasRequeridas();
        initComponents();
        buildUI();
        lblPractica.setText("BITÁCORAS DE LA PRÁCTICA: " + idPractica);
        cargarBitacoras();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Bitácoras - " + idPractica);
        setSize(1220, 700);
        setLocationRelativeTo(null);
    }

    /** Compatibilidad: el segundo parámetro (estudiante) se ignora. */
    public BitacorasForm(String idPractica, long idEstudiante) {
        this(idPractica);
    }

    private void initComponents() {
        lblPractica         = new JLabel();
        lblMensaje          = new JLabel(" ");
        tblBitacoras        = new JTable();
        barraHoras          = new JProgressBar(0, Math.max(horasRequeridasPractica, 1));
        barraHoras.setStringPainted(true);
        barraHoras.setPreferredSize(new Dimension(0, 26));
        lblHorasResumen     = new JLabel(" ");
        btnCrearBitacora    = new JButton("Nueva Bitácora");
        btnVerVisitas       = new JButton("VER VISITAS");
        btnAsignarAsesor    = new JButton("Asignar Asesor");
        btnEliminarBitacora = new JButton("Eliminar");

        btnCrearBitacora.addActionListener(e -> {
            new CrearBitacoraForm(idPractica).setVisible(true);
            this.dispose();
        });
        btnVerVisitas.addActionListener(e -> {
            String id = bitacoraSeleccionada();
            if (id == null) return;
            new VisitasForm(id).setVisible(true);
            this.dispose();
        });
        btnAsignarAsesor.addActionListener(e -> asignarAsesor());
        btnEliminarBitacora.addActionListener(e -> eliminarBitacora());
    }

    private void buildUI() {
        getContentPane().setBackground(FONDO_CLARO);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new panelMenu(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout(0, 16));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(28, 30, 24, 30));

        // Cabecera
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        lblPractica.setFont(new Font("Segoe UI", Font.BOLD, 23));
        lblPractica.setForeground(AZUL_UDI);
        JLabel sub = new JLabel("Cada bitácora aplica a todo el grupo. Selecciona una para ver sus visitas.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(90, 90, 90));
        JButton btnVolver = new JButton("Volver a prácticas");
        btnVolver.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnVolver.setForeground(AZUL_UDI);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setBorder(new LineBorder(AZUL_UDI, 1));
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setPreferredSize(new Dimension(180, 32));
        btnVolver.addActionListener(e -> {
            new GestionPracticasForm().setVisible(true);
            this.dispose();
        });
        JPanel headTxt = new JPanel(new BorderLayout());
        headTxt.setOpaque(false);
        headTxt.add(lblPractica, BorderLayout.NORTH);
        headTxt.add(sub, BorderLayout.SOUTH);
        header.add(headTxt, BorderLayout.CENTER);
        JPanel headRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headRight.setOpaque(false);
        headRight.add(btnVolver);
        header.add(headRight, BorderLayout.EAST);

        // Medidor de horas de la práctica (suma de horas de todas las bitácoras)
        JPanel medidor = new JPanel(new BorderLayout(10, 4));
        medidor.setBackground(BLANCO);
        medidor.setBorder(new javax.swing.border.CompoundBorder(
            new LineBorder(GRIS_BORDE), new EmptyBorder(10, 12, 10, 12)));
        lblHorasResumen.setFont(new Font("Segoe UI", Font.BOLD, 13));
        barraHoras.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel ayudaH = new JLabel("La suma de las horas de todas las bitácoras debe cumplir las horas requeridas por la práctica.");
        ayudaH.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        ayudaH.setForeground(new Color(120, 120, 120));
        medidor.add(lblHorasResumen, BorderLayout.NORTH);
        medidor.add(barraHoras, BorderLayout.CENTER);
        medidor.add(ayudaH, BorderLayout.SOUTH);

        JPanel norte = new JPanel(new BorderLayout(0, 14));
        norte.setOpaque(false);
        norte.add(header, BorderLayout.NORTH);
        norte.add(medidor, BorderLayout.SOUTH);
        main.add(norte, BorderLayout.NORTH);

        // Tabla
        tblBitacoras.setRowHeight(30);
        tblBitacoras.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblBitacoras.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblBitacoras.getTableHeader().setReorderingAllowed(false);
        tblBitacoras.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblBitacoras.setSelectionBackground(new Color(210, 228, 248));
        tblBitacoras.setSelectionForeground(AZUL_UDI);
        JScrollPane sp = new JScrollPane(tblBitacoras);
        sp.setBorder(new LineBorder(GRIS_BORDE));
        main.add(sp, BorderLayout.CENTER);

        // Acciones (paleta solo azul/blanco/rojo)
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        acciones.setOpaque(false);
        estiloPrimario(btnVerVisitas);
        btnVerVisitas.setPreferredSize(new Dimension(150, 38));
        estiloPrimario(btnCrearBitacora);
        estiloSecundario(btnAsignarAsesor);
        estiloPeligro(btnEliminarBitacora);
        acciones.add(btnVerVisitas);
        acciones.add(btnCrearBitacora);
        acciones.add(btnAsignarAsesor);
        acciones.add(btnEliminarBitacora);

        JPanel sur = new JPanel(new BorderLayout());
        sur.setOpaque(false);
        sur.add(acciones, BorderLayout.NORTH);
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sur.add(lblMensaje, BorderLayout.SOUTH);
        main.add(sur, BorderLayout.SOUTH);

        getContentPane().add(main, BorderLayout.CENTER);
    }

    private void cargarBitacoras() {
        java.util.List<p_integrador.modelo.Bitacora> lista =
            new p_integrador.dao.BitacoraDAO().listarPorPractica(idPractica);
        p_integrador.dao.VisitaDAO vDAO = new p_integrador.dao.VisitaDAO();

        String[] cols = {"Bitácora", "Objetivo general", "Horas", "Fecha límite", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        int sumaTotal = 0;
        for (p_integrador.modelo.Bitacora b : lista) {
            String obj = b.getObjetivo() == null || b.getObjetivo().isEmpty()
                ? "(sin objetivo)" : b.getObjetivo();
            int horas = vDAO.sumaHorasBitacora(b.getIdBitacora());
            sumaTotal += horas;
            modelo.addRow(new Object[]{
                b.getIdBitacora(), obj, horas + " h", b.getFechaLimite(), b.getEstado()
            });
        }
        tblBitacoras.setModel(modelo);
        if (tblBitacoras.getColumnModel().getColumnCount() > 0) {
            tblBitacoras.getColumnModel().getColumn(0).setPreferredWidth(180);
            tblBitacoras.getColumnModel().getColumn(1).setPreferredWidth(380);
            tblBitacoras.getColumnModel().getColumn(2).setPreferredWidth(60);
            tblBitacoras.getColumnModel().getColumn(3).setPreferredWidth(110);
            tblBitacoras.getColumnModel().getColumn(4).setPreferredWidth(90);
        }
        actualizarMedidor(sumaTotal);
        if (lista.isEmpty()) msgInfo("Esta práctica aún no tiene bitácoras. Crea la primera con 'Nueva Bitácora'.");
    }

    private void actualizarMedidor(int suma) {
        int req = Math.max(horasRequeridasPractica, 0);
        barraHoras.setMaximum(Math.max(req, 1));
        barraHoras.setValue(Math.min(suma, Math.max(req, 1)));
        barraHoras.setString(suma + " / " + req + " h");

        int faltan = req - suma;
        String estado;
        if (req == 0) {
            estado = "La práctica no tiene horas requeridas definidas.";
            barraHoras.setForeground(new Color(120, 120, 120));
        } else if (faltan > 0) {
            estado = "Acumuladas " + suma + " de " + req + " h  -  faltan " + faltan + " h por programar en bitácoras.";
            barraHoras.setForeground(NARANJA);
        } else if (faltan == 0) {
            estado = "Horas completas: " + suma + " de " + req + " h.";
            barraHoras.setForeground(VERDE_OK);
        } else {
            estado = "Se excede: " + suma + " h acumuladas, la práctica requiere " + req + " h (" + (-faltan) + " h de más).";
            barraHoras.setForeground(ROJO);
        }
        lblHorasResumen.setText(estado);
        lblHorasResumen.setForeground(faltan == 0 && req > 0 ? VERDE_OK : (faltan < 0 ? ROJO : AZUL_UDI));
    }

    private void asignarAsesor() {
        String idBitacora = bitacoraSeleccionada();
        if (idBitacora == null) return;

        p_integrador.dao.UsuarioDAO uDAO = new p_integrador.dao.UsuarioDAO();
        java.util.List<p_integrador.modelo.Usuario> asesores = uDAO.listarPorRol("ASESOR");
        if (asesores.isEmpty()) { msgError("No hay asesores registrados."); return; }

        String[] nombres = asesores.stream()
            .map(u -> u.getNombre1() + " " + u.getApellido1())
            .toArray(String[]::new);

        String seleccion = (String) JOptionPane.showInputDialog(this,
            "Selecciona un asesor para la bitácora:", "Asignar Asesor",
            JOptionPane.PLAIN_MESSAGE, null, nombres, nombres[0]);

        if (seleccion != null) {
            int idx = java.util.Arrays.asList(nombres).indexOf(seleccion);
            long idAsesor = asesores.get(idx).getIdUsuario();
            if (new p_integrador.dao.BitacoraDAO().asignarAsesor(idBitacora, idAsesor))
                msgOk("Asesor asignado correctamente.");
            else
                msgError("Error al asignar asesor.");
        }
    }

    private void eliminarBitacora() {
        String idBitacora = bitacoraSeleccionada();
        if (idBitacora == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Eliminar la bitácora " + idBitacora + " con sus visitas y respuestas?\n"
            + "Esta acción no se puede deshacer.",
            "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (new p_integrador.dao.BitacoraDAO().eliminar(idBitacora)) {
                msgOk("Bitácora eliminada.");
                cargarBitacoras();
            } else {
                msgError("Error al eliminar la bitácora.");
            }
        }
    }

    private String bitacoraSeleccionada() {
        int fila = tblBitacoras.getSelectedRow();
        if (fila == -1) { msgError("Selecciona una bitácora en la tabla."); return null; }
        return tblBitacoras.getValueAt(fila, 0).toString();
    }

    private void msgOk(String m)    { lblMensaje.setText(m); lblMensaje.setForeground(VERDE_OK); }
    private void msgError(String m) { lblMensaje.setText(m); lblMensaje.setForeground(ROJO); }
    private void msgInfo(String m)  { lblMensaje.setText(m); lblMensaje.setForeground(new Color(90, 90, 90)); }

    private void base(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(150, 38));
    }
    private void estiloPrimario(JButton b)   { base(b); b.setBackground(AZUL_UDI); b.setForeground(BLANCO); b.setBorder(new EmptyBorder(6,10,6,10)); }
    private void estiloSecundario(JButton b) { base(b); b.setBackground(BLANCO); b.setForeground(AZUL_UDI); b.setBorder(new LineBorder(AZUL_UDI,1)); }
    private void estiloPeligro(JButton b)    { base(b); b.setBackground(BLANCO); b.setForeground(ROJO); b.setBorder(new LineBorder(ROJO,1)); }

    public static void main(String args[]) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        java.awt.EventQueue.invokeLater(() -> new BitacorasForm("TEST").setVisible(true));
    }
}
