package view;

import java.awt.image.BufferedImage;

public class Assets {

	public static BufferedImage player;
	public static BufferedImage enemy;
	public static BufferedImage coin;
	public static BufferedImage tilePath1;
	public static BufferedImage tilePath2;
	public static BufferedImage tileGoal;

	public static BufferedImage[] playerColors;
	public static BufferedImage playerWeakGreen;

	public static BufferedImage coinRed;
	public static BufferedImage coinBlue;
	public static BufferedImage coinGreen;

	public static void init() {
		player    = Loader.imageLoader("res/players/Player.png");
		enemy     = Loader.imageLoader("res/enemies/Enemy.png");
		coin      = Loader.imageLoader("res/coin/Coin.png");
		coinRed   = Loader.imageLoader("res/coin/red_coin.png");
		coinBlue  = Loader.imageLoader("res/coin/blue_coin.png");
		coinGreen = Loader.imageLoader("res/coin/green_coin.png");
		tilePath1 = Loader.imageLoader("res/tiles/PathTile1.png");
		tilePath2 = Loader.imageLoader("res/tiles/PathTile2.png");
		tileGoal  = Loader.imageLoader("res/tiles/Finish-Checkpoint_Tile.png");

		String[] colorFiles = {
			"player_red", "player_blue", "player_green", "player_yellow",
			"player_orange", "player_purple", "player_cyan", "player_pink"
		};
		playerColors = new BufferedImage[colorFiles.length];
		for (int i = 0; i < colorFiles.length; i++)
			playerColors[i] = Loader.imageLoader("res/players/" + colorFiles[i] + ".png");
	}
}
