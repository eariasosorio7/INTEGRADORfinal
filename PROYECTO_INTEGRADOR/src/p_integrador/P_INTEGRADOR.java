package p_integrador;

import p_integrador.vista.LoginForm;



public class P_INTEGRADOR {
    public static void main(String[] args) {
        java.awt.Font fuente = new java.awt.Font("Leelawadee UI Semilight", java.awt.Font.PLAIN, 14);
        javax.swing.UIManager.put("Button.font", fuente);
        javax.swing.UIManager.put("Label.font", fuente);
        javax.swing.UIManager.put("TextField.font", fuente);
        javax.swing.UIManager.put("ComboBox.font", fuente);
        javax.swing.UIManager.put("Table.font", fuente);
        javax.swing.UIManager.put("TableHeader.font", fuente);
        javax.swing.UIManager.put("PasswordField.font", fuente);
        java.awt.EventQueue.invokeLater(() -> new LoginForm().setVisible(true));
    }
}