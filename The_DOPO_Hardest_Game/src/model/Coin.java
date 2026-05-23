package model;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import View.Vector2D;

/**
 * Moneda amarilla que el jugador puede recoger para sumar puntos.
 * Extiende {@link Collectible} con una animación de parpadeo que hace
 * que la moneda pulse visualmente para atraer la atención del jugador.
 */
public class Coin extends Collectible {

	private static final int BLINK_PERIOD = 40;
	private int animTick = 0;

	/**
	 * @param position posición inicial de la moneda
	 * @param texture  sprite de la moneda
	 */
	public Coin(Vector2D position, BufferedImage texture) {
		super(position, texture);
	}

	/**
	 * Avanza el contador de animación para el efecto de parpadeo.
	 */
	@Override
	public void update() {
		if (!isCollected()) {
			animTick = (animTick + 1) % BLINK_PERIOD;
		}
	}

	/**
	 * Dibuja la moneda con un efecto de pulso de opacidad si no ha sido recogida.
	 * La opacidad oscila entre 0.5 y 1.0 para indicar que es recogible.
	 *
	 * @param g contexto gráfico
	 */
	@Override
	public void draw(Graphics g) {
		if (isCollected()) {
			return;
		}
		float alpha = 0.5f + 0.5f * (float) Math.abs(Math.sin(Math.PI * animTick / BLINK_PERIOD));
		Graphics2D g2 = (Graphics2D) g;
		Composite old = g2.getComposite();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g2.drawImage(texture, (int) position.getX(), (int) position.getY(), null);
		g2.setComposite(old);
	}
}
