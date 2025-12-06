package ageofthreads;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

public class Almacen {
    private String tipo;
    private int capacidadMax;
    private int cantidadActual;
    private Semaphore semDepositar=new Semaphore(3,true);
    private Semaphore semPatrullar=new Semaphore(3,true);
    
    private final ReentrantLock lock = new ReentrantLock(true); 
    private final Condition espacioDisponible = lock.newCondition();
    private Random rand =new Random();
    private boolean enAtaque=false;
    
    private ArrayList<Aldeano> aldeanosOcupantes = new ArrayList<>();
    private ArrayList<Guerrero> guerrerosOcupantes = new ArrayList<>();
    private ArrayList<Barbaro> barbarosOcupantes = new ArrayList<>();
    private ControlEjecucion control;
    
    public Almacen(String tipo, int CapacidadMax, int Cantidad, ControlEjecucion control) {
        this.tipo = tipo;
        this.capacidadMax = CapacidadMax;
        this.cantidadActual = Cantidad;
        this.control = control;
    }

    public boolean isEnAtaque() {
        return enAtaque;
    }
    
    public DefaultListModel<String> combinarListasOcupacionAlmacen() {
        DefaultListModel<String> modelo = new DefaultListModel<>();

        for (Aldeano a : aldeanosOcupantes) {
            modelo.addElement(a.getIdAldeano());
        }

        for (Guerrero g : guerrerosOcupantes) {
            modelo.addElement(g.getIdGuerrero());
        }

        for (Barbaro b : barbarosOcupantes) {
            modelo.addElement(b.getIdBarbaro());
        }

        return modelo;
    }


    
    
    public void setCapacidadMax(int CapacidadMax) {
        this.capacidadMax = CapacidadMax;
    }
    
    public int getCapacidadMax() {
        return capacidadMax;
    }
    public int getCantidad() {
        return cantidadActual;
    }
    public String getTipo() {
        return tipo;
    }
    public void iniciarAtaque(ArrayList<Barbaro> atacantes) {
        
        LoggerSistema.getInstancia().log("¡Ataque bárbaro en el almacen de " + tipo + "!");
        if (semDepositar.availablePermits() == 3) {  // Ningún aldeano dentro
            LoggerSistema.getInstancia().log("En el almacen de " + tipo + " no habia aldeanos durante el ataque.");
        }
        barbarosOcupantes=atacantes;
        setEnAtaque(true);
    }

    public void setEnAtaque(boolean enAtaque) {
        this.enAtaque = enAtaque;
    }
    
    // Método llamado por bárbaros al finalizar el ataque
    public void finalizarAtaque() {
        barbarosOcupantes.clear();
        LoggerSistema.getInstancia().log("El ataque en el almacen de " + tipo + " ha finalizado.");
        setEnAtaque(false);
    }

    public boolean estaSiendoAtacado() {
        return enAtaque;
    }
    public void entrarAlmacenGuerrero(Guerrero g) throws InterruptedException {
        control.esperarSiPausado();
        semPatrullar.acquire();
        synchronized (guerrerosOcupantes) {
            guerrerosOcupantes.add(g);
        }
        LoggerSistema.getInstancia().log("Guerrero " + g.getIdGuerrero() + " entra al almacen de " + tipo);
    }
    
    public void salirAlmacenGuerrero(Guerrero g) {
        control.esperarSiPausado();
        synchronized (guerrerosOcupantes) {
            guerrerosOcupantes.remove(g);
        }
        LoggerSistema.getInstancia().log("Guerrero " + g.getIdGuerrero()+ " sale del almacen de " + tipo);
        semPatrullar.release();
    }
    

    // Método que llama el aldeano antes de entrar
    public void entrarAlmacenAldeano(Aldeano aldeano) throws InterruptedException {
        control.esperarSiPausado();
        semDepositar.acquire();
        synchronized (aldeanosOcupantes) {
            aldeanosOcupantes.add(aldeano);
        }
        LoggerSistema.getInstancia().log("Aldeano " + aldeano.getIdAldeano() + " entra al almacen de " + tipo);
        }

    // Método que llama el aldeano al salir
    public void salirAlmacenAldeano(Aldeano aldeano) {
        control.esperarSiPausado();
        synchronized (aldeanosOcupantes) {
            aldeanosOcupantes.remove(aldeano);
        }
        LoggerSistema.getInstancia().log("Aldeano " + aldeano.getIdAldeano() + " sale del almacen de " + tipo);
        semDepositar.release();
    }
    /*
    public synchronized String estadoOcupantes() {
    return "Almacén de " + tipo + ": " +
        "\n  Aldeanos: " + ids(aldeanosOcupantes) +
        "\n  Guerreros: " + ids(guerrerosOcupantes) +
        "\n  Bárbaros: " + ids(barbarosOcupantes);
    }private String ids(ArrayList<? extends Thread> lista) {
    StringBuilder sb = new StringBuilder("[");
    for (Thread t : lista) {
        if (t instanceof Aldeano a) sb.append(a.getIdAldeano()).append(", ");
        else if (t instanceof Guerrero g) sb.append(g.getIdGuerrero()).append(", ");
        else if (t instanceof Barbaro b) sb.append(b.getIdBarbaro()).append(", ");
    }
    if (sb.length() > 1) sb.setLength(sb.length() - 2); // Quita la última coma
    sb.append("]");
    return sb.toString();
}*/

    public ArrayList<Guerrero> getGuerrerosOcupantes() {
        return guerrerosOcupantes;
    }
    public ArrayList<Aldeano> getAldeanosOcupantes() {
        return aldeanosOcupantes;
    }
    
    
public void Depositar(Aldeano aldeano, int cantidad) {
    String id = aldeano.getIdAldeano();
    boolean acquired = false;
    boolean lockTaken = false;

    try {
        // Esperar semáforo con chequeos de ataque/emergencia
        while (true) {
            control.esperarSiPausado();
            if (estaSiendoAtacado() && getGuerrerosOcupantes().isEmpty()) {
                LoggerSistema.getInstancia().log("Ataque al almacén de " + tipo + ". " + id + " huye antes de entrar.");
                aldeano.irAreaRecuperacion();
                return;
            }

            if (aldeano.hayEmergencia()) {
                LoggerSistema.getInstancia().log(id + " detecta emergencia antes de entrar al almacén de " + tipo + ". Se retira.");
                aldeano.esperarEmergencia();
                return;
            }
            
            LoggerSistema.getInstancia().log(id + " espera semáforo para entrar al almacén de " + tipo);
            if (semDepositar.tryAcquire(500, TimeUnit.MILLISECONDS)) {
                acquired = true;
                LoggerSistema.getInstancia().log(id + " adquirió semáforo para almacén de " + tipo);

                // Chequeo de ataque inmediato tras adquirir semáforo
                if (estaSiendoAtacado() && getGuerrerosOcupantes().isEmpty()) {
                    LoggerSistema.getInstancia().log("Ataque activo detectado justo al entrar. " + id + " huye.");
                    semDepositar.release();
                    LoggerSistema.getInstancia().log(id + " libera semáforo al huir.");
                    aldeano.irAreaRecuperacion();
                    return;
                }

                break;
            }
        }
        control.esperarSiPausado();
        entrarAlmacenAldeano(aldeano);
        LoggerSistema.getInstancia().log(id + " entra al almacén de " + tipo + " para depositar " + cantidad);

        lock.lock();
        lockTaken = true;
        try {
            while (cantidadActual >= capacidadMax) {
                if (estaSiendoAtacado() && getGuerrerosOcupantes().isEmpty()) {
                    LoggerSistema.getInstancia().log("Ataque mientras " + id + " esperaba espacio. Huye.");
                    salirAlmacenAldeano(aldeano);
                    lock.unlock();
                    LoggerSistema.getInstancia().log(id + " libera lock del almacén de " + tipo + " por huida.");
                    semDepositar.release();
                    LoggerSistema.getInstancia().log(id + " libera semáforo al huir.");
                    aldeano.irAreaRecuperacion();
                    return;
                }

                if (aldeano.hayEmergencia()) {
                    LoggerSistema.getInstancia().log(id + " detecta emergencia mientras espera espacio. Se retira.");
                    salirAlmacenAldeano(aldeano);
                    lock.unlock();
                    LoggerSistema.getInstancia().log(id + " libera lock del almacén de " + tipo + " por emergencia.");
                    semDepositar.release();
                    LoggerSistema.getInstancia().log(id + " libera semáforo por emergencia.");
                    aldeano.esperarEmergencia();
                    return;
                }

                LoggerSistema.getInstancia().log("Almacén de " + tipo + " lleno. " + id + " espera espacio.");
                control.esperarSiPausado();
                espacioDisponible.await(500, TimeUnit.MILLISECONDS);
            }

            // Último chequeo antes de depositar
            if (estaSiendoAtacado() && getGuerrerosOcupantes().isEmpty()) {
                LoggerSistema.getInstancia().log("Ataque justo antes de depositar. " + id + " huye.");
                salirAlmacenAldeano(aldeano);
                lock.unlock();
                LoggerSistema.getInstancia().log(id + " libera lock del almacén de " + tipo + " antes de huir.");
                semDepositar.release();
                LoggerSistema.getInstancia().log(id + " libera semáforo antes de huir.");
                aldeano.irAreaRecuperacion();
                return;
            }

            if (aldeano.hayEmergencia()) {
                LoggerSistema.getInstancia().log(id + " detecta emergencia justo antes de depositar. Se retira.");
                salirAlmacenAldeano(aldeano);
                lock.unlock();
                LoggerSistema.getInstancia().log(id + " libera lock del almacén de " + tipo + " por emergencia.");
                semDepositar.release();
                LoggerSistema.getInstancia().log(id + " libera semáforo por emergencia.");
                aldeano.esperarEmergencia();
                return;
            }

            // Depositar
            control.esperarSiPausado();
            Thread.sleep(1000 + new Random().nextInt(1000));
            int aDepositar = Math.min(cantidad, capacidadMax - cantidadActual);
            cantidadActual += aDepositar;

            LoggerSistema.getInstancia().log(id + " depositó " + aDepositar + " en el almacén de " + tipo + ". Total: " + cantidadActual);
            espacioDisponible.signalAll();

        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                LoggerSistema.getInstancia().log(id + " libera lock del almacén de " + tipo);
            }
        }

    } catch (InterruptedException e) {
        LoggerSistema.getInstancia().log(id + " fue interrumpido.");
        Thread.currentThread().interrupt();
    } finally {
        if (acquired) {
            control.esperarSiPausado();
            semDepositar.release();
            LoggerSistema.getInstancia().log(id + " ejecuta release() del semáforo de " + tipo);
        }
        salirAlmacenAldeano(aldeano);
        LoggerSistema.getInstancia().log(id + " sale del método Depositar()");
    }
}


    public synchronized void restar(int cantidad) {
        cantidadActual = Math.max(0, cantidadActual - cantidad);
    }


    
    public int saquear() {
        lock.lock();
        int saqueoReal; 
        try {
            control.esperarSiPausado();
            int cantidadOriginal = cantidadActual;

            // Calcular un porcentaje aleatorio entre 10% y 30%
            double porcentaje = 0.10 + rand.nextDouble() * 0.20; // entre 0.10 y 0.30
            int cantidadSaquear = (int) Math.round(cantidadActual * porcentaje);

            saqueoReal = Math.min(cantidadSaquear, cantidadActual);
            cantidadActual -= saqueoReal;

            LoggerSistema.getInstancia().log("¡Los bárbaros saquearon " + saqueoReal + " de " + tipo +
                "! (Antes: " + cantidadOriginal + ", Después: " + cantidadActual + ")");
        
            espacioDisponible.signalAll();
        } finally {
            lock.unlock();
        }
        return saqueoReal;
    }
}       