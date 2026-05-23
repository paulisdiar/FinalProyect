package View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Manejador de acciones de la pantalla de pre-juego (instrucciones).
 * Almacena pulsaciones en un búfer y las expone como flags estáticos
 * tras {@link #update()}.
 */
public class PreGameInput implements ActionListener {

	private boolean[] buttons = new boolean[2];

	public static boolean VOLVER_MENU, JUGAR;

	/**
	 * Inicializa todos los flags a {@code false}.
	 */
	public PreGameInput() {
		JUGAR       = false;
		VOLVER_MENU = false;
	}

	/**
	 * Transfiere el búfer interno a los flags estáticos y lo limpia.
	 * Debe llamarse una vez por frame.
	 */
	public void update() {
		VOLVER_MENU = buttons[0];
		JUGAR       = buttons[1];

		buttons[0] = false;
		buttons[1] = false;
	}

	/**
	 * Registra la acción del botón pulsado en el búfer interno.
	 *
	 * @param e evento de acción generado por Swing
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "Volver al Menu" -> buttons[0] = true;
			case "Jugar"          -> buttons[1] = true;
		}
	}
}
