package ParteDistribuida;


public class Main {

    public static void main(String[] args) {
        // Iniciar el servidor RMI en un hilo separado
        Thread servidorThread = new Thread(() -> {
            try {
                ParteDistribuida.ServidorRemoto.main(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        servidorThread.start();

        // Esperar un poco a que el servidor esté listo
        try {
            Thread.sleep(2000); // 2 segundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Iniciar JugadorRemoto (interfaz de control del juego)
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                JugadorRemoto Jugador = new JugadorRemoto(); // asegúrate de que esta clase exista
                Jugador.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Iniciar GUIRemoto (interfaz remota de observación)
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                GUIRemoto guiRemoto = new GUIRemoto(); // asegúrate de que esta clase exista
                guiRemoto.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

