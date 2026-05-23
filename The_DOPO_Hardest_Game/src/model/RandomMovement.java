package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import View.Vector2D;

/**
 * Movimiento aleatorio: la entidad avanza en una dirección aleatoria
 * y cambia de dirección al chocar con una pared o al superar
 * el intervalo de cambio.
 */
public class RandomMovement implements MovementLogic {

	private static final int SPEED = 3;
	private static final int CHANGE_INTERVAL = 50;

	private int dx = SPEED, dy = 0;
	private int ticker = 0;
	private final Random random = new Random();

	/**
	 * Calcula el desplazamiento aleatorio para este frame.
	 * Cambia de dirección si hay pared o si se agota el intervalo.
	 *
	 * @param position    posición actual de la entidad
	 * @param w           ancho de la entidad en píxeles
	 * @param h           alto de la entidad en píxeles
	 * @param tileManager mapa de tiles para consultar colisiones
	 * @return arreglo {@code [dx, dy]}, o {@code [0, 0]} si no hay dirección válida
	 */
	@Override
	public int[] getDirection(Vector2D position, int w, int h, TileManager tileManager) {
		int x = (int) position.getX();
		int y = (int) position.getY();

		boolean blockedRight = tileManager.isBlocked(x + w + SPEED, y);
		boolean blockedLeft  = tileManager.isBlocked(x - SPEED, y);
		boolean blockedDown  = tileManager.isBlocked(x, y + h + SPEED);
		boolean blockedUp    = tileManager.isBlocked(x, y - SPEED);

		boolean hitWall = (dx > 0 && blockedRight) || (dx < 0 && blockedLeft)
		               || (dy > 0 && blockedDown)  || (dy < 0 && blockedUp);

		ticker++;
		if (hitWall || ticker >= CHANGE_INTERVAL) {
			ticker = 0;
			pickDirection(blockedRight, blockedLeft, blockedDown, blockedUp);
		}

		int finalDx = (dx > 0 && blockedRight) || (dx < 0 && blockedLeft) ? 0 : dx;
		int finalDy = (dy > 0 && blockedDown)  || (dy < 0 && blockedUp)  ? 0 : dy;

		return new int[]{ finalDx, finalDy };
	}

	/**
	 * Elige aleatoriamente una dirección libre entre las cuatro posibles.
	 *
	 * @param blockedRight {@code true} si la derecha está bloqueada
	 * @param blockedLeft  {@code true} si la izquierda está bloqueada
	 * @param blockedDown  {@code true} si abajo está bloqueado
	 * @param blockedUp    {@code true} si arriba está bloqueado
	 */
	private void pickDirection(boolean blockedRight, boolean blockedLeft,
	                           boolean blockedDown,  boolean blockedUp) {
		List<int[]> valid = new ArrayList<>();
		if (!blockedRight) valid.add(new int[]{ SPEED,  0 });
		if (!blockedLeft)  valid.add(new int[]{ -SPEED, 0 });
		if (!blockedDown)  valid.add(new int[]{ 0,  SPEED });
		if (!blockedUp)    valid.add(new int[]{ 0, -SPEED });

		if (!valid.isEmpty()) {
			int[] chosen = valid.get(random.nextInt(valid.size()));
			dx = chosen[0];
			dy = chosen[1];
		}
	}
}
