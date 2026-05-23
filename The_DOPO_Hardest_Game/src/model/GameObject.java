package model;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import View.Vector2D;

/**
 * Clase base de todos los objetos del mundo de juego.
 * Extiende {@link GameEntity} para integrarse en la jerarquía raíz
 * GameEntity → Player / Enemy / Collectible / Obstacle.
 */
public abstract class GameObject extends GameEntity {

	/**
	 * @param position posición inicial del objeto
	 * @param texture  sprite del objeto
	 */
	public GameObject(Vector2D position, BufferedImage texture) {
		super(position, texture);
	}

	/**
	 * Actualiza el estado del objeto en cada frame.
	 */
	@Override
	public abstract void update();

	/**
	 * Dibuja el objeto en el contexto gráfico dado.
	 *
	 * @param g contexto gráfico sobre el que dibujar
	 */
	@Override
	public abstract void draw(Graphics g);
}
