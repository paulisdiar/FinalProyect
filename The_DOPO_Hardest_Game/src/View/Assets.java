package View;

import java.awt.image.BufferedImage;

public class Assets {

	public static BufferedImage player;
	public static BufferedImage enemy;
	public static BufferedImage coin;
	public static BufferedImage tilePath1;
	public static BufferedImage tilePath2;
	public static BufferedImage tileGoal;

	public static BufferedImage[] playerColors;

	public static void init() {
		player    = Loader.ImageLoader("res/players/Player.png");
		enemy     = Loader.ImageLoader("res/enemies/Enemy.png");
		coin      = Loader.ImageLoader("res/coin/Coin.png");
		tilePath1 = Loader.ImageLoader("res/tiles/PathTile1.png");
		tilePath2 = Loader.ImageLoader("res/tiles/PathTile2.png");
		tileGoal  = Loader.ImageLoader("res/tiles/Finish-Checkpoint_Tile.png");

		String[] colorFiles = {
			"player_red", "player_blue", "player_green", "player_yellow",
			"player_orange", "player_purple", "player_cyan", "player_pink"
		};
		playerColors = new BufferedImage[colorFiles.length];
		for (int i = 0; i < colorFiles.length; i++)
			playerColors[i] = Loader.ImageLoader("res/players/" + colorFiles[i] + ".png");
	}
}
