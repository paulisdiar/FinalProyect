package View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuInput implements ActionListener{
	
	private boolean[] buttons = new boolean[3];
	
	public static boolean JUGAR, OPCIONES, SALIR;
	
	public MenuInput() {
		JUGAR = false;
		OPCIONES = false;
		SALIR = false;
	}
	
	public void update() {
		JUGAR = buttons[0];
		OPCIONES = buttons[1];
		SALIR = buttons[2];
		
		buttons[0] = false;
		buttons[1] = false;
		buttons[2] = false;
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
			case "Jugar" -> buttons[0] = true;
			case "Opciones" -> buttons[1] = true;
			case "Salir" -> buttons[2] = true;
		}
	}
}
