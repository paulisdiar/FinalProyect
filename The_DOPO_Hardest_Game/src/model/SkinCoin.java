package model;

import java.awt.image.BufferedImage;

import View.Vector2D;

/**
 * Moneda especial que al recogerse aplica temporalmente un skin
 * de tipo {@link PlayerType} al jugador que la toca.
 */
public class SkinCoin extends Coin {

	private final PlayerType skinType;

	/**
	 * @param position posición inicial de la moneda
	 * @param texture  sprite de la moneda
	 * @param skinType tipo de jugador que aplica al recogerla
	 */
	public SkinCoin(Vector2D position, BufferedImage texture, PlayerType skinType) {
		super(position, texture);
		this.skinType = skinType;
	}

	/**
	 * @return tipo de jugador que otorga esta moneda
	 */
	public PlayerType getSkinType() {
		return skinType;
	}
}
