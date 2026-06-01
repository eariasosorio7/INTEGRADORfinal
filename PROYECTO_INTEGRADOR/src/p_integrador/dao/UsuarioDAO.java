package p_integrador.dao;

import p_integrador.conexion.ConexionDB;
import p_integrador.modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // Crear usuario
    public boolean crear(Usuario u) {
        String sql = "INSERT INTO usuarios VALUES (?,?,?,?,?,?,?,?,?,SYSDATE,?)";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, u.getIdUsuario());
            ps.setString(2, u.getNombre1().toUpperCase());
            ps.setString(3, u.getNombre2() != null ? u.getNombre2().toUpperCase() : null);
            ps.setString(4, u.getApellido1().toUpperCase());
            ps.setString(5, u.getApellido2() != null ? u.getApellido2().toUpperCase() : null);
            ps.setString(6, u.getCorreo());
            ps.setString(7, u.getContrasena());
            ps.setString(8, u.getRol());
            ps.setString(9, u.getEstado());
            ps.setString(10, u.getGrupo());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            return false;
        }
    }

    // Leer todos
    public List<Usuario> listarTodos() {
        
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY apellido1";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Usuario(
                    rs.getLong("id_usuario"),
                    rs.getString("nombre1"),
                    rs.getString("nombre2"),
                    rs.getString("apellido1"),
                    rs.getString("apellido2"),
                    rs.getString("correo"),
                    rs.getString("contrasena"),
                    rs.getString("rol"),
                    rs.getString("estado"),
                    rs.getString("grupo")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
        
    }
    
    public boolean cambiarContrasena(long idUsuario, String nuevaContrasena) {
    String sql = "UPDATE usuarios SET contrasena=? WHERE id_usuario=?";
    try (Connection con = ConexionDB.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, nuevaContrasena);
        ps.setLong(2, idUsuario);
        ps.executeUpdate();
        return true;
    } catch (SQLException e) {
        System.err.println(e.getMessage());
        return false;
    }
}
    
    public List<Usuario> listarPorRol(String rol) {
    List<Usuario> lista = new ArrayList<>();
    String sql = "SELECT * FROM usuarios WHERE rol=? AND estado='ACTIVO' ORDER BY apellido1";
    try (Connection con = ConexionDB.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, rol);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            lista.add(new Usuario(
                rs.getLong("id_usuario"),
                rs.getString("nombre1"),
                rs.getString("nombre2"),
                rs.getString("apellido1"),
                rs.getString("apellido2"),
                rs.getString("correo"),
                rs.getString("contrasena"),
                rs.getString("rol"),
                rs.getString("estado"),
                rs.getString("grupo")
            ));
        }
    } catch (SQLException e) {
        System.err.println("Error al listar por rol: " + e.getMessage());
    }
    return lista;
}

    // Actualizar
    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuarios SET nombre1=?, nombre2=?, apellido1=?, " +
             "apellido2=?, correo=?, rol=?, estado=?, grupo=? WHERE id_usuario=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getNombre1());
            ps.setString(2, u.getNombre2());
            ps.setString(3, u.getApellido1());
            ps.setString(4, u.getApellido2());
            ps.setString(5, u.getCorreo());
            ps.setString(6, u.getRol());
            ps.setString(7, u.getEstado());
            ps.setString(8, u.getGrupo());
            ps.setLong(9, u.getIdUsuario());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    /** Actualiza sobre una conexión ya abierta (no la cierra). Para lotes en una sola transacción. */
    public void actualizar(Usuario u, Connection con) throws SQLException {
        String sql = "UPDATE usuarios SET nombre1=?, nombre2=?, apellido1=?, " +
             "apellido2=?, correo=?, rol=?, estado=?, grupo=? WHERE id_usuario=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombre1());
            ps.setString(2, u.getNombre2());
            ps.setString(3, u.getApellido1());
            ps.setString(4, u.getApellido2());
            ps.setString(5, u.getCorreo());
            ps.setString(6, u.getRol());
            ps.setString(7, u.getEstado());
            ps.setString(8, u.getGrupo());
            ps.setLong(9, u.getIdUsuario());
            ps.executeUpdate();
        }
    }

    // Eliminar
    public boolean eliminar(long idUsuario) {
    try (Connection con = ConexionDB.conectar()) {
        // 1. Respuestas del estudiante
        PreparedStatement ps1 = con.prepareStatement(
            "DELETE FROM respuestas WHERE id_estudiante=?");
        ps1.setLong(1, idUsuario);
        ps1.executeUpdate();

        // 2. Visitas del estudiante
        PreparedStatement ps2 = con.prepareStatement(
            "DELETE FROM visitas WHERE id_estudiante=?");
        ps2.setLong(1, idUsuario);
        ps2.executeUpdate();
        
        // 3.5 Desasignar asesor de bitácoras
        PreparedStatement ps35 = con.prepareStatement(
            "UPDATE bitacoras SET id_asesor=NULL WHERE id_asesor=?");
        ps35.setLong(1, idUsuario);
        ps35.executeUpdate();

        // 3. Documentos del estudiante
        PreparedStatement ps3 = con.prepareStatement(
            "DELETE FROM documentos WHERE id_estudiante=?");
        ps3.setLong(1, idUsuario);
        ps3.executeUpdate();

        // 4. Eliminar usuario
        PreparedStatement ps4 = con.prepareStatement(
            "DELETE FROM usuarios WHERE id_usuario=?");
        ps4.setLong(1, idUsuario);
        ps4.executeUpdate();

        return true;
    } catch (SQLException e) {
        System.err.println("Error al eliminar usuario: " + e.getMessage());
        return false;
    }
}

    // Buscar por cédula
    public Usuario buscarPorId(long idUsuario) {
        String sql = "SELECT * FROM usuarios WHERE id_usuario=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Usuario(
                    rs.getLong("id_usuario"),
                    rs.getString("nombre1"),
                    rs.getString("nombre2"),
                    rs.getString("apellido1"),
                    rs.getString("apellido2"),
                    rs.getString("correo"),
                    rs.getString("contrasena"),
                    rs.getString("rol"),
                    rs.getString("estado"),
                    rs.getString("grupo")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
        }
        return null;
    }

    // Login
    public Usuario login(String correo, String contrasena) {
        String sql = "SELECT * FROM usuarios WHERE correo=? AND contrasena=? AND estado='ACTIVO'";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, correo);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Usuario(
                    rs.getLong("id_usuario"),
                    rs.getString("nombre1"),
                    rs.getString("nombre2"),
                    rs.getString("apellido1"),
                    rs.getString("apellido2"),
                    rs.getString("correo"),
                    rs.getString("contrasena"),
                    rs.getString("rol"),
                    rs.getString("estado"),
                    rs.getString("grupo")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error en login: " + e.getMessage());
        }
        return null;
    }
    
    // LISTAR POR GRUPO
    public List<Usuario> listarPorGrupo(String grupo) {
    List<Usuario> lista = new ArrayList<>();
    String sql = "SELECT * FROM usuarios WHERE grupo=? AND rol='ESTUDIANTE' ORDER BY apellido1";
    try (Connection con = ConexionDB.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, grupo);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            lista.add(new Usuario(
                rs.getLong("id_usuario"),
                rs.getString("nombre1"),
                rs.getString("nombre2"),
                rs.getString("apellido1"),
                rs.getString("apellido2"),
                rs.getString("correo"),
                rs.getString("contrasena"),
                rs.getString("rol"),
                rs.getString("estado"),
                rs.getString("grupo")
            ));
        }
    } catch (SQLException e) {
        System.err.println("Error al listar por grupo: " + e.getMessage());
    }
    return lista;
}
}