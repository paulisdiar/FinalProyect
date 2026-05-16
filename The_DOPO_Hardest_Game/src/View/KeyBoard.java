package View;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyBoard implements KeyListener{

	private boolean[] keys = new boolean[256];

	public static boolean UP, LEFT, RIGHT, DOWN;
	public static boolean W, A, S, D;

	public KeyBoard() {
		UP = false; LEFT = false; RIGHT = false; DOWN = false;
		W  = false; A    = false; S     = false; D    = false;
	}

	public void update() {
		UP   = keys[KeyEvent.VK_UP];
		LEFT = keys[KeyEvent.VK_LEFT];
		RIGHT= keys[KeyEvent.VK_RIGHT];
		DOWN = keys[KeyEvent.VK_DOWN];
		W    = keys[KeyEvent.VK_W];
		A    = keys[KeyEvent.VK_A];
		S    = keys[KeyEvent.VK_S];
		D    = keys[KeyEvent.VK_D];
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}
}
