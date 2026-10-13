/**
 * Matriz 3x3 en coordenadas homogéneas.
 * Un punto (x, y) se representa como el vector columna (x, y, 1).
 */
public class Matriz3x3 {

    private final double[][] m;

    public Matriz3x3(double[][] valores) {
        m = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                m[i][j] = valores[i][j];
            }
        }
    }

    public static Matriz3x3 identidad() {
        return new Matriz3x3(new double[][] {
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
        });
    }

    /** Traslación: mueve el punto tx en x y ty en y. */
    public static Matriz3x3 traslacion(double tx, double ty) {
        return new Matriz3x3(new double[][] {
            {1, 0, tx},
            {0, 1, ty},
            {0, 0, 1}
        });
    }

    /**
     * Rotación alrededor del origen, ángulo en radianes.
     * Como en Swing el eje y crece hacia abajo, un ángulo positivo gira en sentido horario.
     */
    public static Matriz3x3 rotacion(double ang) {
        double c = Math.cos(ang);
        double s = Math.sin(ang);
        return new Matriz3x3(new double[][] {
            {c, -s, 0},
            {s,  c, 0},
            {0,  0, 1}
        });
    }

    /** Escalamiento respecto al origen. */
    public static Matriz3x3 escala(double sx, double sy) {
        return new Matriz3x3(new double[][] {
            {sx, 0, 0},
            {0, sy, 0},
            {0,  0, 1}
        });
    }

    /** Devuelve this · otra (fila por columna). */
    public Matriz3x3 multiplicar(Matriz3x3 otra) {
        double[][] r = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                double suma = 0;
                for (int k = 0; k < 3; k++) {
                    suma += m[i][k] * otra.m[k][j];
                }
                r[i][j] = suma;
            }
        }
        return new Matriz3x3(r);
    }

    /** Multiplica la matriz por el vector (x, y, 1) y devuelve {x', y'}. */
    public double[] aplicar(double x, double y) {
        double nx = m[0][0] * x + m[0][1] * y + m[0][2];
        double ny = m[1][0] * x + m[1][1] * y + m[1][2];
        double w  = m[2][0] * x + m[2][1] * y + m[2][2];
        return new double[] {nx / w, ny / w};
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (double[] fila : m) {
            sb.append(String.format("[%8.3f %8.3f %8.3f]%n", fila[0], fila[1], fila[2]));
        }
        return sb.toString();
    }
}
