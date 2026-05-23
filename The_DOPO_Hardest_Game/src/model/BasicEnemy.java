package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import View.Vector2D;

/**
 * Enemigo básico que se mueve en línea recta y rebota en las paredes.
 * Extiende {@link Enemy} añadiendo un indicador visual de alerta: un pequeño
 * punto de color encima del sprite que cambia según la dirección de movimiento
 * (rojo = horizontal, azul = vertical, amarillo = diagonal).
 */
public class BasicEnemy extends Enemy {

	private final Color alertColor;

	/**
	 * @param position      posición inicial del enemigo
	 * @param texture       sprite del enemigo
	 * @param tileManager   mapa de tiles para detectar colisiones
	 * @param movementLogic lógica de movimiento (horizontal o vertical)
	 */
	public BasicEnemy(Vector2D position, BufferedImage texture, TileManager tileManager, MovementLogic movementLogic) {
		super(position, texture, tileManager, movementLogic);
		if (movementLogic instanceof HorizontalMovement) {
			alertColor = new Color(220, 50, 50);
		} else if (movementLogic instanceof VerticalMovement) {
			alertColor = new Color(50, 100, 220);
		} else if (movementLogic instanceof DiagonalMovement) {
			alertColor = new Color(220, 200, 30);
		} else {
			alertColor = new Color(180, 60, 180);
		}
	}

	/**
	 * Dibuja el sprite del enemigo y un pequeño indicador de color
	 * en la esquina superior derecha para distinguir su tipo de movimiento.
	 *
	 * @param g contexto gráfico
	 */
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		int x = (int) position.getX();
		int y = (int) position.getY();
		int w = texture != null ? texture.getWidth() : 13;
		g.setColor(alertColor);
		g.fillOval(x + w - 5, y - 1, 5, 5);
		g.setColor(Color.BLACK);
		g.drawOval(x + w - 5, y - 1, 5, 5);
	}
}
