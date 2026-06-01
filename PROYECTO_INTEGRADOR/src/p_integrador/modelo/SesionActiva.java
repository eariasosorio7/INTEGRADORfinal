package p_integrador.modelo;

public class SesionActiva {
    
    private static Usuario usuarioActual;
    
    public static void iniciarSesion(Usuario u) {
        usuarioActual = u;
    }
    
    public static Usuario getUsuario() {
        return usuarioActual;
    }
    
    public static void cerrarSesion() {
        usuarioActual = null;
    }
    
    public static String getNombre() {
        if (usuarioActual == null) return "";
        return usuarioActual.getNombre1() + 
               (usuarioActual.getNombre2() != null ? " " + usuarioActual.getNombre2() : "") +
               " " + usuarioActual.getApellido1();
    }
    
    public static String getRol() {
        if (usuarioActual == null) return "";
        return usuarioActual.getRol();
    }
}