package p_integrador.modelo;

import java.util.Date;

public class Documento {

    private String idDocumento;
    private long idEstudiante;
    private String tipoDocumento;
    private String rutaArchivo;
    private Date fechaSubida;
    private String estado;

    public Documento() {}

    public Documento(String idDocumento, long idEstudiante, String tipoDocumento,
                     String rutaArchivo, Date fechaSubida, String estado) {
        this.idDocumento = idDocumento;
        this.idEstudiante = idEstudiante;
        this.tipoDocumento = tipoDocumento;
        this.rutaArchivo = rutaArchivo;
        this.fechaSubida = fechaSubida;
        this.estado = estado;
    }

    public String getIdDocumento() { return idDocumento; }
    public long getIdEstudiante() { return idEstudiante; }
    public String getTipoDocumento() { return tipoDocumento; }
    public String getRutaArchivo() { return rutaArchivo; }
    public Date getFechaSubida() { return fechaSubida; }
    public String getEstado() { return estado; }

    public void setIdDocumento(String idDocumento) { this.idDocumento = idDocumento; }
    public void setIdEstudiante(long idEstudiante) { this.idEstudiante = idEstudiante; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }
    public void setFechaSubida(Date fechaSubida) { this.fechaSubida = fechaSubida; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() { return tipoDocumento + " [" + estado + "]"; }
}