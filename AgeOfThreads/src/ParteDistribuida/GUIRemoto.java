package ParteDistribuida;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class GUIRemoto extends JFrame {

    private Remoto remoto;

    private JLabel comidaLabel, maderaLabel, oroLabel;
    private JLabel aldeanosCentro, aldeanosGranja, aldeanosBosque, aldeanosMina,  aldeanosTesoreria, aldeanosAserradero, aldeanosGranero;
    private JLabel barbarosCampamento, barbarosPreparacion, barbarosGranja, barbarosBosque, barbarosMina, barbarosTesoreria, barbarosAserradero, barbarosGranero;
    private JLabel guerrerosCentro, guerrerosGranja, guerrerosBosque, guerrerosMina, guerrerosTesoreria, guerrerosAserradero, guerrerosGranero;

    // Botones
    private JButton botonCampana, botonPausa;
    private boolean alarmaActiva = false;
    private boolean enPausa = false;

    public GUIRemoto() {
        setTitle("Monitor Remoto de Simulación");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(800, 600));

        conectarRMI();
        initGUI();

        Timer timer = new Timer(1000, this::actualizarDatos);
        timer.start();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void conectarRMI() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            remoto = (Remoto) registry.lookup("Simulacion");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar al servidor RMI: " + e.getMessage(),
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void initGUI() {
        // Panel Recursos con fondo gris claro
        JPanel panelRecursos = new JPanel(new GridLayout(1, 3, 10, 10));
        panelRecursos.setBackground(new Color(220, 220, 220));
        panelRecursos.setBorder(BorderFactory.createTitledBorder("Recursos"));

        comidaLabel = crearEtiquetaConFondo("Comida: ", new Color(204, 255, 204));
        maderaLabel = crearEtiquetaConFondo("Madera: ", new Color(204, 255, 204));
        oroLabel = crearEtiquetaConFondo("Oro: ", new Color(204, 255, 204));

        panelRecursos.add(comidaLabel);
        panelRecursos.add(maderaLabel);
        panelRecursos.add(oroLabel);
        add(panelRecursos);

        // Panel Aldeanos - fondo amarillo claro para almacenes y áreas
        JPanel panelAldeanos = new JPanel(new GridLayout(2, 4, 10, 10));
        panelAldeanos.setBorder(BorderFactory.createTitledBorder("Aldeanos"));

        aldeanosCentro = crearEtiquetaConFondo("Centro Urbano: ", new Color(102, 178, 255)); // azul claro
        aldeanosGranja = crearEtiquetaConFondo("Granja: ", new Color(180, 255, 180)); // verde claro
        aldeanosBosque = crearEtiquetaConFondo("Bosque: ", new Color(255, 165, 0)); // naranja
        aldeanosMina = crearEtiquetaConFondo("Mina: ", new Color(255, 255, 153)); // amarillo claro
        aldeanosGranero = crearEtiquetaConFondo("Granero: ", new Color(180, 255, 180));
        aldeanosAserradero = crearEtiquetaConFondo("Aserradero: ", new Color(255, 165, 0));
        aldeanosTesoreria = crearEtiquetaConFondo("Tesorería: ", new Color(255, 255, 153));

        panelAldeanos.add(aldeanosCentro);
        panelAldeanos.add(aldeanosGranja);
        panelAldeanos.add(aldeanosBosque);
        panelAldeanos.add(aldeanosMina);
        panelAldeanos.add(aldeanosGranero);
        panelAldeanos.add(aldeanosAserradero);
        panelAldeanos.add(aldeanosTesoreria);
        add(panelAldeanos);

        // Panel Guerreros - mismo esquema colores
        JPanel panelGuerreros = new JPanel(new GridLayout(2, 4, 10, 10));
        panelGuerreros.setBorder(BorderFactory.createTitledBorder("Guerreros"));

        guerrerosCentro = crearEtiquetaConFondo("Centro Urbano: ", new Color(102, 178, 255));
        guerrerosGranja = crearEtiquetaConFondo("Granja: ", new Color(180, 255, 180));
        guerrerosBosque = crearEtiquetaConFondo("Bosque: ", new Color(255, 165, 0));
        guerrerosMina = crearEtiquetaConFondo("Mina: ", new Color(255, 255, 153));
        guerrerosGranero = crearEtiquetaConFondo("Granero: ", new Color(180, 255, 180));
        guerrerosAserradero = crearEtiquetaConFondo("Aserradero: ", new Color(255, 165, 0));
        guerrerosTesoreria = crearEtiquetaConFondo("Tesorería: ", new Color(255, 255, 153));

        panelGuerreros.add(guerrerosCentro);
        panelGuerreros.add(guerrerosGranja);
        panelGuerreros.add(guerrerosBosque);
        panelGuerreros.add(guerrerosMina);
        panelGuerreros.add(guerrerosGranero);
        panelGuerreros.add(guerrerosAserradero);
        panelGuerreros.add(guerrerosTesoreria);
        add(panelGuerreros);

        // Panel Bárbaros - rojo claro
        JPanel panelBarbaros = new JPanel(new GridLayout(2, 4, 10, 10));
        panelBarbaros.setBorder(BorderFactory.createTitledBorder("Bárbaros"));

        barbarosCampamento = crearEtiquetaConFondo("Campamento: ", new Color(255, 182, 193));
        barbarosPreparacion = crearEtiquetaConFondo("Zona de Preparación: ", new Color(255, 182, 193));
        barbarosMina = crearEtiquetaConFondo("Mina: ", new Color(255, 182, 193));
        barbarosBosque = crearEtiquetaConFondo("Bosque: ", new Color(255, 182, 193));
        barbarosGranja = crearEtiquetaConFondo("Granja: ", new Color(255, 182, 193));
        barbarosTesoreria = crearEtiquetaConFondo("Tesorería: ", new Color(255, 182, 193));
        barbarosAserradero = crearEtiquetaConFondo("Aserradero: ", new Color(255, 182, 193));
        barbarosGranero = crearEtiquetaConFondo("Granero: ", new Color(255, 182, 193));

        panelBarbaros.add(barbarosMina);
        panelBarbaros.add(barbarosBosque);
        panelBarbaros.add(barbarosGranja);
        panelBarbaros.add(barbarosTesoreria);
        panelBarbaros.add(barbarosAserradero);
        panelBarbaros.add(barbarosGranero);
        panelBarbaros.add(barbarosCampamento);
        panelBarbaros.add(barbarosPreparacion);
        add(panelBarbaros);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botonCampana = new JButton("Activar emergencia");
        botonPausa = new JButton("Pausar");

        panelBotones.add(botonCampana);
        panelBotones.add(botonPausa);
        add(panelBotones);

        // Botón Campana
        botonCampana.addActionListener(e -> {
            try {
                if (!alarmaActiva) {
                    remoto.activarEmergencia();
                    botonCampana.setText("Desactivar alarma");
                } else {
                    remoto.desactivarEmergencia();
                    botonCampana.setText("Activar alarma");
                }
                alarmaActiva = !alarmaActiva;
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al cambiar estado de alarma: " + ex.getMessage());
            }
        });

        // Botón Pausa
        botonPausa.addActionListener(e -> {
            try {
                if (!enPausa) {
                    remoto.pausarSimulacion();
                    botonPausa.setText("Reanudar");
                } else {
                    remoto.reanudarSimulacion();
                    botonPausa.setText("Pausar");
                }
                enPausa = !enPausa;
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al cambiar estado de pausa: " + ex.getMessage());
            }
        });
    }

    private JLabel crearEtiquetaConFondo(String texto, Color fondo) {
        JLabel label = new JLabel(texto);
        label.setOpaque(true);
        label.setBackground(fondo);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return label;
    }

    private void actualizarDatos(ActionEvent e) {
        try {
            comidaLabel.setText("Comida: " + remoto.obtenerCantidadComida() + "/" + remoto.obtenerCapacidadMaximaGranero());
            maderaLabel.setText("Madera: " + remoto.obtenerCantidadMadera() + "/" + remoto.obtenerCapacidadMaximaAserradero());
            oroLabel.setText("Oro: " + remoto.obtenerCantidadOro() + "/" + remoto.obtenerCapacidadMaximaTesoreria());

            aldeanosCentro.setText("Centro Urbano: " + remoto.obtenerAldeanosCentroUrbano());
            aldeanosGranja.setText("Granja: " + remoto.obtenerAldeanosEnGranja());
            aldeanosBosque.setText("Bosque: " + remoto.obtenerAldeanosEnBosque());
            aldeanosMina.setText("Mina: " + remoto.obtenerAldeanosEnMina());
            aldeanosGranero.setText("Granero: " + remoto.obtenerAldeanosEnGranero());
            aldeanosAserradero.setText("Aserradero: " + remoto.obtenerAldeanosEnAserradero());
            aldeanosTesoreria.setText("Tesorería: " + remoto.obtenerAldeanosEnTesoreria());

            guerrerosCentro.setText("Centro Urbano: " + remoto.obtenerGuerrerosCentroUrbano());
            guerrerosGranja.setText("Granja: " + remoto.obtenerGuerrerosEnGranja());
            guerrerosBosque.setText("Bosque: " + remoto.obtenerGuerrerosEnBosque());
            guerrerosMina.setText("Mina: " + remoto.obtenerGuerrerosEnMina());
            guerrerosGranero.setText("Granero: " + remoto.obtenerGuerrerosEnGranero());
            guerrerosAserradero.setText("Aserradero: " + remoto.obtenerGuerrerosEnAserradero());
            guerrerosTesoreria.setText("Tesorería: " + remoto.obtenerGuerrerosEnTesoreria());

            barbarosCampamento.setText("Campamento: " + remoto.obtenerBarbarosEnCampamento());
            barbarosPreparacion.setText("Zona de Preparación: " + remoto.obtenerBarbarosEnZonaPreparacion());
            barbarosMina.setText("Mina: " + remoto.obtenerBarbarosEnMina());
            barbarosBosque.setText("Bosque: " + remoto.obtenerBarbarosEnBosque());
            barbarosGranja.setText("Granja: " + remoto.obtenerBarbarosEnGranja());
            barbarosTesoreria.setText("Tesorería: " + remoto.obtenerBarbarosEnTesoreria());
            barbarosAserradero.setText("Aserradero: " + remoto.obtenerBarbarosEnAserradero());
            barbarosGranero.setText("Granero: " + remoto.obtenerBarbarosEnGranero());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUIRemoto().setVisible(true));
    }
}
