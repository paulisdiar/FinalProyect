package view;

/**
 * Vector bidimensional de punto flotante para representar posiciones
 * y desplazamientos en el espacio de juego.
 */
public class Vector2D {

	private double x, y;

	/**
	 * Crea un vector con las coordenadas indicadas.
	 *
	 * @param x coordenada horizontal
	 * @param y coordenada vertical
	 */
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Crea un vector en el origen (0, 0).
	 */
	public Vector2D() {
		x = 0;
		y = 0;
	}

	/**
	 * @return coordenada horizontal
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x nueva coordenada horizontal
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return coordenada vertical
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y nueva coordenada vertical
	 */
	public void setY(double y) {
		this.y = y;
	}
}
