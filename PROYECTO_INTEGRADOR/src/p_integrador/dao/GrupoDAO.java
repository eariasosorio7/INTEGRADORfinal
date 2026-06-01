package p_integrador.dao;

import p_integrador.conexion.ConexionDB;
import p_integrador.modelo.Grupo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GrupoDAO {

    public boolean crear(Grupo g) {
        String sql = "INSERT INTO grupos VALUES (?, ?)";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, g.getCodigo());
            ps.setInt(2, g.getSemestre());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear grupo: " + e.getMessage());
            return false;
        }
    }

    public List<Grupo> listarTodos() {
        List<Grupo> lista = new ArrayList<>();
        String sql = "SELECT * FROM grupos ORDER BY semestre, codigo";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Grupo(rs.getString("codigo"), rs.getInt("semestre")));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar grupos: " + e.getMessage());
        }
        return lista;
    }

    public boolean eliminar(String codigo) {
    try (Connection con = ConexionDB.conectar()) {

        // 1. Respuestas de estudiantes de visitas de bitácoras de prácticas del grupo
        PreparedStatement ps1 = con.prepareStatement(
            "DELETE FROM respuestas WHERE id_pregunta IN (" +
            "SELECT id_pregunta FROM preguntas WHERE id_visita IN (" +
            "SELECT id_visita FROM visitas WHERE id_bitacora IN (" +
            "SELECT id_bitacora FROM bitacoras WHERE id_practica IN (" +
            "SELECT id_practica FROM practicas WHERE codigo_grupo=?))))");
        ps1.setString(1, codigo);
        ps1.executeUpdate();

        // 2. Preguntas
        PreparedStatement ps2 = con.prepareStatement(
            "DELETE FROM preguntas WHERE id_visita IN (" +
            "SELECT id_visita FROM visitas WHERE id_bitacora IN (" +
            "SELECT id_bitacora FROM bitacoras WHERE id_practica IN (" +
            "SELECT id_practica FROM practicas WHERE codigo_grupo=?)))");
        ps2.setString(1, codigo);
        ps2.executeUpdate();

        // 3. Visitas
        PreparedStatement ps3 = con.prepareStatement(
            "DELETE FROM visitas WHERE id_bitacora IN (" +
            "SELECT id_bitacora FROM bitacoras WHERE id_practica IN (" +
            "SELECT id_practica FROM practicas WHERE codigo_grupo=?))");
        ps3.setString(1, codigo);
        ps3.executeUpdate();

        // 4. Bitácoras
        PreparedStatement ps4 = con.prepareStatement(
            "DELETE FROM bitacoras WHERE id_practica IN (" +
            "SELECT id_practica FROM practicas WHERE codigo_grupo=?)");
        ps4.setString(1, codigo);
        ps4.executeUpdate();

        // 5. Prácticas
        PreparedStatement ps5 = con.prepareStatement(
            "DELETE FROM practicas WHERE codigo_grupo=?");
        ps5.setString(1, codigo);
        ps5.executeUpdate();

        // 6. Desasignar usuarios del grupo
        PreparedStatement ps6 = con.prepareStatement(
            "UPDATE usuarios SET grupo='NINGUNO' WHERE grupo=?");
        ps6.setString(1, codigo);
        ps6.executeUpdate();

        // 7. Eliminar grupo
        PreparedStatement ps7 = con.prepareStatement(
            "DELETE FROM grupos WHERE codigo=?");
        ps7.setString(1, codigo);
        ps7.executeUpdate();

        return true;
    } catch (SQLException e) {
        System.err.println("Error al eliminar grupo: " + e.getMessage());
        return false;
    }
}

    public boolean tieneEstudiantes(String codigo) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE grupo=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Error al verificar grupo: " + e.getMessage());
        }
        return false;
    }
}