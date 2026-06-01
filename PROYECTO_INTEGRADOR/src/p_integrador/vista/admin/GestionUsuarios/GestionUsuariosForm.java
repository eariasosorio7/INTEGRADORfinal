package p_integrador.vista.admin.GestionUsuarios;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import p_integrador.modelo.*;
import p_integrador.servicio.*;
import p_integrador.vista.admin.panelMenu;

/**
 * Ventana principal unificada para la Gestión de Usuarios.
 *
 * Implementa arquitectura de CAPAS: esta vista solo interactúa con
 * las clases de Servicio (UsuarioService, GrupoService), nunca con DAOs.
 *
 * Organiza todo en un JTabbedPane con 4 pestañas:
 *   - Registrar:  Formulario para crear nuevos usuarios.
 *   - Modificar:  Tabla editable para cambiar rol, grupo o estado.
 *   - Lista:      Vista de solo lectura de todos los usuarios.
 *   - Grupos:     Creación y eliminación de grupos académicos.
 */
public class GestionUsuariosForm extends javax.swing.JFrame {

    // ─── Paleta de colores institucional ──────────────────────────────
    private static final Color AZUL_UDI    = new Color(0, 51, 102);
    private static final Color FONDO_CLARO = new Color(219, 240, 255);
    private static final Color BLANCO      = Color.WHITE;
    private static final Color VERDE_OK    = new Color(0, 130, 0);
    private static final Color ROJO_ERR    = new Color(180, 50, 50);

    // ─── Servicios (CAPA DE SERVICIO) ─────────────────────────────────
    private final UsuarioService usuarioService = new UsuarioService();
    private final GrupoService   grupoService   = new GrupoService();

    // ─── Componentes: Tab REGISTRAR ───────────────────────────────────
    private JTextField txtCedula, txtNombre1, txtNombre2, txtApellido1, txtApellido2, txtCorreoGen;
    private JComboBox<String> cmbRol, cmbGrupoReg;
    private JLabel lblGrupoReg, lblMsgReg;
    private JButton btnGuardarReg;

    // ─── Componentes: Tab MODIFICAR ───────────────────────────────────
    private JTextField txtBuscarCed;
    private JTable tblModificar;
    private final java.util.Map<Long, p_integrador.modelo.Usuario> originalesModificar = new java.util.HashMap<>();
    private JLabel lblMsgMod;
    private JButton btnBuscar, btnGuardarCambios;

    // ─── Componentes: Tab LISTA ───────────────────────────────────────
    private JTable tblLista;
    private JTextField txtFiltroCedula, txtFiltroNombre;
    private JComboBox<String> cmbFiltroRol;

    // ─── Componentes: Tab GRUPOS ──────────────────────────────────────
    private JComboBox<String> cmbSemestre, cmbIdent;
    private JTextField txtCodigoGrupo;
    private JTable tblGrupos;
    private JLabel lblMsgGrupo;

    // ─── Constructor ──────────────────────────────────────────────────
    public GestionUsuariosForm() {
        setTitle("Gestión de Usuarios – UDI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 675);
        setLocationRelativeTo(null);

        initAllComponents();
        buildUI();

        // Carga inicial de datos
        cargarGruposEnCombo(cmbGrupoReg);
        cargarTablaModificar(null);
        cargarTablaLista(null, null, "TODOS");
        cargarTablaGrupos();
    }

    // ══════════════════════════════════════════════════════════════════
    //  INICIALIZACIÓN DE COMPONENTES
    // ══════════════════════════════════════════════════════════════════
    private void initAllComponents() {

        // Tab Registrar
        txtCedula     = crearCampo(); txtNombre1  = crearCampo();
        txtNombre2    = crearCampo(); txtApellido1 = crearCampo();
        txtApellido2  = crearCampo();
        txtCorreoGen  = crearCampo(); txtCorreoGen.setEditable(false);
        txtCorreoGen.setBackground(new Color(245, 245, 245));

        cmbRol      = new JComboBox<>(new String[]{"ESTUDIANTE","DOCENTE","ASESOR","ADMIN"});
        cmbGrupoReg = new JComboBox<>();
        lblGrupoReg = new JLabel("Grupo:"); lblGrupoReg.setVisible(false);
        cmbGrupoReg.setVisible(false);
        lblMsgReg   = new JLabel(" ");
        btnGuardarReg = new JButton("GUARDAR USUARIO");

        java.awt.event.KeyAdapter autoCorreo = new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { generarCorreoPreview(); }
        };
        txtCedula.addKeyListener(autoCorreo);
        txtNombre1.addKeyListener(autoCorreo);
        txtApellido1.addKeyListener(autoCorreo);

        cmbRol.addActionListener(e -> {
            boolean esEst = "ESTUDIANTE".equals(cmbRol.getSelectedItem());
            lblGrupoReg.setVisible(esEst);
            cmbGrupoReg.setVisible(esEst);
        });
        btnGuardarReg.addActionListener(e -> guardarUsuario());

        // Tab Modificar
        txtBuscarCed   = crearCampo();
        tblModificar   = new JTable();
        lblMsgMod      = new JLabel(" ");
        btnBuscar      = new JButton("Buscar");
        btnGuardarCambios = new JButton("GUARDAR CAMBIOS");
        btnBuscar.addActionListener(e -> cargarTablaModificar(txtBuscarCed.getText().trim()));
        btnGuardarCambios.addActionListener(e -> guardarCambiosModificar());

        // Tab Lista (con filtros)
        tblLista        = new JTable();
        txtFiltroCedula = crearCampo(); txtFiltroCedula.setPreferredSize(new Dimension(140, 30));
        txtFiltroNombre = crearCampo(); txtFiltroNombre.setPreferredSize(new Dimension(160, 30));
        cmbFiltroRol    = new JComboBox<>(new String[]{"TODOS","ESTUDIANTE","DOCENTE","ASESOR","ADMIN"});

        // Tab Grupos
        cmbSemestre    = new JComboBox<>(new String[]{"1","2","3","4","5","6","7","8"});
        cmbIdent       = new JComboBox<>(new String[]{"A","B","C","D"});
        txtCodigoGrupo = crearCampo(); txtCodigoGrupo.setEditable(false);
        txtCodigoGrupo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtCodigoGrupo.setForeground(AZUL_UDI);
        tblGrupos      = new JTable();
        lblMsgGrupo    = new JLabel(" ");

        cmbSemestre.addActionListener(e -> actualizarCodigoGrupo());
        cmbIdent.addActionListener(e -> actualizarCodigoGrupo());
        actualizarCodigoGrupo();
    }

    private JTextField crearCampo() {
        JTextField f = new JTextField();
        f.setPreferredSize(new Dimension(200, 32));
        return f;
    }

    // ══════════════════════════════════════════════════════════════════
    //  CONSTRUCCIÓN DE LA INTERFAZ
    // ══════════════════════════════════════════════════════════════════
    private void buildUI() {
        getContentPane().setBackground(FONDO_CLARO);
        getContentPane().setLayout(new BorderLayout());

        // Menú lateral (sidebar)
        getContentPane().add(new panelMenu(), BorderLayout.WEST);

        // Área derecha
        JPanel pnlDerecho = new JPanel(new BorderLayout());
        pnlDerecho.setOpaque(false);
        pnlDerecho.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Título
        JLabel lblTitulo = new JLabel("GESTIÓN DE USUARIOS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(AZUL_UDI);
        lblTitulo.setBorder(new EmptyBorder(0, 0, 12, 0));
        pnlDerecho.add(lblTitulo, BorderLayout.NORTH);

        // Pestañas
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("  ＋ Registrar  ", buildTabRegistrar());
        tabs.addTab("  Modificar   ", buildTabModificar());
        tabs.addTab("  Lista        ", buildTabLista());
        tabs.addTab("  ⊞ Grupos      ", buildTabGrupos());

        // Actualizar datos al cambiar de pestaña
        tabs.addChangeListener(e -> {
            switch (tabs.getSelectedIndex()) {
                case 1: cargarTablaModificar(null); break;
                case 2: cargarTablaLista(null, null, "TODOS"); break;
                case 3: cargarTablaGrupos(); cargarGruposEnCombo(cmbGrupoReg); break;
            }
        });

        pnlDerecho.add(tabs, BorderLayout.CENTER);
        getContentPane().add(pnlDerecho, BorderLayout.CENTER);
    }

    // ─── Tab 1: REGISTRAR ─────────────────────────────────────────────
    private JPanel buildTabRegistrar() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setOpaque(false);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BLANCO);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 210, 225), 1),
            new EmptyBorder(30, 50, 30, 50)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 10, 5, 10);

        // Título del formulario
        JLabel titulo = new JLabel("REGISTRAR NUEVO USUARIO");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(AZUL_UDI);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 4;
        g.insets = new Insets(0, 10, 20, 10);
        card.add(titulo, g);

        // Instrucción rápida
        JLabel hint = new JLabel("( * ) Campo obligatorio  |  La contraseña inicial será la cédula del usuario");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        g.gridy = 1; card.add(hint, g);

        // Campos del formulario – 4 columnas (etiqueta | campo | etiqueta | campo)
        g.gridwidth = 1; g.insets = new Insets(6, 10, 6, 10);

        agregarFila(card, g, 2, "Cédula: *", txtCedula, "Rol: *", cmbRol);
        agregarFila(card, g, 3, "Primer Nombre: *", txtNombre1, "Segundo Nombre:", txtNombre2);
        agregarFila(card, g, 4, "Primer Apellido: *", txtApellido1, "Segundo Apellido:", txtApellido2);
        agregarFila(card, g, 5, "Correo (auto-generado):", txtCorreoGen, null, null);

        // Fila condicional: grupo (solo visible si rol = ESTUDIANTE)
        g.gridy = 5; g.gridx = 2; card.add(lblGrupoReg, g);
        g.gridx = 3; card.add(cmbGrupoReg, g);

        // Botón guardar
        g.gridy = 6; g.gridx = 0; g.gridwidth = 4;
        g.insets = new Insets(22, 10, 5, 10);
        estilizarBotonPrincipal(btnGuardarReg);
        btnGuardarReg.setPreferredSize(new Dimension(0, 44));
        card.add(btnGuardarReg, g);

        // Mensaje de estado
        g.gridy = 7; g.insets = new Insets(5, 10, 0, 10);
        lblMsgReg.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblMsgReg, g);

        pnl.add(card);
        return pnl;
    }

    /** Utilidad para agregar una fila de 2 campos al formulario de registro. */
    private void agregarFila(JPanel p, GridBagConstraints g, int fila,
                             String lbl1, JComponent comp1,
                             String lbl2, JComponent comp2) {
        g.gridy = fila;
        g.gridx = 0; p.add(new JLabel(lbl1), g);
        g.gridx = 1; p.add(comp1, g);
        if (lbl2 != null) {
            g.gridx = 2; p.add(new JLabel(lbl2), g);
            g.gridx = 3; p.add(comp2, g);
        }
    }

    // ─── Tab 2: MODIFICAR ─────────────────────────────────────────────
    private JPanel buildTabModificar() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setOpaque(false);
        pnl.setBorder(new EmptyBorder(12, 0, 0, 0));

        // Barra de búsqueda
        JPanel pnlBusq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        pnlBusq.setOpaque(false);
        pnlBusq.add(new JLabel("Filtrar por Cédula:"));
        txtBuscarCed.setPreferredSize(new Dimension(200, 30));
        pnlBusq.add(txtBuscarCed);
        estilizarBotonSecundario(btnBuscar);
        pnlBusq.add(btnBuscar);

        JLabel instruccion = new JLabel(
            "Haz doble clic en las celdas de Rol, Grupo o Estado para editarlas, luego guarda.");
        instruccion.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        instruccion.setForeground(new Color(80, 80, 80));
        pnlBusq.add(instruccion);
        pnl.add(pnlBusq, BorderLayout.NORTH);

        // Tabla
        tblModificar.setRowHeight(28);
        tblModificar.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnl.add(new JScrollPane(tblModificar), BorderLayout.CENTER);

        // Acciones inferiores
        JPanel pnlAcc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        pnlAcc.setOpaque(false);

        JButton btnEliminar = new JButton("ELIMINAR SELECCIONADO");
        estilizarBotonPeligro(btnEliminar);
        btnEliminar.addActionListener(e -> eliminarUsuarioSeleccionado());
        pnlAcc.add(btnEliminar);

        estilizarBotonPrincipal(btnGuardarCambios);
        pnlAcc.add(btnGuardarCambios);
        pnlAcc.add(lblMsgMod);
        pnl.add(pnlAcc, BorderLayout.SOUTH);

        return pnl;
    }

    // ─── Tab 3: LISTA ─────────────────────────────────────────────────
    private JPanel buildTabLista() {
        JPanel pnl = new JPanel(new BorderLayout(0, 8));
        pnl.setOpaque(false);
        pnl.setBorder(new EmptyBorder(12, 0, 0, 0));

        // Filtros rápidos
        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        pnlFiltros.setOpaque(false);
        pnlFiltros.add(new JLabel("Cédula:"));
        pnlFiltros.add(txtFiltroCedula);
        pnlFiltros.add(new JLabel("Nombre:"));
        pnlFiltros.add(txtFiltroNombre);
        pnlFiltros.add(new JLabel("Rol:"));
        pnlFiltros.add(cmbFiltroRol);
        JButton btnFiltrar = new JButton("Filtrar");
        estilizarBotonSecundario(btnFiltrar);
        btnFiltrar.addActionListener(e -> cargarTablaLista(
            txtFiltroCedula.getText().trim(),
            txtFiltroNombre.getText().trim(),
            cmbFiltroRol.getSelectedItem().toString()
        ));
        pnlFiltros.add(btnFiltrar);

        JButton btnLimpiar = new JButton("Limpiar");
        estilizarBotonSecundario(btnLimpiar);
        btnLimpiar.addActionListener(e -> {
            txtFiltroCedula.setText(""); txtFiltroNombre.setText("");
            cmbFiltroRol.setSelectedIndex(0);
            cargarTablaLista(null, null, "TODOS");
        });
        pnlFiltros.add(btnLimpiar);
        pnl.add(pnlFiltros, BorderLayout.NORTH);

        // Tabla solo lectura
        tblLista.setRowHeight(28);
        tblLista.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnl.add(new JScrollPane(tblLista), BorderLayout.CENTER);

        return pnl;
    }

    // ─── Tab 4: GRUPOS ────────────────────────────────────────────────
    private JPanel buildTabGrupos() {
        JPanel pnl = new JPanel(new BorderLayout(25, 0));
        pnl.setOpaque(false);
        pnl.setBorder(new EmptyBorder(12, 0, 0, 0));

        // Panel de formulario izquierdo
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(BLANCO);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(210, 220, 230), 1),
            new EmptyBorder(30, 25, 30, 25)
        ));
        pnlForm.setPreferredSize(new Dimension(320, 0));

        GridBagConstraints f = new GridBagConstraints();
        f.fill = GridBagConstraints.HORIZONTAL;
        f.gridx = 0; f.weightx = 1.0;
        f.insets = new Insets(8, 0, 8, 0);

        JLabel lblTit = new JLabel("GESTIONAR GRUPOS");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(AZUL_UDI);
        f.gridy = 0; pnlForm.add(lblTit, f);

        JLabel sub = new JLabel("Selecciona semestre e identificador");
        sub.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        sub.setForeground(Color.GRAY);
        f.gridy = 1; f.insets = new Insets(0, 0, 12, 0);
        pnlForm.add(sub, f);

        f.insets = new Insets(6, 0, 6, 0);
        f.gridy = 2; pnlForm.add(new JLabel("Semestre:"), f);
        f.gridy = 3; cmbSemestre.setPreferredSize(new Dimension(0, 32)); pnlForm.add(cmbSemestre, f);
        f.gridy = 4; pnlForm.add(new JLabel("Identificador (LEI-):"), f);
        f.gridy = 5; cmbIdent.setPreferredSize(new Dimension(0, 32)); pnlForm.add(cmbIdent, f);
        f.gridy = 6; pnlForm.add(new JLabel("Código generado:"), f);
        f.gridy = 7; pnlForm.add(txtCodigoGrupo, f);

        JButton btnCrear = new JButton("CREAR GRUPO");
        estilizarBotonPrincipal(btnCrear);
        btnCrear.setPreferredSize(new Dimension(0, 40));
        btnCrear.addActionListener(e -> crearGrupo());
        f.gridy = 8; f.insets = new Insets(22, 0, 8, 0);
        pnlForm.add(btnCrear, f);

        JButton btnEliminar = new JButton("ELIMINAR SELECCIONADO");
        estilizarBotonPeligro(btnEliminar);
        btnEliminar.setPreferredSize(new Dimension(0, 40));
        btnEliminar.addActionListener(e -> eliminarGrupo());
        f.gridy = 9; f.insets = new Insets(0, 0, 8, 0);
        pnlForm.add(btnEliminar, f);

        f.gridy = 10; f.weighty = 1.0;
        lblMsgGrupo.setVerticalAlignment(SwingConstants.TOP);
        pnlForm.add(lblMsgGrupo, f);

        pnl.add(pnlForm, BorderLayout.WEST);

        // Panel de tabla derecho
        JPanel pnlTabla = new JPanel(new BorderLayout(0, 8));
        pnlTabla.setOpaque(false);
        JLabel lblTabla = new JLabel("GRUPOS REGISTRADOS EN EL SISTEMA");
        lblTabla.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTabla.setForeground(AZUL_UDI);
        tblGrupos.setRowHeight(30);
        tblGrupos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlTabla.add(lblTabla, BorderLayout.NORTH);
        pnlTabla.add(new JScrollPane(tblGrupos), BorderLayout.CENTER);
        pnl.add(pnlTabla, BorderLayout.CENTER);

        return pnl;
    }

    // ══════════════════════════════════════════════════════════════════
    //  LÓGICA: TAB REGISTRAR
    // ══════════════════════════════════════════════════════════════════
    private void generarCorreoPreview() {
        String n1  = txtNombre1.getText().trim();
        String a1  = txtApellido1.getText().trim();
        String ced = txtCedula.getText().trim();
        if (!n1.isEmpty() && !a1.isEmpty() && !ced.isEmpty()) {
            try {
                txtCorreoGen.setText(Usuario.generarCorreo(n1, a1, Long.parseLong(ced)));
            } catch (NumberFormatException ex) {
                txtCorreoGen.setText("");
            }
        } else {
            txtCorreoGen.setText("");
        }
    }

    private void guardarUsuario() {
        String cedStr = txtCedula.getText().trim();
        String n1     = txtNombre1.getText().trim();
        String a1     = txtApellido1.getText().trim();

        if (cedStr.isEmpty() || n1.isEmpty() || a1.isEmpty()) {
            mostrarMsg(lblMsgReg, "Cédula, Primer Nombre y Primer Apellido son obligatorios.", false);
            return;
        }
        try {
            long cedula = Long.parseLong(cedStr);
            String rol  = cmbRol.getSelectedItem().toString();
            String grupo = "ESTUDIANTE".equals(rol) && cmbGrupoReg.getSelectedItem() != null
                ? cmbGrupoReg.getSelectedItem().toString() : "NINGUNO";

            String error = usuarioService.registrar(
                cedula, n1, txtNombre2.getText().trim(),
                a1, txtApellido2.getText().trim(), rol, grupo
            );

            if (error == null) {
                mostrarMsg(lblMsgReg, "Usuario registrado con éxito. La contraseña inicial es la cédula.", true);
                limpiarRegistro();
            } else {
                mostrarMsg(lblMsgReg, "" + error, false);
            }
        } catch (NumberFormatException ex) {
            mostrarMsg(lblMsgReg, "La cédula solo puede contener números.", false);
        }
    }

    private void limpiarRegistro() {
        txtCedula.setText(""); txtNombre1.setText(""); txtNombre2.setText("");
        txtApellido1.setText(""); txtApellido2.setText(""); txtCorreoGen.setText("");
        cmbRol.setSelectedIndex(0);
    }

    // ══════════════════════════════════════════════════════════════════
    //  LÓGICA: TAB MODIFICAR
    // ══════════════════════════════════════════════════════════════════
    private void cargarTablaModificar(String filtroCedula) {
        List<Usuario> lista;
        if (filtroCedula != null && !filtroCedula.isEmpty()) {
            lista = new java.util.ArrayList<>();
            try {
                Usuario u = usuarioService.buscarPorId(Long.parseLong(filtroCedula));
                if (u != null) lista.add(u);
            } catch (NumberFormatException ex) {
                lista = usuarioService.listarTodos();
            }
        } else {
            lista = usuarioService.listarTodos();
        }

        String[] cols = {"Cédula", "Nombre Completo", "Correo", "Rol", "Grupo", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return c >= 3; }
        };
        for (Usuario u : lista) {
            modelo.addRow(new Object[]{
                u.getIdUsuario(),
                u.getNombre1() + " " + u.getApellido1(),
                u.getCorreo(), u.getRol(), u.getGrupo(), u.getEstado()
            });
        }
        // Guardar copia original para detectar qué filas cambian realmente
        originalesModificar.clear();
        for (Usuario u : lista) originalesModificar.put(u.getIdUsuario(), u);
        tblModificar.setModel(modelo);
        tblModificar.setRowHeight(28);
        configurarEditoresTabla(tblModificar);
    }

    private void configurarEditoresTabla(JTable tabla) {
        // Columna Rol
        tabla.getColumnModel().getColumn(3).setCellEditor(
            new DefaultCellEditor(new JComboBox<>(new String[]{"ESTUDIANTE","DOCENTE","ASESOR","ADMIN"}))
        );
        // Columna Grupo
        JComboBox<String> cmbG = new JComboBox<>();
        cmbG.addItem("NINGUNO");
        grupoService.listarTodos().forEach(g -> cmbG.addItem(g.getCodigo()));
        tabla.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(cmbG));
        // Columna Estado
        tabla.getColumnModel().getColumn(5).setCellEditor(
            new DefaultCellEditor(new JComboBox<>(new String[]{"ACTIVO","INACTIVO"}))
        );
    }

    private void guardarCambiosModificar() {
        int filas = tblModificar.getRowCount();
        if (filas == 0) { mostrarMsg(lblMsgMod, "No hay usuarios para modificar.", false); return; }

        // Detectar SOLO las filas que realmente cambiaron (rol, grupo o estado)
        java.util.List<Usuario> cambiados = new java.util.ArrayList<>();
        for (int i = 0; i < filas; i++) {
            long ced     = Long.parseLong(tblModificar.getValueAt(i, 0).toString());
            String rol   = tblModificar.getValueAt(i, 3).toString();
            String grupo = tblModificar.getValueAt(i, 4).toString();
            String estado= tblModificar.getValueAt(i, 5).toString();

            Usuario orig = originalesModificar.get(ced);
            if (orig == null) continue;

            boolean cambio = !rol.equals(orig.getRol())
                          || !grupo.equals(orig.getGrupo())
                          || !estado.equals(orig.getEstado());
            if (cambio) {
                Usuario u = new Usuario(
                    orig.getIdUsuario(), orig.getNombre1(), orig.getNombre2(),
                    orig.getApellido1(), orig.getApellido2(), orig.getCorreo(),
                    orig.getContrasena(), rol, estado, grupo
                );
                cambiados.add(u);
            }
        }

        if (cambiados.isEmpty()) {
            mostrarMsg(lblMsgMod, "No hay cambios para guardar.", false);
            return;
        }

        if (JOptionPane.showConfirmDialog(this,
                "Se modificará(n) " + cambiados.size() + " usuario(s). ¿Continuar?",
                "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        String error = usuarioService.actualizarVarios(cambiados);
        if (error == null) {
            mostrarMsg(lblMsgMod, cambiados.size() + " usuario(s) actualizado(s) correctamente.", true);
            cargarTablaModificar(null); // recarga y refresca originales
        } else {
            mostrarMsg(lblMsgMod, error, false);
        }
    }

    private void eliminarUsuarioSeleccionado() {
        int fila = tblModificar.getSelectedRow();
        if (fila == -1) {
            mostrarMsg(lblMsgMod, "Selecciona un usuario de la tabla primero.", false);
            return;
        }
        long cedula = Long.parseLong(tblModificar.getValueAt(fila, 0).toString());
        String nombre = tblModificar.getValueAt(fila, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Eliminar al usuario " + nombre + " (cédula: " + cedula + ")?\n" +
            "Esta acción también eliminará sus registros asociados.",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String err = usuarioService.eliminar(cedula);
            if (err == null) {
                mostrarMsg(lblMsgMod, "Usuario eliminado.", true);
                cargarTablaModificar(null);
            } else {
                mostrarMsg(lblMsgMod, "" + err, false);
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  LÓGICA: TAB LISTA
    // ══════════════════════════════════════════════════════════════════
    private void cargarTablaLista(String filtroCed, String filtroNombre, String filtroRol) {
        List<Usuario> source = usuarioService.listarTodos();
        String[] cols = {"Cédula", "Nombre Completo", "Correo", "Rol", "Grupo", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Usuario u : source) {
            boolean matchCed  = (filtroCed   == null || filtroCed.isEmpty()   || String.valueOf(u.getIdUsuario()).contains(filtroCed));
            boolean matchNom  = (filtroNombre == null || filtroNombre.isEmpty() || (u.getNombre1() + " " + u.getApellido1()).toUpperCase().contains(filtroNombre.toUpperCase()));
            boolean matchRol  = ("TODOS".equals(filtroRol) || u.getRol().equals(filtroRol));
            if (matchCed && matchNom && matchRol) {
                modelo.addRow(new Object[]{
                    u.getIdUsuario(), u.getNombre1() + " " + u.getApellido1(),
                    u.getCorreo(), u.getRol(), u.getGrupo(), u.getEstado()
                });
            }
        }
        tblLista.setModel(modelo);
        tblLista.setRowHeight(28);
        tblLista.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    // ══════════════════════════════════════════════════════════════════
    //  LÓGICA: TAB GRUPOS
    // ══════════════════════════════════════════════════════════════════
    private void actualizarCodigoGrupo() {
        txtCodigoGrupo.setText(cmbSemestre.getSelectedItem() + "LEI-" + cmbIdent.getSelectedItem());
    }

    private void crearGrupo() {
        String codigo = txtCodigoGrupo.getText().trim();
        int semestre  = Integer.parseInt(cmbSemestre.getSelectedItem().toString());
        String err    = grupoService.crear(codigo, semestre);
        if (err == null) {
            mostrarMsg(lblMsgGrupo, "Grupo " + codigo + " creado.", true);
            cargarTablaGrupos();
            cargarGruposEnCombo(cmbGrupoReg);
        } else {
            mostrarMsg(lblMsgGrupo, "" + err, false);
        }
    }

    private void eliminarGrupo() {
        int fila = tblGrupos.getSelectedRow();
        if (fila == -1) {
            mostrarMsg(lblMsgGrupo, "Selecciona un grupo de la tabla.", false);
            return;
        }
        String codigo = tblGrupos.getValueAt(fila, 0).toString();

        if (JOptionPane.showConfirmDialog(this,
                "¿Eliminar el grupo " + codigo + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            String err = grupoService.eliminar(codigo);
            if (err == null) {
                mostrarMsg(lblMsgGrupo, "Grupo eliminado.", true);
                cargarTablaGrupos();
                cargarGruposEnCombo(cmbGrupoReg);
            } else {
                mostrarMsg(lblMsgGrupo, "" + err, false);
            }
        }
    }

    private void cargarTablaGrupos() {
        String[] cols = {"Código de Grupo", "Semestre"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        grupoService.listarTodos().forEach(g ->
            modelo.addRow(new Object[]{g.getCodigo(), g.getSemestre()})
        );
        tblGrupos.setModel(modelo);
    }

    private void cargarGruposEnCombo(JComboBox<String> cmb) {
        cmb.removeAllItems();
        grupoService.listarTodos().forEach(g -> cmb.addItem(g.getCodigo()));
    }

    // ══════════════════════════════════════════════════════════════════
    //  UTILIDADES DE ESTILO
    // ══════════════════════════════════════════════════════════════════
    private void mostrarMsg(JLabel lbl, String msg, boolean ok) {
        lbl.setText(msg);
        lbl.setForeground(ok ? VERDE_OK : ROJO_ERR);
    }

    private void estilizarBotonPrincipal(JButton btn) {
        btn.setBackground(AZUL_UDI); btn.setForeground(BLANCO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(180, 36));
        btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void estilizarBotonSecundario(JButton btn) {
        btn.setBackground(BLANCO); btn.setForeground(AZUL_UDI);
        btn.setBorder(new LineBorder(AZUL_UDI, 1));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setPreferredSize(new Dimension(100, 30));
        btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void estilizarBotonPeligro(JButton btn) {
        btn.setBackground(ROJO_ERR); btn.setForeground(BLANCO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(200, 36));
        btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ══════════════════════════════════════════════════════════════════
    //  MAIN
    // ══════════════════════════════════════════════════════════════════
    public static void main(String args[]) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        java.awt.EventQueue.invokeLater(() -> new GestionUsuariosForm().setVisible(true));
    }
}
