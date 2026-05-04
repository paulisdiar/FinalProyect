package model;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Controller.Window;
import View.Assets;
import View.Vector2D;

public class TileManager {

	GameState gp;
	private int[][] mapData;

	public TileManager(GameState gp, String mapPath) {
		this.gp = gp;
		loadMap(mapPath);
	}

	private void loadMap(String path) {
		try {
			Scanner sc = new Scanner(new File(path));
			int rows = 25, cols = 33;
			mapData = new int[rows][cols];
			for (int row = 0; row < rows; row++)
				for (int col = 0; col < cols; col++)
					mapData[row][col] = sc.nextInt();
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
				if (type == 4) g.drawImage(Assets.tileGoal, x, y, tileW, tileH, null);
				if (type == 5) g.drawImage((row + col) % 2 == 0 ? Assets.tilePath1 : Assets.tilePath2, x, y, tileW, tileH, null);
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

	public boolean isGoal(int x, int y) {
		int tileW = Window.WIDTH  / mapData[0].length;
		int tileH = Window.HEIGHT / mapData.length;
		int col = x / tileW;
		int row = y / tileH;
		if (row < 0 || col < 0 || row >= mapData.length || col >= mapData[0].length)
			return false;
		return mapData[row][col] == 2;
	}

	public boolean isBlocked(int x, int y) {
		int tileW = Window.WIDTH  / mapData[0].length;
		int tileH = Window.HEIGHT / mapData.length;
		int col = x / tileW;
		int row = y / tileH;

		if (row < 0 || col < 0 || row >= mapData.length || col >= mapData[0].length)
			return true;

		return mapData[row][col] == 3;
	}
}
