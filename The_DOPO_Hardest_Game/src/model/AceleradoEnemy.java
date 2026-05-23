package model;

import java.awt.image.BufferedImage;

import view.Vector2D;

/**
 * Enemigo acelerado que envuelve el movimiento base con {@link FastMovement}
 * para moverse al doble de velocidad.
 */
public class AceleradoEnemy extends Enemy {

	/**
	 * @param position     posición inicial del enemigo
	 * @param texture      sprite del enemigo
	 * @param tileManager  mapa de tiles para detectar colisiones
	 * @param baseMovement lógica de movimiento base que será acelerada
	 */
	public AceleradoEnemy(Vector2D position, BufferedImage texture, TileManager tileManager, MovementLogic baseMovement) {
		super(position, texture, tileManager, new FastMovement(baseMovement));
	}
}
