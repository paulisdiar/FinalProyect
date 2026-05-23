package model;

import java.awt.image.BufferedImage;

import view.Vector2D;

/**
 * Jugador rojo: velocidad y tamaño base sin modificadores adicionales.
 */
public class RedPlayer extends HumanPlayer {

	/**
	 * @param position    posición inicial
	 * @param texture     sprite del jugador
	 * @param tileManager mapa de tiles para detectar colisiones
	 * @param controls    esquema de control
	 */
	public RedPlayer(Vector2D position, BufferedImage texture, TileManager tileManager, ControlScheme controls) {
		super(position, texture, tileManager, controls);
	}
}
