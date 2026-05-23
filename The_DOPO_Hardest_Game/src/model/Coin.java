package model;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import view.Vector2D;

/**
 * Moneda amarilla que el jugador puede recoger para sumar puntos.
 * Extiende {@link Collectible} dentro de la jerarquía GameEntity.
 * Desaparece visualmente al ser recogida.
 */
public class Coin extends Collectible {

	/**
	 * @param position posición inicial de la moneda
	 * @param texture  sprite de la moneda
	 */
	public Coin(Vector2D position, BufferedImage texture) {
		super(position, texture);
	}

	/**
	 * Sin lógica de actualización adicional.
	 */
	@Override
	public void update() {
	}

	/**
	 * Dibuja la moneda solo si aún no ha sido recogida.
	 *
	 * @param g contexto gráfico
	 */
	@Override
	public void draw(Graphics g) {
		if (!isCollected()) {
			g.drawImage(texture, (int) position.getX(), (int) position.getY(), null);
		}
	}
}
