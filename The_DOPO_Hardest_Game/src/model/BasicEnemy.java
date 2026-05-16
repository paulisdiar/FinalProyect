package model;

import java.awt.image.BufferedImage;

import View.Vector2D;

public class BasicEnemy extends Enemy {

	public BasicEnemy(Vector2D position, BufferedImage texture, TileManager tileManager, int direction) {
		super(position, texture, tileManager, new HorizontalMovement(direction));
	}
}
