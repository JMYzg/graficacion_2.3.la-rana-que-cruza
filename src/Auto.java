import java.awt.Color;

/**
 * Auto que recorre un carril. La figura mira hacia +x; los que van a la
 * izquierda son la misma figura rotada 180°.
 */
public class Auto extends Figura {

    private double vx;
    private final double anchoPantalla;

    public Auto(double x, double y, double vx, double anchoPantalla) {
        Color llanta = new Color(0x212121);
        Color carroceria = new Color(0xD32F2F);
        Color parabrisas = new Color(0x90CAF9);

        // AUTO — mira hacia la derecha (+x)
        agregarPoligono(new double[][] {{-24, -12, -12, -24}, {-22, -22, -16, -16}}, llanta, Color.BLACK);
        agregarPoligono(new double[][] {{-24, -12, -12, -24}, {16, 16, 22, 22}}, llanta, Color.BLACK);
        agregarPoligono(new double[][] {{16, 28, 28, 16}, {-22, -22, -16, -16}}, llanta, Color.BLACK);
        agregarPoligono(new double[][] {{16, 28, 28, 16}, {16, 16, 22, 22}}, llanta, Color.BLACK);
        agregarPoligono(new double[][] {{-38, 30, 40, 40, 30, -38}, {-17, -17, -8, 8, 17, 17}},
                carroceria, new Color(0x7F1515));
        agregarPoligono(new double[][] {{6, 18, 18, 6}, {-12, -14, 14, 12}}, parabrisas, new Color(0x1B2A3A));

        this.x = x;
        this.y = y;
        this.anchoPantalla = anchoPantalla;
        sx = sy = 0.9;
        setVx(vx);
    }

    public void setVx(double vx) {
        this.vx = vx;
        angulo = vx < 0 ? Math.PI : 0;
    }

    public double getVx() {
        return vx;
    }

    @Override
    public void actualizar() {
        x += vx;
        // Al salir por un borde reaparece por el opuesto
        double mitad = 40 * sx;
        if (vx > 0 && x - mitad > anchoPantalla) {
            x -= anchoPantalla + 2 * mitad;
        } else if (vx < 0 && x + mitad < 0) {
            x += anchoPantalla + 2 * mitad;
        }
    }
}
