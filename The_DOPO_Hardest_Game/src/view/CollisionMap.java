package view;

import java.awt.Color;
import java.awt.image.BufferedImage;

import controller.Window;

public class CollisionMap {

	private BufferedImage map;
	private float scaleX, scaleY;

	public CollisionMap(BufferedImage map) {
		this.map = map;
		this.scaleX = (float) Window.WIDTH / map.getWidth();
		this.scaleY = (float) Window.HEIGHT / map.getHeight();
	}

	public boolean isWall(int x, int y) {
		int px = (int)(x / scaleX);
		int py = (int)(y / scaleY);

		if (px < 0 || py < 0 || px >= map.getWidth() || py >= map.getHeight())
			return true;

		Color color = new Color(map.getRGB(px, py));
		return color.getRed() < 30 && color.getGreen() < 30 && color.getBlue() < 30;
	}
}
