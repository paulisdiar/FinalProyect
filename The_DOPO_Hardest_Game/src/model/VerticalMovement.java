package model;

import View.Vector2D;

public class VerticalMovement implements MovementLogic {

	private static final int SPEED = 3;
	private int dy;

	public VerticalMovement(int direction) {
		this.dy = direction * SPEED;
	}

	@Override
	public int[] getDirection(Vector2D position, int w, int h, TileManager tileManager) {
		int x = (int) position.getX();
		int y = (int) position.getY();

		if (dy > 0 && tileManager.isBlocked(x, y + h + dy))
			dy = -dy;
		else if (dy < 0 && tileManager.isBlocked(x, y + dy))
			dy = -dy;

		return new int[]{ 0, dy };
	}
}
