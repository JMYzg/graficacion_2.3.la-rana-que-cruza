/** Prueba en consola de la clase Matriz3x3 (paso 2). */
public class PruebaMatriz {

    public static void main(String[] args) {
        double[] p = Matriz3x3.rotacion(Math.toRadians(90)).aplicar(1, 0);
        System.out.printf("Rotar (1, 0) 90 grados    -> (%.1f, %.1f)   esperado (0, 1)%n", p[0], p[1]);

        p = Matriz3x3.escala(2, 2).aplicar(2, 3);
        System.out.printf("Escalar (2, 3) por 2      -> (%.1f, %.1f)   esperado (4, 6)%n", p[0], p[1]);

        p = Matriz3x3.traslacion(5, -2).aplicar(1, 1);
        System.out.printf("Trasladar (1, 1) en (5,-2) -> (%.1f, %.1f)   esperado (6, -1)%n", p[0], p[1]);

        // El orden importa: T*R no es lo mismo que R*T
        Matriz3x3 t = Matriz3x3.traslacion(10, 0);
        Matriz3x3 r = Matriz3x3.rotacion(Math.toRadians(90));
        double[] a = t.multiplicar(r).aplicar(1, 0);
        double[] b = r.multiplicar(t).aplicar(1, 0);
        System.out.printf("T*R aplicado a (1, 0)     -> (%.1f, %.1f)   rota y luego traslada%n", a[0], a[1]);
        System.out.printf("R*T aplicado a (1, 0)     -> (%.1f, %.1f)   traslada y luego rota%n", b[0], b[1]);

        System.out.println("\nM = T(10,0) * R(90 grados) * S(2,2):");
        System.out.print(t.multiplicar(r).multiplicar(Matriz3x3.escala(2, 2)));
    }
}
