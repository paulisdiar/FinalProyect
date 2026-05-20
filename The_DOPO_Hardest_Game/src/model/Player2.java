package model;

import View.KeyBoard;

public class Player2 implements ControlScheme {

	@Override
	public boolean isUp() {
		return KeyBoard.W;
	}

	@Override
	public boolean isDown() {
		return KeyBoard.S;
	}

	@Override
	public boolean isLeft() {
		return KeyBoard.A;
	}

	@Override
	public boolean isRight() {
		return KeyBoard.D;
	}
}
