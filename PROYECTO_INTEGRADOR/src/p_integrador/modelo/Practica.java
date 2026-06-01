package p_integrador.modelo;

public class Practica {
    
    private String idPractica;
    private String nivel;
    private int horasRequeridas;
    private String estado;
    private String codigoGrupo;
    private java.util.Date fechaFin;

    public Practica() {}

    public Practica(String idPractica, String nivel, int horasRequeridas, String estado, String codigoGrupo) {
        this.idPractica = idPractica;
        this.nivel = nivel;
        this.horasRequeridas = horasRequeridas;
        this.estado = estado;
        this.codigoGrupo = codigoGrupo;
    }

    public String getIdPractica() { return idPractica; }
    public String getNivel() { return nivel; }
    public int getHorasRequeridas() { return horasRequeridas; }
    public String getEstado() { return estado; }
    public String getCodigoGrupo() { return codigoGrupo; }

    public void setIdPractica(String idPractica) { this.idPractica = idPractica; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    public void setHorasRequeridas(int horasRequeridas) { this.horasRequeridas = horasRequeridas; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setCodigoGrupo(String codigoGrupo) { this.codigoGrupo = codigoGrupo; }

    public java.util.Date getFechaFin() { return fechaFin; }
    public void setFechaFin(java.util.Date fechaFin) { this.fechaFin = fechaFin; }

    @Override
    public String toString() { return idPractica; }
}