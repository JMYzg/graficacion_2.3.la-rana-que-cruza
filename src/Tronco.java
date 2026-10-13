import java.awt.Color;

/**
 * Tronco que flota por un carril del río. Usa escala no uniforme para ser
 * más largo que la rana; si va a la izquierda se rota 180°. Los troncos
 * "giradores" oscilan despacio alrededor de su ángulo base.
 */
public class Tronco extends Figura {

    private double vx;
    private final double anchoPantalla;
    private final boolean girador;
    private double anguloBase;
    private double tiempo;

    public Tronco(double x, double y, double vx, double anchoPantalla, boolean girador) {
        Color madera = new Color(0x8D5A2B);
        Color borde = new Color(0x5D3A1A);

        // TRONCO — horizontal
        agregarPoligono(new double[][] {{-46, 38, 46, 46, 38, -46, -42}, {-14, -14, -7, 7, 14, 14, 0}}, madera, borde);
        agregarPoligono(new double[][] {{46.5, 42.7, 37.3, 33.5, 33.5, 37.3, 42.7, 46.5},
                                        {5.4, 12.9, 12.9, 5.4, -5.4, -12.9, -12.9, -5.4}}, new Color(0xD7A86E), borde);
        agregarPoligono(new double[][] {{43, 41.5, 38.5, 37, 38.5, 41.5}, {0, 5.2, 5.2, 0, -5.2, -5.2}}, madera, null);

        this.x = x;
        this.y = y;
        this.anchoPantalla = anchoPantalla;
        this.girador = girador;
        this.tiempo = x; // desfase para que no oscilen todos igual
        sx = 1.5;
        sy = 1.4;
        setVx(vx);
    }

    public void setVx(double vx) {
        this.vx = vx;
        anguloBase = vx < 0 ? Math.PI : 0;
        angulo = anguloBase;
    }

    public double getVx() {
        return vx;
    }

    public boolean isGirador() {
        return girador;
    }

    @Override
    public void actualizar() {
        x += vx;
        double mitad = 46 * sx;
        if (vx > 0 && x - mitad > anchoPantalla) {
            x -= anchoPantalla + 2 * mitad;
        } else if (vx < 0 && x + mitad < 0) {
            x += anchoPantalla + 2 * mitad;
        }
        if (girador) {
            tiempo += 0.03;
            angulo = anguloBase + 0.25 * Math.sin(tiempo); // ±14° aprox.
        }
    }
}
