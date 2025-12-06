package ageofthreads;

import java.util.ArrayList;

public class AgeOfThreads {

    public static void main(String[] args) {
        ControlEjecucion controlEjecucion = new ControlEjecucion();

        // Crear almacenes
        Almacen granero = new Almacen("comida", 2000, 500, controlEjecucion);//("comida", 200, 50
        Almacen aserradero = new Almacen("madera", 1500, 900, controlEjecucion);//("madera", 150, 30
        Almacen tesoreria = new Almacen("oro", 500, 400, controlEjecucion);//("oro", 50, 20)
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
        
        // Centro urbano
        CentroUrbano centroUrbano = new CentroUrbano(almacenes, areas, controlEjecucion);
        ControlEjecucion control = new ControlEjecucion();

        // Campamento bárbaro
        CampamentoBarbaro campa = new CampamentoBarbaro(areas, almacenes,centroUrbano, control);
        
        
        campa.start();

        // Crear aldeanos (para recolectar y reparar)
        //for (int i = 1; i <= 2; i++) {
          //  centroUrbano.nuevoAldeano();
        //}
        pruebas pru =new pruebas(centroUrbano);
        pru.start();
        
        // Crear guerreros (para patrullar y combatir)
        //for (int i = 1; i <= 3; i++) {
          //  centroUrbano.nuevoGuerrero();
        //}
        


        // Loop de diagnóstico
        while (true) {
            try {
                Thread.sleep(5000); // cada 15 segundos
                System.out.println("Sistema funcionando: aldeanos, guerreros y bárbaros en acción.");
                System.out.println(aserradero.getCantidad());
                System.out.println(granero.getCantidad());
                System.out.println(tesoreria.getCantidad());
                System.out.println("capa aserradero"+ aserradero.getCapacidadMax());
                System.out.println("capa granero"+ granero.getCapacidadMax());
                System.out.println("capa aserradero"+aserradero.getCapacidadMax());
                


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
