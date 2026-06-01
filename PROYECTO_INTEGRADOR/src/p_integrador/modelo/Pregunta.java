package p_integrador.modelo;

public class Pregunta {
    
    private String idPregunta;
    private String idVisita;
    private int numero;
    private String texto;

    public Pregunta() {}

    public Pregunta(String idPregunta, String idVisita, int numero, String texto) {
        this.idPregunta = idPregunta;
        this.idVisita = idVisita;
        this.numero = numero;
        this.texto = texto;
    }

    public String getIdPregunta() { return idPregunta; }
    public String getIdVisita() { return idVisita; }
    public int getNumero() { return numero; }
    public String getTexto() { return texto; }

    public void setIdPregunta(String idPregunta) { this.idPregunta = idPregunta; }
    public void setIdVisita(String idVisita) { this.idVisita = idVisita; }
    public void setNumero(int numero) { this.numero = numero; }
    public void setTexto(String texto) { this.texto = texto; }

    @Override
    public String toString() { return numero + ". " + texto; }
}