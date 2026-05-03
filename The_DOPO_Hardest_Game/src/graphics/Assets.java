package graphics;

import java.awt.image.BufferedImage;

public class Assets {

	public static BufferedImage player;
	public static BufferedImage enemy;
	public static BufferedImage coin;
	public static BufferedImage tilePath1;
	public static BufferedImage tilePath2;
	public static BufferedImage tileGoal;

	public static void init() {
		player    = Loader.ImageLoader("res/players/Player.png");
		enemy     = Loader.ImageLoader("res/enemies/Enemy.png");
		coin      = Loader.ImageLoader("res/coin/Coin.png");
		tilePath1 = Loader.ImageLoader("res/tiles/PathTile1.png");
		tilePath2 = Loader.ImageLoader("res/tiles/PathTile2.png");
		tileGoal  = Loader.ImageLoader("res/tiles/Finish-Checkpoint_Tile.png");
	}
}
