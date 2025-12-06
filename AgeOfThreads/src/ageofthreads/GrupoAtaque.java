package ageofthreads;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GrupoAtaque extends Thread {
    private ArrayList<Barbaro> miembros;
    private AreaRecurso areaObjetivo;
    private Almacen almacenObjetivo;
    private ArrayList<AreaRecurso> areas;
    private ArrayList<Almacen> almacenes;
    private Object objetivo; // Puede ser AreaRecurso o Almacen
    private CampamentoBarbaro campamentoBarbaro;
    private Random rand = new Random();

    private CentroUrbano centro;
    private ControlEjecucion control;

    

    public GrupoAtaque(ArrayList<Barbaro> miembros, ArrayList<AreaRecurso> areas, ArrayList<Almacen> almacenes, CampamentoBarbaro campamentoBarbaro, CentroUrbano centro, ControlEjecucion control) {
        this.miembros = miembros;
        this.areas = areas;
        this.almacenes = almacenes;
        this.campamentoBarbaro = campamentoBarbaro;
        this.centro = centro;
        this.control = control;
    }

  
    
    private AreaRecurso elegirAreaAleatoria() {
        return areas.get(rand.nextInt(areas.size()));
    }
    
    private Almacen elegirAlmaacenAleatoria() {
        return almacenes.get(rand.nextInt(almacenes.size()));
    }
    
     private Object elegirObjetivo() {
        Random rand = new Random();
        if (rand.nextInt(100) < 60) {
            // 60% atacar almacén
            return elegirAlmaacenAleatoria();
        } else {
            // 40% atacar área
            return elegirAreaAleatoria();
        }
    }
     
    @Override
    public void run() {

        List<Barbaro> sobrevivientes = null;
        control.esperarSiPausado(); // espera justo antes de comenzar
        objetivo = (elegirObjetivo());//;
        String tipoObjetivo = (objetivo instanceof AreaRecurso)
            ? ((AreaRecurso) objetivo).getTipo()
            : ((Almacen) objetivo).getTipo();

        LoggerSistema.getInstancia().log("¡Ataque bárbaro en el " +
           (objetivo instanceof AreaRecurso ? "área de " : "almacén de ") +
            tipoObjetivo + " con " + miembros.size() + " bárbaros!");

        try {
            if (objetivo instanceof AreaRecurso area) {
                area.iniciarAtaque(miembros);
                
            // Combatir solo si hay defensores
                List<Guerrero> defensores = area.getGuerrerosOcupantes();
                if (!defensores.isEmpty()) {
                        combatirConGuerreros(defensores);
                }
                


                // Solo si sobreviven bárbaros
                if (!miembros.isEmpty()) {
                    control.esperarSiPausado();
                    Thread.sleep(1000); // espera de observación
                    Thread.sleep(1000 + rand.nextInt(1001)); // 1–2 s adicionales
                    area.setDestrozado();
                    
                    LoggerSistema.getInstancia().log("El área de " + area.getTipo() + " ha sido destrozada tras el ataque bárbaro.");
                    
                }
                
                sobrevivientes = new ArrayList<>(miembros); 

                area.finalizarAtaque();
                
            }else if (objetivo instanceof Almacen almacen) {
                almacen.iniciarAtaque(miembros);
                

                
                List<Guerrero> defensores = almacen.getGuerrerosOcupantes();
                if (!defensores.isEmpty()) {
                    combatirConGuerreros(defensores);
                }
                control.esperarSiPausado();
                Thread.sleep(300); // breve espera antes de volver a revisar
                

                 // Si aún quedan bárbaros, saquean
                if (!miembros.isEmpty()) {
                    control.esperarSiPausado();
                    Thread.sleep(1000); // espera de observación
                     Thread.sleep(1000 + rand.nextInt(1001)); // 1–2 s adicionales
                    int saqueado = almacen.saquear();
                    LoggerSistema.getInstancia().log("Los bárbaros han saqueado " + saqueado + " del almacén de " + almacen.getTipo() + ". Se retiran.");
                }
                sobrevivientes = new ArrayList<>(miembros); // ✅ sin "List<Barbaro>" adelante

                almacen.finalizarAtaque();
                
            }
            
            synchronized (campamentoBarbaro.getCampamento()) {
                for (Barbaro b : sobrevivientes) {
                    campamentoBarbaro.getCampamento().add(b);
                    LoggerSistema.getInstancia().log("El bárbaro " + b.getIdBarbaro() + " regresa al campamento.");
                }
            }
            LoggerSistema.getInstancia().log(" Bárbaros esperando 40s antes de volver al sistema...");

        // Retirada: bárbaros vivos vuelven al campamento tras 40s
            control.esperarSiPausado();
            Thread.sleep(4000);
            LoggerSistema.getInstancia().log(" Se cumplió la espera de 40s. Procesando regreso de bárbaros...");
            LoggerSistema.getInstancia().log(" Tamaño de miembros antes de regresar: " + sobrevivientes.size());
            for (Barbaro b : sobrevivientes) {
                LoggerSistema.getInstancia().log("️ Intentando mover a " + b.getIdBarbaro() + " de campamento a zona de preparación...");
                synchronized (campamentoBarbaro.getCampamento()) {
                    control.esperarSiPausado();
                    synchronized (campamentoBarbaro.getZonaPreparacion()) {
                        control.esperarSiPausado();
                        campamentoBarbaro.getCampamento().remove(b);
                        campamentoBarbaro.getZonaPreparacion().add(b);
                    }
                }
                LoggerSistema.getInstancia().log(" Bárbaro " + b.getIdBarbaro() + " ahora está en zona de preparación.");
            }



            

        } catch (InterruptedException e) {
            LoggerSistema.getInstancia().log("El grupo de bárbaros fue interrumpido durante el ataque.");
        }
    }


    private void combatirConGuerreros(List<Guerrero> defensores) {
    while (!miembros.isEmpty() && hayDefensoresDisponibles(defensores)) {
        List<Barbaro> rondaActual = new ArrayList<>(miembros); 
        for (Barbaro barbaro : rondaActual) {
            control.esperarSiPausado();
            Guerrero guerrero = seleccionarGuerreroLibre(defensores);

            if (guerrero == null) {
                LoggerSistema.getInstancia().log("Bárbaro " + barbaro.getIdBarbaro() + " no encontró guerrero disponible. Espera...");
                try { 
                    control.esperarSiPausado();
                    Thread.sleep(300); 
                } catch (InterruptedException e) {}
                continue;
            }

            guerrero.setEnCombate(true);
            LoggerSistema.getInstancia().log("Bárbaro " + barbaro.getIdBarbaro() + " combate contra Guerrero " + guerrero.getIdGuerrero());

            try {
                control.esperarSiPausado();
                Thread.sleep(500 + rand.nextInt(501));
            } catch (InterruptedException e) {}
            control.esperarSiPausado();     
            int probabilidadBarbaro = 50 - centro.getNivelMejoraArmas() * 5;
            boolean ganaBarbaro = rand.nextInt(100) < probabilidadBarbaro;


            if (ganaBarbaro) {
                LoggerSistema.getInstancia().log("Bárbaro " + barbaro.getIdBarbaro() + " ha vencido al Guerrero " + guerrero.getIdGuerrero());
                if (objetivo instanceof AreaRecurso area) {
                    area.salirAreaGuerrero(guerrero);
                    
                } else if (objetivo instanceof Almacen almacen) {
                    
                    almacen.salirAlmacenGuerrero(guerrero);
                }
                control.esperarSiPausado();
                defensores.remove(guerrero);
                guerrero.setEnCombate(false);
                new Thread(() -> guerrero.irARecuperacion()).start();                
            } else {
                LoggerSistema.getInstancia().log("Guerrero " + guerrero.getIdGuerrero() + " ha vencido al Bárbaro " + barbaro.getIdBarbaro());
                guerrero.setEnCombate(false);
                control.esperarSiPausado();
                miembros.remove(barbaro);
                new Thread(() -> {
                    control.esperarSiPausado();
                    try {
                        control.esperarSiPausado();
                        campamentoBarbaro.getCampamento().add(barbaro);
                        LoggerSistema.getInstancia().log("El bárbaro " + barbaro.getIdBarbaro() + " regresa al campamento. Espera 60s antes de poder atacar otra vez.");
                        control.esperarSiPausado();
                        Thread.sleep(60000); // Espera obligatoria de 60 segundos
                        campamentoBarbaro.getCampamento().remove(barbaro);
                        synchronized (campamentoBarbaro.getZonaPreparacion()) {
                            campamentoBarbaro.getZonaPreparacion().add(barbaro);
                            LoggerSistema.getInstancia().log("El bárbaro " + barbaro.getIdBarbaro() + " ha salido de cooldown y puede unirse a un nuevo grupo.");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }
}
    
    private Guerrero seleccionarGuerreroLibre(List<Guerrero> defensores) {
        ArrayList<Guerrero> disponibles = new ArrayList<>();
        synchronized (defensores) {
            for (Guerrero g : defensores) {
                if (!g.isEnCombate()) {
                    disponibles.add(g);
                }
            }
        }

        if (disponibles.isEmpty()) return null;
        return disponibles.get(new Random().nextInt(disponibles.size()));
    }
    private boolean hayDefensoresDisponibles(List<Guerrero> defensores) {
        for (Guerrero g : defensores) {
            if (!g.isEnCombate()) return true;
        }
        return false;
    }


}