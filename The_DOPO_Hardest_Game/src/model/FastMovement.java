package model;

import view.Vector2D;

public class FastMovement implements MovementLogic {

	private static final float SPEED_MULT = 2.0f;
	private final MovementLogic base;

	public FastMovement(MovementLogic base) {
		this.base = base;
	}

	@Override
	public int[] getDirection(Vector2D position, int w, int h, TileManager tileManager) {
		int[] dir = base.getDirection(position, w, h, tileManager);
		return new int[]{ (int)(dir[0] * SPEED_MULT), (int)(dir[1] * SPEED_MULT) };
	}
}
