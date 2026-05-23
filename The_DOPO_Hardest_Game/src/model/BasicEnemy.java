package model;

import java.awt.image.BufferedImage;

import View.Vector2D;

/**
 * Enemigo básico que se mueve en línea recta y rebota en las paredes.
 * Su comportamiento concreto depende del {@link MovementLogic} recibido.
 */
public class BasicEnemy extends Enemy {

	/**
	 * @param position      posición inicial del enemigo
	 * @param texture       sprite del enemigo
	 * @param tileManager   mapa de tiles para detectar colisiones
	 * @param movementLogic lógica de movimiento (horizontal o vertical)
	 */
	public BasicEnemy(Vector2D position, BufferedImage texture, TileManager tileManager, MovementLogic movementLogic) {
		super(position, texture, tileManager, movementLogic);
	}
}
