package ageofthreads;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.DefaultListModel;

public class CampamentoBarbaro extends Thread {
    private ArrayList<Barbaro> zonaPreparacion = new ArrayList<>();
    private ArrayList<Barbaro> campamento = new ArrayList<>();

    private AtomicInteger contadorBarbaros = new AtomicInteger(1);
    private Random rand = new Random();
    private ArrayList<AreaRecurso> areas;
    private ArrayList<Almacen> almacenes;
    private CentroUrbano centro;
    private ControlEjecucion control;

    

    public CampamentoBarbaro(ArrayList<AreaRecurso> areas, ArrayList<Almacen> almacenes, CentroUrbano centro, ControlEjecucion control) {
        this.areas = areas;
        this.almacenes = almacenes;
        this.centro = centro;
        this.control = control;
    }

    public ArrayList<Barbaro> getCampamento() {
        return campamento;
    }
     public DefaultListModel<String> listaZonaPreparacion() {
        
        DefaultListModel<String> modelo = new DefaultListModel<>();
        
        for (Barbaro b : zonaPreparacion) {
            modelo.addElement(b.getIdBarbaro());
        }
        return modelo;
    }
      public DefaultListModel<String> listaCampamento() {
        
        DefaultListModel<String> modelo = new DefaultListModel<>();
        
        for (Barbaro b : campamento) {
            modelo.addElement(b.getIdBarbaro());
        }
        return modelo;
    }

    
    
    
    
    
    @Override
    public void run() {
        while (contadorBarbaros.get() <= 999){ 
            control.esperarSiPausado();
            try {
                control.esperarSiPausado();
                Thread.sleep(15000 + rand.nextInt(15001)); // 15–30 s
               
                String id = String.format("B%03d", contadorBarbaros.getAndIncrement());
                Barbaro nuevo = new Barbaro(id);

                
                synchronized (zonaPreparacion) {
                    zonaPreparacion.add(nuevo);
                    LoggerSistema.getInstancia().log("Se ha generado el bárbaro " + id + " y entra en la zona de preparación.");
                }
                ArrayList<Barbaro> grupo = formarGrupos();
                   
                if (grupo != null) {
                    synchronized (zonaPreparacion) {
                        for (Barbaro b : grupo) {
                        zonaPreparacion.remove(b);
                        }
                    }    
                    GrupoAtaque ataque = new GrupoAtaque(grupo, areas,almacenes,this,centro,control);
                    ataque.start();
                }
                control.esperarSiPausado();
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Barbaro> getZonaPreparacion() {
        return zonaPreparacion;
    }
    

    


    private ArrayList<Barbaro> formarGrupos() {
        int totalCreados = contadorBarbaros.get() - 1;
        int tamGrupo = 3 + (totalCreados / 10); // Reglas de tamaño del grupo

        if (zonaPreparacion.size() >= tamGrupo) {
            ArrayList<Barbaro> grupo = new ArrayList<>();
            for (int i = 0; i < tamGrupo; i++) {
                grupo.add(zonaPreparacion.remove(0));
            }

            StringBuilder miembros = new StringBuilder();
            for (Barbaro b : grupo) {
                miembros.append(b.getIdBarbaro()).append(" ");
            }

            LoggerSistema.getInstancia().log("Nuevo grupo de ataque formado con " + tamGrupo + " bárbaros: " + miembros.toString().trim());
            return grupo;
        }

        return null; // No hay suficientes bárbaros para formar un grupo aún
    }
}