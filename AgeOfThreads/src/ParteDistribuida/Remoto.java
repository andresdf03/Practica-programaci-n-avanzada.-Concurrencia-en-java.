
package ParteDistribuida;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Remoto extends Remote {
    // Aldeanos en distintas ubicaciones
    int obtenerAldeanosCentroUrbano() throws RemoteException;
    int obtenerAldeanosEnBosque() throws RemoteException;
    int obtenerAldeanosEnAserradero() throws RemoteException;
    int obtenerAldeanosEnGranja() throws RemoteException;
    int obtenerAldeanosEnGranero() throws RemoteException;
    int obtenerAldeanosEnMina() throws RemoteException;
    int obtenerAldeanosEnTesoreria() throws RemoteException;

    // Guerreros patrullando
    int obtenerGuerrerosEnBosque() throws RemoteException;
    int obtenerGuerrerosEnAserradero() throws RemoteException;
    int obtenerGuerrerosEnGranja() throws RemoteException;
    int obtenerGuerrerosEnGranero() throws RemoteException;
    int obtenerGuerrerosEnMina() throws RemoteException;
    int obtenerGuerrerosEnTesoreria() throws RemoteException;
    

    // Bárbaros
    int obtenerBarbarosEnBosque() throws RemoteException;
    int obtenerBarbarosEnAserradero() throws RemoteException;
    int obtenerBarbarosEnGranja() throws RemoteException;
    int obtenerBarbarosEnGranero() throws RemoteException;
    int obtenerBarbarosEnMina() throws RemoteException;
    int obtenerBarbarosEnTesoreria() throws RemoteException;
    int obtenerBarbarosEnCampamento() throws RemoteException;
    int obtenerBarbarosEnZonaPreparacion() throws RemoteException;
    int obtenerGuerrerosCentroUrbano() throws RemoteException;

    // Recursos
    int obtenerCantidadMadera() throws RemoteException;
    int obtenerCapacidadMaximaAserradero() throws RemoteException;
    int obtenerCantidadComida() throws RemoteException;
    int obtenerCapacidadMaximaGranero() throws RemoteException;
    int obtenerCantidadOro() throws RemoteException;
    int obtenerCapacidadMaximaTesoreria() throws RemoteException;

    //Mejoras
    boolean comprarAldeano() throws RemoteException;
    boolean comprarGuerrero() throws RemoteException;
    boolean mejorarAlmacenes() throws RemoteException;
    boolean mejorarArmas() throws RemoteException;
    boolean mejorarHerramientas() throws RemoteException;
    int obtenerNivelMejoraAlmacenes() throws RemoteException;
    int obtenerNivelMejoraArmas() throws RemoteException;
    int obtenerNivelMejoraHerramientas() throws RemoteException;
    
    // Controles
    void activarEmergencia() throws RemoteException;
    void desactivarEmergencia() throws RemoteException;

    void pausarSimulacion() throws RemoteException;
    void reanudarSimulacion() throws RemoteException;
    //Listas
    public List<String> obtenerListaTesoreria() throws RemoteException;
    public List<String> obtenerListaAserradero() throws RemoteException;
    public List<String> obtenerListaGranero() throws RemoteException;

    public List<String> obtenerListaMina() throws RemoteException;
    public List<String> obtenerListaGranja() throws RemoteException;
    public List<String> obtenerListaBosque() throws RemoteException;

    public List<String> obtenerListaCuartel() throws RemoteException;
    public List<String> obtenerListaCasa() throws RemoteException;
    public List<String> obtenerListaPlazaCentral() throws RemoteException;
    public List<String> obtenerListaRecuperacion() throws RemoteException;

    public List<String> obtenerListaZonaPreparacion() throws RemoteException;
    public List<String> obtenerListaCampamento() throws RemoteException;

}