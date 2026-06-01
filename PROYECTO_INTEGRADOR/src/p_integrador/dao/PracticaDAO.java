package p_integrador.dao;

import p_integrador.conexion.ConexionDB;
import p_integrador.modelo.Practica;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PracticaDAO {

    public boolean crear(Practica p) {
        String sql = "INSERT INTO practicas (id_practica, nivel, horas_requeridas, estado, codigo_grupo, fecha_fin) VALUES (?,?,?,?,?,?)";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getIdPractica());
            ps.setString(2, p.getNivel());
            ps.setInt(3, p.getHorasRequeridas());
            ps.setString(4, p.getEstado());
            ps.setString(5, p.getCodigoGrupo());
            if (p.getFechaFin() != null) ps.setDate(6, new java.sql.Date(p.getFechaFin().getTime()));
            else ps.setNull(6, java.sql.Types.DATE);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear práctica: " + e.getMessage());
            return false;
        }
    }

    /** Actualiza la fecha de fin (para finalización automática por fecha). */
    public boolean actualizarFechaFin(String idPractica, java.util.Date fechaFin) {
        String sql = "UPDATE practicas SET fecha_fin=? WHERE id_practica=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (fechaFin != null) ps.setDate(1, new java.sql.Date(fechaFin.getTime()));
            else ps.setNull(1, java.sql.Types.DATE);
            ps.setString(2, idPractica);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar fecha fin: " + e.getMessage());
            return false;
        }
    }

    /**
     * FINALIZACIÓN AUTOMÁTICA POR FECHA: cierra las prácticas cuya fecha_fin ya
     * pasó y que aún están ACTIVAS. Se llama antes de listar para mantener el
     * estado al día sin intervención manual.
     */
    public void cerrarVencidas() {
        String sql = "UPDATE practicas SET estado='CERRADA' WHERE estado='ACTIVA' AND fecha_fin IS NOT NULL AND fecha_fin < TRUNC(SYSDATE)";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al cerrar prácticas vencidas: " + e.getMessage());
        }
    }

    public boolean actualizar(Practica p) {
        String sql = "UPDATE practicas SET nivel=?, horas_requeridas=?, estado=?, codigo_grupo=? WHERE id_practica=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNivel());
            ps.setInt(2, p.getHorasRequeridas());
            ps.setString(3, p.getEstado());
            ps.setString(4, p.getCodigoGrupo());
            ps.setString(5, p.getIdPractica());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar práctica: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String idPractica) {
        // Elimina en cascada: respuestas -> preguntas -> visitas -> bitácoras -> práctica
        try (Connection con = ConexionDB.conectar()) {
            String[] sqls = {
                "DELETE FROM respuestas WHERE id_pregunta IN (SELECT id_pregunta FROM preguntas WHERE id_visita IN (SELECT id_visita FROM visitas WHERE id_bitacora IN (SELECT id_bitacora FROM bitacoras WHERE id_practica=?)))",
                "DELETE FROM preguntas WHERE id_visita IN (SELECT id_visita FROM visitas WHERE id_bitacora IN (SELECT id_bitacora FROM bitacoras WHERE id_practica=?))",
                "DELETE FROM visitas WHERE id_bitacora IN (SELECT id_bitacora FROM bitacoras WHERE id_practica=?)",
                "DELETE FROM bitacoras WHERE id_practica=?",
                "DELETE FROM practicas WHERE id_practica=?"
            };
            for (String sql : sqls) {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, idPractica);
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar práctica: " + e.getMessage());
            return false;
        }
    }

    public List<Practica> listarTodas() {
        List<Practica> lista = new ArrayList<>();
        String sql = "SELECT * FROM practicas ORDER BY codigo_grupo, nivel";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar prácticas: " + e.getMessage());
        }
        return lista;
    }

    public List<Practica> listarPorGrupo(String codigoGrupo) {
        cerrarVencidas();
        List<Practica> lista = new ArrayList<>();
        // Solo retorna prácticas ACTIVAS al estudiante; el admin ve todas
        String sql = "SELECT * FROM practicas WHERE codigo_grupo=? ORDER BY nivel";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoGrupo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar prácticas: " + e.getMessage());
        }
        return lista;
    }

    public List<Practica> listarActivasPorGrupo(String codigoGrupo) {
        cerrarVencidas();
        List<Practica> lista = new ArrayList<>();
        String sql = "SELECT * FROM practicas WHERE codigo_grupo=? AND estado='ACTIVA' ORDER BY nivel";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoGrupo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar prácticas activas: " + e.getMessage());
        }
        return lista;
    }

    /** Prácticas finalizadas (cerradas) de un grupo. */
    public List<Practica> listarFinalizadasPorGrupo(String codigoGrupo) {
        cerrarVencidas();
        List<Practica> lista = new ArrayList<>();
        String sql = "SELECT * FROM practicas WHERE codigo_grupo=? AND estado='CERRADA' ORDER BY nivel";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoGrupo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar prácticas finalizadas: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarEstado(String idPractica, String estado) {
        String sql = "UPDATE practicas SET estado=? WHERE id_practica=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setString(2, idPractica);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado: " + e.getMessage());
            return false;
        }
    }

    public boolean existePractica(String idPractica) {
        String sql = "SELECT COUNT(*) FROM practicas WHERE id_practica=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idPractica);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Error al verificar práctica: " + e.getMessage());
        }
        return false;
    }

    public Practica buscarPorId(String idPractica) {
        String sql = "SELECT * FROM practicas WHERE id_practica=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idPractica);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar práctica: " + e.getMessage());
        }
        return null;
    }

    private Practica mapear(ResultSet rs) throws SQLException {
        Practica p = new Practica(
            rs.getString("id_practica"),
            rs.getString("nivel"),
            rs.getInt("horas_requeridas"),
            rs.getString("estado"),
            rs.getString("codigo_grupo")
        );
        try { p.setFechaFin(rs.getDate("fecha_fin")); } catch (SQLException ignore) {}
        return p;
    }
}
