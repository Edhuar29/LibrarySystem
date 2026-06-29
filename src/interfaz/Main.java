package interfaz;

import javax.swing.SwingUtilities;

/**
 * Clase principal que inicializa y arranca la aplicación.
 * Utiliza SwingUtilities.invokeLater para asegurar que la creación de la GUI
 * se realice en el Event Dispatch Thread (EDT).
 */
public class Main {
    /**
     * Punto de entrada principal de la aplicación.
     * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                VentanaPrincipal ventana = new VentanaPrincipal();
                ventana.setVisible(true);
            }
        });
    }
}
