package model;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import View.Assets;
import View.Vector2D;

/**
 * Jugador verde: puede absorber un golpe de enemigo sin morir.
 * Tras recibir ese golpe el escudo se rompe, la velocidad se reduce
 * y el sprite cambia hasta que el jugador muera y reaparezca.
 */
public class GreenPlayer extends HumanPlayer {

	private static final float WEAKENED_SPEED_MULT = 0.7f;

	private boolean shieldBroken = false;

	/** @return {@code true} si el escudo propio aún está intacto */
	public boolean hasShieldActive() {
		return !shieldBroken;
	}

	/**
	 * @param position    posición inicial
	 * @param texture     sprite del jugador
	 * @param tileManager mapa de tiles para detectar colisiones
	 * @param controls    esquema de control
	 */
	public GreenPlayer(Vector2D position, BufferedImage texture, TileManager tileManager, ControlScheme controls) {
		super(position, texture, tileManager, controls);
	}

	/**
	 * Absorbe un golpe: primero consume el bonus de LifeSource si existe,
	 * luego usa el escudo propio si está intacto.
	 *
	 * @return {@code true} si el golpe fue absorbido
	 */
	@Override
	public boolean absorbHit() {
		if (super.absorbHit()) {
			return true;
		}
		if (!shieldBroken) {
			shieldBroken = true;
			return true;
		}
		return false;
	}

	/**
	 * Restaura el escudo al reaparecer.
	 */
	@Override
	public void onRespawn() {
		super.onRespawn();
		shieldBroken = false;
	}

	/**
	 * @return velocidad reducida si el escudo está roto, velocidad base si no
	 */
	@Override
	protected int getEffectiveSpeed() {
		if (shieldBroken) {
			return (int)(SPEED * WEAKENED_SPEED_MULT);
		}
		return SPEED;
	}

	/**
	 * Dibuja el jugador con sprite alternativo (o semi-transparente) cuando
	 * el escudo está roto, y con sprite normal en caso contrario.
	 *
	 * @param g contexto gráfico
	 */
	@Override
	public void draw(Graphics g) {
		int x = (int) position.getX();
		int y = (int) position.getY();

		if (shieldBroken) {
			if (Assets.playerWeakGreen != null) {
				g.drawImage(Assets.playerWeakGreen, x, y, getWidth(), getHeight(), null);
			} else {
				Graphics2D g2d = (Graphics2D) g;
				Composite old = g2d.getComposite();
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
				g2d.drawImage(texture, x, y, getWidth(), getHeight(), null);
				g2d.setComposite(old);
			}
		} else {
			g.drawImage(texture, x, y, getWidth(), getHeight(), null);
		}
	}
}
