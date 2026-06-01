package p_integrador.dao;

import p_integrador.conexion.ConexionDB;
import p_integrador.modelo.Respuesta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RespuestaDAO {

    public boolean crear(Respuesta r) {
        String sql = "INSERT INTO respuestas VALUES (?,?,?,?)";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getIdRespuesta());
            ps.setString(2, r.getIdPregunta());
            ps.setLong(3, r.getIdEstudiante());
            ps.setString(4, r.getRespuesta());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear respuesta: " + e.getMessage());
            return false;
        }
    }
    
    public List<Respuesta> listarPorPreguntaYEstudiante(String idPregunta, long idEstudiante) {
    List<Respuesta> lista = new ArrayList<>();
    String sql = "SELECT * FROM respuestas WHERE id_pregunta=? AND id_estudiante=?";
    try (Connection con = ConexionDB.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, idPregunta);
        ps.setLong(2, idEstudiante);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            lista.add(new Respuesta(
                rs.getString("id_respuesta"),
                rs.getString("id_pregunta"),
                rs.getLong("id_estudiante"),
                rs.getString("respuesta")
            ));
        }
    } catch (SQLException e) {
        System.err.println(e.getMessage());
    }
    return lista;
}
    
    public List<Respuesta> listarPorPregunta(String idPregunta) {
        List<Respuesta> lista = new ArrayList<>();
        String sql = "SELECT * FROM respuestas WHERE id_pregunta=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idPregunta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Respuesta(
                    rs.getString("id_respuesta"),
                    rs.getString("id_pregunta"),
                    rs.getLong("id_estudiante"),
                    rs.getString("respuesta")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar respuestas: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Respuesta r) {
        String sql = "UPDATE respuestas SET respuesta=? WHERE id_respuesta=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getRespuesta());
            ps.setString(2, r.getIdRespuesta());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar respuesta: " + e.getMessage());
            return false;
        }
    }
}