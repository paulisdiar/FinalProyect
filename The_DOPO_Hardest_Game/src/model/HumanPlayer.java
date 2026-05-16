package model;

import java.awt.image.BufferedImage;

import View.Vector2D;

public abstract class HumanPlayer extends Player {

	public HumanPlayer(Vector2D position, BufferedImage texture, TileManager tileManager) {
		super(position, texture, tileManager);
	}

	protected abstract boolean isUp();
	protected abstract boolean isDown();
	protected abstract boolean isLeft();
	protected abstract boolean isRight();

	@Override
	public void update() {
		int x = (int) position.getX();
		int y = (int) position.getY();
		int w = texture.getWidth();
		int h = texture.getHeight();

		boolean movingH = isLeft() || isRight();
		boolean movingV = isUp()   || isDown();
		int s = (movingH && movingV) ? (int)(SPEED * 0.7071 + 0.5) : SPEED;

		if (isUp()    && !isCollidingUp(x, y, w))       position.setY(y - s);
		if (isDown()  && !isCollidingDown(x, y, w, h))  position.setY(y + s);
		if (isLeft()  && !isCollidingLeft(x, y, h))     position.setX(x - s);
		if (isRight() && !isCollidingRight(x, y, w, h)) position.setX(x + s);
	}
}
