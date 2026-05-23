package model;

import view.KeyBoard;

public class Player1 implements ControlScheme {

	@Override
	public boolean isUp() {
		return KeyBoard.UP;
	}

	@Override
	public boolean isDown() {
		return KeyBoard.DOWN;
	}

	@Override
	public boolean isLeft() {
		return KeyBoard.LEFT;
	}

	@Override
	public boolean isRight() {
		return KeyBoard.RIGHT;
	}
}
