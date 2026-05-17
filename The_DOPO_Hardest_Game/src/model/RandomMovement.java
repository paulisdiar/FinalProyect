package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import View.Vector2D;

public class RandomMovement implements MovementLogic {

	private static final int SPEED = 3;
	private static final int CHANGE_INTERVAL = 50;

	private int dx = SPEED, dy = 0;
	private int ticker = 0;
	private final Random random = new Random();

	@Override
	public int[] getDirection(Vector2D position, int w, int h, TileManager tileManager) {
		int x = (int) position.getX();
		int y = (int) position.getY();

		boolean blockedRight = tileManager.isBlocked(x + w + SPEED, y);
		boolean blockedLeft  = tileManager.isBlocked(x - SPEED, y);
		boolean blockedDown  = tileManager.isBlocked(x, y + h + SPEED);
		boolean blockedUp    = tileManager.isBlocked(x, y - SPEED);

		boolean hitWall = (dx > 0 && blockedRight) || (dx < 0 && blockedLeft)
		               || (dy > 0 && blockedDown)  || (dy < 0 && blockedUp);

		ticker++;
		if (hitWall || ticker >= CHANGE_INTERVAL) {
			ticker = 0;
			pickDirection(blockedRight, blockedLeft, blockedDown, blockedUp);
		}

		int finalDx = (dx > 0 && blockedRight) || (dx < 0 && blockedLeft) ? 0 : dx;
		int finalDy = (dy > 0 && blockedDown)  || (dy < 0 && blockedUp)  ? 0 : dy;

		return new int[]{ finalDx, finalDy };
	}

	private void pickDirection(boolean blockedRight, boolean blockedLeft,
	                           boolean blockedDown,  boolean blockedUp) {
		List<int[]> valid = new ArrayList<>();
		if (!blockedRight) valid.add(new int[]{ SPEED,  0 });
		if (!blockedLeft)  valid.add(new int[]{ -SPEED, 0 });
		if (!blockedDown)  valid.add(new int[]{ 0,  SPEED });
		if (!blockedUp)    valid.add(new int[]{ 0, -SPEED });

		if (!valid.isEmpty()) {
			int[] chosen = valid.get(random.nextInt(valid.size()));
			dx = chosen[0];
			dy = chosen[1];
		}
	}
}
