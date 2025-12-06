package ageofthreads;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantLock;

public class LoggerSistema {
    private static LoggerSistema instancia = null;
    private static final ReentrantLock lock = new ReentrantLock();
    private BufferedWriter writer;
    private static final String NOMBRE_ARCHIVO = "centro_urbano.txt";
    private static final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Constructor privado
    private LoggerSistema() {
        try {
            writer = new BufferedWriter(new FileWriter(NOMBRE_ARCHIVO, true)); // Modo append
        } catch (IOException e) {
            System.err.println("Error al abrir el archivo de log: " + e.getMessage());
        }
    }

    // Método para obtener la instancia singleton
    public static LoggerSistema getInstancia() {
        if (instancia == null) {
            synchronized (LoggerSistema.class) {
                if (instancia == null) {
                    instancia = new LoggerSistema();
                }
            }
        }
        return instancia;
    }

    // Método para registrar un evento
    public void log(String mensaje) {
        lock.lock();
        try {
            String timestamp = LocalDateTime.now().format(formatoFecha);
            writer.write("[" + timestamp + "] " + mensaje);
            writer.newLine();
            writer.flush(); // Asegura que se escriba inmediatamente
        } catch (IOException e) {
            System.err.println("Error al escribir en el log: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    // Método para cerrar el escritor si se desea detener el sistema
    public void cerrar() {
        lock.lock();
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar el log: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

}
