package p_integrador.dao;

import p_integrador.conexion.ConexionDB;
import p_integrador.modelo.Documento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentoDAO {

    public boolean crear(Documento d) {
        String sql = "INSERT INTO documentos VALUES (?,?,?,?,SYSDATE,?)";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getIdDocumento());
            ps.setLong(2, d.getIdEstudiante());
            ps.setString(3, d.getTipoDocumento());
            ps.setString(4, d.getRutaArchivo());
            ps.setString(5, d.getEstado());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear documento: " + e.getMessage());
            return false;
        }
    }

    public List<Documento> listarPorEstudiante(long idEstudiante) {
        List<Documento> lista = new ArrayList<>();
        String sql = "SELECT * FROM documentos WHERE id_estudiante=? ORDER BY tipo_documento";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idEstudiante);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar documentos: " + e.getMessage());
        }
        return lista;
    }

    public List<Documento> listarTodos() {
        List<Documento> lista = new ArrayList<>();
        String sql = "SELECT * FROM documentos ORDER BY id_estudiante, tipo_documento";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar documentos: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarEstado(String idDocumento, String estado) {
        String sql = "UPDATE documentos SET estado=? WHERE id_documento=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setString(2, idDocumento);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String idDocumento) {
        String sql = "DELETE FROM documentos WHERE id_documento=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idDocumento);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar documento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si el estudiante tiene al menos un documento aprobado.
     * Esta condición debe cumplirse para acceder a la gestión de prácticas.
     */
    public boolean tieneDocumentosAprobados(long idEstudiante) {
        String sql = "SELECT COUNT(*) FROM documentos WHERE id_estudiante=? AND estado='APROBADO'";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idEstudiante);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Error al verificar documentos: " + e.getMessage());
        }
        return false;
    }

    /**
     * Documentación COMPLETA: el estudiante tiene al menos un documento y
     * TODOS sus documentos están APROBADOS (ninguno pendiente ni rechazado).
     */
    public boolean documentacionCompleta(long idEstudiante) {
        String sql = "SELECT COUNT(*) AS total, "
                   + "SUM(CASE WHEN estado='APROBADO' THEN 1 ELSE 0 END) AS aprobados "
                   + "FROM documentos WHERE id_estudiante=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idEstudiante);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("total");
                int aprobados = rs.getInt("aprobados");
                return total > 0 && total == aprobados;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar documentación completa: " + e.getMessage());
        }
        return false;
    }

    private Documento mapear(ResultSet rs) throws SQLException {
        return new Documento(
            rs.getString("id_documento"),
            rs.getLong("id_estudiante"),
            rs.getString("tipo_documento"),
            rs.getString("ruta_archivo"),
            rs.getDate("fecha_subida"),
            rs.getString("estado")
        );
    }
}