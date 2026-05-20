package model;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import Controller.Window;
import View.Assets;
import View.GameMenuInput;

public class GameState {

	private static final String SAVE_FILE = "save.dat";

	private GameMenuInput gameMenuInput;
	private Window window;
	private Level currentLevel;
	private int currentLevelIndex = 1;

	private GameMode mode;
	private BufferedImage texture1;
	private BufferedImage texture2;
	private PlayerType type1;
	private PlayerType type2;

	public GameState(Window window, GameMode mode, BufferedImage texture1, BufferedImage texture2, PlayerType type1, PlayerType type2) {
		this.window = window;
		this.mode = mode;
		this.texture1 = texture1;
		this.texture2 = texture2;
		this.type1 = type1;
		this.type2 = type2;
		gameMenuInput = new GameMenuInput();
		initMenu();
		loadLevel(1);
	}

	private void loadLevel(int index) {
		currentLevelIndex = index;
		if (index == 1) {
			String map = (mode == GameMode.SOLO) ? "res/maps/level1.txt" : "res/maps/level1_2p.txt";
			currentLevel = new Level(this, map, mode, texture1, texture2, type1, type2);
		}
	}

	private void save() {
		SaveData data      = currentLevel.getSaveData(currentLevelIndex);
		data.mode          = mode;
		data.type1         = type1;
		data.type2         = type2;
		data.textureIndex1 = typeToIndex(type1);
		data.textureIndex2 = typeToIndex(type2);
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
			oos.writeObject(data);
			JOptionPane.showMessageDialog(null, "Partida guardada.", "Guardar", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void load() {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
			SaveData data = (SaveData) ois.readObject();
			mode     = data.mode;
			type1    = data.type1;
			type2    = data.type2;
			texture1 = Assets.playerColors[data.textureIndex1];
			texture2 = Assets.playerColors[data.textureIndex2];
			loadLevel(data.levelIndex);
			currentLevel.applyLoad(data);
			JOptionPane.showMessageDialog(null, "Partida cargada.", "Cargar", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException | ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "No se encontró partida guardada.", "Cargar", JOptionPane.WARNING_MESSAGE);
		}
	}

	private int typeToIndex(PlayerType type) {
		return switch (type) {
			case ROJO  -> 0;
			case AZUL  -> 1;
			case VERDE -> 2;
		};
	}

	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Opciones");

		JMenuItem guardar    = new JMenuItem("Guardar");
		JMenuItem cargar     = new JMenuItem("Cargar");
		JMenuItem volverMenu = new JMenuItem("Volver al Menu");
		JMenuItem salir      = new JMenuItem("Salir");

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

		if (GameMenuInput.GUARDAR) {
			save();
		}
		if (GameMenuInput.CARGAR) {
			load();
		}
		if (GameMenuInput.VOLVER_MENU) {
			window.goToMenu();
		}
		if (GameMenuInput.SALIR) {
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
