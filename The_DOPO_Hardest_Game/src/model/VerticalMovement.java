package model;

import View.Vector2D;

/**
 * Movimiento vertical con rebote: el enemigo avanza en el eje Y
 * e invierte su dirección al chocar con una pared.
 */
public class VerticalMovement implements MovementLogic {

	private static final int SPEED = 3;
	private int dy;

	/**
	 * @param direction dirección inicial: {@code 1} = abajo, {@code -1} = arriba
	 */
	public VerticalMovement(int direction) {
		this.dy = direction * SPEED;
	}

	/**
	 * Devuelve el desplazamiento vertical e invierte la dirección si hay pared.
	 *
	 * @param position    posición actual de la entidad
	 * @param w           ancho de la entidad en píxeles
	 * @param h           alto de la entidad en píxeles
	 * @param tileManager mapa de tiles para consultar colisiones
	 * @return arreglo {@code [0, dy]}
	 */
	@Override
	public int[] getDirection(Vector2D position, int w, int h, TileManager tileManager) {
		int x = (int) position.getX();
		int y = (int) position.getY();

		if (dy > 0 && tileManager.isBlocked(x, y + h + dy)) {
			dy = -dy;
		} else if (dy < 0 && tileManager.isBlocked(x, y + dy)) {
			dy = -dy;
		}

		return new int[]{ 0, dy };
	}
}
