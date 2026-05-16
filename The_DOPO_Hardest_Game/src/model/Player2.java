package model;

import java.awt.image.BufferedImage;

import View.KeyBoard;
import View.Vector2D;

public class Player2 extends HumanPlayer {

	public Player2(Vector2D position, BufferedImage texture, TileManager tileManager) {
		super(position, texture, tileManager);
	}

	@Override protected boolean isUp()    { return KeyBoard.W; }
	@Override protected boolean isDown()  { return KeyBoard.S; }
	@Override protected boolean isLeft()  { return KeyBoard.A; }
	@Override protected boolean isRight() { return KeyBoard.D; }
}
