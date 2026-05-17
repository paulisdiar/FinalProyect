package model;

import java.awt.Graphics;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import java.awt.image.BufferedImage;

import Controller.Window;
import View.GameMenuInput;

public class GameState {

	private GameMenuInput gameMenuInput;
	private Window window;
	private Level currentLevel;
	final int originalTileSize = 18;
	public final int tileSize = originalTileSize * 3;

	private GameMode mode;
	private BufferedImage texture1;
	private BufferedImage texture2;

	public GameState(Window window, GameMode mode, BufferedImage texture1, BufferedImage texture2) {
		this.window = window;
		this.mode = mode;
		this.texture1 = texture1;
		this.texture2 = texture2;
		gameMenuInput = new GameMenuInput();
		initMenu();
		loadLevel(1);
	}

	private void loadLevel(int index) {
		if(index == 1) {
			String map = (mode == GameMode.SOLO) ? "res/maps/level1.txt" : "res/maps/level1_2p.txt";
			currentLevel = new Level(this, map, mode, texture1, texture2);
		}
	}

	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Opciones");

		JMenuItem guardar   = new JMenuItem("Guardar");
		JMenuItem cargar    = new JMenuItem("Cargar");
		JMenuItem volverMenu= new JMenuItem("Volver al Menu");
		JMenuItem salir     = new JMenuItem("Salir");

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
