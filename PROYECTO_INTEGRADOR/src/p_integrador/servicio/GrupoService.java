package p_integrador.servicio;

import p_integrador.dao.GrupoDAO;
import p_integrador.modelo.Grupo;
import java.util.List;

/**
 * Capa de Servicio para la gestión de Grupos Académicos.
 * Centraliza validaciones y reglas de negocio relacionadas con grupos,
 * separándolas completamente de las vistas y del DAO.
 *
 * Las vistas SOLO deben llamar métodos de esta clase.
 */
public class GrupoService {

    private final GrupoDAO dao = new GrupoDAO();

    /**
     * Crea un nuevo grupo académico.
     *
     * @param codigo   Código generado (ej: "3LEI-A")
     * @param semestre Número de semestre (1–8)
     * @return null si fue exitoso, o mensaje de error si falló.
     */
    public String crear(String codigo, int semestre) {
        if (codigo == null || codigo.trim().isEmpty())
            return "El código del grupo es obligatorio.";
        if (semestre < 1 || semestre > 8)
            return "El semestre debe estar entre 1 y 8.";

        return dao.crear(new Grupo(codigo.trim(), semestre))
            ? null
            : "El código de grupo '" + codigo + "' ya existe en el sistema.";
    }

    /**
     * Elimina un grupo verificando primero que no tenga estudiantes asignados.
     *
     * @return null si fue exitoso, o mensaje de error si falló.
     */
    public String eliminar(String codigo) {
        if (codigo == null || codigo.trim().isEmpty())
            return "Código de grupo inválido.";
        if (dao.tieneEstudiantes(codigo))
            return "No se puede eliminar el grupo '" + codigo + "': tiene estudiantes asignados. Reasígnalos primero.";

        return dao.eliminar(codigo)
            ? null
            : "Error al eliminar el grupo de la base de datos.";
    }

    /** Retorna todos los grupos ordenados por semestre y código. */
    public List<Grupo> listarTodos() {
        return dao.listarTodos();
    }

    /**
     * Verifica si un grupo tiene estudiantes asignados.
     * Útil para advertir al usuario antes de eliminar.
     */
    public boolean tieneEstudiantes(String codigo) {
        return dao.tieneEstudiantes(codigo);
    }
}
