package model;

import java.awt.image.BufferedImage;

import View.Vector2D;

public class RedPlayer extends HumanPlayer {

	public RedPlayer(Vector2D position, BufferedImage texture, TileManager tileManager, ControlScheme controls) {
		super(position, texture, tileManager, controls);
	}
}
