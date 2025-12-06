package ageofthreads;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.DefaultListModel;

public class CentroUrbano {
    private Random rand = new Random();
    private String recurso;
    private ArrayList<Aldeano> listaCasa =new ArrayList();
    private ArrayList<Aldeano> listaPlaza =new ArrayList();
    private ArrayList<Aldeano> listaAreaRecuperacionAldeanos =new ArrayList();
    private ArrayList<Guerrero>listaAreaRecuperacionGuerreros=new ArrayList();
    private final AtomicBoolean emergenciaActiva = new AtomicBoolean(false);
    private ArrayList<Guerrero> cuartel =new ArrayList();
    
    private ArrayList<Almacen> almacenes;
    private ArrayList<AreaRecurso> areasRecursos;
    private ArrayList<Aldeano> aldeanos = new ArrayList<>();
    private ArrayList<Guerrero> guerreros=new ArrayList<>();
    private int contadorAldeanos=0;
    private int contadorGuerreros=0;

    
    private int nivelMejoraHerramientas = 0;
    private int nivelMejoraAlmacenes = 0;
    private int nivelMejoraArmas=0;
    private ControlEjecucion control;

    
    
    public DefaultListModel<String> combinarListasAreaRecuperacion() {
        DefaultListModel<String> modelo = new DefaultListModel<>();

        for (Aldeano a : listaAreaRecuperacionAldeanos) {
            modelo.addElement(a.getIdAldeano());
        }

        for (Guerrero g : listaAreaRecuperacionGuerreros) {
            modelo.addElement(g.getIdGuerrero());
        }

        return modelo;
    }
    
    public DefaultListModel<String> listaCuartel() {
        
        DefaultListModel<String> modelo = new DefaultListModel<>();
        
        for (Guerrero g : cuartel) {
            modelo.addElement(g.getIdGuerrero());
        }
        return modelo;
    }
    public DefaultListModel<String> mostrarListaCasa() {
        
        DefaultListModel<String> modelo = new DefaultListModel<>();
        
        for (Aldeano a : listaCasa) {
            if (a != null) {
                modelo.addElement(a.getIdAldeano());
            }
            
            
        }
        return modelo;
    }
    public DefaultListModel<String> mostrarPlazaCentral(){
        
        DefaultListModel<String> modelo = new DefaultListModel<>();
        
        for (Aldeano a : listaPlaza) {
            modelo.addElement(a.getIdAldeano());
        }
        return modelo;
    }


    public ArrayList<AreaRecurso> getAreasRecursos() {
        return areasRecursos;
    }
    public ArrayList<Aldeano> getListaCasa() {
        return listaCasa;
    }

    public ArrayList<Aldeano> getListaPlaza() {
        return listaPlaza;
    }

    public ArrayList<Aldeano> getListaAreaRecuperacionAldeanos() {
        return listaAreaRecuperacionAldeanos;
    }

    public ArrayList<Guerrero> getListaAreaRecuperacionGuerreros() {
        return listaAreaRecuperacionGuerreros;
    }

    public ArrayList<Guerrero> getCuartel() {
        return cuartel;
    }

    public ArrayList<Aldeano> getAldeanos() {
        return aldeanos;
    }
    
    
    


    
    
    public CentroUrbano(ArrayList<Almacen> almacenes,ArrayList<AreaRecurso> areas, ControlEjecucion control) {
        this.almacenes = almacenes;
        this.areasRecursos=areas;
        this.control = control;
    }
    
    public String elegirRecurso(){
        int i = rand.nextInt(3);
        if(i==0){
            recurso="oro";
        }else if(i==1){
            recurso="madera";
        }
        else{
            recurso="comida";
        }return recurso;
    }
    public void registrarAldeano(Aldeano a) {
        synchronized (aldeanos) {
            aldeanos.add(a);
        }
    }public boolean emergenciaActiva() {
        return emergenciaActiva.get();
    }
        /*
    }public void activarEmergencia() {
        if (!emergenciaActiva.get()) {
            emergenciaActiva.set(true);
            LoggerSistema.getInstancia().log("LLAMADA DE EMERGENCIA ACTIVADA. Todos los aldeanos regresan a casa.");
        } else {
            // Desactivar emergencia: notificar a todos
            emergenciaActiva.set(false);
            synchronized (aldeanos) {
                for (Aldeano a : aldeanos) {
                    synchronized (a) {
                        a.notify(); // los despierta
                    }
                }
            }
            LoggerSistema.getInstancia().log("EMERGENCIA FINALIZADA. Los aldeanos reanudan su trabajo.");
        }
    }*/
    public void activarEmergencia() {
        if (!emergenciaActiva.get()) {
            emergenciaActiva.set(true);
            LoggerSistema.getInstancia().log("LLAMADA DE EMERGENCIA ACTIVADA. Todos los aldeanos regresan a casa.");
        }
    }

    public void desactivarEmergencia() {
        if (emergenciaActiva.get()) {
            emergenciaActiva.set(false);
            synchronized (aldeanos) {
                for (Aldeano a : aldeanos) {
                    synchronized (a) {
                    a.notify(); // los despierta
                    }
                }
            }
            LoggerSistema.getInstancia().log("EMERGENCIA FINALIZADA. Los aldeanos reanudan su trabajo.");
        }
    }

    public void entrarCuartel(Guerrero g){
        control.esperarSiPausado();
        cuartel.add(g);
    }public void salirCuartel(Guerrero g){
        control.esperarSiPausado();
        cuartel.remove(g);
    }
    public void entrarCasa(Aldeano aldeano){
        control.esperarSiPausado();
        listaCasa.add(aldeano);
    }public void salirCasa(Aldeano aldeano){
        control.esperarSiPausado();
        listaCasa.remove(aldeano);
        
    }public void entrarPlaza(Aldeano aldeano){
        control.esperarSiPausado();
        listaPlaza.add(aldeano);
    }public void salirPlaza(Aldeano aldeano){
        control.esperarSiPausado();
        listaPlaza.remove(aldeano);
        
    }public void entrarAreaRecuperacionAldeano(Aldeano aldeano){
        control.esperarSiPausado();
        listaAreaRecuperacionAldeanos.add(aldeano);
    }public void salirAreaRecuperacionAldeano(Aldeano aldeano){
        control.esperarSiPausado();
        listaAreaRecuperacionAldeanos.remove(aldeano);
    }
    public void entrarAreaRecuperacionGuerrero(Guerrero g){
        control.esperarSiPausado();
        listaAreaRecuperacionGuerreros.add(g);
    }public void salirAreaRecuperacionGuerrero(Guerrero g){
        control.esperarSiPausado();
        listaAreaRecuperacionGuerreros.remove(g);
    }
    
    public synchronized Aldeano nuevoAldeano(){
        control.esperarSiPausado();
        contadorAldeanos++;
        String idAldeano = String.format("A%03d", contadorAldeanos);
        Aldeano aldeano = new Aldeano(idAldeano, this, areasRecursos, almacenes, control);
        registrarAldeano(aldeano);
        aldeano.start();
        return aldeano;
    }
    public synchronized Guerrero nuevoGuerrero(){
        control.esperarSiPausado();
        contadorGuerreros++;
        String idGuerrero = String.format("G%03d", contadorGuerreros);
        Guerrero guerrero = new Guerrero(idGuerrero, this, areasRecursos, almacenes, control);
        
        guerrero.start();
        return guerrero;
    }
    
    public synchronized boolean comprarAldeano() {
        control.esperarSiPausado();
        if (almacenes.get(0).getCantidad() >= 50) {
            almacenes.get(0).restar(50);

            //contadorAldeanos++;
            Aldeano nuevo =nuevoAldeano();
            LoggerSistema.getInstancia().log("Se ha comprado un nuevo aldeano " + nuevo.getIdAldeano() + ". -50 comida");
            return true;
        }
        LoggerSistema.getInstancia().log("No hay suficiente comida para comprar un aldeano. Requiere: 50 comida.");
        System.out.println("No hay suficiente comida para comprar un aldeano. Requiere: 50 comida.");
        return false;
    }
    public synchronized boolean comprarGuerrero() {
        control.esperarSiPausado();
        Almacen comida = almacenes.get(0);  // Se asume que almacén 0 es comida
        Almacen oro = almacenes.get(2);     // Se asume que almacén 2 es oro
        Almacen madera = almacenes.get(1);  // Se asume que almacén 1 es madera

        if (comida.getCantidad() >= 50 && oro.getCantidad() >= 80 && madera.getCantidad()>= 50) {
            comida.restar(50);
            oro.restar(80);
            madera.restar(50);

            Guerrero nuevo = nuevoGuerrero();
            LoggerSistema.getInstancia().log("Se ha comprado un nuevo guerrero " + nuevo.getIdGuerrero() + ". -50 comida, -80 oro y -50 madera.");
            return true;
        } else {
            LoggerSistema.getInstancia().log("No hay suficientes recursos para comprar un guerrero. Requiere: 50 comida, 80 oro y 50 madera.");
            System.out.println("No hay suficiente comida para comprar un guerrero.Requiere: 50 comida, 80 oro y 50 madera.");
            return false;
        }
    }

    public boolean mejorarAlmacenes() {
        if (nivelMejoraAlmacenes >= 3) {
            LoggerSistema.getInstancia().log("Los almacenes ya están al nivel máximo de mejora.");
            System.out.println("Los almacenes ya están al nivel máximo de mejora.");
            return false;
        }
        Almacen madera = almacenes.get(1);
        Almacen oro = almacenes.get(2);
        if (madera.getCantidad() >= 150 && oro.getCantidad() >= 50) {
            madera.restar(150);
            oro.restar(50);
            for (Almacen a : almacenes) {
                a.setCapacidadMax(a.getCapacidadMax() + 100);
            }
            nivelMejoraAlmacenes++;
            LoggerSistema.getInstancia().log("Mejora de almacenes realizada. Nivel actual: " + nivelMejoraAlmacenes +
                ". Capacidad total de cada almacén aumentada en +100.");
            
            System.out.println("Mejora de almacenes realizada. Nivel actual: " + nivelMejoraAlmacenes +
                ". Capacidad total de cada almacén aumentada en +100.");
            return true;
        } else {
            LoggerSistema.getInstancia().log("Recursos insuficientes para mejorar almacenes. Requiere: 150 madera, 50 oro.");
            System.out.println("Recursos insuficientes para mejorar almacenes. Requiere: 150 madera, 50 oro.");
            return false;
        }
    }


    public boolean mejorarHerramientas() {
        if (nivelMejoraHerramientas >= 3) {
            LoggerSistema.getInstancia().log("Las herramientas ya están al nivel máximo de mejora.");
            System.out.println("Las herramientas ya están al nivel máximo de mejora.");
            return false;
        }
        Almacen madera = almacenes.get(1);
        Almacen oro = almacenes.get(2);
        if (madera.getCantidad() >= 120 && oro.getCantidad() >= 80) {
            madera.restar(120);
            oro.restar(80);
            nivelMejoraHerramientas++;
            areasRecursos.get(0).setNivelHerramienta(nivelMejoraHerramientas);
            areasRecursos.get(1).setNivelHerramienta(nivelMejoraHerramientas);
            areasRecursos.get(2).setNivelHerramienta(nivelMejoraHerramientas);
            LoggerSistema.getInstancia().log("Mejora de herramientas realizada. Nivel actual: " + nivelMejoraHerramientas +
                ". Todas las áreas recolectan +5 unidades más por nivel.");
            System.out.println("Mejora de herramientas realizada. Nivel actual: " + nivelMejoraHerramientas +
                ". Todas las áreas recolectan +5 unidades más por nivel.");
            return true;
        } else {
            LoggerSistema.getInstancia().log("Recursos insuficientes para mejorar herramientas. Requiere: 120 madera, 80 oro.");
            System.out.println("Recursos insuficientes para mejorar herramientas. Requiere: 120 madera, 80 oro.");
            return false;
        }
    }
    public boolean mejorarArmas() {
        if (nivelMejoraArmas >= 5) {
            LoggerSistema.getInstancia().log("Las armas ya están al nivel máximo de mejora.");
            System.out.println("Las armas ya están al nivel máximo de mejora.");
            return false;
        }

        Almacen comida = almacenes.get(0); // comida
        Almacen oro = almacenes.get(2);    // oro

        if (comida.getCantidad() >= 150 && oro.getCantidad() >= 100) {
            comida.restar(150);
            oro.restar(100);
            nivelMejoraArmas++;

            LoggerSistema.getInstancia().log("Mejora de armas realizada. Nivel actual: " + nivelMejoraArmas +
                ". Probabilidad de victoria de guerreros aumentada a " + (50 + nivelMejoraArmas * 5) + "%.");
            System.out.println("Mejora de armas realizada. Nivel actual: " + nivelMejoraArmas +
                ". Probabilidad de victoria de guerreros aumentada a " + (50 + nivelMejoraArmas * 5) + "%.");
            return true;
        } else {
            LoggerSistema.getInstancia().log("Recursos insuficientes para mejorar armas. Requiere: 150 comida, 100 oro.");
            System.out.println("Recursos insuficientes para mejorar armas. Requiere: 150 comida, 100 oro.");
            return false;
        }
    }
    public ArrayList<Almacen> getAlmacenes() {
        return almacenes;
    }
    
    public int getNivelMejoraArmas() {
        return nivelMejoraArmas;
    }

    public int getNivelMejoraHerramientas() {
        return nivelMejoraHerramientas;
    }

    public int getNivelMejoraAlmacenes() {
        return nivelMejoraAlmacenes;
    }

}
