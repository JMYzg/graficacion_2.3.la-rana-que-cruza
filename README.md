# La rana que cruza

Juego 2D en Java con Swing donde una rana debe cruzar una carretera con autos y un río con troncos.
Todas las figuras se construyen con polígonos en coordenadas locales y se mueven aplicando
**traslación, rotación y escalamiento** con matrices homogéneas 3×3 programadas a mano
(sin `AffineTransform` ni las transformaciones de `Graphics2D`).

## Requisitos

- Java 17 o superior (JDK)

## Cómo correrlo

```bash
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

Prueba en consola de la clase `Matriz3x3`:

```bash
java -cp out PruebaMatriz
```

## Controles

### Menú

| Tecla | Acción |
|---|---|
| ENTER | Jugar |
| T | Pantalla de prueba de transformaciones |

### Juego

| Tecla | Acción |
|---|---|
| ← ↑ → ↓ | Saltar una casilla (la rana gira hacia donde salta) |
| R | Reiniciar partida |
| ESC | Volver al menú |

### Pantalla de prueba

| Tecla | Acción |
|---|---|
| W / A / S / D, ↑ / ↓ | Trasladar |
| ← / → | Rotar -45° / +45° |
| Q / E | Rotar -5° / +5° |
| + / - | Escalar |
| O | Alternar el orden `T·R·S` / `S·R·T` |
| 1 / 2 / 3 | Rana / auto / tronco |
| ESPACIO | Reiniciar la figura |
| ESC | Volver al menú |

## Reglas

- La rana empieza abajo y gana un cruce al llegar a la orilla superior.
- Pierde una vida si la atropella un auto, si cae al agua, si un tronco la saca de la pantalla o si se acaba el tiempo.
- 3 vidas y 60 segundos por cruce.
- Cada cruce suma 100 puntos y aumenta un 10 % la velocidad de los carriles.
- Se gana al completar 5 cruces.

## Transformaciones en el juego

| Transformación | Uso |
|---|---|
| Traslación | Autos y troncos recorren sus carriles y reaparecen por el borde opuesto; la rana salta de casilla en casilla y se mueve con el tronco sobre el que está |
| Rotación | La rana gira 0°, 90°, 180° o 270° hacia donde salta; autos y troncos que van a la izquierda son la misma figura rotada 180°; algunos troncos oscilan despacio |
| Escalamiento | Durante el salto la rana crece de 1.0 a 1.3 y vuelve a 1.0; los troncos usan escala no uniforme |

Cada figura arma su matriz como `M = T(x, y) · R(ángulo) · S(sx, sy)`.

## Estructura

| Clase | Responsabilidad |
|---|---|
| `Main` | Crea la ventana y agrega el panel |
| `PanelJuego` | Ciclo de juego, teclado, reglas y dibujo |
| `Matriz3x3` | Matrices de traslación, rotación y escala; multiplicación y aplicación a puntos |
| `Figura` | Polígonos locales + posición, ángulo y escala; dibujo y caja de colisión |
| `Rana`, `Auto`, `Tronco` | Figuras del juego con su comportamiento |
| `Carril` | Agrupa los autos o troncos de un carril |
| `Caja` | Caja alineada a los ejes (AABB) para colisiones |
| `PruebaMatriz` | Prueba en consola de `Matriz3x3` |
