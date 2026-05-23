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

/**
 * Estado central de la partida en curso. Gestiona la carga de niveles,
 * la acumulación de puntaje entre niveles, el menú de juego y las
 * operaciones de guardar/cargar.
 */
public class GameState {

	private final int TOTAL_LEVELS;

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

	/**
	 * Crea el estado de juego, inicializa el menú y carga el primer nivel.
	 *
	 * @param window   ventana principal
	 * @param mode     modo de juego
	 * @param texture1 sprite del Jugador 1
	 * @param texture2 sprite del Jugador 2 (o máquina)
	 * @param type1    tipo del Jugador 1
	 * @param type2    tipo del Jugador 2
	 * @param name1    nombre del Jugador 1
	 * @param name2    nombre del Jugador 2
	 */
	public GameState(Window window, GameMode mode, BufferedImage texture1, BufferedImage texture2, PlayerType type1, PlayerType type2, String name1, String name2) {
		this(window, mode, texture1, texture2, type1, type2, name1, name2, 1);
	}

	/**
	 * Crea el estado de juego e inicia desde el nivel indicado.
	 *
	 * @param window     ventana principal
	 * @param mode       modo de juego
	 * @param texture1   sprite del Jugador 1
	 * @param texture2   sprite del Jugador 2 (o máquina)
	 * @param type1      tipo del Jugador 1
	 * @param type2      tipo del Jugador 2
	 * @param name1      nombre del Jugador 1
	 * @param name2      nombre del Jugador 2
	 * @param startLevel nivel inicial (1-3)
	 */
	public GameState(Window window, GameMode mode, BufferedImage texture1, BufferedImage texture2, PlayerType type1, PlayerType type2, String name1, String name2, int startLevel) {
		this.window = window;
		this.mode = mode;
		this.texture1 = texture1;
		this.texture2 = texture2;
		this.type1 = type1;
		this.type2 = type2;
		this.name1 = name1;
		this.name2 = name2;
		this.TOTAL_LEVELS = LevelRegistry.countLevels(mode != GameMode.SOLO);
		gameMenuInput = new GameMenuInput();
		initMenu();
		loadLevel(startLevel);
	}
	
	public GameState() {
		this.window = null;
		this.mode = GameMode.SOLO;
		this.TOTAL_LEVELS = 1;
	}

	/**
	 * @return {@code true} si el nivel actual es el último de la partida
	 */
	public boolean isLastLevel() {
		return currentLevelIndex >= TOTAL_LEVELS;
	}

	/**
	 * Indica si el nivel actual es el último sin acumular los puntajes todavía.
	 * Usado por {@link Level} para decidir el tipo de diálogo antes de llamar a nextLevel.
	 *
	 * @param score1 puntaje neto del Jugador 1 en el nivel (aún no acumulado)
	 * @param score2 puntaje neto del Jugador 2 en el nivel (aún no acumulado)
	 * @return {@code true} si es el último nivel
	 */
	public boolean isLastLevel(int score1, int score2) {
		return currentLevelIndex >= TOTAL_LEVELS;
	}

	/**
	 * Acumula los puntajes del nivel completado y carga el siguiente
	 * si no es el último.
	 *
	 * @param score1 puntaje neto del Jugador 1 en el nivel
	 * @param score2 puntaje neto del Jugador 2 en el nivel
	 */
	public void nextLevel(int score1, int score2) {
		accumulatedScore1 += score1;
		accumulatedScore2 += score2;
		if (!isLastLevel()) {
			loadLevel(currentLevelIndex + 1);
		}
	}

	/**
	 * @return puntaje acumulado del Jugador 1 en todos los niveles completados
	 */
	public int getAccumulatedScore1() {
		return accumulatedScore1;
	}

	/**
	 * @return puntaje acumulado del Jugador 2 en todos los niveles completados
	 */
	public int getAccumulatedScore2() {
		return accumulatedScore2;
	}

	/**
	 * @return nombre del Jugador 1
	 */
	public String getName1() {
		return name1;
	}

	/**
	 * @return nombre del Jugador 2 o de la máquina
	 */
	public String getName2() {
		return name2;
	}

	/**
	 * Carga el nivel indicado según el modo de juego activo.
	 *
	 * @param index número de nivel a cargar (1-based)
	 */
	private void loadLevel(int index) {
		currentLevelIndex = index;
		if (window != null) {
			window.resetKeys();
		}
		String suffix = (mode == GameMode.SOLO) ? ".txt" : "_2p.txt";
		String map = "res/maps/level" + index + suffix;
		currentLevel = new Level(this, map, mode, texture1, texture2, type1, type2, name1, name2);
	}

	/**
	 * Punto de acceso público al diálogo de guardar, usado por {@link Level}
	 * en la opción "Guardar y continuar" del fin de nivel.
	 */
	public void optionSavePublic() {
		optionSave();
	}

	/**
	 * Serializa el estado actual de la partida en el archivo indicado.
	 *
	 * @param archivo destino del archivo .dat
	 * @throws GameException si ocurre un error de E/S al escribir
	 */
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
			GameLogger.logInfo("GUARDAR", "Partida guardada en: " + archivo.getAbsolutePath() + " | Nivel: " + currentLevelIndex + " | Jugador: " + name1);
		} catch (IOException e) {
			GameLogger.logError("GUARDAR", "No se pudo guardar en: " + archivo.getAbsolutePath() + " — " + e.getMessage());
			throw new GameException("Error al guardar el archivo");
		}
	}

	/**
	 * Deserializa una partida guardada y aplica su estado al juego actual.
	 *
	 * @param archivo archivo .dat a cargar
	 * @throws GameException si el archivo no puede leerse o no es válido
	 */
	private void open(File archivo) throws GameException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
			SaveData data = (SaveData) ois.readObject();
			applyLoad(data);
			GameLogger.logInfo("CARGAR", "Partida cargada desde: " + archivo.getAbsolutePath() + " | Nivel: " + data.levelIndex);
		} catch (IOException | ClassNotFoundException e) {
			GameLogger.logError("CARGAR", "No se pudo cargar: " + archivo.getAbsolutePath() + " — " + e.getMessage());
			throw new GameException("Error al abrir el archivo");
		}
	}

	/**
	 * Aplica los datos de una partida guardada al estado actual del juego.
	 * Usado tanto por {@link #open} como por {@link Controller.Window#startGameFromSave}.
	 *
	 * @param data datos de la partida a restaurar
	 */
	public void applyLoad(SaveData data) {
		mode     = data.mode;
		type1    = data.type1;
		type2    = data.type2;
		texture1 = Assets.playerColors[data.textureIndex1];
		texture2 = Assets.playerColors[data.textureIndex2];
		loadLevel(data.levelIndex);
		currentLevel.applyLoad(data);
	}

	/**
	 * Muestra un {@link JFileChooser} para elegir dónde guardar la partida
	 * y llama a {@link #saveAs} con el archivo seleccionado.
	 */
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

	/**
	 * Muestra un {@link JFileChooser} para elegir un archivo .dat y carga
	 * la partida seleccionada llamando a {@link #open}.
	 */
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

	/**
	 * Convierte un {@link PlayerType} en el índice del array de texturas.
	 *
	 * @param type tipo de jugador
	 * @return índice correspondiente (0=Rojo, 1=Azul, 2=Verde)
	 */
	private int typeToIndex(PlayerType type) {
		return switch (type) {
			case ROJO  -> 0;
			case AZUL  -> 1;
			case VERDE -> 2;
		};
	}

	/**
	 * Construye y registra la barra de menú JMenu con las opciones de juego
	 * (Guardar, Cargar, Volver al Menu, Salir, Pausar).
	 */
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

	/**
	 * Procesa la entrada del menú de juego y delega la actualización al nivel activo.
	 * Debe llamarse una vez por frame.
	 */
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

	/**
	 * @return ventana principal de la aplicación
	 */
	public Window getWindow() {
		return window;
	}

	/**
	 * Delega el dibujado al nivel activo.
	 *
	 * @param g contexto gráfico del canvas
	 */
	public void draw(Graphics g) {
		currentLevel.draw(g);
	}
}
