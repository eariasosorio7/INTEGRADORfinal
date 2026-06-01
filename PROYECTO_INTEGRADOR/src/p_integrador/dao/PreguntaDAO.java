package p_integrador.dao;

import p_integrador.conexion.ConexionDB;
import p_integrador.modelo.Pregunta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreguntaDAO {

    public boolean crear(Pregunta p) {
        String sql = "INSERT INTO preguntas VALUES (?,?,?,?)";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getIdPregunta());
            ps.setString(2, p.getIdVisita());
            ps.setInt(3, p.getNumero());
            ps.setString(4, p.getTexto());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear pregunta: " + e.getMessage());
            return false;
        }
    }

    /** Inserta la pregunta sobre una conexión ya abierta (no la cierra). */
    public void crear(Pregunta p, Connection con) throws SQLException {
        String sql = "INSERT INTO preguntas VALUES (?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getIdPregunta());
            ps.setString(2, p.getIdVisita());
            ps.setInt(3, p.getNumero());
            ps.setString(4, p.getTexto());
            ps.executeUpdate();
        }
    }

    public List<Pregunta> listarPorVisita(String idVisita) {
        List<Pregunta> lista = new ArrayList<>();
        String sql = "SELECT * FROM preguntas WHERE id_visita=? ORDER BY numero";
        System.out.println("SQL visita: " + idVisita + " resultados: " + lista.size());
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idVisita);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Pregunta(
                    rs.getString("id_pregunta"),
                    rs.getString("id_visita"),
                    rs.getInt("numero"),
                    rs.getString("texto")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar preguntas: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Pregunta p) {
        String sql = "UPDATE preguntas SET texto=? WHERE id_pregunta=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getTexto());
            ps.setString(2, p.getIdPregunta());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar pregunta: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String idPregunta) {
        String sql = "DELETE FROM preguntas WHERE id_pregunta=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idPregunta);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar pregunta: " + e.getMessage());
            return false;
        }
    }

    public int contarPorVisita(String idVisita) {
        String sql = "SELECT COUNT(*) FROM preguntas WHERE id_visita=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idVisita);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error al contar preguntas: " + e.getMessage());
        }
        return 0;
    }
}