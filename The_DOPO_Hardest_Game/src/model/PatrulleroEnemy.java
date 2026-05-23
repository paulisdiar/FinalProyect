package model;

import java.util.List;

import view.Vector2D;

import java.awt.image.BufferedImage;

public class PatrulleroEnemy extends Enemy {

	public PatrulleroEnemy(Vector2D position, BufferedImage texture, TileManager tileManager, List<Vector2D> waypoints) {
		super(position, texture, tileManager, new PatrolMovement(waypoints));
	}
}
