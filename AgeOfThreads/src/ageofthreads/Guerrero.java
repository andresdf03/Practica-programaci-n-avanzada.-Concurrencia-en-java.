package ageofthreads;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Guerrero extends Thread {
    private String idGuerrero;
    private CentroUrbano centro;
    private ArrayList<AreaRecurso> areas;
    private ArrayList<Almacen> almacenes;
    private Random rand = new Random();
    private Object zonaAPatrullar;
    private volatile boolean enCombate=false;
    private volatile boolean enRecuperacion=false;
    private boolean bajoAtaque=false;
    private ControlEjecucion control;
    
    public Guerrero(String idGuerrero, CentroUrbano centro, ArrayList<AreaRecurso> areas, ArrayList<Almacen> almacenes, ControlEjecucion control) {
        this.idGuerrero = idGuerrero;
        this.centro = centro;
        this.areas = areas;
        this.almacenes = almacenes;
        this.control = control;
        
    }

    public boolean isBajoAtaque() {
        return bajoAtaque;
    }

    public void setBajoAtaque(boolean bajoAtaque) {
        this.bajoAtaque = bajoAtaque;
    }

    public boolean isEnCombate() {
        return enCombate;
    }

    public void setEnCombate(boolean enCombate) {
        this.enCombate = enCombate;
    }
    

 
    public void setZonaAPatrullar(Object zonaAPatrullar) {
        this.zonaAPatrullar = zonaAPatrullar;
    }

    @Override
    public void run() {
        try {
            centro.entrarCuartel(this);
            LoggerSistema.getInstancia().log("Guerrero " + idGuerrero + " se entrena...");
            control.esperarSiPausado();
            Thread.sleep(5000 + rand.nextInt(3000)); // 5-8 s
            centro.salirCuartel(this);
            LoggerSistema.getInstancia().log("Guerrero " + idGuerrero + " inicia patrullaje.");

            while (true) {
                control.esperarSiPausado();
                    if (enRecuperacion) {
                        try {
                            Thread.sleep(500); // espera pasiva hasta que se recupere
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Guerrero.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        continue;
                    }
                    
               Object zona = elegirZona();

                if (zona instanceof AreaRecurso) {
                    AreaRecurso area = (AreaRecurso) zona;
                    while(area.isEnAtaque()){
                        LoggerSistema.getInstancia().log("Guerrero " + idGuerrero + " espera para entrar al área de " + area.getTipo() + " (en ataque)");
                        control.esperarSiPausado();
                        Thread.sleep(1000);
                    }
                    area.entrarAreaGuerrero(this);
                    LoggerSistema.getInstancia().log("Guerrero " + idGuerrero + " vigilando área de " + area.getTipo());
                    control.esperarSiPausado();
                    Thread.sleep(3000 + rand.nextInt(2000)); // 3–5 s
                    while(area.isEnAtaque()&& !enRecuperacion) {
                        LoggerSistema.getInstancia().log("Guerrero " + idGuerrero + " permanece defendiendo el área de " + area.getTipo() + " (en ataque)");
                        control.esperarSiPausado();
                        Thread.sleep(1000);
                    }
                    if(area.getGuerrerosOcupantes().contains(this)){
                        area.salirAreaGuerrero(this);
                    }
                    
                } 
                else if (zona instanceof Almacen) {
                   
                    Almacen almacen = (Almacen) zona; 
                    while(almacen.isEnAtaque()){
                        LoggerSistema.getInstancia().log("Guerrero " + idGuerrero + " espera para entrar al almacen de " + almacen.getTipo() + " (en ataque)");
                        control.esperarSiPausado();
                        Thread.sleep(1000);
                    }
                    almacen.entrarAlmacenGuerrero(this);
                    LoggerSistema.getInstancia().log("Guerrero " + idGuerrero + " vigilando almacén de " + almacen.getTipo());
                    control.esperarSiPausado();
                    Thread.sleep(3000 + rand.nextInt(2000)); // 3–5 s
                    while(almacen.isEnAtaque()&& !enRecuperacion) {
                        LoggerSistema.getInstancia().log("Guerrero " + idGuerrero + " permanece defendiendo el almacen de " + almacen.getTipo() + " (en ataque)");
                        control.esperarSiPausado();
                        Thread.sleep(1000);
                    }
                    if(almacen.getGuerrerosOcupantes().contains(this)){
                        almacen.salirAlmacenGuerrero(this);

                    }
                }

                control.esperarSiPausado();
                Thread.sleep(2000); // tiempo entre patrullajes
                // En la implementación completa: lógica de combate si hay ataque en esta zona
            }

        } catch (InterruptedException e) {
            LoggerSistema.getInstancia().log("Guerrero " + idGuerrero + " fue interrumpido.");
        }
    }
    public void irARecuperacion() {
        enRecuperacion = true;
        LoggerSistema.getInstancia().log("Guerrero " + idGuerrero + " ha sido derrotado y va a recuperación.");
        centro.entrarAreaRecuperacionGuerrero(this);
    
        try {
            int tiempo=10000 + rand.nextInt(5001);
            control.esperarSiPausado();
            Thread.sleep(tiempo); // Simula recuperación 10s (puedes ajustarlo)
            LoggerSistema.getInstancia().log("Guerrero " + idGuerrero + " ha terminado su recuperación (" + (tiempo / 1000) + " s)");
        } catch (InterruptedException e) {
            
            LoggerSistema.getInstancia().log("Error durante recuperación del Guerrero " + idGuerrero + ".");
        }finally{
            centro.salirAreaRecuperacionGuerrero(this);
            enRecuperacion = false;
        }
    }

    private Object elegirZona() {
        int zona = rand.nextInt(6); // 0–2 almacenes, 3–5 áreas
        switch (zona) {
            case 0: return almacenes.get(0);
            case 1: return almacenes.get(1);
            case 2: return almacenes.get(2);
            case 3: return areas.get(0);
            case 4: return areas.get(1);
            default: return areas.get(2);
        }
    }
    public String getIdGuerrero() {
        return idGuerrero;
    }
}
