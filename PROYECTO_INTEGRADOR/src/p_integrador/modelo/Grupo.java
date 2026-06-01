package p_integrador.modelo;

public class Grupo {
    
    private String codigo;
    private int semestre;

    public Grupo() {}

    public Grupo(String codigo, int semestre) {
        this.codigo = codigo;
        this.semestre = semestre;
    }

    public String getCodigo() { return codigo; }
    public int getSemestre() { return semestre; }

    public void setCodigo(String codigo) { this.codigo = codigo; }
    public void setSemestre(int semestre) { this.semestre = semestre; }

    @Override
    public String toString() { return codigo; }
}