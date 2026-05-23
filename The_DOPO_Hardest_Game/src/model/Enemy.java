package model;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import view.Vector2D;

public abstract class Enemy extends GameObject {

	protected TileManager tileManager;
	protected MovementLogic movementLogic;

	public Enemy(Vector2D position, BufferedImage texture, TileManager tileManager, MovementLogic movementLogic) {
		super(position, texture);
		this.tileManager = tileManager;
		this.movementLogic = movementLogic;
	}

	@Override
	public void update() {
		int w = texture.getWidth();
		int h = texture.getHeight();
		int[] dir = movementLogic.getDirection(position, w, h, tileManager);
		position.setX(position.getX() + dir[0]);
		position.setY(position.getY() + dir[1]);
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(texture, (int) position.getX(), (int) position.getY(), null);
	}
}
