package model;

import View.KeyBoard;

/**
 * Esquema de control del Jugador 2: teclas WASD del teclado.
 */
public class Player2 implements ControlScheme {

	/**
	 * @return {@code true} si la tecla W está presionada
	 */
	@Override
	public boolean isUp() {
		return KeyBoard.W;
	}

	/**
	 * @return {@code true} si la tecla S está presionada
	 */
	@Override
	public boolean isDown() {
		return KeyBoard.S;
	}

	/**
	 * @return {@code true} si la tecla A está presionada
	 */
	@Override
	public boolean isLeft() {
		return KeyBoard.A;
	}

	/**
	 * @return {@code true} si la tecla D está presionada
	 */
	@Override
	public boolean isRight() {
		return KeyBoard.D;
	}
}
