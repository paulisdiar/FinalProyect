package model;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import controller.Window;
import view.Assets;
import view.Vector2D;

public class TileManager {

	GameState gp;
	private int[][]    mapData;
	private String[][] rawData;

	public TileManager(GameState gp, String mapPath) {
		this.gp = gp;
		loadMap(mapPath);
	}

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
					if (token.startsWith("C")) {
						mapData[row][col] = 200;
					} else if (token.startsWith("P")) {
						mapData[row][col] = 201;
					} else if (token.equals("F")) {
						mapData[row][col] = 15;
					} else if (token.equals("G")) {
						mapData[row][col] = 16;
					} else if (token.equals("H")) {
						mapData[row][col] = 17;
					} else {
						mapData[row][col] = Integer.parseInt(token, 16);
					}
				}
			}
			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
				if (type >= 8 && type <= 17) g.drawImage(Assets.tilePath1, x, y, tileW, tileH, null);
				if (type == 200 || type == 201) g.drawImage(Assets.tilePath1, x, y, tileW, tileH, null);
			}
		}
	}

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

	public Vector2D getSpawnPlayer1() {
		return getSpawnPosition(6);
	}

	public Vector2D getSpawnPlayer2() {
		return getSpawnPosition(7);
	}

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
