package p_integrador.modelo;

public class Respuesta {
    
    private String idRespuesta;
    private String idPregunta;
    private long idEstudiante;
    private String respuesta;

    public Respuesta() {}

    public Respuesta(String idRespuesta, String idPregunta, long idEstudiante, String respuesta) {
        this.idRespuesta = idRespuesta;
        this.idPregunta = idPregunta;
        this.idEstudiante = idEstudiante;
        this.respuesta = respuesta;
    }

    public String getIdRespuesta() { return idRespuesta; }
    public String getIdPregunta() { return idPregunta; }
    public long getIdEstudiante() { return idEstudiante; }
    public String getRespuesta() { return respuesta; }

    public void setIdRespuesta(String idRespuesta) { this.idRespuesta = idRespuesta; }
    public void setIdPregunta(String idPregunta) { this.idPregunta = idPregunta; }
    public void setIdEstudiante(long idEstudiante) { this.idEstudiante = idEstudiante; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }
}