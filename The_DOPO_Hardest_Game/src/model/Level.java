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

import controller.Window;
import view.Assets;
import view.KeyBoard;
import view.Vector2D;

/**
 * Representa un nivel de juego en ejecución. Gestiona jugadores, enemigos,
 * monedas, checkpoints, pausa, colisiones y condición de victoria.
 */
public class Level {

	private GameState gp;
	private Player player1;
	private Player player2;
	private GameMode mode;
	private TileManager tileManager;
	private List<Enemy> enemies;
	private List<Coin> coins;
	private List<SkinCoin> skinCoins;
	private List<LifeSource> lifeSources;
	private List<Bomb> bombs;
	private List<GameEntity> allEntities;

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
	private boolean paused = false;

	private static final int COIN_VALUE      = 10;
	private static final int INVINCIBLE_TIME = 90;

	private String name1;
	private String name2;

	/**
	 * Crea el nivel cargando el mapa, instanciando jugadores, enemigos y monedas.
	 *
	 * @param gp       estado global del juego
	 * @param mapPath  ruta al archivo de mapa (.txt)
	 * @param mode     modo de juego (SOLO, PVP, PVM)
	 * @param texture1 sprite del Jugador 1
	 * @param texture2 sprite del Jugador 2 o máquina
	 * @param type1    tipo del Jugador 1
	 * @param type2    tipo del Jugador 2
	 * @param name1    nombre del Jugador 1
	 * @param name2    nombre del Jugador 2 o máquina
	 */
	public Level(GameState gp, String mapPath, GameMode mode, BufferedImage texture1, BufferedImage texture2, PlayerType type1, PlayerType type2, String name1, String name2) {
		this.gp              = gp;
		this.mode            = mode;
		this.originalType1   = type1;
		this.originalType2   = type2;
		this.originalTexture1 = texture1;
		this.originalTexture2 = texture2;
		this.name1           = name1;
		this.name2           = name2;
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
		initLifeSources();
		initBombs();
		rebuildEntityList();
	}

	/**
	 * Instancia el subtipo de jugador humano adecuado según el tipo indicado.
	 *
	 * @param type     tipo de jugador
	 * @param pos      posición inicial
	 * @param texture  sprite del jugador
	 * @param controls esquema de control
	 * @return instancia de {@link HumanPlayer} correspondiente al tipo
	 */
	private Player createHumanPlayer(PlayerType type, Vector2D pos, BufferedImage texture, ControlScheme controls) {
		return switch (type) {
			case ROJO  -> new RedPlayer(pos, texture, tileManager, controls);
			case AZUL  -> new BluePlayer(pos, texture, tileManager, controls);
			case VERDE -> new GreenPlayer(pos, texture, tileManager, controls);
		};
	}

	/**
	 * @return {@code true} si el modo de juego incluye un segundo jugador (PVP o PVM)
	 */
	private boolean hasSecondPlayer() {
		return mode == GameMode.PVP || mode == GameMode.PVM;
	}

	/**
	 * Instancia todos los enemigos del nivel a partir de los datos del mapa.
	 */
	private void initEnemies() {
		enemies = new ArrayList<>();
		for (int[] s : tileManager.getEnemySpawns()) {
			Vector2D pos = new Vector2D(s[0], s[1]);
			switch (s[2]) {
				case 8  -> enemies.add(new BasicEnemy(pos, Assets.enemy, tileManager, new HorizontalMovement( 1)));
				case 9  -> enemies.add(new BasicEnemy(pos, Assets.enemy, tileManager, new HorizontalMovement(-1)));
				case 10 -> enemies.add(new BasicEnemy(pos, Assets.enemy, tileManager, new VerticalMovement( 1)));
				case 11 -> enemies.add(new BasicEnemy(pos, Assets.enemy, tileManager, new VerticalMovement(-1)));
				case 12 -> enemies.add(new BasicEnemy(pos, Assets.enemy, tileManager, new DiagonalMovement( 1,  1)));
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

	/**
	 * Instancia todas las monedas amarillas del nivel a partir de los datos del mapa.
	 */
	private void initCoins() {
		coins = new ArrayList<>();
		for (Vector2D pos : tileManager.getCoinPositions()) {
			coins.add(new Coin(pos, Assets.coin));
		}
	}

	/**
	 * Instancia todas las monedas de skin del nivel a partir de los datos del mapa.
	 */
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

	/**
	 * Detecta si algún jugador toca una moneda de skin no recogida y aplica
	 * el skin correspondiente al jugador que la recoge.
	 */
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

	/**
	 * Crea una nueva instancia del jugador con el skin aplicado,
	 * conservando la posición actual.
	 *
	 * @param skin     tipo de jugador a aplicar
	 * @param current  jugador actual cuyos datos se preservan
	 * @param texture  sprite base del jugador
	 * @param controls esquema de control
	 * @return nuevo jugador con el skin activo
	 */
	private Player applySkinToPlayer(PlayerType skin, Player current, BufferedImage texture, ControlScheme controls) {
		Vector2D pos = new Vector2D(current.getPosition().getX(), current.getPosition().getY());
		if (skin == PlayerType.VERDE) {
			return new GreenPlayer(pos, texture, tileManager, controls);
		}
		Player next = createHumanPlayer(skin, pos, texture, controls);
		next.applySkin(skin);
		return next;
	}

	/**
	 * @param p jugador del que obtener el rectángulo de colisión
	 * @return rectángulo que ocupa el jugador en pantalla
	 */
	private Rectangle getRect(Player p) {
		return new Rectangle(
			(int) p.getPosition().getX(),
			(int) p.getPosition().getY(),
			p.getWidth(), p.getHeight()
		);
	}

	/**
	 * Reposiciona un jugador en el punto de reaparición y llama a {@link Player#onRespawn()}.
	 *
	 * @param p          jugador a reaparecer
	 * @param respawnPos posición donde reaparecer
	 */
	private void respawn(Player p, Vector2D respawnPos) {
		p.getPosition().setX(respawnPos.getX());
		p.getPosition().setY(respawnPos.getY());
		p.onRespawn();
	}

	/**
	 * Recrea el Jugador 1 con su tipo y textura originales en la posición dada.
	 *
	 * @param respawnPos posición donde reaparecer
	 */
	private void respawnPlayer1(Vector2D respawnPos) {
		player1 = createHumanPlayer(originalType1, new Vector2D(respawnPos.getX(), respawnPos.getY()), originalTexture1, new Player1());
	}

	/**
	 * Recrea el Jugador 2 (PVP) o reposiciona la máquina (PVM) en la posición dada.
	 *
	 * @param respawnPos posición donde reaparecer
	 */
	private void respawnPlayer2(Vector2D respawnPos) {
		if (mode == GameMode.PVP) {
			player2 = createHumanPlayer(originalType2, new Vector2D(respawnPos.getX(), respawnPos.getY()), originalTexture2, new Player2());
		} else {
			respawn(player2, respawnPos);
		}
	}

	/**
	 * Actualiza los puntos de reaparición de los jugadores cuando pisan
	 * un tile de checkpoint.
	 */
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

	/**
	 * Detecta si algún jugador toca una moneda amarilla no recogida y la recoge.
	 */
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

	/**
	 * @return {@code true} si todas las monedas (amarillas y de skin) han sido recogidas
	 */
	private boolean allCoinsCollected() {
		for (Coin c : coins) {
			if (!c.isCollected()) return false;
		}
		for (SkinCoin sc : skinCoins) {
			if (!sc.isCollected()) return false;
		}
		return true;
	}

	/**
	 * @return número total de monedas recogidas hasta ahora (amarillas + skin)
	 */
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

	/**
	 * @param p jugador a comprobar
	 * @return {@code true} si el centro del jugador está sobre un tile de checkpoint
	 */
	private boolean onCheckpoint(Player p) {
		int cx = (int) p.getPosition().getX() + p.getWidth()  / 2;
		int cy = (int) p.getPosition().getY() + p.getHeight() / 2;
		return tileManager.isCheckpoint(cx, cy);
	}

	/**
	 * Detecta colisión entre los dos jugadores. Si se tocan y ninguno está
	 * en invencibilidad ni sobre un checkpoint, ambos mueren (salvo que absorban el golpe).
	 */
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
		if (onCheckpoint(player1) || onCheckpoint(player2)) {
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

	/**
	 * Detecta colisión de los jugadores con cualquier enemigo.
	 * Si un jugador toca un enemigo sin invencibilidad, muere (salvo que absorba el golpe)
	 * y se activa el período de invencibilidad.
	 */
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

	/**
	 * Determina el mensaje de ganador según el modo de juego.
	 * En PVP/PVM: gana quien tenga más monedas; en empate, gana quien haya muerto menos.
	 *
	 * @param coins1 monedas recogidas por el Jugador 1
	 * @param coins2 monedas recogidas por el Jugador 2
	 * @param d1     muertes del Jugador 1
	 * @param d2     muertes del Jugador 2
	 * @param p1Goal {@code true} si el Jugador 1 llegó a la meta
	 * @return mensaje de resultado del nivel
	 */
	private String determinarGanador(int coins1, int coins2, int d1, int d2, boolean p1Goal) {
		if (mode == GameMode.SOLO) {
			return "¡Nivel completado!";
		}
		if (mode == GameMode.PVP || mode == GameMode.PVM) {
			if (coins1 > coins2) {
				return "¡" + name1 + " ganó!";
			}
			if (coins2 > coins1) {
				return "¡" + name2 + " ganó!";
			}
			if (d1 < d2) {
				return "¡" + name1 + " ganó! (empate en monedas, menos muertes)";
			}
			if (d2 < d1) {
				return "¡" + name2 + " ganó! (empate en monedas, menos muertes)";
			}
			return "¡Empate!";
		}
		return "";
	}

	/**
	 * Comprueba si algún jugador llegó a la meta con todas las monedas recogidas.
	 * Si es así, muestra el diálogo de fin de nivel y avanza o termina la partida.
	 */
	private void checkGoal() {
		if (!allCoinsCollected()) {
			return;
		}
		int p1cx = (int) player1.getPosition().getX() + player1.getWidth()  / 2;
		int p1cy = (int) player1.getPosition().getY() + player1.getHeight() / 2;
		boolean p1Goal = tileManager.isGoal(p1cx, p1cy);

		boolean p2Goal = false;
		if (mode == GameMode.PVP) {
			int p2cx = (int) player2.getPosition().getX() + player2.getWidth()  / 2;
			int p2cy = (int) player2.getPosition().getY() + player2.getHeight() / 2;
			p2Goal = tileManager.isGoal(p2cx, p2cy);
		}
		if (!p1Goal && !p2Goal) {
			return;
		}

		levelComplete = true;
		int total1 = Math.max(0, score1 - deaths1 * 5);
		int total2 = Math.max(0, score2 - deaths2 * 5);
		int coins1 = score1, coins2 = score2, d1 = deaths1, d2 = deaths2;

		SwingUtilities.invokeLater(() -> {
			String ganador = determinarGanador(coins1, coins2, d1, d2, p1Goal);
			String breakdown1 = name1 + ":  Monedas: +" + coins1 + "  Muertes: -" + (d1 * 5) + "  =  " + total1;
			String breakdown2 = name2 + ":  Monedas: +" + coins2 + "  Muertes: -" + (d2 * 5) + "  =  " + total2;

			String msg;
			if (mode == GameMode.PVP) {
				msg = ganador + "\n\n" + breakdown1 + "\n" + breakdown2;
			} else if (mode == GameMode.PVM) {
				msg = ganador + "\n\n" + breakdown1;
			} else {
				msg = "¡Nivel completado!\n\n" + breakdown1;
			}

			if (gp.isLastLevel(total1, total2)) {
				int grandTotal1 = gp.getAccumulatedScore1() + total1;
				int grandTotal2 = gp.getAccumulatedScore2() + total2;
				String finalMsg = msg + "\n\n--- PUNTAJE FINAL ---\n" + name1 + ": " + grandTotal1;
				if (mode != GameMode.SOLO) {
					finalMsg += "\n" + name2 + ": " + grandTotal2;
				}
				gp.nextLevel(total1, total2);
				JOptionPane.showMessageDialog(null, finalMsg, "Juego completado", JOptionPane.INFORMATION_MESSAGE);
				gp.getWindow().goToMenu();
			} else {
				String[] opciones = { "Continuar", "Guardar y continuar", "Volver al menú" };
				int eleccion = JOptionPane.showOptionDialog(null, msg, "Fin del nivel",
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
					null, opciones, opciones[0]);
				if (eleccion == 0) {
					gp.nextLevel(total1, total2);
				} else if (eleccion == 1) {
					gp.nextLevel(total1, total2);
					gp.optionSavePublic();
				} else {
					gp.getWindow().goToMenu();
				}
			}
		});
	}

	/**
	 * Instancia todas las fuentes de vida del nivel a partir de los datos del mapa.
	 */
	private void initLifeSources() {
		lifeSources = new ArrayList<>();
		for (Vector2D pos : tileManager.getLifeSourcePositions()) {
			lifeSources.add(new LifeSource(pos, Assets.coinGreen));
		}
	}

	/**
	 * Instancia todas las bombas del nivel a partir de los datos del mapa.
	 */
	private void initBombs() {
		bombs = new ArrayList<>();
		for (Vector2D pos : tileManager.getBombPositions()) {
			bombs.add(new Bomb(pos, Assets.enemy));
		}
	}

	/**
	 * Reconstruye la lista unificada {@code allEntities} con todas las entidades
	 * del nivel para iterar polimórficamente (R22).
	 */
	private void rebuildEntityList() {
		allEntities = new ArrayList<>();
		allEntities.addAll(enemies);
		allEntities.addAll(coins);
		allEntities.addAll(skinCoins);
		allEntities.addAll(lifeSources);
		allEntities.addAll(bombs);
	}

	/**
	 * Detecta si algún jugador toca una fuente de vida y le otorga el bonus.
	 */
	private void checkLifeSourceCollisions() {
		for (LifeSource ls : lifeSources) {
			if (ls.isCollected()) {
				continue;
			}
			Rectangle lsRect = new Rectangle(
				(int) ls.getPosition().getX(),
				(int) ls.getPosition().getY(),
				14, 14
			);
			if (getRect(player1).intersects(lsRect)) {
				ls.collect();
				player1.grantLifeBonus();
			} else if (hasSecondPlayer() && getRect(player2).intersects(lsRect)) {
				ls.collect();
				player2.grantLifeBonus();
			}
		}
	}

	/**
	 * Detecta colisión de jugadores y enemigos con las bombas.
	 * Una bomba explota al contacto y destruye a la entidad que la toca.
	 */
	private void checkBombCollisions() {
		for (Bomb b : bombs) {
			if (b.hasExploded()) {
				continue;
			}
			Rectangle bRect = new Rectangle(
				(int) b.getPosition().getX(),
				(int) b.getPosition().getY(),
				14, 14
			);
			if (invincible1 == 0 && getRect(player1).intersects(bRect)) {
				b.explode();
				if (!player1.absorbHit()) {
					deaths1++;
					respawnPlayer1(respawnP1);
				}
				invincible1 = INVINCIBLE_TIME;
			} else if (hasSecondPlayer() && invincible2 == 0 && getRect(player2).intersects(bRect)) {
				b.explode();
				if (!player2.absorbHit()) {
					deaths2++;
					respawnPlayer2(respawnP2);
				}
				invincible2 = INVINCIBLE_TIME;
			} else {
				for (Enemy e : enemies) {
					Rectangle eRect = new Rectangle(
						(int) e.getPosition().getX(),
						(int) e.getPosition().getY(),
						13, 13
					);
					if (eRect.intersects(bRect)) {
						b.explode();
						break;
					}
				}
			}
		}
	}

	/**
	 * Actualiza el estado del nivel en cada frame: procesa pausa, mueve
	 * entidades, detecta colisiones y comprueba la condición de victoria.
	 */
	public void update() {
		if (levelComplete) {
			return;
		}
		if (KeyBoard.ESCAPE_PRESSED) {
			togglePause();
		}
		if (paused) {
			if (KeyBoard.M_PRESSED) {
				gp.getWindow().goToMenu();
			}
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
		checkLifeSourceCollisions();
		checkBombCollisions();
		checkGoal();
		updateTimer();
	}

	/**
	 * @return {@code true} si el nivel está actualmente pausado
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * Alterna el estado de pausa del nivel.
	 */
	public void togglePause() {
		paused = !paused;
	}

	/**
	 * Descuenta el temporizador de nivel; si llega a cero mata a ambos jugadores
	 * y los envía al inicio del nivel.
	 */
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

	/**
	 * Construye un objeto {@link SaveData} con el estado actual del nivel.
	 *
	 * @param levelIndex índice del nivel a guardar
	 * @return datos de partida listos para serializar
	 */
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

	/**
	 * Restaura el estado del nivel a partir de datos previamente guardados.
	 *
	 * @param data datos de partida a restaurar
	 */
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

	/**
	 * Dibuja el mapa, enemigos, monedas, jugadores, HUD y la superposición
	 * de pausa si el nivel está pausado.
	 *
	 * @param g contexto gráfico del canvas
	 */
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
		for (LifeSource ls : lifeSources) {
			ls.draw(g);
		}
		for (Bomb b : bombs) {
			b.draw(g);
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
			String t1 = name1 + " — Muertes: " + deaths1;
			String t2 = name2 + " — Muertes: " + deaths2;
			g.drawString(t1, 10, 40);
			g.drawString(t2, Window.WIDTH - fm.stringWidth(t2) - 10, 40);
		} else if (mode == GameMode.PVM) {
			String t1 = name1 + " — Muertes: " + deaths1;
			String t2 = name2 + " — Muertes: " + deaths2;
			g.drawString(t1, 10, 40);
			g.drawString(t2, Window.WIDTH - fm.stringWidth(t2) - 10, 40);
		} else {
			String deathsText = name1 + " — Muertes: " + deaths1;
			g.drawString(deathsText, (Window.WIDTH - fm.stringWidth(deathsText)) / 2, 20);
		}

		if (paused) {
			drawPauseOverlay(g);
		}
	}

	/**
	 * Dibuja la superposición semi-transparente de pausa con el texto
	 * "PAUSADO" y las instrucciones para reanudar o salir al menú.
	 *
	 * @param g contexto gráfico del canvas
	 */
	private void drawPauseOverlay(Graphics g) {
		g.setColor(new Color(0, 0, 0, 140));
		g.fillRect(0, 0, Window.WIDTH, Window.HEIGHT);

		g.setFont(new Font("Arial", Font.BOLD, 48));
		FontMetrics fm = g.getFontMetrics();
		String titulo = "PAUSADO";
		g.setColor(Color.WHITE);
		g.drawString(titulo, (Window.WIDTH - fm.stringWidth(titulo)) / 2, Window.HEIGHT / 2 - 40);

		g.setFont(new Font("Arial", Font.PLAIN, 20));
		fm = g.getFontMetrics();
		String reanudar = "ESC — Reanudar";
		String salirMenu = "M — Volver al menú";
		g.drawString(reanudar,  (Window.WIDTH - fm.stringWidth(reanudar))  / 2, Window.HEIGHT / 2 + 20);
		g.drawString(salirMenu, (Window.WIDTH - fm.stringWidth(salirMenu)) / 2, Window.HEIGHT / 2 + 50);
	}
}
