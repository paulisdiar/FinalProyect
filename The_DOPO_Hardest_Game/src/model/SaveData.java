package model;

import java.io.Serializable;

/**
 * Objeto serializable que contiene el estado completo de una partida
 * guardada: nivel, posiciones, puntuaciones y monedas recogidas.
 */
public class SaveData implements Serializable {

	private static final long serialVersionUID = 1L;

	public GameMode mode;
	public PlayerType type1;
	public PlayerType type2;
	public int textureIndex1;
	public int textureIndex2;
	public int levelIndex;

	public float playerX;
	public float playerY;
	public float player2X;
	public float player2Y;

	public int score1;
	public int score2;
	public int deaths1;
	public int deaths2;
	public int timerTicks;

	public boolean[] coinsCollected;
	public boolean[] skinCoinsCollected;
	public String name1;
	public String name2;
}
