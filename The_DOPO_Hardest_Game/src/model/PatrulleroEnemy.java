package model;

import java.util.List;
import java.awt.image.BufferedImage;

import View.Vector2D;

public class PatrulleroEnemy extends Enemy {

	public PatrulleroEnemy(Vector2D position, BufferedImage texture, TileManager tileManager, List<Vector2D> waypoints) {
		super(position, texture, tileManager, new PatrolMovement(waypoints));
	}
}
