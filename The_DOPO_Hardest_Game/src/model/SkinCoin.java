package model;

import java.awt.image.BufferedImage;

import view.Vector2D;

public class SkinCoin extends Coin {

	private final PlayerType skinType;

	public SkinCoin(Vector2D position, BufferedImage texture, PlayerType skinType) {
		super(position, texture);
		this.skinType = skinType;
	}

	public PlayerType getSkinType() {
		return skinType;
	}
}
