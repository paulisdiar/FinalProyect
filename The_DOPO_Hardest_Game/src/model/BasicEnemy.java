package model;

import java.awt.image.BufferedImage;

import View.Vector2D;

public class BasicEnemy extends Enemy {

	public BasicEnemy(Vector2D position, BufferedImage texture, TileManager tileManager, MovementLogic movementLogic) {
		super(position, texture, tileManager, movementLogic);
	}
}
