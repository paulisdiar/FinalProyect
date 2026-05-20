package model;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import View.Vector2D;

public abstract class Player extends GameObject {

	protected static final int SPEED = 3;
	protected TileManager tileManager;
	private PlayerType activeSkin = null;

	public Player(Vector2D position, BufferedImage texture, TileManager tileManager) {
		super(position, texture);
		this.tileManager = tileManager;
	}

	public void applySkin(PlayerType skin) {
		activeSkin = skin;
	}

	public void clearSkin() {
		activeSkin = null;
	}

	public PlayerType getActiveSkin() {
		return activeSkin;
	}

	public int getWidth() {
		int base = texture.getWidth();
		if (activeSkin != null) {
			return (int)(base * activeSkin.sizeMult);
		}
		return base;
	}

	public int getHeight() {
		int base = texture.getHeight();
		if (activeSkin != null) {
			return (int)(base * activeSkin.sizeMult);
		}
		return base;
	}

	public boolean absorbHit() {
		return false;
	}

	public void onRespawn() {
		clearSkin();
	}

	protected boolean isCollidingUp(int x, int y, int w) {
		return tileManager.isBlocked(x, y) || tileManager.isBlocked(x + w/2, y) || tileManager.isBlocked(x + w, y);
	}

	protected boolean isCollidingDown(int x, int y, int w, int h) {
		return tileManager.isBlocked(x, y + h) || tileManager.isBlocked(x + w/2, y + h) || tileManager.isBlocked(x + w, y + h);
	}

	protected boolean isCollidingLeft(int x, int y, int h) {
		return tileManager.isBlocked(x, y) || tileManager.isBlocked(x, y + h/2) || tileManager.isBlocked(x, y + h);
	}

	protected boolean isCollidingRight(int x, int y, int w, int h) {
		return tileManager.isBlocked(x + w, y) || tileManager.isBlocked(x + w, y + h/2) || tileManager.isBlocked(x + w, y + h);
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(texture, (int) position.getX(), (int) position.getY(), getWidth(), getHeight(), null);
	}
}
