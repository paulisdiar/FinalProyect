package model;

/**
 * Abstracción del esquema de control de un jugador. Desacopla
 * la lógica de movimiento del origen concreto de la entrada
 * (teclado, IA, etc.).
 */
public interface ControlScheme {

    /**
     * @return {@code true} si la dirección arriba está activa
     */
    boolean isUp();

    /**
     * @return {@code true} si la dirección abajo está activa
     */
    boolean isDown();

    /**
     * @return {@code true} si la dirección izquierda está activa
     */
    boolean isLeft();

    /**
     * @return {@code true} si la dirección derecha está activa
     */
    boolean isRight();
}
