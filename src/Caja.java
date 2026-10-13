/** Caja alineada a los ejes (AABB) que encierra una figura ya transformada. */
public record Caja(double minX, double minY, double maxX, double maxY) {

    public boolean intersecta(Caja o) {
        return minX < o.maxX && maxX > o.minX && minY < o.maxY && maxY > o.minY;
    }

    public boolean contiene(double x, double y) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    /** Caja más pequeña por "margen" en cada lado, para colisiones menos estrictas. */
    public Caja reducida(double margen) {
        return new Caja(minX + margen, minY + margen, maxX - margen, maxY - margen);
    }

    public double ancho() {
        return maxX - minX;
    }
}
