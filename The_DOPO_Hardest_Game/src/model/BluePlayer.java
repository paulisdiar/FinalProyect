package model;

import java.awt.image.BufferedImage;

import view.Vector2D;

public class BluePlayer extends HumanPlayer {

	private static final float SIZE_MULT  = 1.5f;
	private static final float SPEED_MULT = 1.5f;

	public BluePlayer(Vector2D position, BufferedImage texture, TileManager tileManager, ControlScheme controls) {
		super(position, texture, tileManager, controls);
	}

	@Override
	public int getWidth() {
		return (int)(texture.getWidth() * SIZE_MULT);
	}

	@Override
	public int getHeight() {
		return (int)(texture.getHeight() * SIZE_MULT);
	}

	@Override
	protected int getEffectiveSpeed() {
		return (int)(SPEED * SPEED_MULT);
	}
}
