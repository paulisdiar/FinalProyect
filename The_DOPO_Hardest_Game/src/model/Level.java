package model;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import Controller.Window;
import View.Assets;
import View.Vector2D;

public class Level {

	private GameState gp;
	private Player player1;
	private Player player2;
	private GameMode mode;
	private TileManager tileManager;
	private List<Enemy> enemies;
	private List<Coin> coins;
	private List<SkinCoin> skinCoins;

	private PlayerType     originalType1;
	private PlayerType     originalType2;
	private BufferedImage  originalTexture1;
	private BufferedImage  originalTexture2;

	private int deaths1 = 0;
	private int deaths2 = 0;
	private int score1  = 0;
	private int score2  = 0;
	private int timerTicks = 180 * 60;
	private boolean levelComplete = false;
	private int totalCoins;

	private Vector2D startP1;
	private Vector2D startP2;
	private Vector2D respawnP1;
	private Vector2D respawnP2;

	private int invincible1 = 0;
	private int invincible2 = 0;

	private static final int COIN_VALUE      = 10;
	private static final int INVINCIBLE_TIME = 90;

	public Level(GameState gp, String mapPath, GameMode mode, BufferedImage texture1, BufferedImage texture2, PlayerType type1, PlayerType type2) {
		this.gp              = gp;
		this.mode            = mode;
		this.originalType1   = type1;
		this.originalType2   = type2;
		this.originalTexture1 = texture1;
		this.originalTexture2 = texture2;
		tileManager = new TileManager(gp, mapPath);
		startP1  = tileManager.getSpawnPlayer1();
		respawnP1 = new Vector2D(startP1.getX(), startP1.getY());
		player1  = createHumanPlayer(type1, new Vector2D(startP1.getX(), startP1.getY()), texture1, new Player1());
		if (mode == GameMode.PVP || mode == GameMode.PVM) {
			startP2   = tileManager.getSpawnPlayer2();
			respawnP2 = new Vector2D(startP2.getX(), startP2.getY());
			if (mode == GameMode.PVP) {
				player2 = createHumanPlayer(type2, new Vector2D(startP2.getX(), startP2.getY()), texture2, new Player2());
			} else {
				player2 = new MachinePlayer(new Vector2D(startP2.getX(), startP2.getY()), texture2, tileManager, new RandomMovement());
			}
		}
		initEnemies();
		initCoins();
		totalCoins = coins.size() + tileManager.getSkinCoinPositions().size();
		initSkinCoins();
	}

	private Player createHumanPlayer(PlayerType type, Vector2D pos, BufferedImage texture, ControlScheme controls) {
		return switch (type) {
			case ROJO  -> new RedPlayer(pos, texture, tileManager, controls);
			case AZUL  -> new BluePlayer(pos, texture, tileManager, controls);
			case VERDE -> new GreenPlayer(pos, texture, tileManager, controls);
		};
	}

	private boolean hasSecondPlayer() {
		return mode == GameMode.PVP || mode == GameMode.PVM;
	}

	private void initEnemies() {
		enemies = new ArrayList<>();
		for (int[] s : tileManager.getEnemySpawns()) {
			Vector2D pos = new Vector2D(s[0], s[1]);
			switch (s[2]) {
				case 8  -> enemies.add(new BasicEnemy(pos, Assets.enemy, tileManager, new HorizontalMovement( 1)));
				case 9  -> enemies.add(new BasicEnemy(pos, Assets.enemy, tileManager, new HorizontalMovement(-1)));
				case 10 -> enemies.add(new BasicEnemy(pos, Assets.enemy, tileManager, new VerticalMovement( 1)));
				case 11 -> enemies.add(new BasicEnemy(pos, Assets.enemy, tileManager, new VerticalMovement(-1)));
				case 13 -> enemies.add(new AceleradoEnemy(pos, Assets.enemy, tileManager, new HorizontalMovement( 1)));
				case 14 -> enemies.add(new AceleradoEnemy(pos, Assets.enemy, tileManager, new VerticalMovement( 1)));
				default -> enemies.add(new BasicEnemy(pos, Assets.enemy, tileManager, new HorizontalMovement( 1)));
			}
		}
		Map<String, List<Vector2D>> patrolGroups  = tileManager.getPatrolGroups();
		Map<String, Vector2D>       patrolSpawns  = tileManager.getPatrolSpawns();
		for (Map.Entry<String, Vector2D> entry : patrolSpawns.entrySet()) {
			String         group     = entry.getKey();
			Vector2D       spawn     = entry.getValue();
			List<Vector2D> waypoints = patrolGroups.getOrDefault(group, new ArrayList<>());
			enemies.add(new PatrulleroEnemy(spawn, Assets.enemy, tileManager, waypoints));
		}
	}

	private void initCoins() {
		coins = new ArrayList<>();
		for (Vector2D pos : tileManager.getCoinPositions()) {
			coins.add(new Coin(pos, Assets.coin));
		}
	}

	private void initSkinCoins() {
		skinCoins = new ArrayList<>();
		for (int[] s : tileManager.getSkinCoinPositions()) {
			Vector2D pos = new Vector2D(s[0], s[1]);
			switch (s[2]) {
				case 15 -> skinCoins.add(new SkinCoin(pos, Assets.coinRed,   PlayerType.ROJO));
				case 16 -> skinCoins.add(new SkinCoin(pos, Assets.coinBlue,  PlayerType.AZUL));
				case 17 -> skinCoins.add(new SkinCoin(pos, Assets.coinGreen, PlayerType.VERDE));
			}
		}
	}

	private void checkSkinCoinCollisions() {
		for (SkinCoin sc : skinCoins) {
			if (sc.isCollected()) {
				continue;
			}
			Rectangle scRect = new Rectangle(
				(int) sc.getPosition().getX(),
				(int) sc.getPosition().getY(),
				Assets.coinRed.getWidth(), Assets.coinRed.getHeight()
			);
			if (getRect(player1).intersects(scRect)) {
				sc.collect();
				score1 += COIN_VALUE;
				player1 = applySkinToPlayer(sc.getSkinType(), player1, originalTexture1, new Player1());
			} else if (hasSecondPlayer() && getRect(player2).intersects(scRect)) {
				sc.collect();
				score2 += COIN_VALUE;
				player2 = applySkinToPlayer(sc.getSkinType(), player2, originalTexture2, new Player2());
			}
		}
	}

	private Player applySkinToPlayer(PlayerType skin, Player current, BufferedImage texture, ControlScheme controls) {
		Vector2D pos = new Vector2D(current.getPosition().getX(), current.getPosition().getY());
		if (skin == PlayerType.VERDE) {
			return new GreenPlayer(pos, texture, tileManager, controls);
		}
		Player next = createHumanPlayer(skin, pos, texture, controls);
		next.applySkin(skin);
		return next;
	}

	private Rectangle getRect(Player p) {
		return new Rectangle(
			(int) p.getPosition().getX(),
			(int) p.getPosition().getY(),
			p.getWidth(), p.getHeight()
		);
	}

	private void respawn(Player p, Vector2D respawnPos) {
		p.getPosition().setX(respawnPos.getX());
		p.getPosition().setY(respawnPos.getY());
		p.onRespawn();
	}

	private void respawnPlayer1(Vector2D respawnPos) {
		player1 = createHumanPlayer(originalType1, new Vector2D(respawnPos.getX(), respawnPos.getY()), originalTexture1, new Player1());
	}

	private void respawnPlayer2(Vector2D respawnPos) {
		if (mode == GameMode.PVP) {
			player2 = createHumanPlayer(originalType2, new Vector2D(respawnPos.getX(), respawnPos.getY()), originalTexture2, new Player2());
		} else {
			respawn(player2, respawnPos);
		}
	}

	private void updateCheckpointRespawn() {
		int p1cx = (int) player1.getPosition().getX() + player1.getWidth()  / 2;
		int p1cy = (int) player1.getPosition().getY() + player1.getHeight() / 2;
		if (tileManager.isCheckpoint(p1cx, p1cy)) {
			respawnP1 = new Vector2D(player1.getPosition().getX(), player1.getPosition().getY());
		}
		if (hasSecondPlayer() && player2 instanceof HumanPlayer) {
			int p2cx = (int) player2.getPosition().getX() + player2.getWidth()  / 2;
			int p2cy = (int) player2.getPosition().getY() + player2.getHeight() / 2;
			if (tileManager.isCheckpoint(p2cx, p2cy)) {
				respawnP2 = new Vector2D(player2.getPosition().getX(), player2.getPosition().getY());
			}
		}
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
			} else if (hasSecondPlayer() && getRect(player2).intersects(coinRect)) {
				c.collect();
				score2 += COIN_VALUE;
			}
		}
	}

	private boolean allCoinsCollected() {
		for (Coin c : coins) {
			if (!c.isCollected()) return false;
		}
		for (SkinCoin sc : skinCoins) {
			if (!sc.isCollected()) return false;
		}
		return true;
	}

	private int collectedCoins() {
		int count = 0;
		for (Coin c : coins) {
			if (c.isCollected()) count++;
		}
		for (SkinCoin sc : skinCoins) {
			if (sc.isCollected()) count++;
		}
		return count;
	}

	private void checkPlayerCollision() {
		if (!hasSecondPlayer()) {
			return;
		}
		if (invincible1 > 0 || invincible2 > 0) {
			return;
		}
		if (!getRect(player1).intersects(getRect(player2))) {
			return;
		}

		if (!player1.absorbHit()) {
			deaths1++;
			respawnPlayer1(respawnP1);
		}
		if (!player2.absorbHit()) {
			deaths2++;
			respawnPlayer2(respawnP2);
		}
		invincible1 = INVINCIBLE_TIME;
		invincible2 = INVINCIBLE_TIME;
	}

	private void checkEnemyCollisions() {
		for (Enemy e : enemies) {
			Rectangle enemyRect = new Rectangle(
				(int) e.getPosition().getX(),
				(int) e.getPosition().getY(),
				13, 13
			);
			if (invincible1 == 0 && getRect(player1).intersects(enemyRect)) {
				if (!player1.absorbHit()) {
					deaths1++;
					respawnPlayer1(respawnP1);
				}
				invincible1 = INVINCIBLE_TIME;
			}
			if (hasSecondPlayer() && invincible2 == 0 && getRect(player2).intersects(enemyRect)) {
				if (!player2.absorbHit()) {
					deaths2++;
					respawnPlayer2(respawnP2);
				}
				invincible2 = INVINCIBLE_TIME;
			}
		}
	}

	private void checkGoal() {
		if (!allCoinsCollected()) {
			return;
		}
		int p1cx = (int) player1.getPosition().getX() + player1.getWidth()  / 2;
		int p1cy = (int) player1.getPosition().getY() + player1.getHeight() / 2;
		boolean p1Goal = tileManager.isGoal(p1cx, p1cy);

		boolean p2Goal = false;
		if (hasSecondPlayer()) {
			int p2cx = (int) player2.getPosition().getX() + player2.getWidth()  / 2;
			int p2cy = (int) player2.getPosition().getY() + player2.getHeight() / 2;
			p2Goal = tileManager.isCheckpoint(p2cx, p2cy);
		}
		if (!p1Goal && !p2Goal) {
			return;
		}

		levelComplete = true;
		int timeBonus = timerTicks / 60;
		int total1 = Math.max(0, score1 + timeBonus - deaths1 * 5);
		int total2 = Math.max(0, score2 + timeBonus - deaths2 * 5);
		int coins1 = score1, coins2 = score2, d1 = deaths1, d2 = deaths2;
		boolean winner1 = p1Goal;

		SwingUtilities.invokeLater(() -> {
			String breakdown1 = "  Monedas: +" + coins1 + "  Tiempo: +" + timeBonus + "  Muertes: -" + (d1 * 5) + "  =  " + total1;
			String breakdown2 = "  Monedas: +" + coins2 + "  Tiempo: +" + timeBonus + "  Muertes: -" + (d2 * 5) + "  =  " + total2;

			String msg;
			if (mode == GameMode.PVP) {
				String ganador = winner1 ? "¡Jugador 1 ganó!" : "¡Jugador 2 ganó!";
				msg = ganador + "\n\nJugador 1:" + breakdown1 + "\nJugador 2:" + breakdown2;
			} else if (mode == GameMode.PVM) {
				String ganador = winner1 ? "¡Jugador ganó!" : "¡La Máquina ganó!";
				msg = ganador + "\n\nJugador:" + breakdown1;
			} else {
				msg = "¡Nivel completado!\n\nPuntaje:" + breakdown1;
			}
			JOptionPane.showMessageDialog(null, msg, "Fin del nivel", JOptionPane.INFORMATION_MESSAGE);
			gp.getWindow().goToMenu();
		});
	}

	public void update() {
		if (levelComplete) {
			return;
		}
		if (invincible1 > 0) {
			invincible1--;
		}
		if (invincible2 > 0) {
			invincible2--;
		}
		player1.update();
		if (hasSecondPlayer()) {
			player2.update();
		}
		for (Enemy e : enemies) {
			e.update();
		}
		updateCheckpointRespawn();
		checkPlayerCollision();
		checkEnemyCollisions();
		checkCoinCollisions();
		checkSkinCoinCollisions();
		checkGoal();
		updateTimer();
	}

	private void updateTimer() {
		timerTicks--;
		if (timerTicks <= 0) {
			deaths1++;
			respawnPlayer1(startP1);
			respawnP1 = new Vector2D(startP1.getX(), startP1.getY());
			if (hasSecondPlayer()) {
				deaths2++;
				respawnPlayer2(startP2);
				respawnP2 = new Vector2D(startP2.getX(), startP2.getY());
			}
			timerTicks = 180 * 60;
		}
	}

	public SaveData getSaveData(int levelIndex) {
		SaveData data = new SaveData();
		data.levelIndex  = levelIndex;
		data.playerX     = (float) player1.getPosition().getX();
		data.playerY     = (float) player1.getPosition().getY();
		data.score1      = score1;
		data.deaths1     = deaths1;
		data.timerTicks  = timerTicks;
		if (hasSecondPlayer()) {
			data.player2X = (float) player2.getPosition().getX();
			data.player2Y = (float) player2.getPosition().getY();
			data.score2   = score2;
			data.deaths2  = deaths2;
		}
		data.coinsCollected = new boolean[coins.size()];
		for (int i = 0; i < coins.size(); i++) {
			data.coinsCollected[i] = coins.get(i).isCollected();
		}
		return data;
	}

	public void applyLoad(SaveData data) {
		player1.getPosition().setX(data.playerX);
		player1.getPosition().setY(data.playerY);
		score1     = data.score1;
		deaths1    = data.deaths1;
		timerTicks = data.timerTicks;
		if (hasSecondPlayer()) {
			player2.getPosition().setX(data.player2X);
			player2.getPosition().setY(data.player2Y);
			score2  = data.score2;
			deaths2 = data.deaths2;
		}
		if (data.coinsCollected != null) {
			for (int i = 0; i < Math.min(coins.size(), data.coinsCollected.length); i++) {
				if (data.coinsCollected[i]) {
					coins.get(i).collect();
				}
			}
		}
	}

	public void draw(Graphics g) {
		tileManager.draw(g);
		for (Enemy e : enemies) {
			e.draw(g);
		}
		for (Coin c : coins) {
			c.draw(g);
		}
		for (SkinCoin sc : skinCoins) {
			sc.draw(g);
		}
		player1.draw(g);
		if (hasSecondPlayer()) {
			player2.draw(g);
		}

		g.setFont(new Font("Arial", Font.BOLD, 16));
		g.setColor(Color.BLACK);

		int seconds = timerTicks / 60;
		String timeText  = String.format("Tiempo: %02d:%02d", seconds / 60, seconds % 60);
		String coinsText = "Monedas: " + collectedCoins() + "/" + totalCoins;

		FontMetrics fm = g.getFontMetrics();
		g.drawString(timeText, 10, 20);
		g.drawString(coinsText, Window.WIDTH - fm.stringWidth(coinsText) - 10, 20);

		if (mode == GameMode.PVP) {
			g.drawString("J1 — Muertes: " + deaths1, 10, 40);
			g.drawString("J2 — Muertes: " + deaths2, Window.WIDTH - fm.stringWidth("J2 — Muertes: " + deaths2) - 10, 40);
		} else if (mode == GameMode.PVM) {
			g.drawString("J — Muertes: " + deaths1, 10, 40);
			g.drawString("MÁQ — Muertes: " + deaths2, Window.WIDTH - fm.stringWidth("MÁQ — Muertes: " + deaths2) - 10, 40);
		} else {
			String deathsText = "Muertes: " + deaths1;
			g.drawString(deathsText, (Window.WIDTH - fm.stringWidth(deathsText)) / 2, 20);
		}
	}
}
