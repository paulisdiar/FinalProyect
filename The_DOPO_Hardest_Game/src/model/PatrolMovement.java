package model;

import java.util.List;

import view.Vector2D;

public class PatrolMovement implements MovementLogic {

	private static final int SPEED     = 3;
	private static final int TOLERANCE = 4;

	private final List<Vector2D> waypoints;
	private int current = 0;

	public PatrolMovement(List<Vector2D> waypoints) {
		this.waypoints = waypoints;
	}

	@Override
	public int[] getDirection(Vector2D position, int w, int h, TileManager tileManager) {
		if (waypoints.isEmpty()) {
			return new int[]{ 0, 0 };
		}

		Vector2D target = waypoints.get(current);
		double tx = target.getX();
		double ty = target.getY();
		double px = position.getX();
		double py = position.getY();

		if (Math.abs(px - tx) <= TOLERANCE && Math.abs(py - ty) <= TOLERANCE) {
			current = (current + 1) % waypoints.size();
			target  = waypoints.get(current);
			tx      = target.getX();
			ty      = target.getY();
		}

		int dx = 0;
		int dy = 0;

		if (Math.abs(px - tx) > TOLERANCE)
			dx = (px < tx) ? SPEED : -SPEED;
		else if (Math.abs(py - ty) > TOLERANCE)
			dy = (py < ty) ? SPEED : -SPEED;

		return new int[]{ dx, dy };
	}
}
