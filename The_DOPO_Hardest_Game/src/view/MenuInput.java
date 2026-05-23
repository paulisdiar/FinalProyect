package View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Manejador de acciones del menú principal. Almacena las pulsaciones
 * en un búfer y las expone como flags estáticos tras {@link #update()}.
 */
public class MenuInput implements ActionListener {

	private boolean[] buttons = new boolean[3];

	public static boolean JUGAR;
	public static boolean OPCIONES;
	public static boolean SALIR;

	/**
	 * Inicializa todos los flags a {@code false}.
	 */
	public MenuInput() {
		JUGAR    = false;
		OPCIONES = false;
		SALIR    = false;
	}

	/**
	 * Transfiere el búfer interno a los flags estáticos y lo limpia.
	 * Debe llamarse una vez por frame.
	 */
	public void update() {
		JUGAR    = buttons[0];
		OPCIONES = buttons[1];
		SALIR    = buttons[2];

		buttons[0] = false;
		buttons[1] = false;
		buttons[2] = false;
	}

	/**
	 * Registra la acción del botón pulsado en el búfer interno.
	 *
	 * @param e evento de acción generado por Swing
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "Jugar"    -> buttons[0] = true;
			case "Opciones" -> buttons[1] = true;
			case "Salir"    -> buttons[2] = true;
		}
	}
}
