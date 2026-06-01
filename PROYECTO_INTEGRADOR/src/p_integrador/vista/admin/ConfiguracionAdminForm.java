package p_integrador.vista.admin;

public class ConfiguracionAdminForm extends javax.swing.JFrame {

    public ConfiguracionAdminForm() {
        initComponents();
        setSize(1220, 675);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new java.awt.Color(219, 240, 255));
        getContentPane().setLayout(null);

        p_integrador.vista.admin.panelMenu menu = new p_integrador.vista.admin.panelMenu();
        menu.setBounds(0, 0, 200, 675);
        getContentPane().add(menu);

        javax.swing.JLabel lblTitulo = new javax.swing.JLabel("CONFIGURACIÓN");
        lblTitulo.setFont(new java.awt.Font("Leelawadee UI Semilight", java.awt.Font.BOLD, 18));
        lblTitulo.setBounds(220, 20, 400, 35);
        getContentPane().add(lblTitulo);

        javax.swing.JLabel lblContrasena = new javax.swing.JLabel("Cambiar contraseña:");
        lblContrasena.setBounds(220, 80, 200, 25);
        getContentPane().add(lblContrasena);

        javax.swing.JLabel lblActual = new javax.swing.JLabel("Contraseña actual:");
        lblActual.setBounds(220, 115, 180, 25);
        getContentPane().add(lblActual);

        txtActual = new javax.swing.JPasswordField();
        txtActual.setBounds(410, 115, 200, 25);
        getContentPane().add(txtActual);

        javax.swing.JLabel lblNueva = new javax.swing.JLabel("Nueva contraseña:");
        lblNueva.setBounds(220, 150, 180, 25);
        getContentPane().add(lblNueva);

        txtNueva = new javax.swing.JPasswordField();
        txtNueva.setBounds(410, 150, 200, 25);
        getContentPane().add(txtNueva);

        javax.swing.JLabel lblConfirmar = new javax.swing.JLabel("Confirmar contraseña:");
        lblConfirmar.setBounds(220, 185, 180, 25);
        getContentPane().add(lblConfirmar);

        txtConfirmar = new javax.swing.JPasswordField();
        txtConfirmar.setBounds(410, 185, 200, 25);
        getContentPane().add(txtConfirmar);

        javax.swing.JButton btnCambiar = new javax.swing.JButton("CAMBIAR CONTRASEÑA");
        btnCambiar.setBounds(220, 225, 200, 35);
        btnCambiar.addActionListener(e -> cambiarContrasena());
        getContentPane().add(btnCambiar);

        lblMensaje = new javax.swing.JLabel();
        lblMensaje.setBounds(220, 270, 700, 25);
        getContentPane().add(lblMensaje);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    }

    private void cambiarContrasena() {
        String actual = new String(txtActual.getPassword()).trim();
        String nueva = new String(txtNueva.getPassword()).trim();
        String confirmar = new String(txtConfirmar.getPassword()).trim();

        if (actual.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
            lblMensaje.setText("Complete todos los campos."); return;
        }
        if (!nueva.equals(confirmar)) {
            lblMensaje.setText("Las contraseñas no coinciden."); return;
        }

        p_integrador.modelo.Usuario u = p_integrador.modelo.SesionActiva.getUsuario();
        if (!u.getContrasena().equals(actual)) {
            lblMensaje.setText("Contraseña actual incorrecta."); return;
        }

        u.setContrasena(nueva);
        p_integrador.dao.UsuarioDAO dao = new p_integrador.dao.UsuarioDAO();
        if (dao.cambiarContrasena(u.getIdUsuario(), nueva)) {
            lblMensaje.setText("Contraseña cambiada correctamente.");
            txtActual.setText(""); txtNueva.setText(""); txtConfirmar.setText("");
        } else {
            lblMensaje.setText("Error al cambiar contraseña.");
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new ConfiguracionAdminForm().setVisible(true));
    }

    private javax.swing.JPasswordField txtActual, txtNueva, txtConfirmar;
    private javax.swing.JLabel lblMensaje;
}