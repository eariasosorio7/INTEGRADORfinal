package p_integrador.servicio;

import p_integrador.dao.PracticaDAO;
import p_integrador.modelo.Practica;
import java.util.List;

/**
 * Capa de Servicio para la gestión de Prácticas.
 * Centraliza validaciones: no permite prácticas para el grupo NINGUNO,
 * soporta habilitar/deshabilitar y CRUD completo.
 */
public class PracticaService {

    private final PracticaDAO dao = new PracticaDAO();

    public String crearPractica(String codigoGrupo, String nivel) {
        return crearPractica(codigoGrupo, nivel, null);
    }

    public String crearPractica(String codigoGrupo, String nivel, java.util.Date fechaFin) {
        if (codigoGrupo == null || codigoGrupo.trim().isEmpty())
            return "Selecciona un grupo válido.";
        if ("NINGUNO".equalsIgnoreCase(codigoGrupo.trim()))
            return "No se puede crear una práctica para el grupo NINGUNO.";
        if (nivel == null || nivel.trim().isEmpty())
            return "Selecciona un nivel de práctica (I, II o III).";

        String idPrac = codigoGrupo.trim() + "_" + nivel.trim();
        if (dao.existePractica(idPrac))
            return "Ya existe una Práctica " + nivel + " para el grupo " + codigoGrupo + ".";

        int horas;
        switch (nivel.trim()) {
            case "I":   horas = 20; break;
            case "II":  horas = 30; break;
            case "III": horas = 40; break;
            default:    return "Nivel de práctica inválido: " + nivel;
        }

        Practica p = new Practica(idPrac, nivel.trim(), horas, "ACTIVA", codigoGrupo.trim());
        p.setFechaFin(fechaFin);
        return dao.crear(p) ? null : "Error al crear la práctica en la base de datos.";
    }

    public String editarPractica(String idPractica, int horasRequeridas) {
        if (idPractica == null || idPractica.trim().isEmpty())
            return "ID de práctica inválido.";
        if (horasRequeridas <= 0)
            return "Las horas requeridas deben ser mayores a 0.";

        Practica p = dao.buscarPorId(idPractica);
        if (p == null) return "Práctica no encontrada.";

        p.setHorasRequeridas(horasRequeridas);
        return dao.actualizar(p) ? null : "Error al actualizar la práctica.";
    }

    public String eliminarPractica(String idPractica) {
        if (idPractica == null || idPractica.trim().isEmpty())
            return "ID de práctica inválido.";
        return dao.eliminar(idPractica) ? null : "Error al eliminar la práctica.";
    }

    public String habilitarPractica(String idPractica) {
        return dao.actualizarEstado(idPractica, "ACTIVA")
            ? null : "Error al habilitar la práctica.";
    }

    public String deshabilitarPractica(String idPractica) {
        return dao.actualizarEstado(idPractica, "CERRADA")
            ? null : "Error al deshabilitar la práctica.";
    }

    public List<Practica> listarPorGrupo(String codigoGrupo) {
        return dao.listarPorGrupo(codigoGrupo);
    }

    public List<Practica> listarActivasPorGrupo(String codigoGrupo) {
        return dao.listarActivasPorGrupo(codigoGrupo);
    }

    public boolean existePractica(String idPractica) {
        return dao.existePractica(idPractica);
    }

    public String actualizarEstado(String idPractica, String estado) {
        return dao.actualizarEstado(idPractica, estado)
            ? null : "Error al actualizar el estado de la práctica.";
    }
}
