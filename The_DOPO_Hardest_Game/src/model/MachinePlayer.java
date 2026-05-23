package model;

import java.awt.image.BufferedImage;

import View.Vector2D;

/**
 * Jugador controlado por la máquina. Usa una {@link MovementLogic}
 * para determinar su dirección y verifica colisiones antes de moverse.
 */
public class MachinePlayer extends Player {

	private MovementLogic movementLogic;

	/**
	 * @param position      posición inicial
	 * @param texture       sprite del jugador
	 * @param tileManager   mapa de tiles para detectar colisiones
	 * @param movementLogic estrategia de movimiento de la IA
	 */
	public MachinePlayer(Vector2D position, BufferedImage texture, TileManager tileManager, MovementLogic movementLogic) {
		super(position, texture, tileManager);
		this.movementLogic = movementLogic;
	}

	/**
	 * Mueve al jugador máquina según su lógica de IA, comprobando
	 * colisiones en cada eje antes de aplicar el desplazamiento.
	 */
	@Override
	public void update() {
		int x = (int) position.getX();
		int y = (int) position.getY();
		int w = texture.getWidth();
		int h = texture.getHeight();

		int[] dir = movementLogic.getDirection(position, w, h, tileManager);

		if (dir[1] < 0 && !isCollidingUp(x, y, w))       position.setY(y + dir[1]);
		if (dir[1] > 0 && !isCollidingDown(x, y, w, h))  position.setY(y + dir[1]);
		if (dir[0] < 0 && !isCollidingLeft(x, y, h))     position.setX(x + dir[0]);
		if (dir[0] > 0 && !isCollidingRight(x, y, w, h)) position.setX(x + dir[0]);
	}
}
