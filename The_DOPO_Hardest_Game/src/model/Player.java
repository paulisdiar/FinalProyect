package model;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import View.Vector2D;

public abstract class Player extends GameObject {

	protected static final int SPEED = 3;
	protected TileManager tileManager;

	public Player(Vector2D position, BufferedImage texture, TileManager tileManager) {
		super(position, texture);
		this.tileManager = tileManager;
	}

	protected boolean isCollidingUp(int x, int y, int w) {
		return tileManager.isBlocked(x, y) || tileManager.isBlocked(x + w/2, y) || tileManager.isBlocked(x + w, y);
	}
	protected boolean isCollidingDown(int x, int y, int w, int h) {
		return tileManager.isBlocked(x, y + h) || tileManager.isBlocked(x + w/2, y + h) || tileManager.isBlocked(x + w, y + h);
	}
	protected boolean isCollidingLeft(int x, int y, int h) {
		return tileManager.isBlocked(x, y) || tileManager.isBlocked(x, y + h/2) || tileManager.isBlocked(x, y + h);
	}
	protected boolean isCollidingRight(int x, int y, int w, int h) {
		return tileManager.isBlocked(x + w, y) || tileManager.isBlocked(x + w, y + h/2) || tileManager.isBlocked(x + w, y + h);
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(texture, (int) position.getX(), (int) position.getY(), null);
	}
}
