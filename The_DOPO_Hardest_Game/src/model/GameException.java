package model;

/**
 * Excepción de dominio lanzada ante errores de guardado/carga de partida
 * u otras condiciones de error controladas dentro del juego.
 */
public class GameException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message descripción del error
	 */
	public GameException(String message) {
		super(message);
	}
}
