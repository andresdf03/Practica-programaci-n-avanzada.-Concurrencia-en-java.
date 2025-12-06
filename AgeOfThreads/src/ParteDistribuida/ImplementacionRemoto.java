
package ParteDistribuida;

import ageofthreads.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListModel;

public class ImplementacionRemoto extends UnicastRemoteObject implements Remoto {

    private CentroUrbano centro;
    private CampamentoBarbaro campamento;
    private List<Almacen> almacenes;
    private List<AreaRecurso> areas;
    private ControlEjecucion control;

    public ImplementacionRemoto(Simulador simulador) throws RemoteException{
        this.centro = simulador.getCentroUrbano();
        this.campamento = simulador.getCampamento();
        this.almacenes = simulador.getCentroUrbano().getAlmacenes();
        this.areas = simulador.getCentroUrbano().getAreasRecursos();
        this.control = simulador.getControlEjecucion();
    }


    // ---------------- ALDEANOS ----------------
    public int obtenerAldeanosCentroUrbano() {
        return centro.getListaCasa().size() + centro.getListaPlaza().size();
    }

    public int obtenerAldeanosEnBosque() {
        return contar("madera", true); 
    }

    public int obtenerAldeanosEnAserradero() {
        return contar("madera", false);
    }

    public int obtenerAldeanosEnGranja() {
        return contar("comida", true);
    }

    public int obtenerAldeanosEnGranero() {
        return contar("comida", false);
    }

    public int obtenerAldeanosEnMina() {
        return contar("oro", true);
    }

    public int obtenerAldeanosEnTesoreria() {
        return contar("oro", false);
    }

    // ---------------- GUERREROS ----------------
    public int obtenerGuerrerosEnBosque() {
        return contarGuerreros("madera", true);
    }

    public int obtenerGuerrerosEnAserradero() {
        return contarGuerreros("madera", false);
    }

    public int obtenerGuerrerosEnGranja() {
        return contarGuerreros("comida", true);
    }

    public int obtenerGuerrerosEnGranero() {
        return contarGuerreros("comida", false);
    }

    public int obtenerGuerrerosEnMina() {
        return contarGuerreros("oro", true);
    }

    public int obtenerGuerrerosEnTesoreria() {
        return contarGuerreros("oro", false);
    }
    public int obtenerGuerrerosCentroUrbano() throws RemoteException {
        return centro.getCuartel().size();  
    }

    // ---------------- BÁRBAROS ----------------
    public int obtenerBarbarosEnBosque() {
        return contarBarbaros("madera", true);
    }

    public int obtenerBarbarosEnAserradero() {
        return contarBarbaros("madera", false);
    }

    public int obtenerBarbarosEnGranja() {
        return contarBarbaros("comida", true);
    }

    public int obtenerBarbarosEnGranero() {
        return contarBarbaros("comida", false);
    }

    public int obtenerBarbarosEnMina() {
        return contarBarbaros("oro", true);
    }

    public int obtenerBarbarosEnTesoreria() {
        return contarBarbaros("oro", false);
    }

    public int obtenerBarbarosEnCampamento() {
        return campamento.getCampamento().size();
    }

    public int obtenerBarbarosEnZonaPreparacion() {
        return campamento.getZonaPreparacion().size();
    }

    // ---------------- RECURSOS ----------------
    public int obtenerCantidadMadera() {
        return getAlmacen("madera").getCantidad();
    }

    public int obtenerCapacidadMaximaAserradero() {
        return getAlmacen("madera").getCapacidadMax();
    }

    public int obtenerCantidadComida() {
        return getAlmacen("comida").getCantidad();
    }

    public int obtenerCapacidadMaximaGranero() {
        return getAlmacen("comida").getCapacidadMax();
    }

    public int obtenerCantidadOro() {
        return getAlmacen("oro").getCantidad();
    }

    public int obtenerCapacidadMaximaTesoreria() {
        return getAlmacen("oro").getCapacidadMax();
    }

    // ---------------- CONTROLES ----------------
    public void activarEmergencia() {
        centro.activarEmergencia();
    }

    public void desactivarEmergencia() {
        centro.desactivarEmergencia();
    }

    public void pausarSimulacion() {
        control.pausar();
    }

    public void reanudarSimulacion() {
        control.reanudar();
    }

    // ---------------- AYUDA PRIVADA ----------------
    private int contar(String tipo, boolean esArea) {
        if (esArea) {
            for (AreaRecurso a : areas)
                if (a.getTipo().equals(tipo))
                    return a.getAldeanosOcupantes().size();
        } else {
            for (Almacen a : almacenes)
                if (a.getTipo().equals(tipo))
                    return a.getAldeanosOcupantes().size(); 
        }
        return 0;
    }

    private int contarGuerreros(String tipo, boolean esArea) {
        if (esArea) {
            for (AreaRecurso a : areas)
                if (a.getTipo().equals(tipo))
                    return a.getGuerrerosOcupantes().size();
        } else {
            for (Almacen a : almacenes)
                if (a.getTipo().equals(tipo))
                    return a.getGuerrerosOcupantes().size();
        }
        return 0;
    }

    private int contarBarbaros(String tipo, boolean esArea) {
        if (esArea) {
            for (AreaRecurso a : areas)
                if (a.getTipo().equals(tipo))
                    return a.combinarListasOcupacionArea().size() -
                           a.getGuerrerosOcupantes().size()- a.getAldeanosOcupantes().size();// barbaros = total - guerreros - aldeanos
        } else {
            for (Almacen a : almacenes)
                if (a.getTipo().equals(tipo))
                    return a.combinarListasOcupacionAlmacen().size() -
                           a.getGuerrerosOcupantes().size()- a.getAldeanosOcupantes().size();
        }
        return 0;
    }

    private Almacen getAlmacen(String tipo) {
        for (Almacen a : almacenes)
            if (a.getTipo().equals(tipo))
                return a;
        return null;
    }
    public boolean mejorarHerramientas() throws RemoteException {
        return centro.mejorarHerramientas();
    }
    public boolean mejorarArmas() throws RemoteException {
        return centro.mejorarArmas();
    }
    public boolean mejorarAlmacenes() throws RemoteException {
        return centro.mejorarAlmacenes();
    }
    public boolean comprarGuerrero() throws RemoteException {
        return centro.comprarGuerrero();
    }
    public boolean comprarAldeano() throws RemoteException {
        return centro.comprarAldeano();
    }

    public List<String> obtenerListaTesoreria() throws RemoteException {
        DefaultListModel<String> modelo = centro.getAlmacenes().get(2).combinarListasOcupacionAlmacen();
        return Collections.list(modelo.elements());
    }


    public List<String> obtenerListaAserradero() throws RemoteException {
        DefaultListModel<String> modelo = centro.getAlmacenes().get(1).combinarListasOcupacionAlmacen();
        return Collections.list(modelo.elements());
    }


    public List<String> obtenerListaGranero() throws RemoteException {
        DefaultListModel<String> modelo = centro.getAlmacenes().get(0).combinarListasOcupacionAlmacen();
        return Collections.list(modelo.elements());
    }


    public List<String> obtenerListaMina() throws RemoteException {
        DefaultListModel<String> modelo = centro.getAreasRecursos().get(2).combinarListasOcupacionArea();
        return Collections.list(modelo.elements());
    }


    public List<String> obtenerListaGranja() throws RemoteException {
        DefaultListModel<String> modelo = centro.getAreasRecursos().get(0).combinarListasOcupacionArea();
        return Collections.list(modelo.elements());
    }

    public List<String> obtenerListaBosque() throws RemoteException {
        DefaultListModel<String> modelo = centro.getAreasRecursos().get(1).combinarListasOcupacionArea();
        return Collections.list(modelo.elements());
    }


    public List<String> obtenerListaCuartel() throws RemoteException {
        DefaultListModel<String> modelo = centro.listaCuartel();
        return Collections.list(modelo.elements());
    }

    public List<String> obtenerListaCasa() throws RemoteException {
        DefaultListModel<String> modelo = centro.mostrarListaCasa();
        return Collections.list(modelo.elements());
    }


    public List<String> obtenerListaPlazaCentral() throws RemoteException {
        DefaultListModel<String> modelo = centro.mostrarPlazaCentral();
        return Collections.list(modelo.elements());
    }


    public List<String> obtenerListaRecuperacion() throws RemoteException {
        DefaultListModel<String> modelo = centro.combinarListasAreaRecuperacion();
        return Collections.list(modelo.elements());
    }

    public List<String> obtenerListaZonaPreparacion() throws RemoteException {
        DefaultListModel<String> modelo = campamento.listaZonaPreparacion();
        return Collections.list(modelo.elements());
    }


    public List<String> obtenerListaCampamento() throws RemoteException {
        DefaultListModel<String> modelo = campamento.listaCampamento();
        return Collections.list(modelo.elements());
    }
    public int obtenerNivelMejoraAlmacenes() throws RemoteException {
        return centro.getNivelMejoraAlmacenes(); 
    }
    public int obtenerNivelMejoraArmas() throws RemoteException {
        return centro.getNivelMejoraArmas(); 
    }
    public int obtenerNivelMejoraHerramientas() throws RemoteException {
        return centro.getNivelMejoraHerramientas(); 
    }
}