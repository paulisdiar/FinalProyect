package model;

import View.Vector2D;

public class HorizontalMovement implements MovementLogic {

	private static final int SPEED = 3;
	private int dx;

	public HorizontalMovement(int direction) {
		this.dx = direction * SPEED;
	}

	@Override
	public int[] getDirection(Vector2D position, int w, int h, TileManager tileManager) {
		int x = (int) position.getX();
		int y = (int) position.getY();

		if (dx > 0 && tileManager.isBlocked(x + w + dx, y))
			dx = -dx;
		else if (dx < 0 && tileManager.isBlocked(x + dx, y))
			dx = -dx;

		return new int[]{ dx, 0 };
	}
}
