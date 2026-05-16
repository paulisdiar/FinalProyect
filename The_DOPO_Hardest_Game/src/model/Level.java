package model;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import Controller.Window;
import View.Assets;
import View.Vector2D;

public class Level {

	private GameState gp;
	private Player player1;
	private Player player2;
	private boolean twoPlayer;
	private TileManager tileManager;
	private List<Enemy> enemies;
	private List<Coin> coins;

	private int deaths1 = 0;
	private int deaths2 = 0;
	private int score1  = 0;
	private int score2  = 0;
	private int timerTicks = 180 * 60;
	private boolean levelComplete = false;
	private int totalCoins;
	private Vector2D startP1;
	private Vector2D startP2;

	private static final int COIN_VALUE = 10;

	public Level(GameState gp, String mapPath, boolean twoPlayer) {
		this.gp = gp;
		this.twoPlayer = twoPlayer;
		tileManager = new TileManager(gp, mapPath);
		startP1 = tileManager.getSpawnPlayer1();
		player1  = new Player1(new Vector2D(startP1.getX(), startP1.getY()), Assets.player, tileManager);
		if (twoPlayer) {
			startP2 = tileManager.getSpawnPlayer2();
			player2 = new Player2(new Vector2D(startP2.getX(), startP2.getY()), Assets.player, tileManager);
		}
		initEnemies();
		initCoins();
		totalCoins = coins.size();
	}

	private void initEnemies() {
		enemies = new ArrayList<>();
		enemies.add(new BasicEnemy(new Vector2D(240, 240), Assets.enemy, tileManager,  1));
		enemies.add(new BasicEnemy(new Vector2D(552, 264), Assets.enemy, tileManager, -1));
		enemies.add(new BasicEnemy(new Vector2D(240, 288), Assets.enemy, tileManager,  1));
		enemies.add(new BasicEnemy(new Vector2D(552, 312), Assets.enemy, tileManager, -1));
		enemies.add(new BasicEnemy(new Vector2D(240, 336), Assets.enemy, tileManager,  1));
	}

	private void initCoins() {
		coins = new ArrayList<>();
		for (Vector2D pos : tileManager.getCoinPositions())
			coins.add(new Coin(pos, Assets.coin));
	}

	private Rectangle getRect(Player p) {
		return new Rectangle(
			(int) p.getPosition().getX(),
			(int) p.getPosition().getY(),
			Assets.player.getWidth(), Assets.player.getHeight()
		);
	}

	private void checkCoinCollisions() {
		for (Coin c : coins) {
			if (c.isCollected()) continue;
			Rectangle coinRect = new Rectangle(
				(int) c.getPosition().getX(),
				(int) c.getPosition().getY(),
				Assets.coin.getWidth(), Assets.coin.getHeight()
			);
			if (getRect(player1).intersects(coinRect)) {
				c.collect();
				score1 += COIN_VALUE;
			} else if (twoPlayer && getRect(player2).intersects(coinRect)) {
				c.collect();
				score2 += COIN_VALUE;
			}
		}
	}

	private boolean allCoinsCollected() {
		for (Coin c : coins)
			if (!c.isCollected()) return false;
		return true;
	}

	private int collectedCoins() {
		int count = 0;
		for (Coin c : coins) if (c.isCollected()) count++;
		return count;
	}

	private void checkEnemyCollisions() {
		for (Enemy e : enemies) {
			Rectangle enemyRect = new Rectangle(
				(int) e.getPosition().getX(),
				(int) e.getPosition().getY(),
				13, 13
			);
			if (getRect(player1).intersects(enemyRect)) {
				deaths1++;
				player1.getPosition().setX(startP1.getX());
				player1.getPosition().setY(startP1.getY());
			}
			if (twoPlayer && getRect(player2).intersects(enemyRect)) {
				deaths2++;
				player2.getPosition().setX(startP2.getX());
				player2.getPosition().setY(startP2.getY());
			}
		}
	}

	private void checkGoal() {
		if (!allCoinsCollected()) return;

		int p1cx = (int) player1.getPosition().getX() + Assets.player.getWidth()  / 2;
		int p1cy = (int) player1.getPosition().getY() + Assets.player.getHeight() / 2;
		boolean p1Goal = tileManager.isGoal(p1cx, p1cy);

		boolean p2Goal = false;
		if (twoPlayer) {
			int p2cx = (int) player2.getPosition().getX() + Assets.player.getWidth()  / 2;
			int p2cy = (int) player2.getPosition().getY() + Assets.player.getHeight() / 2;
			p2Goal = tileManager.isCheckpoint(p2cx, p2cy);
		}

		if (!p1Goal && !p2Goal) return;

		levelComplete = true;
		int fs1 = score1, fs2 = score2, d1 = deaths1, d2 = deaths2;
		boolean winner1 = p1Goal;

		SwingUtilities.invokeLater(() -> {
			String msg;
			if (twoPlayer) {
				String ganador = winner1 ? "¡Jugador 1 ganó!" : "¡Jugador 2 ganó!";
				msg = ganador + "\n\nJugador 1 — Puntaje: " + fs1 + "  Muertes: " + d1
					+ "\nJugador 2 — Puntaje: " + fs2 + "  Muertes: " + d2;
			} else {
				msg = "¡Nivel completado!\nPuntaje: " + fs1 + "\nMuertes: " + d1;
			}
			JOptionPane.showMessageDialog(null, msg, "Fin del nivel", JOptionPane.INFORMATION_MESSAGE);
			gp.getWindow().goToMenu();
		});
	}

	public void update() {
		if (levelComplete) return;
		player1.update();
		if (twoPlayer) player2.update();
		for (Enemy e : enemies) e.update();
		checkEnemyCollisions();
		checkCoinCollisions();
		checkGoal();
		updateTimer();
	}

	private void updateTimer() {
		timerTicks--;
		if (timerTicks <= 0) {
			deaths1++;
			player1.getPosition().setX(startP1.getX());
			player1.getPosition().setY(startP1.getY());
			if (twoPlayer) {
				deaths2++;
				player2.getPosition().setX(startP2.getX());
				player2.getPosition().setY(startP2.getY());
			}
			timerTicks = 180 * 60;
		}
	}

	public void draw(Graphics g) {
		tileManager.draw(g);
		for (Enemy e : enemies) e.draw(g);
		for (Coin c : coins)   c.draw(g);
		player1.draw(g);
		if (twoPlayer) player2.draw(g);

		g.setFont(new Font("Arial", Font.BOLD, 16));
		g.setColor(Color.BLACK);

		int seconds = timerTicks / 60;
		String timeText   = String.format("Tiempo: %02d:%02d", seconds / 60, seconds % 60);
		String coinsText  = "Monedas: " + collectedCoins() + "/" + totalCoins;

		FontMetrics fm = g.getFontMetrics();
		g.drawString(timeText, 10, 20);
		g.drawString(coinsText, Window.WIDTH - fm.stringWidth(coinsText) - 10, 20);

		if (twoPlayer) {
			g.drawString("J1 — Muertes: " + deaths1, 10, 40);
			g.drawString("J2 — Muertes: " + deaths2, Window.WIDTH - fm.stringWidth("J2 — Muertes: " + deaths2) - 10, 40);
		} else {
			String deathsText = "Muertes: " + deaths1;
			g.drawString(deathsText, (Window.WIDTH - fm.stringWidth(deathsText)) / 2, 20);
		}
	}
}
