package p_integrador.servicio;

import p_integrador.dao.UsuarioDAO;
import p_integrador.modelo.Usuario;
import java.util.List;

/**
 * Capa de Servicio para la gestión de Usuarios.
 * Centraliza toda la lógica de negocio y validaciones,
 * separándola de las vistas y del acceso a datos (DAO).
 *
 * Las vistas SOLO deben llamar métodos de esta clase,
 * nunca instanciar UsuarioDAO directamente.
 */
public class UsuarioService {

    private final UsuarioDAO dao = new UsuarioDAO();

    /**
     * Registra un nuevo usuario en el sistema.
     * Valida campos obligatorios y unicidad de cédula.
     *
     * @return null si fue exitoso, o mensaje de error si falló.
     */
    public String registrar(long cedula, String nombre1, String nombre2,
                            String apellido1, String apellido2,
                            String rol, String grupo) {
        if (nombre1 == null || nombre1.trim().isEmpty())
            return "El primer nombre es obligatorio.";
        if (apellido1 == null || apellido1.trim().isEmpty())
            return "El primer apellido es obligatorio.";
        if (cedula <= 0)
            return "La cédula debe ser un número positivo.";
        if (dao.buscarPorId(cedula) != null)
            return "La cédula " + cedula + " ya está registrada en el sistema.";

        String correo = Usuario.generarCorreo(nombre1.trim(), apellido1.trim(), cedula);
        String contrasenaInicial = String.valueOf(cedula); // Contraseña inicial = cédula

        Usuario u = new Usuario(
            cedula,
            nombre1.trim().toUpperCase(),
            (nombre2 != null && !nombre2.trim().isEmpty()) ? nombre2.trim().toUpperCase() : null,
            apellido1.trim().toUpperCase(),
            (apellido2 != null && !apellido2.trim().isEmpty()) ? apellido2.trim().toUpperCase() : null,
            correo,
            contrasenaInicial,
            rol,
            "ACTIVO",
            grupo
        );

        return dao.crear(u) ? null : "Error de base de datos al guardar el usuario.";
    }

    /**
     * Actualiza los datos modificables de un usuario existente.
     *
     * @return null si fue exitoso, o mensaje de error si falló.
     */
    public String actualizar(Usuario u) {
        if (u == null) return "Usuario inválido.";
        return dao.actualizar(u) ? null : "Error al actualizar el usuario en la base de datos.";
    }

    /**
     * Actualiza varios usuarios en UNA sola conexión/transacción.
     * Evita abrir una conexión por fila (causa del ORA-12519 en Oracle XE).
     *
     * @return null si fue exitoso, o mensaje de error (con rollback) si falló.
     */
    public String actualizarVarios(java.util.List<Usuario> usuarios) {
        if (usuarios == null || usuarios.isEmpty()) return null;
        try (java.sql.Connection con = p_integrador.conexion.ConexionDB.conectar()) {
            con.setAutoCommit(false);
            try {
                for (Usuario u : usuarios) dao.actualizar(u, con);
                con.commit();
                return null;
            } catch (java.sql.SQLException ex) {
                try { con.rollback(); } catch (java.sql.SQLException ignore) {}
                return "No se pudieron guardar los cambios (se revirtieron): " + ex.getMessage();
            }
        } catch (java.sql.SQLException e) {
            return "Error de conexión con la base de datos: " + e.getMessage();
        }
    }

    /**
     * Elimina un usuario y todos sus datos relacionados.
     *
     * @return null si fue exitoso, o mensaje de error si falló.
     */
    public String eliminar(long idUsuario) {
        if (dao.buscarPorId(idUsuario) == null)
            return "El usuario con cédula " + idUsuario + " no existe.";
        return dao.eliminar(idUsuario) ? null : "Error al eliminar el usuario.";
    }

    /** Retorna todos los usuarios ordenados por apellido. */
    public List<Usuario> listarTodos() {
        return dao.listarTodos();
    }

    /** Busca un usuario por su cédula. Retorna null si no existe. */
    public Usuario buscarPorId(long id) {
        return dao.buscarPorId(id);
    }

    /** Retorna usuarios activos filtrados por rol. */
    public List<Usuario> listarPorRol(String rol) {
        return dao.listarPorRol(rol);
    }

    /** Retorna estudiantes de un grupo específico. */
    public List<Usuario> listarPorGrupo(String grupo) {
        return dao.listarPorGrupo(grupo);
    }

    /**
     * Autentica un usuario por correo y contraseña.
     *
     * @return el Usuario autenticado, o null si las credenciales son incorrectas.
     */
    public Usuario login(String correo, String contrasena) {
        return dao.login(correo, contrasena);
    }

    /**
     * Cambia la contraseña de un usuario.
     *
     * @return null si fue exitoso, o mensaje de error si falló.
     */
    public String cambiarContrasena(long idUsuario, String nuevaContrasena) {
        if (nuevaContrasena == null || nuevaContrasena.trim().length() < 4)
            return "La contraseña debe tener al menos 4 caracteres.";
        return dao.cambiarContrasena(idUsuario, nuevaContrasena) ? null : "Error al cambiar la contraseña.";
    }
}
