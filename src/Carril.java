import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/** Un carril de 60 px de la carretera o del río, con sus objetos en movimiento. */
public class Carril {

    public enum Tipo { CARRETERA, RIO }

    private final Tipo tipo;
    private final double yCentro;
    private final double velocidadBase;
    private final List<Figura> objetos = new ArrayList<>();

    /**
     * @param velocidad px por cuadro; negativa = hacia la izquierda
     * @param cantidad  objetos repartidos a lo largo del carril
     * @param giradores si los troncos de este carril giran despacio
     */
    public Carril(Tipo tipo, double yCentro, double velocidad, int cantidad, double desfase,
                  double anchoPantalla, boolean giradores) {
        this.tipo = tipo;
        this.yCentro = yCentro;
        this.velocidadBase = velocidad;
        double separacion = (anchoPantalla + 150) / cantidad;
        for (int i = 0; i < cantidad; i++) {
            double x = desfase + i * separacion;
            if (tipo == Tipo.CARRETERA) {
                objetos.add(new Auto(x, yCentro, velocidad, anchoPantalla));
            } else {
                objetos.add(new Tronco(x, yCentro, velocidad, anchoPantalla, giradores && i % 2 == 0));
            }
        }
    }

    /** Aplica un multiplicador a la velocidad base (1.1^cruces). */
    public void setMultiplicador(double factor) {
        for (Figura f : objetos) {
            if (f instanceof Auto a) a.setVx(velocidadBase * factor);
            else if (f instanceof Tronco t) t.setVx(velocidadBase * factor);
        }
    }

    public void actualizar() {
        for (Figura f : objetos) {
            f.actualizar();
        }
    }

    public void dibujar(Graphics g) {
        for (Figura f : objetos) {
            f.dibujar(g);
        }
    }

    public Tipo getTipo() { return tipo; }
    public double getYCentro() { return yCentro; }
    public List<Figura> getObjetos() { return objetos; }
}
