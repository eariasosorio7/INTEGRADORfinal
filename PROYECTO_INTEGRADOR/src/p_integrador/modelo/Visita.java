package p_integrador.modelo;

import java.util.Date;

public class Visita {
    
    private String idVisita;
    private String idBitacora;
    private long idEstudiante;
    private int numeroVisita;
    private Date fechaVisita;
    private Date fechaLimite;
    private int horasRegistradas;
    private String respuestas;
    private String estado;
    private String rutaPlantilla;
    private int horasValidadas;
    private String estadoValidacion;
    private String retroalimentacion;
    private double nota;
    private String ubicacion;
    private int horasObjetivo;

    public Visita() {}

    public Visita(String idVisita, String idBitacora, long idEstudiante, 
              int numeroVisita, Date fechaVisita, Date fechaLimite, 
              int horasRegistradas, String respuestas, String estado,
              String rutaPlantilla, int horasValidadas, String estadoValidacion,
              String retroalimentacion, double nota) {
        this.idVisita = idVisita;
        this.idBitacora = idBitacora;
        this.idEstudiante = idEstudiante;
        this.numeroVisita = numeroVisita;
        this.fechaVisita = fechaVisita;
        this.fechaLimite = fechaLimite;
        this.horasRegistradas = horasRegistradas;
        this.respuestas = respuestas;
        this.estado = estado;
        this.rutaPlantilla = rutaPlantilla;
        this.horasValidadas = horasValidadas;
        this.estadoValidacion = estadoValidacion;
        this.retroalimentacion = retroalimentacion;
        this.nota = nota;
    }

    public String getIdVisita() { return idVisita; }
    public String getIdBitacora() { return idBitacora; }
    public long getIdEstudiante() { return idEstudiante; }
    public int getNumeroVisita() { return numeroVisita; }
    public Date getFechaVisita() { return fechaVisita; }
    public Date getFechaLimite() { return fechaLimite; }
    public int getHorasRegistradas() { return horasRegistradas; }
    public String getRespuestas() { return respuestas; }
    public String getEstado() { return estado; }
    public String getRutaPlantilla() { return rutaPlantilla; }
    public int getHorasValidadas() { return horasValidadas; }
    public String getEstadoValidacion() { return estadoValidacion; }
    public String getRetroalimentacion() { return retroalimentacion; }
    public double getNota() { return nota; }
    public void setRetroalimentacion(String retroalimentacion) { this.retroalimentacion = retroalimentacion; }
    public void setNota(double nota) { this.nota = nota; }

    public void setIdVisita(String idVisita) { this.idVisita = idVisita; }
    public void setIdBitacora(String idBitacora) { this.idBitacora = idBitacora; }
    public void setIdEstudiante(long idEstudiante) { this.idEstudiante = idEstudiante; }
    public void setNumeroVisita(int numeroVisita) { this.numeroVisita = numeroVisita; }
    public void setFechaVisita(Date fechaVisita) { this.fechaVisita = fechaVisita; }
    public void setFechaLimite(Date fechaLimite) { this.fechaLimite = fechaLimite; }
    public void setHorasRegistradas(int horasRegistradas) { this.horasRegistradas = horasRegistradas; }
    public void setRespuestas(String respuestas) { this.respuestas = respuestas; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setRutaPlantilla(String rutaPlantilla) { this.rutaPlantilla = rutaPlantilla; }
    public void setHorasValidadas(int horasValidadas) { this.horasValidadas = horasValidadas; }
    public void setEstadoValidacion(String estadoValidacion) { this.estadoValidacion = estadoValidacion; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public int getHorasObjetivo() { return horasObjetivo; }
    public void setHorasObjetivo(int horasObjetivo) { this.horasObjetivo = horasObjetivo; }

    @Override
    public String toString() { return "Visita " + numeroVisita; }
}