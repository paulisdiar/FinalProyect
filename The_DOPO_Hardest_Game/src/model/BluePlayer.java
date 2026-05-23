package model;

import java.awt.image.BufferedImage;

import view.Vector2D;

/**
 * Jugador azul: 1.5× más rápido y 1.5× más grande que el jugador base.
 */
public class BluePlayer extends HumanPlayer {

	private static final float SIZE_MULT  = 1.5f;
	private static final float SPEED_MULT = 1.5f;

	/**
	 * @param position    posición inicial
	 * @param texture     sprite del jugador
	 * @param tileManager mapa de tiles para detectar colisiones
	 * @param controls    esquema de control
	 */
	public BluePlayer(Vector2D position, BufferedImage texture, TileManager tileManager, ControlScheme controls) {
		super(position, texture, tileManager, controls);
	}

	/**
	 * @return ancho del sprite multiplicado por {@code SIZE_MULT}
	 */
	@Override
	public int getWidth() {
		return (int)(texture.getWidth() * SIZE_MULT);
	}

	/**
	 * @return alto del sprite multiplicado por {@code SIZE_MULT}
	 */
	@Override
	public int getHeight() {
		return (int)(texture.getHeight() * SIZE_MULT);
	}

	/**
	 * @return velocidad base multiplicada por {@code SPEED_MULT}
	 */
	@Override
	protected int getEffectiveSpeed() {
		return (int)(SPEED * SPEED_MULT);
	}
}
