package ageofthreads;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.DefaultListModel;

public class AreaRecurso {
    private String tipo;
    private final Semaphore semAforoAldeanos = new Semaphore(4, true); // máximo 4 aldeanos a la vez
    private final Semaphore semAforoGuerrero = new Semaphore(3, true);
    
    private volatile boolean enAtaque = false; // indicador de ataque
    private Random rand = new Random();
    private ArrayList<Aldeano> aldeanosOcupantes = new ArrayList<>();
    private ArrayList<Guerrero> guerrerosOcupantes = new ArrayList<>();
    private ArrayList<Barbaro> barbarosOcupantes=new ArrayList<>();
    
    private boolean destrozado=false;
    private final Lock lockReparacion = new ReentrantLock(true);
    
    private int nivelHerramienta;
    private ControlEjecucion control;
    
    public AreaRecurso(String tipo, ControlEjecucion control ) {
        this.tipo = tipo;
        this.control = control;
      
    }   
    public DefaultListModel<String> combinarListasOcupacionArea() {
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


    
    public void setNivelHerramienta(int nivelHerramienta) {
        this.nivelHerramienta = nivelHerramienta;
    }

    
    public ArrayList<Guerrero> getGuerrerosOcupantes() {
        return guerrerosOcupantes;
    }
    public ArrayList<Aldeano> getAldeanosOcupantes() {
        return aldeanosOcupantes;
    }
    
    

    public String getTipo() {
        return tipo;
    }

    public boolean isDestrozado() {
        lockReparacion.lock();
        try {
            return destrozado;
        } finally {
            lockReparacion.unlock();
        }
    }
    
    public void entrarAreaGuerrero(Guerrero g) throws InterruptedException {
        control.esperarSiPausado();
        semAforoGuerrero.acquire();
        synchronized (guerrerosOcupantes) {
            guerrerosOcupantes.add(g);
        }
        LoggerSistema.getInstancia().log("Guerrero " + g.getIdGuerrero() + " entra al área de " + tipo);
        }
    
    public void salirAreaGuerrero(Guerrero g) {
        control.esperarSiPausado();
        synchronized (guerrerosOcupantes) {
            guerrerosOcupantes.remove(g);
        }
        LoggerSistema.getInstancia().log("Guerrero " + g.getIdGuerrero()+ " sale del área de " + tipo);
        semAforoGuerrero.release();
    }
    

    // Método que llama el aldeano antes de entrar
    public void entrarAreaAldeano(Aldeano aldeano) throws InterruptedException {
        control.esperarSiPausado();
        semAforoAldeanos.acquire();
        synchronized (aldeanosOcupantes) {
            aldeanosOcupantes.add(aldeano);
        }
        LoggerSistema.getInstancia().log("Aldeano " + aldeano.getIdAldeano() + " entra al área de " + tipo);
        }

    // Método que llama el aldeano al salir
    public void salirAreaAldeano(Aldeano aldeano) {
        control.esperarSiPausado();
        synchronized (aldeanosOcupantes) {
            aldeanosOcupantes.remove(aldeano);
        }
        LoggerSistema.getInstancia().log("Aldeano " + aldeano.getIdAldeano() + " sale del área de " + tipo);
        semAforoAldeanos.release();
    }

    // Método llamado por bárbaros cuando atacan el área
    public void iniciarAtaque(ArrayList<Barbaro> atacantes) {
        
        LoggerSistema.getInstancia().log("¡Ataque bárbaro en el área de " + tipo + "!");
        if (semAforoAldeanos.availablePermits() == 4) {  // Ningún aldeano dentro
            LoggerSistema.getInstancia().log("En el área de " + tipo + " no habia aldeanos durante el ataque.");
        }
        barbarosOcupantes=atacantes;
        setEnAtaque(true);
    }

    public void setEnAtaque(boolean enAtaque) {
        this.enAtaque = enAtaque;
    }
    
    // Método llamado por bárbaros al finalizar el ataque
    public void finalizarAtaque() {
        
        LoggerSistema.getInstancia().log("El ataque en el área de " + tipo + " ha finalizado.");
        control.esperarSiPausado();
        barbarosOcupantes.clear();
        setEnAtaque(false);
    }


    // Comprobación del aldeano si está siendo atacada
    public boolean isEnAtaque() {
        return enAtaque;
    }
    
    public void reparar(Aldeano aldeano) throws InterruptedException {
        control.esperarSiPausado();
        lockReparacion.lock();
        try {
            LoggerSistema.getInstancia().log("Aldeano " + aldeano.getIdAldeano() + " está reparando el área de " + tipo + " (estaba destrozada)");
            int tiempoReparacion = 3000 + rand.nextInt(2001); // 3-5 s
            control.esperarSiPausado();
            Thread.sleep(tiempoReparacion);
            destrozado = false;
            LoggerSistema.getInstancia().log("Aldeano " + aldeano.getIdAldeano() + " ha reparado el área de " + tipo);
        } finally {
        lockReparacion.unlock();
        }
    }

    // Método para la recolección (simplemente devuelve la cantidad recolectada)
    public int recolectarRecurso() {
        int mejoraPorNivel= nivelHerramienta*5;
        return (rand.nextInt(11) + 10)+mejoraPorNivel;  
    }

    public void setDestrozado() {
        lockReparacion.lock();
        try {
            this.destrozado = true;
        } finally {
            lockReparacion.unlock();
        }
    }

}
