package ageofthreads;

import java.util.ArrayList;


public class Simulador {
     
    private CentroUrbano centroUrbano;
    private CampamentoBarbaro campamento;
    private final ControlEjecucion controlEjecucion = new ControlEjecucion();

    
    public Simulador() {
        // Crear almacenes
        Almacen granero = new Almacen("comida", 200, 50, controlEjecucion);//("comida", 200, 50)
        Almacen aserradero = new Almacen("madera", 150, 30, controlEjecucion);//("madera", 150, 30)
        Almacen tesoreria = new Almacen("oro", 50, 20,controlEjecucion);//("oro", 50, 20)
        ArrayList<Almacen> almacenes = new ArrayList<>();
        almacenes.add(granero);
        almacenes.add(aserradero);
        almacenes.add(tesoreria);

        // Crear áreas
        AreaRecurso granja = new AreaRecurso("comida", controlEjecucion);
        AreaRecurso bosque = new AreaRecurso("madera", controlEjecucion);
        AreaRecurso mina = new AreaRecurso("oro", controlEjecucion);
        ArrayList<AreaRecurso> areas = new ArrayList<>();
        areas.add(granja);
        areas.add(bosque);
        areas.add(mina);

        // Crear centro urbano
        centroUrbano = new CentroUrbano(almacenes, areas, controlEjecucion);

        // Crear campamento y lanzar hilo
        campamento = new CampamentoBarbaro(areas, almacenes, centroUrbano, controlEjecucion);
        campamento.start();

        // Crear los 2 aldeanos iniciales
        centroUrbano.nuevoAldeano();
        centroUrbano.nuevoAldeano();
    }   
        public CentroUrbano getCentroUrbano() {
           return centroUrbano;
        }

    public CampamentoBarbaro getCampamento() {
        return campamento;
    }

        public void activarEmergencia() {
            centroUrbano.activarEmergencia();
        }

        public boolean comprarGuerrero() {
            return centroUrbano.comprarGuerrero();
        }

        public boolean comprarAldeano() {
            return centroUrbano.comprarAldeano();
        }

         public boolean mejorarArmas() {
            return centroUrbano.mejorarArmas();
        }
        public boolean mejorarHerramientas() {
            return centroUrbano.mejorarHerramientas();
        }
         public boolean mejorarAlmacenes() {
            return centroUrbano.mejorarAlmacenes();
        }
         public ControlEjecucion getControlEjecucion() {
            return controlEjecucion;
        }
}
