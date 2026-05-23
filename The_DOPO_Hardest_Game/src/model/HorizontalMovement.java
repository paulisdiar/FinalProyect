package model;

import View.Vector2D;

/**
 * Movimiento horizontal con rebote: el enemigo avanza en el eje X
 * e invierte su dirección al chocar con una pared.
 */
public class HorizontalMovement implements MovementLogic {

	private static final int SPEED = 3;
	private int dx;

	/**
	 * @param direction dirección inicial: {@code 1} = derecha, {@code -1} = izquierda
	 */
	public HorizontalMovement(int direction) {
		this.dx = direction * SPEED;
	}

	/**
	 * Devuelve el desplazamiento horizontal e invierte la dirección si hay pared.
	 *
	 * @param position    posición actual de la entidad
	 * @param w           ancho de la entidad en píxeles
	 * @param h           alto de la entidad en píxeles
	 * @param tileManager mapa de tiles para consultar colisiones
	 * @return arreglo {@code [dx, 0]}
	 */
	@Override
	public int[] getDirection(Vector2D position, int w, int h, TileManager tileManager) {
		int x = (int) position.getX();
		int y = (int) position.getY();

		if (dx > 0 && tileManager.isBlocked(x + w + dx, y)) {
			dx = -dx;
		} else if (dx < 0 && tileManager.isBlocked(x + dx, y)) {
			dx = -dx;
		}

		return new int[]{ dx, 0 };
	}
}
