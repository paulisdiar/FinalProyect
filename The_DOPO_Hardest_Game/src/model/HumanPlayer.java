package model;

import java.awt.image.BufferedImage;

import view.Vector2D;

/**
 * Jugador controlado por un humano. Delega la lectura de entrada a un
 * {@link ControlScheme} y normaliza la velocidad en movimiento diagonal.
 */
public abstract class HumanPlayer extends Player {

	protected ControlScheme controls;

	/**
	 * @param position  posición inicial
	 * @param texture   sprite del jugador
	 * @param tileManager mapa de tiles para detectar colisiones
	 * @param controls  esquema de control que provee la dirección de entrada
	 */
	public HumanPlayer(Vector2D position, BufferedImage texture, TileManager tileManager, ControlScheme controls) {
		super(position, texture, tileManager);
		this.controls = controls;
	}

	/**
	 * Devuelve la velocidad real del jugador, considerando el multiplicador
	 * del skin activo si lo hay.
	 *
	 * @return velocidad en píxeles por frame
	 */
	protected int getEffectiveSpeed() {
		PlayerType skin = getActiveSkin();
		if (skin != null) {
			return (int)(SPEED * skin.speedMult);
		}
		return SPEED;
	}

	/**
	 * Mueve el jugador según la entrada del esquema de control,
	 * comprobando colisiones en cada dirección y normalizando la
	 * velocidad en diagonal.
	 */
	@Override
	public void update() {
		int x = (int) position.getX();
		int y = (int) position.getY();
		int w = getWidth();
		int h = getHeight();

		boolean movingH = controls.isLeft() || controls.isRight();
		boolean movingV = controls.isUp()   || controls.isDown();
		int s = (movingH && movingV) ? (int)(getEffectiveSpeed() * 0.7071 + 0.5) : getEffectiveSpeed();

		if (controls.isUp()    && !isCollidingUp(x, y, w)) {
			position.setY(y - s);
		}
		if (controls.isDown()  && !isCollidingDown(x, y, w, h)) {
			position.setY(y + s);
		}
		if (controls.isLeft()  && !isCollidingLeft(x, y, h)) {
			position.setX(x - s);
		}
		if (controls.isRight() && !isCollidingRight(x, y, w, h)) {
			position.setX(x + s);
		}
	}
}
