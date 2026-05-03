package states;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import controlador.Window;
import gameObjects.Coin;
import gameObjects.Enemy;
import gameObjects.Player;
import graphics.Assets;
import math.Vector2D;
import tile.TileManager;

public class Level {

	private GameState gp;
	private Player player;
	private TileManager tileManager;
	private List<Enemy> enemies;
	private List<Coin> coins;
	private int deaths = 0;
	private boolean levelComplete = false;

	private static final Vector2D START = new Vector2D(150, 250);

	public Level(GameState gp, String mapPath) {
		this.gp = gp;
		tileManager = new TileManager(gp, mapPath);
		player = new Player(new Vector2D(START.getX(), START.getY()), Assets.player, tileManager);
		initEnemies();
		initCoins();
	}

	private void initEnemies() {
		enemies = new ArrayList<>();
		enemies.add(new Enemy(new Vector2D(240, 240), Assets.enemy, tileManager,  1));
		enemies.add(new Enemy(new Vector2D(552, 264), Assets.enemy, tileManager, -1));
		enemies.add(new Enemy(new Vector2D(240, 288), Assets.enemy, tileManager,  1));
		enemies.add(new Enemy(new Vector2D(552, 312), Assets.enemy, tileManager, -1));
		enemies.add(new Enemy(new Vector2D(240, 336), Assets.enemy, tileManager,  1));
	}

	private void initCoins() {
		coins = new ArrayList<>();
		for (Vector2D pos : tileManager.getCoinPositions())
			coins.add(new Coin(pos, Assets.coin));
	}

	private void checkCoinCollisions() {
		Rectangle playerRect = new Rectangle(
			(int) player.getPosition().getX(),
			(int) player.getPosition().getY(),
			Assets.player.getWidth(), Assets.player.getHeight()
		);
		for (Coin c : coins) {
			if (!c.isCollected()) {
				Rectangle coinRect = new Rectangle(
					(int) c.getPosition().getX(),
					(int) c.getPosition().getY(),
					Assets.coin.getWidth(), Assets.coin.getHeight()
				);
				if (playerRect.intersects(coinRect))
					c.collect();
			}
		}
	}

	private boolean allCoinsCollected() {
		for (Coin c : coins)
			if (!c.isCollected()) return false;
		return true;
	}

	private void checkCollisions() {
		int pw = Assets.player.getWidth();
		int ph = Assets.player.getHeight();
		Rectangle playerRect = new Rectangle(
			(int) player.getPosition().getX(),
			(int) player.getPosition().getY(),
			pw, ph
		);

		for (Enemy e : enemies) {
			Rectangle enemyRect = new Rectangle(
				(int) e.getPosition().getX(),
				(int) e.getPosition().getY(),
				13, 13
			);
			if (playerRect.intersects(enemyRect)) {
				deaths++;
				player.getPosition().setX(START.getX());
				player.getPosition().setY(START.getY());
				return;
			}
		}
	}

	private void checkGoal() {
		int cx = (int) player.getPosition().getX() + Assets.player.getWidth() / 2;
		int cy = (int) player.getPosition().getY() + Assets.player.getHeight() / 2;

		if (allCoinsCollected() && tileManager.isGoal(cx, cy)) {
			levelComplete = true;
			SwingUtilities.invokeLater(() -> {
				JOptionPane.showMessageDialog(null, "¡Nivel completado!", "Nivel completado", JOptionPane.INFORMATION_MESSAGE);
				gp.getWindow().goToMenu();
			});
		}
	}

	public void update() {
		if (levelComplete) return;
		player.update();
		for (Enemy e : enemies)
			e.update();
		checkCollisions();
		checkCoinCollisions();
		checkGoal();
	}

	public void draw(Graphics g) {
		tileManager.draw(g);
		for (Enemy e : enemies)
			e.draw(g);
		for (Coin c : coins)
			c.draw(g);
		player.draw(g);

		g.setFont(new Font("Arial", Font.BOLD, 16));
		g.setColor(Color.BLACK);
		String text = "DEATHS: " + deaths;
		FontMetrics fm = g.getFontMetrics();
		g.drawString(text, Window.WIDTH - fm.stringWidth(text) - 10, 20);
	}
}
