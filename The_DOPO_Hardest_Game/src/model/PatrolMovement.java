package model;

import java.util.List;

import view.Vector2D;

/**
 * Movimiento de patrulla: la entidad recorre en bucle una lista de
 * puntos de paso. Avanza hacia el siguiente waypoint en cuanto
 * se acerca lo suficiente al actual.
 */
public class PatrolMovement implements MovementLogic {

	private static final int SPEED     = 3;
	private static final int TOLERANCE = 4;

	private final List<Vector2D> waypoints;
	private int current = 0;

	/**
	 * @param waypoints lista ordenada de posiciones a patrullar, en bucle
	 */
	public PatrolMovement(List<Vector2D> waypoints) {
		this.waypoints = waypoints;
	}

	/**
	 * Mueve la entidad hacia el siguiente waypoint de la ruta.
	 * Si la lista está vacía devuelve desplazamiento cero.
	 *
	 * @param position    posición actual de la entidad
	 * @param w           ancho de la entidad en píxeles
	 * @param h           alto de la entidad en píxeles
	 * @param tileManager mapa de tiles (no usado directamente, requerido por la interfaz)
	 * @return arreglo {@code [dx, dy]} hacia el waypoint activo
	 */
	@Override
	public int[] getDirection(Vector2D position, int w, int h, TileManager tileManager) {
		if (waypoints.isEmpty()) {
			return new int[]{ 0, 0 };
		}

		Vector2D target = waypoints.get(current);
		double tx = target.getX();
		double ty = target.getY();
		double px = position.getX();
		double py = position.getY();

		if (Math.abs(px - tx) <= TOLERANCE && Math.abs(py - ty) <= TOLERANCE) {
			current = (current + 1) % waypoints.size();
			target  = waypoints.get(current);
			tx      = target.getX();
			ty      = target.getY();
		}

		int dx = 0;
		int dy = 0;

		if (Math.abs(px - tx) > TOLERANCE) {
			dx = (px < tx) ? SPEED : -SPEED;
		} else if (Math.abs(py - ty) > TOLERANCE) {
			dy = (py < ty) ? SPEED : -SPEED;
		}

		return new int[]{ dx, dy };
	}
}
