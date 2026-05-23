package model;

import java.awt.image.BufferedImage;

import view.Vector2D;

public class AceleradoEnemy extends Enemy {

	public AceleradoEnemy(Vector2D position, BufferedImage texture, TileManager tileManager, MovementLogic baseMovement) {
		super(position, texture, tileManager, new FastMovement(baseMovement));
	}
}
