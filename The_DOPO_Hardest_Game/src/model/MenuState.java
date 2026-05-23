package model;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import Controller.Window;
import View.MenuInput;

/**
 * Estado del menú principal. Construye la interfaz Swing con los botones
 * Jugar, Opciones y Salir, y procesa su entrada en cada frame.
 */
public class MenuState {

	private MenuInput menuInput;
	private Window window;

	/**
	 * Crea y muestra el menú principal.
	 *
	 * @param window ventana principal de la aplicación
	 */
	public MenuState(Window window) {
		this.window = window;
		menuInput = new MenuInput();
		show();
	}

	/**
	 * Construye y muestra los componentes Swing del menú principal.
	 */
	private void show() {
		window.getContentPane().removeAll();
		window.setLayout(new BorderLayout());

		JLabel titulo = new JLabel("THE DOPO HARDEST GAME", SwingConstants.CENTER);
		titulo.setFont(new Font("Arial", Font.BOLD, 36));
		window.add(titulo, BorderLayout.CENTER);

		JPanel panelBotones = new JPanel(new GridLayout(1, 3, 10, 10));

		JButton bJugar   = new JButton("Jugar");
		JButton bOpciones= new JButton("Opciones");
		JButton bSalir   = new JButton("Salir");

		bJugar.setActionCommand("Jugar");
		bOpciones.setActionCommand("Opciones");
		bSalir.setActionCommand("Salir");

		bJugar.addActionListener(menuInput);
		bOpciones.addActionListener(menuInput);
		bSalir.addActionListener(menuInput);

		panelBotones.add(bJugar);
		panelBotones.add(bOpciones);
		panelBotones.add(bSalir);

		window.add(panelBotones, BorderLayout.SOUTH);

		window.revalidate();
		window.repaint();
	}

	/**
	 * Procesa la entrada del menú y navega a la pantalla correspondiente.
	 * Debe llamarse una vez por frame desde el hilo del bucle de juego.
	 */
	public void update() {
		menuInput.update();

		if (MenuInput.JUGAR) {
			window.showInstructions();
		}
		if (MenuInput.OPCIONES) {
			window.showOptions();
		}
		if (MenuInput.SALIR) {
			System.exit(0);
		}
	}
}
