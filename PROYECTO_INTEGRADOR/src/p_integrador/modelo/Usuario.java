package p_integrador.modelo;

public class Usuario {
    
    private long idUsuario;
    private String nombre1;
    private String nombre2;
    private String apellido1;
    private String apellido2;
    private String correo;
    private String contrasena;
    private String rol;
    private String estado;
    private String grupo;

    public Usuario() {}

    public Usuario(long idUsuario, String nombre1, String nombre2, 
                   String apellido1, String apellido2, String correo, 
                   String contrasena, String rol, String estado, String grupo) {
        this.idUsuario = idUsuario;
        this.nombre1 = nombre1;
        this.nombre2 = nombre2;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
        this.grupo = grupo;
        this.estado = estado;
        
    }

    // Getters
    public long getIdUsuario() { return idUsuario; }
    public String getNombre1() { return nombre1; }
    public String getNombre2() { return nombre2; }
    public String getApellido1() { return apellido1; }
    public String getApellido2() { return apellido2; }
    public String getCorreo() { return correo; }
    public String getContrasena() { return contrasena; }
    public String getRol() { return rol; }
    public String getEstado() { return estado; }
    public String getGrupo() { return grupo; }

    // Setters
    public void setIdUsuario(long idUsuario) { this.idUsuario = idUsuario; }
    public void setNombre1(String nombre1) { this.nombre1 = nombre1; }
    public void setNombre2(String nombre2) { this.nombre2 = nombre2; }
    public void setApellido1(String apellido1) { this.apellido1 = apellido1; }
    public void setApellido2(String apellido2) { this.apellido2 = apellido2; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public void setRol(String rol) { this.rol = rol; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setGrupo(String grupo) { this.grupo = grupo; }

    // Genera correo automático
    public static String generarCorreo(String nombre1, String apellido1, long cedula) {
        String inicial = String.valueOf(nombre1.charAt(0)).toLowerCase();
        String ap1 = apellido1.toLowerCase().trim();
        String cedStr = String.valueOf(cedula);
        String ultimos2 = cedStr.substring(cedStr.length() - 2);
        return inicial + ap1 + ultimos2 + "@udi.edu.co";
    }

    @Override
    public String toString() {
        return nombre1 + " " + apellido1;
    }
}