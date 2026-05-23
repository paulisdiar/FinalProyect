package model;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import Controller.Window;
import View.Assets;
import View.Vector2D;

/**
 * Carga y gestiona el mapa de tiles del nivel activo.
 * Proporciona métodos para consultar el tipo de tile en coordenadas de pantalla
 * y para obtener las posiciones de todos los objetos del mapa.
 */
public class TileManager {

	GameState gp;
	private int[][]    mapData;
	private String[][] rawData;

	/**
	 * Crea el gestor cargando el mapa desde el archivo indicado.
	 *
	 * @param gp      estado global del juego
	 * @param mapPath ruta al archivo de mapa (.txt)
	 */
	public TileManager(GameState gp, String mapPath) {
		this.gp = gp;
		loadMap(mapPath);
	}

	/**
	 * Lee el archivo de mapa y rellena {@code mapData} y {@code rawData}.
	 * Los tokens especiales (C, P, F, G, H) se convierten a códigos internos.
	 *
	 * @param path ruta al archivo de mapa
	 */
	private void loadMap(String path) {
		try {
			Scanner sc = new Scanner(new File(path));
			int rows = 25, cols = 33;
			mapData = new int[rows][cols];
			rawData = new String[rows][cols];
			for (int row = 0; row < rows; row++) {
				for (int col = 0; col < cols; col++) {
					String token = sc.next();
					rawData[row][col] = token;
					if (token.equals("C")) {
						mapData[row][col] = 12;
					} else if (token.startsWith("C")) {
						mapData[row][col] = 200;
					} else if (token.startsWith("P")) {
						mapData[row][col] = 201;
					} else if (token.equals("F")) {
						mapData[row][col] = 15;
					} else if (token.equals("G")) {
						mapData[row][col] = 16;
					} else if (token.equals("H")) {
						mapData[row][col] = 17;
					} else if (token.equals("I")) {
						mapData[row][col] = 18;
					} else if (token.equals("J")) {
						mapData[row][col] = 19;
					} else {
						try {
							mapData[row][col] = Integer.parseInt(token, 16);
						} catch (NumberFormatException ex) {
							GameLogger.logError("MAPA", "Token desconocido '" + token + "' en [" + row + "," + col + "] — tratado como camino");
							mapData[row][col] = 0;
						}
					}
				}
			}
			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Dibuja todos los tiles del mapa en el contexto gráfico.
	 *
	 * @param g contexto gráfico
	 */
	public void draw(Graphics g) {
		int tileW = Window.WIDTH  / mapData[0].length;
		int tileH = Window.HEIGHT / mapData.length;

		for (int row = 0; row < mapData.length; row++) {
			for (int col = 0; col < mapData[0].length; col++) {
				int type = mapData[row][col];
				int x = col * tileW;
				int y = row * tileH;

				if (type == 3) {
					g.setColor(new Color(173, 181, 217));
					g.fillRect(x, y, tileW, tileH);
				}
				if (type == 0) g.drawImage(Assets.tilePath1, x, y, tileW, tileH, null);
				if (type == 1) g.drawImage(Assets.tilePath2, x, y, tileW, tileH, null);
				if (type == 2) g.drawImage(Assets.tileGoal, x, y, tileW, tileH, null);
				if (type == 4 || type == 6 || type == 7) g.drawImage(Assets.tileGoal, x, y, tileW, tileH, null);
				if (type == 5) g.drawImage((row + col) % 2 == 0 ? Assets.tilePath1 : Assets.tilePath2, x, y, tileW, tileH, null);
				if (type >= 8 && type <= 19) g.drawImage(Assets.tilePath1, x, y, tileW, tileH, null);
				if (type == 200 || type == 201) g.drawImage(Assets.tilePath1, x, y, tileW, tileH, null);
			}
		}
	}

	/**
	 * @return lista de posiciones en pantalla de todas las monedas amarillas (tipo 5)
	 */
	public List<Vector2D> getCoinPositions() {
		int tileW = Window.WIDTH  / mapData[0].length;
		int tileH = Window.HEIGHT / mapData.length;
		List<Vector2D> positions = new ArrayList<>();
		for (int row = 0; row < mapData.length; row++)
			for (int col = 0; col < mapData[0].length; col++)
				if (mapData[row][col] == 5)
					positions.add(new Vector2D(col * tileW, row * tileH));
		return positions;
	}

	/**
	 * @return posición de spawn del Jugador 1 (tile tipo 6)
	 */
	public Vector2D getSpawnPlayer1() {
		return getSpawnPosition(6);
	}

	/**
	 * @return posición de spawn del Jugador 2 (tile tipo 7)
	 */
	public Vector2D getSpawnPlayer2() {
		return getSpawnPosition(7);
	}

	/**
	 * Busca la primera celda con el tipo indicado y devuelve su posición en pantalla.
	 *
	 * @param tileType código de tile a buscar
	 * @return posición encontrada, o (0, 0) si no existe ninguna celda de ese tipo
	 */
	private Vector2D getSpawnPosition(int tileType) {
		int tileW = Window.WIDTH  / mapData[0].length;
		int tileH = Window.HEIGHT / mapData.length;
		for (int row = 0; row < mapData.length; row++) {
			for (int col = 0; col < mapData[0].length; col++) {
				if (mapData[row][col] == tileType) {
					return new Vector2D(col * tileW, row * tileH);
				}
			}
		}
		return new Vector2D(0, 0);
	}

	/**
	 * @return lista de arreglos {@code [x, y, tipo]} para cada moneda de skin (tipos 15, 16, 17)
	 */
	public List<int[]> getSkinCoinPositions() {
		int tileW = Window.WIDTH  / mapData[0].length;
		int tileH = Window.HEIGHT / mapData.length;
		List<int[]> positions = new ArrayList<>();
		for (int row = 0; row < mapData.length; row++) {
			for (int col = 0; col < mapData[0].length; col++) {
				int t = mapData[row][col];
				if (t == 15 || t == 16 || t == 17) {
					positions.add(new int[]{ col * tileW, row * tileH, t });
				}
			}
		}
		return positions;
	}

	/**
	 * @return lista de posiciones de todas las fuentes de vida (tipo 18, token I)
	 */
	public List<Vector2D> getLifeSourcePositions() {
		int tileW = Window.WIDTH  / mapData[0].length;
		int tileH = Window.HEIGHT / mapData.length;
		List<Vector2D> positions = new ArrayList<>();
		for (int row = 0; row < mapData.length; row++) {
			for (int col = 0; col < mapData[0].length; col++) {
				if (mapData[row][col] == 18) {
					positions.add(new Vector2D(col * tileW, row * tileH));
				}
			}
		}
		return positions;
	}

	/**
	 * @return lista de posiciones de todas las bombas (tipo 19, token J)
	 */
	public List<Vector2D> getBombPositions() {
		int tileW = Window.WIDTH  / mapData[0].length;
		int tileH = Window.HEIGHT / mapData.length;
		List<Vector2D> positions = new ArrayList<>();
		for (int row = 0; row < mapData.length; row++) {
			for (int col = 0; col < mapData[0].length; col++) {
				if (mapData[row][col] == 19) {
					positions.add(new Vector2D(col * tileW, row * tileH));
				}
			}
		}
		return positions;
	}

	/**
	 * @return lista de arreglos {@code [x, y, tipo]} para cada enemigo normal (tipos 8–14)
	 */
	public List<int[]> getEnemySpawns() {
		int tileW = Window.WIDTH  / mapData[0].length;
		int tileH = Window.HEIGHT / mapData.length;
		List<int[]> spawns = new ArrayList<>();
		for (int row = 0; row < mapData.length; row++) {
			for (int col = 0; col < mapData[0].length; col++) {
				int t = mapData[row][col];
				if (t >= 8 && t <= 14) {
					spawns.add(new int[]{ col * tileW, row * tileH, t });
				}
			}
		}
		return spawns;
	}

	/**
	 * Devuelve los waypoints agrupados por identificador de patrulla (tokens P1, P2…).
	 *
	 * @return mapa de {@code grupoId -> lista de posiciones de waypoints}
	 */
	public Map<String, List<Vector2D>> getPatrolGroups() {
		int tileW = Window.WIDTH  / mapData[0].length;
		int tileH = Window.HEIGHT / mapData.length;
		Map<String, List<Vector2D>> groups = new HashMap<>();
		for (int row = 0; row < mapData.length; row++) {
			for (int col = 0; col < mapData[0].length; col++) {
				String token = rawData[row][col];
				if (token != null && token.startsWith("P")) {
					String group = token.substring(1);
					groups.computeIfAbsent(group, k -> new ArrayList<>())
					      .add(new Vector2D(col * tileW, row * tileH));
				}
			}
		}
		return groups;
	}

	/**
	 * Devuelve la posición de spawn de cada patrullero (tokens C1, C2…).
	 *
	 * @return mapa de {@code grupoId -> posición de spawn del patrullero}
	 */
	public Map<String, Vector2D> getPatrolSpawns() {
		int tileW = Window.WIDTH  / mapData[0].length;
		int tileH = Window.HEIGHT / mapData.length;
		Map<String, Vector2D> spawns = new HashMap<>();
		for (int row = 0; row < mapData.length; row++) {
			for (int col = 0; col < mapData[0].length; col++) {
				String token = rawData[row][col];
				if (token != null && token.startsWith("C")) {
					String group = token.substring(1);
					spawns.put(group, new Vector2D(col * tileW, row * tileH));
				}
			}
		}
		return spawns;
	}

	/**
	 * Indica si las coordenadas de pantalla corresponden a un tile de checkpoint (tipo 4).
	 *
	 * @param x coordenada horizontal en píxeles
	 * @param y coordenada vertical en píxeles
	 * @return {@code true} si hay checkpoint en esa posición
	 */
	public boolean isCheckpoint(int x, int y) {
		int tileW = Window.WIDTH  / mapData[0].length;
		int tileH = Window.HEIGHT / mapData.length;
		int col = x / tileW;
		int row = y / tileH;
		if (row < 0 || col < 0 || row >= mapData.length || col >= mapData[0].length) {
			return false;
		}
		int type = mapData[row][col];
		return type == 4;
	}

	/**
	 * Indica si las coordenadas de pantalla corresponden a un tile de meta (tipo 2).
	 *
	 * @param x coordenada horizontal en píxeles
	 * @param y coordenada vertical en píxeles
	 * @return {@code true} si hay meta en esa posición
	 */
	public boolean isGoal(int x, int y) {
		int tileW = Window.WIDTH  / mapData[0].length;
		int tileH = Window.HEIGHT / mapData.length;
		int col = x / tileW;
		int row = y / tileH;
		if (row < 0 || col < 0 || row >= mapData.length || col >= mapData[0].length) {
			return false;
		}
		return mapData[row][col] == 2;
	}

	/**
	 * Indica si las coordenadas de pantalla corresponden a una pared (tipo 3)
	 * o están fuera de los límites del mapa.
	 *
	 * @param x coordenada horizontal en píxeles
	 * @param y coordenada vertical en píxeles
	 * @return {@code true} si la posición está bloqueada
	 */
	public boolean isBlocked(int x, int y) {
		int tileW = Window.WIDTH  / mapData[0].length;
		int tileH = Window.HEIGHT / mapData.length;
		int col = x / tileW;
		int row = y / tileH;

		if (row < 0 || col < 0 || row >= mapData.length || col >= mapData[0].length) {
			return true;
		}
		return mapData[row][col] == 3;
	}
}
