
package ageofthreads;

public class ControlEjecucion {
    private volatile boolean enPausa = false;

    public synchronized void pausar() {
        enPausa = true;
    }

    public synchronized void reanudar() {
        enPausa = false;
        notifyAll();  // Despierta a todos los hilos pausados
    }

    public synchronized void esperarSiPausado() {
        while (enPausa) {
            try {
                wait();  // Pausa el hilo actual hasta que se llame a reanudar()
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}