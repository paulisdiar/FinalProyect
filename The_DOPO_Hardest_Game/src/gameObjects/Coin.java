package gameObjects;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import math.Vector2D;

public class Coin extends GameObject {

	private boolean collected = false;

	public Coin(Vector2D position, BufferedImage texture) {
		super(position, texture);
	}

	public boolean isCollected() { return collected; }
	public void collect()        { collected = true; }

	@Override public void update() {}

	@Override
	public void draw(Graphics g) {
		if (!collected)
			g.drawImage(texture, (int) position.getX(), (int) position.getY(), null);
	}
}
