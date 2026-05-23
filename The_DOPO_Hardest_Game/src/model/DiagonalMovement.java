package model;

import View.Vector2D;

/**
 * Movimiento diagonal con rebote: el enemigo avanza en X e Y simultáneamente
 * e invierte cada eje de forma independiente al chocar con una pared.
 */
public class DiagonalMovement implements MovementLogic {

	private static final int SPEED = 2;
	private int dx;
	private int dy;

	/**
	 * @param dx dirección inicial en X: {@code 1} = derecha, {@code -1} = izquierda
	 * @param dy dirección inicial en Y: {@code 1} = abajo,   {@code -1} = arriba
	 */
	public DiagonalMovement(int dx, int dy) {
		this.dx = dx * SPEED;
		this.dy = dy * SPEED;
	}

	/**
	 * Devuelve el desplazamiento diagonal e invierte el eje que choca con una pared.
	 *
	 * @param position    posición actual de la entidad
	 * @param w           ancho de la entidad en píxeles
	 * @param h           alto de la entidad en píxeles
	 * @param tileManager mapa de tiles para consultar colisiones
	 * @return arreglo {@code [dx, dy]}
	 */
	@Override
	public int[] getDirection(Vector2D position, int w, int h, TileManager tileManager) {
		int x = (int) position.getX();
		int y = (int) position.getY();

		if (dx > 0 && tileManager.isBlocked(x + w + dx, y)) dx = -dx;
		else if (dx < 0 && tileManager.isBlocked(x + dx, y))    dx = -dx;

		if (dy > 0 && tileManager.isBlocked(x, y + h + dy)) dy = -dy;
		else if (dy < 0 && tileManager.isBlocked(x, y + dy))    dy = -dy;

		return new int[]{ dx, dy };
	}
}
