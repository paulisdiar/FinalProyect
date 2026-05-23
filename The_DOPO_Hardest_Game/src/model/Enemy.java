package model;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import View.Vector2D;

/**
 * Clase base de todos los enemigos. Delega el cálculo de movimiento
 * a una estrategia {@link MovementLogic} intercambiable.
 */
public abstract class Enemy extends GameObject {

	protected TileManager tileManager;
	protected MovementLogic movementLogic;

	/**
	 * @param position      posición inicial del enemigo
	 * @param texture       sprite del enemigo
	 * @param tileManager   mapa de tiles para detectar colisiones
	 * @param movementLogic estrategia de movimiento a aplicar
	 */
	public Enemy(Vector2D position, BufferedImage texture, TileManager tileManager, MovementLogic movementLogic) {
		super(position, texture);
		this.tileManager   = tileManager;
		this.movementLogic = movementLogic;
	}

	/**
	 * Mueve el enemigo según su lógica de movimiento.
	 */
	@Override
	public void update() {
		int w = texture.getWidth();
		int h = texture.getHeight();
		int[] dir = movementLogic.getDirection(position, w, h, tileManager);
		position.setX(position.getX() + dir[0]);
		position.setY(position.getY() + dir[1]);
	}

	/**
	 * Dibuja el sprite del enemigo en su posición actual.
	 *
	 * @param g contexto gráfico
	 */
	@Override
	public void draw(Graphics g) {
		g.drawImage(texture, (int) position.getX(), (int) position.getY(), null);
	}
}
