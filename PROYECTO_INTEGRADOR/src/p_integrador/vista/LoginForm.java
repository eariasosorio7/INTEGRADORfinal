package p_integrador.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.geom.RoundRectangle2D;

public class LoginForm extends javax.swing.JFrame {

    // Colores institucionales y sugeridos
    private final Color COLOR_FONDO = new Color(219, 240, 255); // Tu color azul claro
    private final Color COLOR_UDI_AZUL = new Color(0, 51, 102);  // Azul institucional UDI
    private final Color COLOR_BLANCO = Color.WHITE;

    public LoginForm() {
        p_integrador.conexion.ConexionDB.reiniciarPerfil(); // perfil de arranque para autenticar
        initComponents();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // Panel Principal con el color de fondo que pediste
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setBackground(COLOR_FONDO);
        panelPrincipal.setLayout(new GridBagLayout());
        setContentPane(panelPrincipal);

        // Contenedor del Formulario (Tarjeta Blanca)
        JPanel card = new JPanel();
        card.setBackground(COLOR_BLANCO);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(30, 40, 30, 40)
        ));

        // --- Componentes ---
        JLabel lblTitulo = new JLabel("BITÁCORA DIGITAL");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(COLOR_UDI_AZUL);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("UDI - Universitario");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(Color.GRAY);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblUser = new JLabel("Correo electrónico:");
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtCorreo = new JTextField();
        txtCorreo.setMaximumSize(new Dimension(300, 35));

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtContrasena = new JPasswordField();
        txtContrasena.setMaximumSize(new Dimension(300, 35));

        btnLogin = new JButton("INICIAR SESIÓN");
        btnLogin.setBackground(COLOR_UDI_AZUL);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setMaximumSize(new Dimension(300, 40));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblError = new JLabel(" ");
        lblError.setForeground(Color.RED);
        lblError.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Agregar al card (verticalmente) ---
        card.add(lblTitulo);
        card.add(lblSubtitulo);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        card.add(lblUser);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(txtCorreo);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(lblPass);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(txtContrasena);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(btnLogin);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(lblError);

        // Agregar card al panel principal
        panelPrincipal.add(card);

        // Eventos
        btnLogin.addActionListener(this::btnLoginActionPerformed);
        
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login - Sistema Bitácora Digital UDI");
        setSize(1220, 675);
    }

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {
        String correo = txtCorreo.getText().trim();
        String clave = new String(txtContrasena.getPassword()).trim();

        if (correo.isEmpty() || clave.isEmpty()) {
            lblError.setText("Complete todos los campos.");
            return;
        }

        try (java.sql.Connection con = p_integrador.conexion.ConexionDB.conectar()) {
            String sql = "SELECT * FROM usuarios WHERE correo=? AND contrasena=?";
            java.sql.PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, correo);
            ps.setString(2, clave);
            java.sql.ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                p_integrador.modelo.Usuario u = new p_integrador.modelo.Usuario(
                    rs.getLong("id_usuario"),
                    rs.getString("nombre1"),
                    rs.getString("nombre2"),
                    rs.getString("apellido1"),
                    rs.getString("apellido2"),
                    rs.getString("correo"),
                    null,
                    rs.getString("rol"),
                    rs.getString("estado"),
                    rs.getString("grupo")
                );
                p_integrador.modelo.SesionActiva.iniciarSesion(u);
                // A partir de aquí, las conexiones usan el usuario de BD del rol
                p_integrador.conexion.ConexionDB.configurarPorRol(u.getRol());
                JOptionPane.showMessageDialog(this, "Bienvenido, " + u.getNombre1());
                
                String rol = u.getRol();
                if (rol.equals("ADMIN") || rol.equals("DOCENTE")) {
                    new p_integrador.vista.admin.MenuAdminForm().setVisible(true);
                } else if (rol.equals("ESTUDIANTE")) {
                    new p_integrador.vista.estudiante.MenuEstudianteForm().setVisible(true);
                } else if (rol.equals("ASESOR")) {
                    new p_integrador.vista.asesor.MenuAsesorForm().setVisible(true);
                } else if (rol.equals("ADMIN") || rol.equals("DOCENTE")) {
                    new p_integrador.vista.admin.MenuAdminForm().setVisible(true);
}
                this.dispose();
            } else {
                lblError.setText("Correo o contraseña incorrectos.");
            }
        } catch (java.sql.SQLException e) {
            lblError.setText("Error de conexión.");
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        
        java.awt.EventQueue.invokeLater(() -> new LoginForm().setVisible(true));
    }

    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel lblError;
    private javax.swing.JPasswordField txtContrasena;
    private javax.swing.JTextField txtCorreo;
}