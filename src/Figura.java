import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * Figura formada por polígonos en coordenadas locales (centrados en (0,0))
 * más su estado de transformación: posición, ángulo y escala.
 */
public class Figura {

    protected double x, y;            // traslación
    protected double angulo;          // rotación en radianes
    protected double sx = 1, sy = 1;  // escalamiento
    protected List<double[][]> poligonos = new ArrayList<>(); // cada polígono: {xs[], ys[]} locales
    protected List<Color> colores = new ArrayList<>();
    protected List<Color> bordes = new ArrayList<>();

    /** Agrega un polígono; los primeros quedan debajo. borde puede ser null. */
    protected void agregarPoligono(double[][] poligono, Color relleno, Color borde) {
        poligonos.add(poligono);
        colores.add(relleno);
        bordes.add(borde);
    }

    /**
     * M = T(x,y) · R(angulo) · S(sx,sy).
     * Al aplicar M a un vértice local primero se escala, luego se rota sobre el
     * centro de la figura y al final se traslada a su posición en la ventana.
     */
    public Matriz3x3 getMatriz() {
        return Matriz3x3.traslacion(x, y)
                .multiplicar(Matriz3x3.rotacion(angulo))
                .multiplicar(Matriz3x3.escala(sx, sy));
    }

    public void dibujar(Graphics g) {
        dibujarCon(g, getMatriz());
    }

    /** Transforma cada vértice con M, redondea a int y rellena el polígono. */
    public void dibujarCon(Graphics g, Matriz3x3 m) {
        for (int p = 0; p < poligonos.size(); p++) {
            double[][] pol = poligonos.get(p);
            int n = pol[0].length;
            int[] xs = new int[n];
            int[] ys = new int[n];
            for (int i = 0; i < n; i++) {
                double[] v = m.aplicar(pol[0][i], pol[1][i]);
                xs[i] = (int) Math.round(v[0]);
                ys[i] = (int) Math.round(v[1]);
            }
            g.setColor(colores.get(p));
            g.fillPolygon(xs, ys, n);
            if (bordes.get(p) != null) {
                g.setColor(bordes.get(p));
                g.drawPolygon(xs, ys, n);
            }
        }
    }

    /** AABB: mínimo y máximo de todos los vértices ya transformados. */
    public Caja getCaja() {
        Matriz3x3 m = getMatriz();
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        for (double[][] pol : poligonos) {
            for (int i = 0; i < pol[0].length; i++) {
                double[] v = m.aplicar(pol[0][i], pol[1][i]);
                minX = Math.min(minX, v[0]);
                maxX = Math.max(maxX, v[0]);
                minY = Math.min(minY, v[1]);
                maxY = Math.max(maxY, v[1]);
            }
        }
        return new Caja(minX, minY, maxX, maxY);
    }

    public void actualizar() {
    }

    public boolean colisionaCon(Figura otra) {
        return getCaja().intersecta(otra.getCaja());
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getAngulo() { return angulo; }
    public double getSx() { return sx; }
    public double getSy() { return sy; }

    public void setPosicion(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setAngulo(double angulo) {
        this.angulo = angulo;
    }

    public void setEscala(double sx, double sy) {
        this.sx = sx;
        this.sy = sy;
    }
}
