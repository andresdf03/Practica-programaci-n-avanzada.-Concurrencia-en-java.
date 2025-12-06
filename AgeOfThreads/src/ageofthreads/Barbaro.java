package ageofthreads;

public class Barbaro extends Thread {
    private String idBarbaro;

    public Barbaro(String idBarbaro)  {
        this.idBarbaro = idBarbaro;
        LoggerSistema.getInstancia().log("Bárbaro " + idBarbaro + " ha sido creado y está listo para ser asignado.");
    }
    public void run(){
        
    }

    public String getIdBarbaro() {
        return idBarbaro;
    }
}
