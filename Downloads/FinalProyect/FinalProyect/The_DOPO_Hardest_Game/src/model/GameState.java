package model;

import java.awt.Graphics;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import Controller.Window;
import View.Assets;
import View.GameMenuInput;
import View.Vector2D;

public class GameState {
	
	private GameMenuInput gameMenuInput;
	private Window window;
	private Player player;
	private Level currentLevel;
	final int originalTileSize = 18;
	public final int tileSize = originalTileSize * 3;
	
	public GameState(Window window) {
		this.window = window;
		gameMenuInput = new GameMenuInput();
		initMenu();
		loadLevel(1);
	}
	
	private void loadLevel(int index) {
		if(index == 1) {
			currentLevel = new Level(this, "res/maps/level1.txt");
		}
	}

	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Opciones");
		
		JMenuItem guardar = new JMenuItem("Guardar");
		JMenuItem cargar = new JMenuItem("Cargar");
		JMenuItem volverMenu = new JMenuItem("Volver al Menu");
		JMenuItem salir = new JMenuItem("Salir");
		
		guardar.setActionCommand("Guardar");
		cargar.setActionCommand("Cargar");
		volverMenu.setActionCommand("Volver al Menu");
		salir.setActionCommand("Salir");
		
		
		guardar.addActionListener(gameMenuInput);
		cargar.addActionListener(gameMenuInput);
		volverMenu.addActionListener(gameMenuInput);
		salir.addActionListener(gameMenuInput);
		
		menu.add(guardar);
		menu.add(cargar);
		menu.addSeparator();
		menu.add(volverMenu);
		menu.add(salir);
		
		menuBar.add(menu);
		window.setJMenuBar(menuBar);
		window.revalidate();
		
	}

	public void update() {
		gameMenuInput.update();
		
		if(GameMenuInput.GUARDAR) {
			System.out.println("Guardar...");
		}
		if(GameMenuInput.CARGAR) {
			System.out.println("Cargar...");
		}
		if(GameMenuInput.VOLVER_MENU) {
			window.goToMenu();
		}
		if(GameMenuInput.SALIR) {
			System.exit(0);
		}
		
		currentLevel.update();
	}
	
	public Window getWindow() {
		return window;
	}

	public void draw(Graphics g) {
		currentLevel.draw(g);
	}
}
