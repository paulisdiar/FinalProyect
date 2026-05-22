package model;

import View.Vector2D;
@FunctionalInterface
public interface MovementLogic {

	int[] getDirection(Vector2D position, int w, int h, TileManager tileManager);
}
