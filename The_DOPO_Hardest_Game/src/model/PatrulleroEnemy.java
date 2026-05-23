package model;

import java.util.List;

import view.Vector2D;

import java.awt.image.BufferedImage;

/**
 * Enemigo patrullero que recorre una ruta fija de waypoints en bucle
 * usando {@link PatrolMovement}.
 */
public class PatrulleroEnemy extends Enemy {

	/**
	 * @param position    posición inicial del patrullero
	 * @param texture     sprite del enemigo
	 * @param tileManager mapa de tiles para detectar colisiones
	 * @param waypoints   lista de posiciones que forman la ruta de patrulla
	 */
	public PatrulleroEnemy(Vector2D position, BufferedImage texture, TileManager tileManager, List<Vector2D> waypoints) {
		super(position, texture, tileManager, new PatrolMovement(waypoints));
	}
}
