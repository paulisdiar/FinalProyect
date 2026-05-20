package model;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import View.Assets;
import View.Vector2D;

public class GreenPlayer extends HumanPlayer {

	private static final float WEAKENED_SPEED_MULT = 0.7f;

	private boolean shieldBroken = false;

	public GreenPlayer(Vector2D position, BufferedImage texture, TileManager tileManager, ControlScheme controls) {
		super(position, texture, tileManager, controls);
	}

	@Override
	public boolean absorbHit() {
		if (!shieldBroken) {
			shieldBroken = true;
			return true;
		}
		return false;
	}

	@Override
	public void onRespawn() {
		super.onRespawn();
		shieldBroken = false;
	}

	@Override
	protected int getEffectiveSpeed() {
		if (shieldBroken) {
			return (int)(SPEED * WEAKENED_SPEED_MULT);
		}
		return SPEED;
	}

	@Override
	public void draw(Graphics g) {
		int x = (int) position.getX();
		int y = (int) position.getY();

		if (shieldBroken) {
			if (Assets.playerWeakGreen != null) {
				g.drawImage(Assets.playerWeakGreen, x, y, getWidth(), getHeight(), null);
			} else {
				Graphics2D g2d = (Graphics2D) g;
				Composite old = g2d.getComposite();
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
				g2d.drawImage(texture, x, y, getWidth(), getHeight(), null);
				g2d.setComposite(old);
			}
		} else {
			g.drawImage(texture, x, y, getWidth(), getHeight(), null);
		}
	}
}
