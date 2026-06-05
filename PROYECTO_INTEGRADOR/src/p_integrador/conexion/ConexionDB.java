package p_integrador.conexion;

import java.sql.*;

/**
 * Conexión a Oracle con PERFIL SEGÚN ROL.
 *
 * Cada rol del sistema usa su propio usuario de base de datos, con distintos
 * privilegios (principio de mínimo privilegio):
 *
 *   ADMIN      -> USR_ADMIN      (acceso total)
 *   DOCENTE    -> USR_DOCENTE    (gestiona prácticas, bitácoras, visitas, documentos)
 *   ASESOR     -> USR_ASESOR     (lee y evalúa bitácoras/visitas asignadas)
 *   ESTUDIANTE -> USR_ESTUDIANTE (lee lo suyo; escribe sus visitas/respuestas/documentos)
 *
 * Antes de iniciar sesión se usa una cuenta de arranque (PI20261) solo para
 * autenticar (leer la tabla usuarios). Tras el login, LoginForm llama a
 * configurarPorRol(...) y a partir de ahí TODAS las conexiones usan el usuario
 * de BD correspondiente al rol. Al cerrar sesión se vuelve al perfil de arranque.
 *
 * Las tablas pertenecen al esquema PI20261. Para que las consultas con nombres
 * sin prefijo (p. ej. "SELECT * FROM usuarios") funcionen con cualquier usuario,
 * al conectar se ejecuta ALTER SESSION SET CURRENT_SCHEMA = PI20261. Eso solo
 * cambia la resolución de nombres; NO otorga privilegios, así que cada rol sigue
 * limitado a lo que su rol de BD permite. No hacen falta sinónimos.
 */
public class ConexionDB {

    private static final String URL     = "jdbc:oracle:thin:@192.168.254.215:1521:orcl";
    private static final String ESQUEMA = "PI20261"; // dueño de las tablas

    // Perfil actual (por defecto: cuenta de arranque para el login)
    private static String user = "PI20261";
    private static String pass = "PI20261";

    /** Ajusta el usuario de BD según el rol del usuario que inició sesión. */
    public static void configurarPorRol(String rol) {
        if (rol == null) rol = "";
        switch (rol.trim().toUpperCase()) {
            case "ADMIN":
                user = "USR_ADMIN";      pass = "Adm1n2026"; break;
            case "DOCENTE":
                user = "USR_DOCENTE";    pass = "Doce2026";  break;
            case "ASESOR":
                user = "USR_ASESOR";     pass = "Ases2026";  break;
            case "ESTUDIANTE":
                user = "USR_ESTUDIANTE"; pass = "Estu2026";  break;
            default:
                user = "PI20261";        pass = "PI20261";    break;
        }
    }

    /** Vuelve al perfil de arranque (se llama al cerrar sesión / al mostrar el login). */
    public static void reiniciarPerfil() {
        user = "PI20261";
        pass = "PI20261";
    }

    /** Nombre del usuario de BD que se está usando ahora (informativo). */
    public static String getPerfilActual() {
        return user;
    }

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver Oracle no encontrado: " + e.getMessage());
        }
        Connection con = DriverManager.getConnection(URL, user, pass);
        // Resolver nombres sin prefijo apuntando al esquema dueño de las tablas.
        if (!ESQUEMA.equalsIgnoreCase(user)) {
            try (Statement st = con.createStatement()) {
                st.execute("ALTER SESSION SET CURRENT_SCHEMA = " + ESQUEMA);
            } catch (SQLException ignore) {
                // Si no se puede, las consultas usarán el esquema propio del usuario.
            }
        }
        return con;
    }
}
