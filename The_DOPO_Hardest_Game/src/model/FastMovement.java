package model;

import view.Vector2D;

/**
 * Decorador de movimiento que duplica la velocidad del movimiento base.
 * Implementa el patrón Decorator sobre {@link MovementLogic}.
 */
public class FastMovement implements MovementLogic {

	private static final float SPEED_MULT = 2.0f;
	private final MovementLogic base;

	/**
	 * @param base lógica de movimiento a decorar
	 */
	public FastMovement(MovementLogic base) {
		this.base = base;
	}

	/**
	 * Delega al movimiento base y multiplica el resultado por {@code SPEED_MULT}.
	 *
	 * @param position    posición actual de la entidad
	 * @param w           ancho de la entidad en píxeles
	 * @param h           alto de la entidad en píxeles
	 * @param tileManager mapa de tiles para consultar colisiones
	 * @return arreglo {@code [dx, dy]} con velocidad duplicada
	 */
	@Override
	public int[] getDirection(Vector2D position, int w, int h, TileManager tileManager) {
		int[] dir = base.getDirection(position, w, h, tileManager);
		return new int[]{ (int)(dir[0] * SPEED_MULT), (int)(dir[1] * SPEED_MULT) };
	}
}
