import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

/** Ciclo de juego, teclado, lógica y dibujo de "La rana que cruza". */
public class PanelJuego extends JPanel {

    public static final int ANCHO = 800;
    public static final int ALTO = 600;
    public static final int FILA = 60;

    // Filas (de arriba hacia abajo): 0 HUD, 1 orilla de llegada, 2-4 río,
    // 5 pasto de descanso, 6-8 carretera, 9 pasto de inicio.
    private static final int FILA_META = 1;
    private static final int FILA_INICIO = 9;

    private static final int VIDAS_INICIALES = 3;
    private static final int FRAMES_POR_CRUCE = 60 * 60; // 60 s a ~60 cuadros/s
    private static final int FRAMES_MUERTE = 45;
    private static final int CRUCES_PARA_GANAR = 5;

    private static final Color C_HUD = new Color(0x1B2A1B);
    private static final Color C_PASTO = new Color(0x6A9F3A);
    private static final Color C_META = new Color(0x558B2F);
    private static final Color C_RIO = new Color(0x1F5AA6);
    private static final Color C_ASFALTO = new Color(0x454545);
    private static final Color C_LINEA = new Color(0xFFEB3B);

    private enum Estado { INICIO, JUGANDO, PRUEBA, VICTORIA, DERROTA }

    private Estado estado = Estado.INICIO;
    private final List<Carril> carriles = new ArrayList<>();
    private final Rana rana = new Rana();

    private int vidas;
    private int puntos;
    private int cruces;
    private int framesRestantes;
    private int framesMuerte;       // > 0 mientras se muestra la muerte
    private String motivoMuerte = "";

    // Pantalla de prueba de transformaciones
    private final Figura[] figurasPrueba = {new Rana(), new Auto(0, 0, 1, ANCHO), new Tronco(0, 0, 1, ANCHO, false)};
    private final String[] nombresPrueba = {"Rana", "Auto", "Tronco"};
    private int figuraPrueba;
    private double pruebaX, pruebaY, pruebaAng, pruebaEsc;
    private boolean ordenTRS = true;

    public PanelJuego() {
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setFocusable(true);
        configurarTeclado();
        reiniciarJuego();
        reiniciarPrueba();

        // ~60 cuadros por segundo: actualizar y luego repintar
        Timer timer = new Timer(16, e -> {
            actualizar();
            repaint();
        });
        timer.start();
    }

    // ------------------------------------------------------------------ estado

    private void crearCarriles() {
        carriles.clear();
        // Carretera
        carriles.add(new Carril(Carril.Tipo.CARRETERA, centroFila(8),  1.6, 3, 0,   ANCHO, false));
        carriles.add(new Carril(Carril.Tipo.CARRETERA, centroFila(7), -2.2, 2, 120, ANCHO, false));
        carriles.add(new Carril(Carril.Tipo.CARRETERA, centroFila(6),  2.8, 2, 300, ANCHO, false));
        // Río
        carriles.add(new Carril(Carril.Tipo.RIO, centroFila(4), -1.2, 3, 50,  ANCHO, false));
        carriles.add(new Carril(Carril.Tipo.RIO, centroFila(3),  1.6, 3, 200, ANCHO, true));
        carriles.add(new Carril(Carril.Tipo.RIO, centroFila(2), -1.0, 3, 0,   ANCHO, false));
    }

    private void reiniciarJuego() {
        crearCarriles();
        vidas = VIDAS_INICIALES;
        puntos = 0;
        cruces = 0;
        framesMuerte = 0;
        nuevaRana();
    }

    private void nuevaRana() {
        rana.reiniciar(ANCHO / 2.0, centroFila(FILA_INICIO));
        framesRestantes = FRAMES_POR_CRUCE;
    }

    private void reiniciarPrueba() {
        pruebaX = ANCHO / 2.0;
        pruebaY = ALTO / 2.0;
        pruebaAng = 0;
        pruebaEsc = 1;
    }

    private static double centroFila(int fila) {
        return fila * FILA + FILA / 2.0;
    }

    private int filaDeRana() {
        return (int) Math.floor(rana.getY() / FILA);
    }

    // ------------------------------------------------------------------ teclado

    private void configurarTeclado() {
        tecla("UP",    () -> flecha(0, -1));
        tecla("DOWN",  () -> flecha(0, 1));
        tecla("LEFT",  () -> flecha(-1, 0));
        tecla("RIGHT", () -> flecha(1, 0));

        tecla("ENTER", () -> {
            if (estado == Estado.INICIO) {
                reiniciarJuego();
                estado = Estado.JUGANDO;
            }
        });
        tecla("R", () -> {
            if (estado == Estado.JUGANDO || estado == Estado.VICTORIA || estado == Estado.DERROTA) {
                reiniciarJuego();
                estado = Estado.JUGANDO;
            }
        });
        tecla("T", () -> {
            if (estado == Estado.INICIO) {
                reiniciarPrueba();
                estado = Estado.PRUEBA;
            }
        });
        tecla("ESCAPE", () -> estado = Estado.INICIO);

        // Pantalla de prueba
        tecla("W", () -> moverPrueba(0, -10));
        tecla("S", () -> moverPrueba(0, 10));
        tecla("A", () -> moverPrueba(-10, 0));
        tecla("D", () -> moverPrueba(10, 0));
        tecla("Q", () -> rotarPrueba(-5));
        tecla("E", () -> rotarPrueba(5));
        tecla("PLUS",     () -> escalarPrueba(1.1));
        tecla("EQUALS",   () -> escalarPrueba(1.1));
        tecla("ADD",      () -> escalarPrueba(1.1));
        tecla("MINUS",    () -> escalarPrueba(1 / 1.1));
        tecla("SUBTRACT", () -> escalarPrueba(1 / 1.1));
        tecla("O", () -> { if (estado == Estado.PRUEBA) ordenTRS = !ordenTRS; });
        tecla("SPACE", () -> { if (estado == Estado.PRUEBA) reiniciarPrueba(); });
        tecla("1", () -> { if (estado == Estado.PRUEBA) figuraPrueba = 0; });
        tecla("2", () -> { if (estado == Estado.PRUEBA) figuraPrueba = 1; });
        tecla("3", () -> { if (estado == Estado.PRUEBA) figuraPrueba = 2; });
    }

    /** Registra una tecla con Key Bindings (InputMap / ActionMap). */
    private void tecla(String nombre, Runnable accion) {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(nombre), nombre);
        getActionMap().put(nombre, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accion.run();
            }
        });
    }

    private void flecha(int dx, int dy) {
        if (estado == Estado.JUGANDO && framesMuerte == 0) {
            rana.saltar(dx, dy, FILA / 2.0, ANCHO - FILA / 2.0, centroFila(FILA_META), centroFila(FILA_INICIO));
        } else if (estado == Estado.PRUEBA) {
            if (dx != 0) rotarPrueba(45 * dx);       // ◀ / ▶ : ±45°
            else moverPrueba(0, 10 * dy);           // ▲ / ▼ : trasladar
        }
    }

    private void moverPrueba(double dx, double dy) {
        if (estado == Estado.PRUEBA) {
            pruebaX += dx;
            pruebaY += dy;
        }
    }

    private void rotarPrueba(double grados) {
        if (estado == Estado.PRUEBA) {
            pruebaAng += Math.toRadians(grados);
        }
    }

    private void escalarPrueba(double factor) {
        if (estado == Estado.PRUEBA) {
            pruebaEsc = Math.max(0.1, Math.min(5, pruebaEsc * factor));
        }
    }

    // ------------------------------------------------------------------ lógica

    public void actualizar() {
        if (estado == Estado.INICIO) {
            for (Carril c : carriles) c.actualizar(); // fondo animado
            return;
        }
        if (estado != Estado.JUGANDO) {
            return;
        }

        for (Carril c : carriles) {
            c.actualizar();
        }

        if (framesMuerte > 0) {
            framesMuerte--;
            if (framesMuerte == 0) {
                if (vidas <= 0) estado = Estado.DERROTA;
                else nuevaRana();
            }
            return;
        }

        rana.actualizar();

        framesRestantes--;
        if (framesRestantes <= 0) {
            perderVida("¡Se acabó el tiempo!");
            return;
        }

        int fila = filaDeRana();
        Carril carril = carrilEnFila(fila);

        // Carretera: colisión AABB con los autos
        if (carril != null && carril.getTipo() == Carril.Tipo.CARRETERA) {
            for (Figura auto : carril.getObjetos()) {
                if (rana.getCaja().reducida(6).intersecta(auto.getCaja())) {
                    perderVida("¡Atropellada!");
                    return;
                }
            }
        }

        // Río: la rana debe estar sobre un tronco y se mueve con él
        if (carril != null && carril.getTipo() == Carril.Tipo.RIO && !rana.isSaltando()) {
            Tronco debajo = null;
            for (Figura f : carril.getObjetos()) {
                if (f.getCaja().contiene(rana.getX(), rana.getY())) {
                    debajo = (Tronco) f;
                    break;
                }
            }
            if (debajo == null) {
                perderVida("¡Cayó al agua!");
                return;
            }
            rana.moverCon(debajo);
            if (rana.getX() < 0 || rana.getX() > ANCHO) {
                perderVida("¡El tronco la sacó de la pantalla!");
                return;
            }
        }

        // Llegó a la orilla superior
        if (fila == FILA_META && !rana.isSaltando()) {
            puntos += 100;
            cruces++;
            if (cruces >= CRUCES_PARA_GANAR) {
                estado = Estado.VICTORIA;
                return;
            }
            double factor = Math.pow(1.1, cruces); // +10 % por cruce
            for (Carril c : carriles) c.setMultiplicador(factor);
            nuevaRana();
        }
    }

    private Carril carrilEnFila(int fila) {
        double yc = centroFila(fila);
        for (Carril c : carriles) {
            if (c.getYCentro() == yc) return c;
        }
        return null;
    }

    private void perderVida(String motivo) {
        vidas--;
        motivoMuerte = motivo;
        framesMuerte = FRAMES_MUERTE;
    }

    // ------------------------------------------------------------------ dibujo

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (estado == Estado.PRUEBA) {
            dibujarPrueba(g);
            return;
        }

        dibujarEscenario(g);
        for (Carril c : carriles) {
            c.dibujar(g);
        }

        switch (estado) {
            case INICIO -> dibujarInicio(g);
            case JUGANDO -> {
                // Parpadea mientras muere
                if (framesMuerte == 0 || (framesMuerte / 5) % 2 == 0) {
                    rana.dibujar(g);
                }
                dibujarHud(g);
                if (framesMuerte > 0) {
                    textoCentrado(g, motivoMuerte, ALTO / 2 + 10, 28, Color.WHITE);
                }
            }
            case VICTORIA -> {
                dibujarHud(g);
                pantallaFinal(g, "¡GANASTE!", new Color(0x81C784));
            }
            case DERROTA -> {
                dibujarHud(g);
                pantallaFinal(g, "GAME OVER", new Color(0xE57373));
            }
            default -> { }
        }
    }

    private void dibujarEscenario(Graphics g) {
        rect(g, 0, 0, ANCHO, FILA, C_HUD);
        rect(g, 0, FILA_META * FILA, ANCHO, FILA, C_META);
        rect(g, 0, 2 * FILA, ANCHO, 3 * FILA, C_RIO);
        rect(g, 0, 5 * FILA, ANCHO, FILA, C_PASTO);
        rect(g, 0, 6 * FILA, ANCHO, 3 * FILA, C_ASFALTO);
        rect(g, 0, FILA_INICIO * FILA, ANCHO, FILA, C_PASTO);

        // Líneas divisorias de la carretera
        g.setColor(C_LINEA);
        for (int fila = 7; fila <= 8; fila++) {
            int y = fila * FILA;
            for (int x = 0; x < ANCHO; x += 40) {
                g.drawLine(x, y - 1, x + 22, y - 1);
                g.drawLine(x, y, x + 22, y);
                g.drawLine(x, y + 1, x + 22, y + 1);
            }
        }
    }

    private void dibujarHud(Graphics g) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        g.setColor(Color.WHITE);
        g.drawString("Vidas: " + vidas, 20, 36);
        g.drawString("Puntos: " + puntos, 160, 36);
        g.drawString("Cruces: " + cruces + "/" + CRUCES_PARA_GANAR, 320, 36);
        int segundos = (framesRestantes + 59) / 60;
        g.setColor(segundos <= 10 ? new Color(0xFF8A80) : Color.WHITE);
        g.drawString("Tiempo: " + segundos + " s", 500, 36);
        g.setColor(new Color(0xB0BEC5));
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g.drawString("R reiniciar · ESC menú", 650, 36);
    }

    private void dibujarInicio(Graphics g) {
        oscurecer(g, 160);
        textoCentrado(g, "LA RANA QUE CRUZA", 190, 44, new Color(0x81C784));
        textoCentrado(g, "Cruza la carretera y el río hasta la orilla superior.", 250, 18, Color.WHITE);
        textoCentrado(g, "Flechas: saltar   ·   3 vidas   ·   60 s por cruce", 280, 18, Color.WHITE);
        textoCentrado(g, "Cada cruce: +100 puntos y los carriles van 10 % más rápido", 310, 18, Color.WHITE);
        textoCentrado(g, "ENTER  jugar", 380, 24, new Color(0xFFEB3B));
        textoCentrado(g, "T  pantalla de prueba de transformaciones", 420, 18, new Color(0xFFEB3B));
    }

    private void pantallaFinal(Graphics g, String titulo, Color color) {
        oscurecer(g, 170);
        textoCentrado(g, titulo, 260, 52, color);
        textoCentrado(g, "Puntos: " + puntos + "   ·   Cruces: " + cruces, 320, 22, Color.WHITE);
        textoCentrado(g, "R  jugar de nuevo   ·   ESC  menú", 380, 18, new Color(0xFFEB3B));
    }

    /** Pantalla del paso 4: transformar una figura con el teclado y comparar T·R·S contra S·R·T. */
    private void dibujarPrueba(Graphics g) {
        rect(g, 0, 0, ANCHO, ALTO, new Color(0xECEFF1));

        // Ejes que pasan por el origen de la ventana y por la posición de la figura
        g.setColor(new Color(0xB0BEC5));
        g.drawLine((int) pruebaX, 0, (int) pruebaX, ALTO);
        g.drawLine(0, (int) pruebaY, ANCHO, (int) pruebaY);

        Figura f = figurasPrueba[figuraPrueba];
        f.setPosicion(pruebaX, pruebaY);
        f.setAngulo(pruebaAng);
        f.setEscala(pruebaEsc, pruebaEsc);

        Matriz3x3 m;
        if (ordenTRS) {
            m = f.getMatriz(); // T · R · S
        } else {
            m = Matriz3x3.escala(pruebaEsc, pruebaEsc)
                    .multiplicar(Matriz3x3.rotacion(pruebaAng))
                    .multiplicar(Matriz3x3.traslacion(pruebaX, pruebaY)); // S · R · T
        }
        f.dibujarCon(g, m);

        g.setColor(new Color(0x263238));
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        g.drawString("PRUEBA DE TRANSFORMACIONES - " + nombresPrueba[figuraPrueba], 16, 26);
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        g.drawString(String.format("Orden: %s", ordenTRS ? "M = T · R · S" : "M = S · R · T"), 16, 50);
        g.drawString(String.format("x=%.0f  y=%.0f  ángulo=%.0f°  escala=%.2f",
                pruebaX, pruebaY, Math.toDegrees(pruebaAng), pruebaEsc), 16, 70);

        String[] ayuda = {
            "W/A/S/D, Arr/Ab trasladar",
            "Izq / Der       rotar -45° / +45°",
            "Q / E           rotar -5° / +5°",
            "+ / -           escalar",
            "O               alternar T·R·S / S·R·T",
            "1 / 2 / 3       rana / auto / tronco",
            "ESPACIO         reiniciar    ESC  menú"
        };
        for (int i = 0; i < ayuda.length; i++) {
            g.drawString(ayuda[i], 16, ALTO - 16 - (ayuda.length - 1 - i) * 18);
        }

        // Matriz actual
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        String[] filas = m.toString().split("\\R");
        for (int i = 0; i < filas.length; i++) {
            g.drawString(filas[i], ANCHO - 270, 30 + i * 16);
        }
    }

    // ------------------------------------------------------------------ utilidades de dibujo

    /** Rectángulo dibujado como polígono de 4 vértices. */
    private static void rect(Graphics g, int x, int y, int w, int h, Color color) {
        g.setColor(color);
        g.fillPolygon(new int[] {x, x + w, x + w, x}, new int[] {y, y, y + h, y + h}, 4);
    }

    private static void oscurecer(Graphics g, int alfa) {
        rect(g, 0, 0, ANCHO, ALTO, new Color(0, 0, 0, alfa));
    }

    private static void textoCentrado(Graphics g, String texto, int y, int tamano, Color color) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, tamano));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(color);
        g.drawString(texto, (ANCHO - fm.stringWidth(texto)) / 2, y);
    }
}
