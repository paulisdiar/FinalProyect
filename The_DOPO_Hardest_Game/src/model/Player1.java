package model;

import java.awt.image.BufferedImage;

import View.KeyBoard;
import View.Vector2D;

public class Player1 extends HumanPlayer {

	public Player1(Vector2D position, BufferedImage texture, TileManager tileManager) {
		super(position, texture, tileManager);
	}

	@Override protected boolean isUp()    { return KeyBoard.UP; }
	@Override protected boolean isDown()  { return KeyBoard.DOWN; }
	@Override protected boolean isLeft()  { return KeyBoard.LEFT; }
	@Override protected boolean isRight() { return KeyBoard.RIGHT; }
}
