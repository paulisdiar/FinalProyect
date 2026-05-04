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

public class MenuState {
	
	private MenuInput menuInput;
	private Window window;
	
	public MenuState (Window window) {
		this.window = window;
		menuInput = new MenuInput();
		show();
	}

	private void show() {
		window.getContentPane().removeAll();
		window.setLayout(new BorderLayout());
		
		JLabel titulo = new JLabel("THE DOPO HARDEST GAME", SwingConstants.CENTER);
		titulo.setFont(new Font("Arial", Font.BOLD, 36));
		window.add(titulo,BorderLayout.CENTER);
		
		JPanel panelBotones = new JPanel(new GridLayout(1, 3, 10, 10));
		
		JButton bJugar = new JButton("Jugar");
		JButton bOpciones = new JButton("Opciones");
		JButton bSalir = new JButton("Salir");
		
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
	
	public void update(){
		menuInput.update();
		
		if(MenuInput.JUGAR) {
			window.showInstructions();
		}
		if(MenuInput.SALIR) {
			System.exit(0);
		}
		
	}
}
