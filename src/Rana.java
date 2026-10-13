import java.awt.Color;

/**
 * Rana del jugador. Salta 60 px por tecla; durante el salto:
 *  - traslación: avanza de la casilla de origen a la de destino,
 *  - rotación:   gira 0°, 90°, 180° o 270° para mirar hacia donde salta,
 *  - escala:     sube de 1.0 a 1.3 y regresa a 1.0 (como si se elevara).
 */
public class Rana extends Figura {

    public static final double PASO = 60;
    public static final int FRAMES_SALTO = 10;
    private static final double ESCALA_BASE = 0.8;

    private boolean saltando;
    private int frame;
    private double origenX, origenY, destinoX, destinoY;

    public Rana() {
        Color pata = new Color(0x2E7D32);
        Color cuerpo = new Color(0x4CAF50);
        Color ojo = new Color(0x111111);
        Color borde = new Color(0x1B2A1B);

        // RANA — mira hacia arriba (-y)
        agregarPoligono(new double[][] {{-12, -30, -34, -24, -22, -10}, {6, 10, 34, 34, 18, 18}}, pata, borde);
        agregarPoligono(new double[][] {{10, 22, 24, 34, 30, 12}, {18, 18, 34, 34, 10, 6}}, pata, borde);
        agregarPoligono(new double[][] {{-12, -30, -26, -10}, {-6, -20, -26, -12}}, pata, borde);
        agregarPoligono(new double[][] {{10, 26, 30, 12}, {-12, -26, -20, -6}}, pata, borde);
        agregarPoligono(new double[][] {{14.8, 6.1, -6.1, -14.8, -14.8, -6.1, 6.1, 14.8},
                                        {11.7, 22.5, 22.5, 11.7, -3.7, -14.5, -14.5, -3.7}}, cuerpo, borde);
        agregarPoligono(new double[][] {{-12, -8, 8, 12}, {-12, -28, -28, -12}}, cuerpo, borde);
        agregarPoligono(new double[][] {{-10, -4, -4, -10}, {-23, -23, -17, -17}}, ojo, borde);
        agregarPoligono(new double[][] {{4, 10, 10, 4}, {-23, -23, -17, -17}}, ojo, borde);

        sx = sy = ESCALA_BASE;
    }

    /** Coloca la rana quieta en (x, y) mirando hacia arriba. */
    public void reiniciar(double x, double y) {
        this.x = x;
        this.y = y;
        angulo = 0;
        sx = sy = ESCALA_BASE;
        saltando = false;
    }

    /**
     * Inicia un salto en la dirección (dx, dy), cada uno -1, 0 o 1.
     * minX..maxX / minY..maxY limitan el destino dentro de la pantalla.
     */
    public void saltar(int dx, int dy, double minX, double maxX, double minY, double maxY) {
        if (saltando) {
            return;
        }
        // Eje y hacia abajo: ángulo positivo = sentido horario.
        if (dy < 0) angulo = 0;
        else if (dx > 0) angulo = Math.PI / 2;
        else if (dy > 0) angulo = Math.PI;
        else angulo = 3 * Math.PI / 2;

        double nx = x + dx * PASO;
        double ny = y + dy * PASO;
        if (nx < minX || nx > maxX || ny < minY || ny > maxY) {
            return; // solo gira, no salta fuera de la pantalla
        }
        origenX = x;
        origenY = y;
        destinoX = nx;
        destinoY = ny;
        frame = 0;
        saltando = true;
    }

    @Override
    public void actualizar() {
        if (!saltando) {
            return;
        }
        frame++;
        double t = (double) frame / FRAMES_SALTO;    // 0 → 1
        x = origenX + (destinoX - origenX) * t;
        y = origenY + (destinoY - origenY) * t;
        double s = 1 + 0.3 * Math.sin(Math.PI * t);  // 1.0 → 1.3 → 1.0
        sx = sy = ESCALA_BASE * s;
        if (frame >= FRAMES_SALTO) {
            x = destinoX;
            y = destinoY;
            sx = sy = ESCALA_BASE;
            saltando = false;
        }
    }

    /** Mientras está sobre un tronco, su posición se suma a la traslación del tronco. */
    public void moverCon(Tronco tronco) {
        x += tronco.getVx();
        origenX += tronco.getVx();
        destinoX += tronco.getVx();
    }

    public boolean isSaltando() {
        return saltando;
    }
}
