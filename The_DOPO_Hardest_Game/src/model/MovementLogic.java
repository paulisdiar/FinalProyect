package model;

import view.Vector2D;

@FunctionalInterface
public interface MovementLogic {

	int[] getDirection(Vector2D position, int width, int height, TileManager tileManager);
}
