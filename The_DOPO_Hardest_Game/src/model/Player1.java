package model;

import View.KeyBoard;

/**
 * Esquema de control del Jugador 1: teclas de dirección del teclado.
 */
public class Player1 implements ControlScheme {

	/**
	 * @return {@code true} si la tecla arriba está presionada
	 */
	@Override
	public boolean isUp() {
		return KeyBoard.UP;
	}

	/**
	 * @return {@code true} si la tecla abajo está presionada
	 */
	@Override
	public boolean isDown() {
		return KeyBoard.DOWN;
	}

	/**
	 * @return {@code true} si la tecla izquierda está presionada
	 */
	@Override
	public boolean isLeft() {
		return KeyBoard.LEFT;
	}

	/**
	 * @return {@code true} si la tecla derecha está presionada
	 */
	@Override
	public boolean isRight() {
		return KeyBoard.RIGHT;
	}
}
