package View;

import java.awt.Color;
import java.awt.image.BufferedImage;

import Controller.Window;

/**
 * Mapa de colisiones basado en imagen: los píxeles casi negros se
 * interpretan como paredes.
 */
public class CollisionMap {

	private BufferedImage map;
	private float scaleX, scaleY;

	/**
	 * Crea el mapa escalando la imagen al tamaño de la ventana.
	 *
	 * @param map imagen de colisiones (píxeles negros = pared)
	 */
	public CollisionMap(BufferedImage map) {
		this.map = map;
		this.scaleX = (float) Window.WIDTH  / map.getWidth();
		this.scaleY = (float) Window.HEIGHT / map.getHeight();
	}

	/**
	 * Indica si las coordenadas de pantalla corresponden a una pared.
	 * Puntos fuera de límites también se consideran pared.
	 *
	 * @param x coordenada horizontal en píxeles
	 * @param y coordenada vertical en píxeles
	 * @return {@code true} si hay pared en ese punto
	 */
	public boolean isWall(int x, int y) {
		int px = (int)(x / scaleX);
		int py = (int)(y / scaleY);

		if (px < 0 || py < 0 || px >= map.getWidth() || py >= map.getHeight()) {
			return true;
		}

		Color color = new Color(map.getRGB(px, py));
		return color.getRed() < 30 && color.getGreen() < 30 && color.getBlue() < 30;
	}
}
