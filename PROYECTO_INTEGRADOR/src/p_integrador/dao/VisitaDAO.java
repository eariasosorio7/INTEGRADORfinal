package p_integrador.dao;

import p_integrador.conexion.ConexionDB;
import p_integrador.modelo.Visita;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VisitaDAO {

    public boolean crear(Visita v) {
        String sql = "INSERT INTO visitas (id_visita, id_bitacora, id_estudiante, numero_visita, fecha_limite, respuestas, estado, ubicacion) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getIdVisita());
            ps.setString(2, v.getIdBitacora());
            if (v.getIdEstudiante() == 0) ps.setNull(3, java.sql.Types.NUMERIC);
            else ps.setLong(3, v.getIdEstudiante());
            ps.setInt(4, v.getNumeroVisita());
            ps.setDate(5, new java.sql.Date(v.getFechaLimite().getTime()));
            ps.setString(6, v.getRespuestas());
            ps.setString(7, v.getEstado());
            ps.setString(8, v.getUbicacion());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear visita: " + e.getMessage());
            return false;
        }
        
    }

    /**
     * Inserta la visita usando una conexión ya abierta (no la cierra). Pensado
     * para insertar muchas visitas dentro de una sola transacción y así no
     * agotar los manejadores de Oracle XE (ORA-12519).
     */
    public void crear(Visita v, Connection con) throws SQLException {
        String sql = "INSERT INTO visitas (id_visita, id_bitacora, id_estudiante, numero_visita, fecha_limite, respuestas, estado, ubicacion) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getIdVisita());
            ps.setString(2, v.getIdBitacora());
            if (v.getIdEstudiante() == 0) ps.setNull(3, java.sql.Types.NUMERIC);
            else ps.setLong(3, v.getIdEstudiante());
            ps.setInt(4, v.getNumeroVisita());
            ps.setDate(5, new java.sql.Date(v.getFechaLimite().getTime()));
            ps.setString(6, v.getRespuestas());
            ps.setString(7, v.getEstado());
            ps.setString(8, v.getUbicacion());
            ps.executeUpdate();
        }
    }

    /** Actualiza las horas objetivo (planeadas) de una visita base. */
    public boolean actualizarUbicacion(String idVisita, String ubicacion) {
        String sql = "UPDATE visitas SET ubicacion=? WHERE id_visita=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ubicacion);
            ps.setString(2, idVisita);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar ubicación: " + e.getMessage());
            return false;
        }
    }

    public List<String> listarEstudiantesDebitacora(String idBitacora) {
    List<String> lista = new ArrayList<>();
    String sql = "SELECT DISTINCT id_estudiante FROM visitas WHERE id_bitacora=? AND id_estudiante IS NOT NULL";
    
    System.out.println("SQL buscando estudiantes en bitacora: " + idBitacora);
    
    try (Connection con = ConexionDB.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, idBitacora);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) lista.add(rs.getString("id_estudiante"));
    } catch (SQLException e) {
        System.err.println(e.getMessage());
    }
    return lista;
}
    
    public List<Visita> listarVisitasBase(String idBitacora) {
    List<Visita> lista = new ArrayList<>();
    String sql = "SELECT * FROM visitas WHERE id_bitacora=? AND id_estudiante IS NULL ORDER BY numero_visita";
    try (Connection con = ConexionDB.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, idBitacora);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) lista.add(mapear(rs));
    } catch (SQLException e) {
        System.err.println(e.getMessage());
    }
    return lista;
}

    /** Suma de horas validadas (las que pone el asesor al calificar) de una bitácora. */
    public int sumaHorasBitacora(String idBitacora) {
        String sql = "SELECT NVL(SUM(horas_validadas),0) FROM visitas WHERE id_bitacora=? AND id_estudiante IS NULL";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idBitacora);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error al sumar horas de bitácora: " + e.getMessage());
        }
        return 0;
    }
    
    public boolean estudianteYaUnido(String idBitacora, long idEstudiante) {
    String sql = "SELECT COUNT(*) FROM visitas WHERE id_bitacora=? AND id_estudiante=?";
    try (Connection con = ConexionDB.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, idBitacora);
        ps.setLong(2, idEstudiante);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1) > 0;
    } catch (SQLException e) {
        System.err.println(e.getMessage());
    }
    return false;
}

public List<String> listarBitacorasDeEstudiante(long idEstudiante) {
    List<String> lista = new ArrayList<>();
    String sql = "SELECT DISTINCT id_bitacora FROM visitas WHERE id_estudiante=?";
    try (Connection con = ConexionDB.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setLong(1, idEstudiante);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) lista.add(rs.getString("id_bitacora"));
    } catch (SQLException e) {
        System.err.println(e.getMessage());
    }
    return lista;
}
    
public List<Visita> listarTodas() {
    List<Visita> lista = new ArrayList<>();
    String sql = "SELECT * FROM visitas ORDER BY id_visita";
    try (Connection con = ConexionDB.conectar();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) lista.add(mapear(rs));
    } catch (SQLException e) {
        System.err.println(e.getMessage());
    }
    return lista;
}

    public boolean evaluarVisita(String idVisita, String retroalimentacion, double nota) {
    String sql = "UPDATE visitas SET retroalimentacion=?, nota=? WHERE id_visita=?";
    System.out.println("Guardando evaluación: " + idVisita + " nota: " + nota);
    try (Connection con = ConexionDB.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, retroalimentacion);
        ps.setDouble(2, nota);
        ps.setString(3, idVisita);
        ps.executeUpdate();
        return true;
    } catch (SQLException e) {
        System.err.println("Error al evaluar visita: " + e.getMessage());
        return false;
    }
}
    
    public List<Visita> listarPorBitacoraAdmin(String idBitacora) {
    List<Visita> lista = new ArrayList<>();
    String sql = "SELECT * FROM visitas WHERE id_bitacora=? ORDER BY numero_visita";
    try (Connection con = ConexionDB.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, idBitacora);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) lista.add(mapear(rs));
    } catch (SQLException e) {
        System.err.println("Error: " + e.getMessage());
    }
    return lista;
}
    
    public List<Visita> listarPorBitacoraYNumero(String idBitacora, long idEstudiante, int numeroVisita) {
    List<Visita> lista = new ArrayList<>();
    String sql = "SELECT * FROM visitas WHERE id_bitacora=? AND id_estudiante=? AND numero_visita=?";
    try (Connection con = ConexionDB.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, idBitacora);
        ps.setLong(2, idEstudiante);
        ps.setInt(3, numeroVisita);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) lista.add(mapear(rs));
    } catch (SQLException e) {
        System.err.println("Error: " + e.getMessage());
    }
    return lista;
}
    
    public List<Visita> listarPorBitacora(String idBitacora, long idEstudiante) {
        List<Visita> lista = new ArrayList<>();
        String sql = "SELECT * FROM visitas WHERE id_bitacora=? AND id_estudiante=? ORDER BY numero_visita";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idBitacora);
            ps.setLong(2, idEstudiante);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar visitas: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarPlantilla(String idVisita, String rutaPlantilla) {
        String sql = "UPDATE visitas SET ruta_plantilla=? WHERE id_visita=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rutaPlantilla);
            ps.setString(2, idVisita);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar plantilla: " + e.getMessage());
            return false;
        }
    }

    public boolean validarHoras(String idVisita, int horas) {
        String sql = "UPDATE visitas SET horas_validadas=?, estado_validacion='VALIDADO' WHERE id_visita=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, horas);
            ps.setString(2, idVisita);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al validar horas: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String idVisita) {
        try (Connection con = ConexionDB.conectar()) {
            PreparedStatement ps1 = con.prepareStatement(
                "DELETE FROM respuestas WHERE id_pregunta IN (SELECT id_pregunta FROM preguntas WHERE id_visita=?)");
            ps1.setString(1, idVisita); ps1.executeUpdate();
            PreparedStatement ps2 = con.prepareStatement("DELETE FROM preguntas WHERE id_visita=?");
            ps2.setString(1, idVisita); ps2.executeUpdate();
            PreparedStatement ps3 = con.prepareStatement("DELETE FROM visitas WHERE id_visita=?");
            ps3.setString(1, idVisita); ps3.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar visita: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarFechaLimite(String idVisita, java.util.Date nuevaFecha) {
        String sql = "UPDATE visitas SET fecha_limite=? WHERE id_visita=?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(nuevaFecha.getTime()));
            ps.setString(2, idVisita);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar fecha visita: " + e.getMessage());
            return false;
        }
    }

    private Visita mapear(ResultSet rs) throws SQLException {
        Visita v = new Visita(
            rs.getString("id_visita"),
            rs.getString("id_bitacora"),
            rs.getLong("id_estudiante"),
            rs.getInt("numero_visita"),
            rs.getDate("fecha_visita"),
            rs.getDate("fecha_limite"),
            rs.getInt("horas_registradas"),
            rs.getString("respuestas"),
            rs.getString("estado"),
            rs.getString("ruta_plantilla"),
            rs.getInt("horas_validadas"),
            rs.getString("estado_validacion"),
            rs.getString("retroalimentacion"),
            rs.getDouble("nota")
        );
        v.setUbicacion(rs.getString("ubicacion"));
        v.setHorasObjetivo(rs.getInt("horas_validadas"));
        return v;
    }
}
