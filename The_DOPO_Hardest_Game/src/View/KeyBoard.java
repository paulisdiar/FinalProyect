package View;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Listener de teclado que expone el estado de las teclas de juego
 * como flags estáticos. {@code ESCAPE_PRESSED} y {@code M_PRESSED}
 * usan detección de flanco: solo son {@code true} durante un frame
 * por pulsación, sin importar cuánto tiempo se mantenga la tecla.
 */
public class KeyBoard implements KeyListener {

	private boolean[] keys    = new boolean[256];
	private boolean[] wasDown = new boolean[256];

	public static boolean UP, LEFT, RIGHT, DOWN;
	public static boolean W, A, S, D;
	public static boolean ESCAPE_PRESSED;
	public static boolean M_PRESSED;

	/**
	 * Inicializa todos los flags a {@code false}.
	 */
	public KeyBoard() {
		UP = false; LEFT = false; RIGHT = false; DOWN = false;
		W  = false; A    = false; S     = false; D    = false;
		ESCAPE_PRESSED = false;
		M_PRESSED      = false;
	}

	/**
	 * Actualiza los flags estáticos según el estado actual de las teclas.
	 * Debe llamarse una vez por frame antes de leer los flags.
	 */
	public void update() {
		UP    = keys[KeyEvent.VK_UP];
		LEFT  = keys[KeyEvent.VK_LEFT];
		RIGHT = keys[KeyEvent.VK_RIGHT];
		DOWN  = keys[KeyEvent.VK_DOWN];
		W     = keys[KeyEvent.VK_W];
		A     = keys[KeyEvent.VK_A];
		S     = keys[KeyEvent.VK_S];
		D     = keys[KeyEvent.VK_D];

		ESCAPE_PRESSED = keys[KeyEvent.VK_ESCAPE] && !wasDown[KeyEvent.VK_ESCAPE];
		M_PRESSED      = keys[KeyEvent.VK_M]      && !wasDown[KeyEvent.VK_M];

		wasDown[KeyEvent.VK_ESCAPE] = keys[KeyEvent.VK_ESCAPE];
		wasDown[KeyEvent.VK_M]      = keys[KeyEvent.VK_M];
	}

	/**
	 * Marca la tecla como presionada.
	 *
	 * @param e evento de tecla generado por AWT
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	/**
	 * Marca la tecla como liberada.
	 *
	 * @param e evento de tecla generado por AWT
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	/**
	 * No utilizado; requerido por la interfaz {@link KeyListener}.
	 *
	 * @param e evento de tecla generado por AWT
	 */
	@Override
	public void keyTyped(KeyEvent e) {
	}
}
