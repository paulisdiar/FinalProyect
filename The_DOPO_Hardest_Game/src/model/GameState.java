package model;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import Controller.Window;
import View.Assets;
import View.GameMenuInput;

public class GameState {

	private static final int TOTAL_LEVELS = 3;

	private GameMenuInput gameMenuInput;
	private Window window;
	private Level currentLevel;
	private int currentLevelIndex = 1;

	private GameMode mode;
	private BufferedImage texture1;
	private BufferedImage texture2;
	private PlayerType type1;
	private PlayerType type2;
	private String name1 = "Jugador 1";
	private String name2 = "Jugador 2";

	private int accumulatedScore1 = 0;
	private int accumulatedScore2 = 0;

	public GameState(Window window, GameMode mode, BufferedImage texture1, BufferedImage texture2, PlayerType type1, PlayerType type2, String name1, String name2) {
		this.window = window;
		this.mode = mode;
		this.texture1 = texture1;
		this.texture2 = texture2;
		this.type1 = type1;
		this.type2 = type2;
		this.name1 = name1;
		this.name2 = name2;
		gameMenuInput = new GameMenuInput();
		initMenu();
		loadLevel(1);
	}
	
	public GameState() {
        this.window = null; 
        this.mode = model.GameMode.SOLO;
	}

	public boolean isLastLevel() {
		return currentLevelIndex >= TOTAL_LEVELS;
	}

	public void nextLevel(int score1, int score2) {
		accumulatedScore1 += score1;
		accumulatedScore2 += score2;
		if (!isLastLevel()) {
			loadLevel(currentLevelIndex + 1);
		}
	}

	public int getAccumulatedScore1() {
		return accumulatedScore1;
	}

	public int getAccumulatedScore2() {
		return accumulatedScore2;
	}

	public String getName1() {
		return name1;
	}

	public String getName2() {
		return name2;
	}

	private void loadLevel(int index) {
		currentLevelIndex = index;
		String suffix = (mode == GameMode.SOLO) ? ".txt" : "_2p.txt";
		String map = "res/maps/level" + index + suffix;
		currentLevel = new Level(this, map, mode, texture1, texture2, type1, type2, name1, name2);
	}

	public void optionSavePublic() {
		optionSave();
	}

	private void saveAs(File archivo) throws GameException {
		SaveData data      = currentLevel.getSaveData(currentLevelIndex);
		data.mode          = mode;
		data.type1         = type1;
		data.type2         = type2;
		data.textureIndex1 = typeToIndex(type1);
		data.textureIndex2 = typeToIndex(type2);
		data.name1         = name1;
		data.name2         = name2;
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
			oos.writeObject(data);
		} catch (IOException e) {
			throw new GameException("Error al guardar el archivo");
		}
	}

	private void open(File archivo) throws GameException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
			SaveData data = (SaveData) ois.readObject();
			applyLoad(data);
		} catch (IOException | ClassNotFoundException e) {
			throw new GameException("Error al abrir el archivo");
		}
	}

	public void applyLoad(SaveData data) {
		mode     = data.mode;
		type1    = data.type1;
		type2    = data.type2;
		texture1 = Assets.playerColors[data.textureIndex1];
		texture2 = Assets.playerColors[data.textureIndex2];
		loadLevel(data.levelIndex);
		currentLevel.applyLoad(data);
	}

	private void optionSave() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("Archivos de partida (*.dat)", "dat"));
		int result = chooser.showSaveDialog(window);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			String path = file.getAbsolutePath();
			if (!path.toLowerCase().endsWith(".dat")) {
				file = new File(path + ".dat");
			}
			try {
				saveAs(file);
				JOptionPane.showMessageDialog(window, "Partida guardada en: " + file.getName(), "Guardar", JOptionPane.INFORMATION_MESSAGE);
			} catch (GameException e) {
				JOptionPane.showMessageDialog(window, e.getMessage(), "Error al guardar", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void optionLoad() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("Archivos de partida (*.dat)", "dat"));
		int result = chooser.showOpenDialog(window);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (!file.getName().toLowerCase().endsWith(".dat")) {
				JOptionPane.showMessageDialog(window, "El archivo debe tener extensión .dat", "Archivo inválido", JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				open(file);
				JOptionPane.showMessageDialog(window, "Partida cargada.", "Cargar", JOptionPane.INFORMATION_MESSAGE);
			} catch (GameException e) {
				JOptionPane.showMessageDialog(window, e.getMessage(), "Error al cargar", JOptionPane.WARNING_MESSAGE);
			}
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

		JMenuItem pausar = new JMenuItem("Pausar");
		pausar.setActionCommand("Pausar");
		pausar.addActionListener(gameMenuInput);
		menu.addSeparator();
		menu.add(pausar);

		menuBar.add(menu);
		window.setJMenuBar(menuBar);
		window.revalidate();
	}

	public void update() {
		gameMenuInput.update();

		if (GameMenuInput.GUARDAR) {
			optionSave();
		}
		if (GameMenuInput.CARGAR) {
			optionLoad();
		}
		if (GameMenuInput.VOLVER_MENU) {
			window.goToMenu();
		}
		if (GameMenuInput.SALIR) {
			System.exit(0);
		}
		if (GameMenuInput.PAUSAR) {
			currentLevel.togglePause();
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
