package p_integrador.dao;

import p_integrador.conexion.ConexionDB;
import p_integrador.modelo.Bitacora;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BitacoraDAO {

    public boolean crear(Bitacora b) {
        String sql = "INSERT INTO bitacoras (id_bitacora, codigo, id_practica, fecha_limite, estado, objetivo) VALUES (?,?,?,?,?,?)";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, b.getIdBitacora());
            ps.setString(2, b.getCodigo());
            ps.setString(3, b.getIdPractica());
            ps.setDate(4, new java.sql.Date(b.getFechaLimite().getTime()));
            ps.setString(5, b.getEstado());
            ps.setString(6, b.getObjetivo());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear bitácora: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserta la bitácora usando una conexión ya abierta (sin abrir ni cerrar
     * una nueva). Útil para crear bitácora + visitas + preguntas dentro de una
     * sola transacción y evitar agotar los manejadores de Oracle XE (ORA-12519).
     */
    public void crear(Bitacora b, Connection con) throws SQLException {
        String sql = "INSERT INTO bitacoras (id_bitacora, codigo, id_practica, fecha_limite, estado, objetivo) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, b.getIdBitacora());
            ps.setString(2, b.getCodigo());
            ps.setString(3, b.getIdPractica());
            ps.setDate(4, new java.sql.Date(b.getFechaLimite().getTime()));
            ps.setString(5, b.getEstado());
            ps.setString(6, b.getObjetivo());
            ps.executeUpdate();
        }
    }
    
    public List<Bitacora> listarTodas() {
    List<Bitacora> lista = new ArrayList<>();
    String sql = "SELECT * FROM bitacoras ORDER BY id_bitacora";
    try (Connection con = ConexionDB.conectar();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) lista.add(mapear(rs));
    } catch (SQLException e) {
        System.err.println(e.getMessage());
    }
    return lista;
}
    
    public boolean asignarAsesor(String idBitacora, long idAsesor) {
    String sql = "UPDATE bitacoras SET id_asesor=? WHERE id_bitacora=?";
    try (Connection con = ConexionDB.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setLong(1, idAsesor);
        ps.setString(2, idBitacora);
        ps.executeUpdate();
        return true;
    } catch (SQLException e) {
        System.err.println("Error al asignar asesor: " + e.getMessage());
        return false;
    }
}

public List<Bitacora> listarPorAsesor(long idAsesor) {
    List<Bitacora> lista = new ArrayList<>();
    String sql = "SELECT * FROM bitacoras WHERE id_asesor=?";
    try (Connection con = ConexionDB.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setLong(1, idAsesor);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) lista.add(mapear(rs));
    } catch (SQLException e) {
        System.err.println("Error al listar por asesor: " + e.getMessage());
    }
    return lista;
}
    
    public Bitacora buscarPorId(String idBitacora) {
    String sql = "SELECT * FROM bitacoras WHERE id_bitacora=?";
    try (Connection con = ConexionDB.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, idBitacora);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapear(rs);
    } catch (SQLException e) {
        System.err.println(e.getMessage());
    }
    return null;
}
    
    public List<Bitacora> listarPorPractica(String idPractica) {
        List<Bitacora> lista = new ArrayList<>();
        String sql = "SELECT * FROM bitacoras WHERE id_practica=? ORDER BY id_bitacora";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idPractica);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar bitácoras: " + e.getMessage());
        }
        return lista;
    }

    public Bitacora buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM bitacoras WHERE codigo=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar bitácora: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizar(Bitacora b) {
        String sql = "UPDATE bitacoras SET fortalezas=?, areas_mejora=?, comentario_final=?, calificacion=?, estado=? WHERE id_bitacora=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, b.getFortalezas());
            ps.setString(2, b.getAreasMejora());
            ps.setString(3, b.getComentarioFinal());
            ps.setDouble(4, b.getCalificacion());
            ps.setString(5, b.getEstado());
            ps.setString(6, b.getIdBitacora());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar bitácora: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminar(String idBitacora) {
    try (Connection con = ConexionDB.conectar()) {
        // Eliminar respuestas
        PreparedStatement ps1 = con.prepareStatement(
            "DELETE FROM respuestas WHERE id_pregunta IN (SELECT id_pregunta FROM preguntas WHERE id_visita IN (SELECT id_visita FROM visitas WHERE id_bitacora=?))");
        ps1.setString(1, idBitacora);
        ps1.executeUpdate();

        // Eliminar preguntas
        PreparedStatement ps2 = con.prepareStatement(
            "DELETE FROM preguntas WHERE id_visita IN (SELECT id_visita FROM visitas WHERE id_bitacora=?)");
        ps2.setString(1, idBitacora);
        ps2.executeUpdate();

        // Eliminar visitas
        PreparedStatement ps3 = con.prepareStatement(
            "DELETE FROM visitas WHERE id_bitacora=?");
        ps3.setString(1, idBitacora);
        ps3.executeUpdate();

        // Eliminar bitácora
        PreparedStatement ps4 = con.prepareStatement(
            "DELETE FROM bitacoras WHERE id_bitacora=?");
        ps4.setString(1, idBitacora);
        ps4.executeUpdate();

        return true;
    } catch (SQLException e) {
        System.err.println("Error al eliminar bitácora: " + e.getMessage());
        return false;
    }
}
    
    public static String generarCodigo() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private Bitacora mapear(ResultSet rs) throws SQLException {
        Bitacora b = new Bitacora(
            rs.getString("id_bitacora"),
            rs.getString("codigo"),
            rs.getString("id_practica"),
            rs.getDate("fecha_limite"),
            rs.getString("estado"),
            rs.getString("fortalezas"),
            rs.getString("areas_mejora"),
            rs.getString("comentario_final"),
            rs.getDouble("calificacion"),
            rs.getLong("id_asesor")
        );
        b.setObjetivo(rs.getString("objetivo"));
        return b;
    }
}