package Controller;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import model.ColorConfigState;
import model.GameMode;
import model.GameState;
import model.MenuOptionsState;
import model.MenuState;
import model.PlayerType;
import model.PreGameState;
import model.SaveData;
import View.Assets;
import View.KeyBoard;

/**
 * Ventana principal del juego. Implementa el bucle de juego a 60 FPS
 * en un hilo dedicado y gestiona la transición entre las pantallas
 * (menú, opciones, juego).
 */
public class Window extends JFrame implements Runnable {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	
	private Canvas canvas;
	private Thread thread;
	private boolean running = false;

	private BufferStrategy bs;
	private Graphics o;

	private static final int FPS = 60;
	private static final double TARGETTIME = 1000000000/FPS;
	private double delta = 0;
	private static int AVERAGEFPS = FPS;

	private GameState        gameState;
	private MenuState        menuState;
	private MenuOptionsState optionsState;
	private KeyBoard         keyBoard;
	public static int CANVAS_HEIGHT;
	public static int CANVAS_WIDTH;

	private boolean inMenu    = true;
	private boolean inOptions = false;

	/**
	 * Inicializa la ventana, carga los assets, muestra el menú principal
	 * y arranca el hilo del bucle de juego.
	 */
	public Window() {

        initializeWindow();

        Assets.init();
        
        this.menuState = new MenuState(this);
        
        start();
    }

    /**
     * Configura los parámetros visuales del JFrame. 
     * Al ser un método 'private', PMD no saltará por llamadas a métodos heredados.
     */
    private void initializeWindow() {
        setTitle("The DOPO Hardest Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

	/**
	 * Muestra la pantalla de selección de tipo/nombre para el modo indicado.
	 *
	 * @param mode modo de juego seleccionado
	 */
	public void showColorConfig(GameMode mode) {
		new ColorConfigState(this, mode);
	}

	/**
	 * Configura el canvas, crea el {@link GameState} e inicia la partida.
	 *
	 * @param mode     modo de juego
	 * @param texture1 sprite del Jugador 1
	 * @param texture2 sprite del Jugador 2 o máquina
	 * @param type1    tipo del Jugador 1
	 * @param type2    tipo del Jugador 2
	 * @param name1    nombre del Jugador 1
	 * @param name2    nombre del Jugador 2 o máquina
	 */
	public void startGame(GameMode mode, BufferedImage texture1, BufferedImage texture2, PlayerType type1, PlayerType type2, String name1, String name2) {
		startGame(mode, texture1, texture2, type1, type2, name1, name2, 1);
	}

	/**
	 * Configura el canvas, crea el {@link GameState} e inicia la partida desde el nivel indicado.
	 *
	 * @param mode       modo de juego
	 * @param texture1   sprite del Jugador 1
	 * @param texture2   sprite del Jugador 2 o máquina
	 * @param type1      tipo del Jugador 1
	 * @param type2      tipo del Jugador 2
	 * @param name1      nombre del Jugador 1
	 * @param name2      nombre del Jugador 2 o máquina
	 * @param startLevel nivel inicial (1-3)
	 */
	public void startGame(GameMode mode, BufferedImage texture1, BufferedImage texture2, PlayerType type1, PlayerType type2, String name1, String name2, int startLevel) {
		keyBoard = new KeyBoard();

		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		canvas.setMaximumSize(new Dimension(WIDTH, HEIGHT));
		canvas.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		canvas.setFocusable(true);

		getContentPane().removeAll();
		setLayout(new BorderLayout());
		add(canvas);
		canvas.addKeyListener(keyBoard);
		revalidate();
		pack();
		CANVAS_WIDTH = canvas.getWidth();
		CANVAS_HEIGHT = canvas.getHeight();
		setLocationRelativeTo(null);
		repaint();

		gameState = new GameState(this, mode, texture1, texture2, type1, type2, name1, name2, startLevel);
		inMenu = false;
		inOptions = false;
	}

	/**
	 * Inicia una partida a partir de datos previamente guardados.
	 * Llama a {@link #startGame} con los datos del {@link SaveData} y luego
	 * aplica el estado guardado mediante {@link GameState#applyLoad}.
	 *
	 * @param data datos de la partida guardada
	 */
	public void startGameFromSave(SaveData data) {
		String n1 = (data.name1 != null) ? data.name1 : "Jugador 1";
		String n2 = (data.name2 != null) ? data.name2 : "Jugador 2";
		startGame(data.mode,
			Assets.playerColors[data.textureIndex1],
			Assets.playerColors[data.textureIndex2],
			data.type1,
			data.type2,
			n1,
			n2
		);
		gameState.applyLoad(data);
	}

	/**
	 * Punto de entrada de la aplicación.
	 *
	 * @param args argumentos de línea de comandos (no usados)
	 */
	public static void main(String[] args) {
		new Window();
	}

	/**
	 * Delega la actualización al estado activo (opciones, menú o juego).
	 */
	private void update() {
		if (inOptions) {
			optionsState.update();
		} else if (inMenu) {
			menuState.update();
		} else {
			keyBoard.update();
			gameState.update();
		}
	}

	/**
	 * Dibuja el frame actual del juego usando {@link java.awt.image.BufferStrategy}.
	 * No hace nada si la pantalla activa es el menú o las opciones.
	 */
	private void draw() {

		if(inMenu || inOptions) {
			return;
		}

		bs = canvas.getBufferStrategy();
		if(bs == null) {
			canvas.createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();


		g.setColor(Color.WHITE);

		g.fillRect(0, 0, WIDTH, HEIGHT);

		gameState.draw(g);

		g.drawString(""+AVERAGEFPS, 10, 100);

		g.dispose();
		bs.show();
	}

	/**
	 * Bucle de juego principal a 60 FPS con acumulador de delta.
	 * Ejecuta {@link #update()} y {@link #draw()} cada frame y
	 * calcula los FPS reales cada segundo.
	 */
	@Override
	public void run() {

		long now = 0;
		long lastTime = System.nanoTime();
		int frames = 0;
		long time = 0;

		while(running) {
			now = System.nanoTime();
			delta += (now - lastTime)/TARGETTIME;
			time += (now - lastTime);
			lastTime = now;

			if(delta >= 1) {
				update();
				draw();
				delta --;
				frames ++;
			}

			if(time >= 1000000000) {
				AVERAGEFPS = frames;
				frames = 0;
				time = 0;
			}
		}

		stop();
	}

	/**
	 * Crea y arranca el hilo del bucle de juego.
	 */
	private void start() {
		running = true;
		thread = new Thread(this);
		thread.start();

	}

	/**
	 * Detiene el hilo del bucle de juego esperando a que termine.
	 */
	private void stop() {
		try {
			thread.join();
			running = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Muestra la pantalla de instrucciones pre-juego.
	 */
	public void showInstructions() {
		new PreGameState(this);
	}

	/**
	 * Muestra la pantalla de opciones del menú principal y activa el flag
	 * {@code inOptions} para que el bucle de juego la procese correctamente.
	 */
	public void showOptions() {
		inMenu    = false;
		inOptions = true;
		optionsState = new MenuOptionsState(this);
	}

	/**
	 * Limpia el estado del teclado. Llamar antes de cargar un nivel nuevo
	 * para que las teclas retenidas durante un diálogo no queden activas.
	 */
	public void resetKeys() {
		if (keyBoard != null) {
			keyBoard.resetKeys();
		}
	}

	/**
	 * Elimina la barra de menú del juego, vuelve al menú principal
	 * y recrea el {@link MenuState}.
	 */
	public void goToMenu() {
		setJMenuBar(null);
		inMenu    = true;
		inOptions = false;
		menuState = new MenuState(this);
	}
}
