package ageofthreads;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Aldeano extends Thread {
    private String idAldeano;
    private Random rand = new Random();
    private String recurso;
    private CentroUrbano centroUrbano;
    private ArrayList<AreaRecurso> areasRecursos;
    private ArrayList<Almacen> almacenesRecursos;
    private Almacen almacenRecurso;
    private AreaRecurso areaRecurso;
    private int cantidadRecolectada;
    private ControlEjecucion control;


    public Aldeano(String idAldeano, CentroUrbano centroUrbano, ArrayList<AreaRecurso> areasRecursos, ArrayList<Almacen> almacenesRecursos, ControlEjecucion control) {
        this.idAldeano = idAldeano;
        this.centroUrbano = centroUrbano;
        this.areasRecursos = areasRecursos;
        this.almacenesRecursos = almacenesRecursos;
        this.control = control;
    }
    public boolean hayEmergencia(){
        return centroUrbano.emergenciaActiva();
    }

    @Override
    public void run() {
        try {
            centroUrbano.entrarCasa(this);
            LoggerSistema.getInstancia().log("Aldeano " + idAldeano + " en casa principal");
            control.esperarSiPausado();
            Thread.sleep(2000 + rand.nextInt(2001));
            centroUrbano.salirCasa(this);
        } catch (InterruptedException ex) {
            Logger.getLogger(Aldeano.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (true) {
            control.esperarSiPausado();
            LoggerSistema.getInstancia().log("Aldeano " + idAldeano + " inicia nuevo ciclo");

            if (centroUrbano.emergenciaActiva()) {
                try {
                    esperarEmergencia();
                    continue;
                } catch (InterruptedException e) {
                
                }
            }
            try {
                if (centroUrbano.emergenciaActiva()) {
                    esperarEmergencia();
                    continue;
                }
                control.esperarSiPausado();
                centroUrbano.entrarPlaza(this);
                LoggerSistema.getInstancia().log("Aldeano " + idAldeano + " planificando su trabajo");
                control.esperarSiPausado();
                Thread.sleep(1000 + rand.nextInt(1001));
                recurso = centroUrbano.elegirRecurso();
                seleccionarArea();
                seleccionarAlmacen();
                centroUrbano.salirPlaza(this);

                recolectarRecurso();
                
                
                if (centroUrbano.emergenciaActiva()) {
                    LoggerSistema.getInstancia().log("Aldeano " + idAldeano + " detecta emergencia antes de intentar depositar.");
                    esperarEmergencia();
                    continue; // volver al inicio del ciclo
                    }

                if (cantidadRecolectada > 0) {
                    while(almacenRecurso.estaSiendoAtacado()&&almacenRecurso.getGuerrerosOcupantes().isEmpty())
                        {
                        LoggerSistema.getInstancia().log("Aldeano " + idAldeano + " no puede depositar en " + recurso + " por ataque al almacén. Espera...");
                        control.esperarSiPausado();
                        Thread.sleep(600);
                    }
                    almacenRecurso.Depositar(this, cantidadRecolectada);
                }

            } catch (InterruptedException ex) {
                Logger.getLogger(Aldeano.class.getName()).log(Level.SEVERE, null, ex);
            }
            LoggerSistema.getInstancia().log("Aldeano " + idAldeano + " termina ciclo, vuelve a planificar");
            control.esperarSiPausado();
        }
    }
    
    private void seleccionarArea() {
        for (AreaRecurso ar : areasRecursos) {
            if (ar.getTipo().equalsIgnoreCase(recurso)) {
                areaRecurso = ar;
                break;
            }
        }
    }
    


    private void seleccionarAlmacen() {
        for (Almacen alr : almacenesRecursos) {
            if (alr.getTipo().equalsIgnoreCase(recurso)) {
                almacenRecurso = alr;
                break;
            }
        }
    }

    private void recolectarRecurso() throws InterruptedException {
        

        if (areaRecurso.isEnAtaque()&& areaRecurso.getGuerrerosOcupantes().isEmpty()) {
            LoggerSistema.getInstancia().log("Aldeano " + idAldeano + " no puede recolectar en " + recurso + " por ataque bárbaro.");
            cantidadRecolectada = 0;
            
            irAreaRecuperacion();
            
            return;
        }
        control.esperarSiPausado();
        areaRecurso.entrarAreaAldeano(this);
        if (centroUrbano.emergenciaActiva()) {
                LoggerSistema.getInstancia().log("Aldeano " + idAldeano + " interrumpe recolección por emergencia.");
                areaRecurso.salirAreaAldeano(this);
                esperarEmergencia();
                cantidadRecolectada = 0;
                return;
        }

        if (areaRecurso.isDestrozado()) {
            areaRecurso.reparar(this);
            areaRecurso.salirAreaAldeano(this);
            cantidadRecolectada = 0;
            return;  // Se va sin recolectar
        }
        LoggerSistema.getInstancia().log("Aldeano " + idAldeano + " recolectando " + recurso);
        int tiempoRecoleccion = 5000 + rand.nextInt(5001); // 5-10 seg
        long inicio = System.currentTimeMillis();

        try {
            while (System.currentTimeMillis() - inicio < tiempoRecoleccion) {
                
                control.esperarSiPausado();
                Thread.sleep(500); // comprobamos cada 0.5 seg si hay ataque o interrupción
                if (centroUrbano.emergenciaActiva()) {
                    LoggerSistema.getInstancia().log("Aldeano " + idAldeano + " interrumpe recolección por emergencia.");
                    areaRecurso.salirAreaAldeano(this);
                    esperarEmergencia();
                     cantidadRecolectada = 0;
                return;
            }

                if (Thread.interrupted()) {
                    LoggerSistema.getInstancia().log("Aldeano " + idAldeano + " fue interrumpido por ataque mientras recolectaba " + recurso);
                    areaRecurso.salirAreaAldeano(this);
                    irAreaRecuperacion();
                    cantidadRecolectada = 0;
                    return;
                }
                if (areaRecurso.isEnAtaque()&& areaRecurso.getGuerrerosOcupantes().isEmpty()) {
                    LoggerSistema.getInstancia().log("Aldeano " + idAldeano + " fue interrumpido por ataque mientras recolectaba " + recurso);
                    areaRecurso.salirAreaAldeano(this);
                    irAreaRecuperacion();
                    cantidadRecolectada = 0;
                    return;
                }
            }
            cantidadRecolectada = areaRecurso.recolectarRecurso();
            LoggerSistema.getInstancia().log("Aldeano " + idAldeano + " ha recolectado " + cantidadRecolectada + " de " + recurso);
            areaRecurso.salirAreaAldeano(this);
        } catch (InterruptedException e) {
            LoggerSistema.getInstancia().log("Aldeano " + idAldeano + " fue interrumpido bruscamente por ataque.");
            areaRecurso.salirAreaAldeano(this);
            irAreaRecuperacion();
            cantidadRecolectada = 0;
            throw e; // importante para mantener la semántica de interrupción
        }
    }

    
    public void interrumpirPorAtaque() {
        this.interrupt();
    }
    public void esperarEmergencia() throws InterruptedException{
        centroUrbano.entrarCasa(this);
        LoggerSistema.getInstancia().log("Aldeano " + idAldeano + " se refugia por emergencia.");
        control.esperarSiPausado();
        Thread.sleep(2000 + rand.nextInt(3001)); // entre 2 y 5 s
        
        synchronized (this) {
            wait(); // espera indefinidamente hasta que le "avisen"
        }
        centroUrbano.salirCasa(this);
        LoggerSistema.getInstancia().log("Aldeano " + idAldeano + " reanuda actividad tras emergencia.");
    }

    public void irAreaRecuperacion() throws InterruptedException {
        centroUrbano.entrarAreaRecuperacionAldeano(this);
        int tiempoRecuperacion = 12000 + rand.nextInt(3001); // 12-15 seg
        LoggerSistema.getInstancia().log("Aldeano " + idAldeano + " recuperándose tras ataque (" + tiempoRecuperacion / 1000 + " seg)");
        control.esperarSiPausado();
        Thread.sleep(tiempoRecuperacion);
        centroUrbano.salirAreaRecuperacionAldeano(this);
    }
    
    public String getIdAldeano() {
        return idAldeano;
    }

}
