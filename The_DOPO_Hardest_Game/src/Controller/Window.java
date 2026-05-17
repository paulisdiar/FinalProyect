package Controller;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import java.awt.image.BufferedImage;

import View.Assets;
import View.KeyBoard;
import model.ColorConfigState;
import model.GameMode;
import model.GameState;
import model.MenuState;
import model.PreGameState;

public class Window extends JFrame implements Runnable{

	public static final int WIDTH = 800, HEIGHT = 600;
	private Canvas canvas;
	private Thread thread;
	private boolean running = false;

	private BufferStrategy bs;
	private Graphics o;

	private final int FPS = 60;
	private double TARGETTIME = 1000000000/FPS;
	private double delta = 0;
	private int AVERAGEFPS = FPS;

	private GameState gameState;
	private MenuState menuState;
	private KeyBoard keyBoard;
	public static int CANVAS_WIDTH, CANVAS_HEIGHT;

	private boolean inMenu = true;

	public Window(){


		setTitle("The DOPO Hardest Game");
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);

		Assets.init();
		menuState = new MenuState(this);
		start();

	}

	public void showColorConfig(GameMode mode) {
		new ColorConfigState(this, mode);
	}

	public void startGame(GameMode mode, BufferedImage texture1, BufferedImage texture2) {
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

		gameState = new GameState(this, mode, texture1, texture2);
		inMenu = false;
	}

	public static void main(String[] args) {
		new Window();
	}

	private void update() {
		if(inMenu) {
			menuState.update();
		} else {
			keyBoard.update();
			gameState.update();
		}
	}

	private void draw(){

		if(inMenu) {
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

	private void start() {
		running = true;
		thread = new Thread(this);
		thread.start();

	}

	private void stop() {
		try {
			thread.join();
			running = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void showInstructions() {
		new PreGameState(this);
	}

	public void goToMenu() {
		setJMenuBar(null);
		inMenu = true;
		menuState = new MenuState(this);
	}
}
