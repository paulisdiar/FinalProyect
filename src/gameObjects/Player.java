package gameObjects;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import input.KeyBoard;
import math.Vector2D;
import tile.TileManager;

public class Player extends GameObject {

	private static final int SPEED = 3;
	private TileManager tileManager;

	public Player(Vector2D position, BufferedImage texture, TileManager tileManager) {
		super(position, texture);
		this.tileManager = tileManager;
	}

	@Override
	public void update() {
		int x = (int) position.getX();
		int y = (int) position.getY();
		int w = texture.getWidth();
		int h = texture.getHeight();

		if (KeyBoard.UP    && !isCollidingUp(x, y, w))       position.setY(y - SPEED);
		if (KeyBoard.DOWN  && !isCollidingDown(x, y, w, h))  position.setY(y + SPEED);
		if (KeyBoard.LEFT  && !isCollidingLeft(x, y, h))     position.setX(x - SPEED);
		if (KeyBoard.RIGHT && !isCollidingRight(x, y, w, h)) position.setX(x + SPEED);
	}

	private boolean isCollidingUp(int x, int y, int w) {
		return tileManager.isBlocked(x, y) || tileManager.isBlocked(x + w/2, y) || tileManager.isBlocked(x + w, y);
	}
	private boolean isCollidingDown(int x, int y, int w, int h) {
		return tileManager.isBlocked(x, y + h) || tileManager.isBlocked(x + w/2, y + h) || tileManager.isBlocked(x + w, y + h);
	}
	private boolean isCollidingLeft(int x, int y, int h) {
		return tileManager.isBlocked(x, y) || tileManager.isBlocked(x, y + h/2) || tileManager.isBlocked(x, y + h);
	}
	private boolean isCollidingRight(int x, int y, int w, int h) {
		return tileManager.isBlocked(x + w, y) || tileManager.isBlocked(x + w, y + h/2) || tileManager.isBlocked(x + w, y + h);
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(texture, (int) position.getX(), (int) position.getY(), null);
	}
}
