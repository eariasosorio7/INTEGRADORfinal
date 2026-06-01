package p_integrador.modelo;

import java.util.Date;

public class Bitacora {
    
    private String idBitacora;
    private String codigo;
    private String idPractica;
    private Date fechaLimite;
    private String estado;
    private String fortalezas;
    private String areasMejora;
    private String comentarioFinal;
    private double calificacion;
    private long idAsesor;
    private String objetivo;

    public Bitacora() {}

    public Bitacora(String idBitacora, String codigo, String idPractica, 
                    Date fechaLimite, String estado, String fortalezas, 
                    String areasMejora, String comentarioFinal, double calificacion, long idAsesor) {
        this.idBitacora = idBitacora;
        this.codigo = codigo;
        this.idPractica = idPractica;
        this.fechaLimite = fechaLimite;
        this.estado = estado;
        this.fortalezas = fortalezas;
        this.areasMejora = areasMejora;
        this.comentarioFinal = comentarioFinal;
        this.calificacion = calificacion;
        this.idAsesor = idAsesor;
    }

    public String getIdBitacora() { return idBitacora; }
    public String getCodigo() { return codigo; }
    public String getIdPractica() { return idPractica; }
    public Date getFechaLimite() { return fechaLimite; }
    public String getEstado() { return estado; }
    public String getFortalezas() { return fortalezas; }
    public String getAreasMejora() { return areasMejora; }
    public String getComentarioFinal() { return comentarioFinal; }
    public double getCalificacion() { return calificacion; }
    public long getIdAsesor() { return idAsesor; }

    public void setIdBitacora(String idBitacora) { this.idBitacora = idBitacora; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public void setIdPractica(String idPractica) { this.idPractica = idPractica; }
    public void setFechaLimite(Date fechaLimite) { this.fechaLimite = fechaLimite; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setFortalezas(String fortalezas) { this.fortalezas = fortalezas; }
    public void setAreasMejora(String areasMejora) { this.areasMejora = areasMejora; }
    public void setComentarioFinal(String comentarioFinal) { this.comentarioFinal = comentarioFinal; }
    public void setCalificacion(double calificacion) { this.calificacion = calificacion; }
    public void setIdAsesor(long idAsesor) { this.idAsesor = idAsesor; }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    @Override
    public String toString() { return idBitacora + " [" + codigo + "]"; }
}