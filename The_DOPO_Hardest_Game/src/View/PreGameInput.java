package View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PreGameInput implements ActionListener{

	private boolean[] buttons = new boolean[2];

	public static boolean VOLVER_MENU, JUGAR;

	public PreGameInput() {
		JUGAR = false;
		VOLVER_MENU = false;
	}

	public void update() {
		VOLVER_MENU = buttons[0];
		JUGAR       = buttons[1];

		buttons[0] = false;
		buttons[1] = false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
			case "Volver al Menu"-> buttons[0] = true;
			case "Jugar"         -> buttons[1] = true;
		}
	}
}
