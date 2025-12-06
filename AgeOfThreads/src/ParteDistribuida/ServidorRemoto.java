package ParteDistribuida;

import ageofthreads.Simulador;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServidorRemoto extends Thread {

    @Override
    public void run() {
        try {
            System.out.println("Iniciando servidor remoto...");

            // Iniciar simulador
            Simulador simulador = new Simulador();

            // Crear implementación del objeto remoto
            ImplementacionRemoto implementacion = new ImplementacionRemoto(simulador);

            // Exportar objeto remoto
            // No necesitas exportar manualmente si ya extiende UnicastRemoteObject
            Remoto stub = implementacion;


            // Crear el registro RMI si no está iniciado
            Registry registry = LocateRegistry.createRegistry(1099);

            // Publicar el objeto remoto en el registro
            registry.rebind("Simulacion", stub);

            System.out.println("Servidor RMI listo y escuchando en puerto 1099 con nombre 'Simulacion'.");

        } catch (Exception e) {
            System.err.println("Error en el servidor RMI:");
            e.printStackTrace();
        }
    }

    // Método main opcional para ejecutar de forma independiente
    public static void main(String[] args) {
        new ServidorRemoto().start();
    }
}

