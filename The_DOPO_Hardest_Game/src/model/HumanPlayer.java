package model;

import java.awt.image.BufferedImage;

import view.Vector2D;

public abstract class HumanPlayer extends Player {

	protected ControlScheme controls;

	public HumanPlayer(Vector2D position, BufferedImage texture, TileManager tileManager, ControlScheme controls) {
		super(position, texture, tileManager);
		this.controls = controls;
	}

	protected int getEffectiveSpeed() {
		PlayerType skin = getActiveSkin();
		if (skin != null) {
			return (int)(SPEED * skin.speedMult);
		}
		return SPEED;
	}

	@Override
	public void update() {
		int x = (int) position.getX();
		int y = (int) position.getY();
		int w = getWidth();
		int h = getHeight();

		boolean movingH = controls.isLeft() || controls.isRight();
		boolean movingV = controls.isUp()   || controls.isDown();
		int s = (movingH && movingV) ? (int)(getEffectiveSpeed() * 0.7071 + 0.5) : getEffectiveSpeed();

		if (controls.isUp()    && !isCollidingUp(x, y, w)) {
			position.setY(y - s);
		}
		if (controls.isDown()  && !isCollidingDown(x, y, w, h)) {
			position.setY(y + s);
		}
		if (controls.isLeft()  && !isCollidingLeft(x, y, h)) {
			position.setX(x - s);
		}
		if (controls.isRight() && !isCollidingRight(x, y, w, h)) {
			position.setX(x + s);
		}
	}
}
