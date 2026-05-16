package View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameMenuInput implements ActionListener{

	private boolean[] opciones = new boolean[4];

	public static boolean GUARDAR, CARGAR, VOLVER_MENU, SALIR;

	public GameMenuInput() {
		GUARDAR = false;
		CARGAR = false;
		VOLVER_MENU = false;
		SALIR = false;
	}

	public void update() {
		GUARDAR     = opciones[0];
		CARGAR      = opciones[1];
		VOLVER_MENU = opciones[2];
		SALIR       = opciones[3];

		opciones[0] = false;
		opciones[1] = false;
		opciones[2] = false;
		opciones[3] = false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
			case "Guardar"       -> opciones[0] = true;
			case "Cargar"        -> opciones[1] = true;
			case "Volver al Menu"-> opciones[2] = true;
			case "Salir"         -> opciones[3] = true;
		}
	}
}
