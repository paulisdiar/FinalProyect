package model;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import View.Vector2D;

public class Enemy extends GameObject {

	private static final int SPEED = 4;
	private int dx;
	private TileManager tileManager;

	public Enemy(Vector2D position, BufferedImage texture, TileManager tileManager, int direction) {
		super(position, texture);
		this.tileManager = tileManager;
		this.dx = direction * SPEED;
	}

	@Override
	public void update() {
		int x = (int) position.getX();
		int y = (int) position.getY();
		int w = texture.getWidth();
		int h = texture.getHeight();

		if (dx > 0 && tileManager.isBlocked(x + w + dx, y))
			dx = -dx;
		else if (dx < 0 && tileManager.isBlocked(x + dx, y))
			dx = -dx;

		position.setX(x + dx);
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(texture, (int) position.getX(), (int) position.getY(), null);
	}
}
