package model;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import Controller.Window;
import View.Vector2D;

/**
 * Clase base de todos los jugadores. Gestiona la posición, el skin activo,
 * el tamaño escalado, la detección de colisión con tiles en los cuatro lados,
 * la vida extra otorgada por {@link LifeSource}, el efecto de parpadeo durante
 * la invencibilidad y el clamp de posición dentro de los bordes del mapa.
 */
public abstract class Player extends GameObject {

	protected static final int SPEED = 3;
	protected TileManager tileManager;
	private PlayerType activeSkin = null;
	private boolean lifeBonus = false;

	private int invincibleTicks = 0;
	private static final int BLINK_INTERVAL = 6;

	/**
	 * @param position    posición inicial del jugador
	 * @param texture     sprite base del jugador
	 * @param tileManager mapa de tiles para detectar colisiones
	 */
	public Player(Vector2D position, BufferedImage texture, TileManager tileManager) {
		super(position, texture);
		this.tileManager = tileManager;
	}

	/**
	 * Aplica un skin temporal que modifica velocidad y tamaño.
	 *
	 * @param skin tipo de jugador a aplicar como skin
	 */
	public void applySkin(PlayerType skin) {
		activeSkin = skin;
	}

	/**
	 * Elimina el skin activo y vuelve a los atributos base.
	 */
	public void clearSkin() {
		activeSkin = null;
	}

	/**
	 * @return skin activo, o {@code null} si no hay ninguno
	 */
	public PlayerType getActiveSkin() {
		return activeSkin;
	}

	/**
	 * @return ancho efectivo del jugador, aplicando el multiplicador del skin si hay uno
	 */
	public int getWidth() {
		int base = texture.getWidth();
		if (activeSkin != null) {
			return (int)(base * activeSkin.sizeMult);
		}
		return base;
	}

	/**
	 * @return alto efectivo del jugador, aplicando el multiplicador del skin si hay uno
	 */
	public int getHeight() {
		int base = texture.getHeight();
		if (activeSkin != null) {
			return (int)(base * activeSkin.sizeMult);
		}
		return base;
	}

	/**
	 * Otorga al jugador una vida extra (bonus de LifeSource) que absorbe
	 * el siguiente golpe recibido.
	 */
	public void grantLifeBonus() {
		lifeBonus = true;
	}

	/**
	 * Intenta absorber un golpe de enemigo sin morir.
	 * Primero consume el bonus de LifeSource si existe.
	 * Las subclases con escudo propio sobreescriben este método.
	 *
	 * @return {@code true} si el golpe fue absorbido
	 */
	public boolean absorbHit() {
		if (lifeBonus) {
			lifeBonus = false;
			return true;
		}
		return false;
	}

	/**
	 * Activa el período de invencibilidad visual durante {@code ticks} frames.
	 *
	 * @param ticks duración del efecto de parpadeo
	 */
	public void startInvincibility(int ticks) {
		invincibleTicks = ticks;
	}

	/**
	 * @return {@code true} si el jugador está en el período de invencibilidad visual
	 */
	public boolean isInvincible() {
		return invincibleTicks > 0;
	}

	/**
	 * Descuenta un tick de invencibilidad. Debe llamarse cada frame desde {@link Level}.
	 */
	public void tickInvincibility() {
		if (invincibleTicks > 0) {
			invincibleTicks--;
		}
	}

	/**
	 * Lógica ejecutada al reaparecer tras una muerte.
	 * Limpia el skin activo, el bonus de vida extra y la invencibilidad.
	 */
	public void onRespawn() {
		clearSkin();
		lifeBonus = false;
		invincibleTicks = 0;
	}

	/**
	 * Aplica el clamp de posición para que el jugador no salga de los bordes del canvas.
	 */
	protected void clampToBounds() {
		double x = position.getX();
		double y = position.getY();
		int w = getWidth();
		int h = getHeight();
		if (x < 0) {
			position.setX(0);
		} else if (x + w > Window.WIDTH) {
			position.setX(Window.WIDTH - w);
		}
		if (y < 0) {
			position.setY(0);
		} else if (y + h > Window.HEIGHT) {
			position.setY(Window.HEIGHT - h);
		}
	}

	/**
	 * Verifica colisión con una pared en el borde superior del jugador.
	 *
	 * @param x coordenada X del jugador
	 * @param y coordenada Y del jugador
	 * @param w ancho del jugador
	 * @return {@code true} si algún punto del borde superior está bloqueado
	 */
	protected boolean isCollidingUp(int x, int y, int w) {
		return tileManager.isBlocked(x, y) || tileManager.isBlocked(x + w/2, y) || tileManager.isBlocked(x + w, y);
	}

	/**
	 * Verifica colisión con una pared en el borde inferior del jugador.
	 *
	 * @param x coordenada X del jugador
	 * @param y coordenada Y del jugador
	 * @param w ancho del jugador
	 * @param h alto del jugador
	 * @return {@code true} si algún punto del borde inferior está bloqueado
	 */
	protected boolean isCollidingDown(int x, int y, int w, int h) {
		return tileManager.isBlocked(x, y + h) || tileManager.isBlocked(x + w/2, y + h) || tileManager.isBlocked(x + w, y + h);
	}

	/**
	 * Verifica colisión con una pared en el borde izquierdo del jugador.
	 *
	 * @param x coordenada X del jugador
	 * @param y coordenada Y del jugador
	 * @param h alto del jugador
	 * @return {@code true} si algún punto del borde izquierdo está bloqueado
	 */
	protected boolean isCollidingLeft(int x, int y, int h) {
		return tileManager.isBlocked(x, y) || tileManager.isBlocked(x, y + h/2) || tileManager.isBlocked(x, y + h);
	}

	/**
	 * Verifica colisión con una pared en el borde derecho del jugador.
	 *
	 * @param x coordenada X del jugador
	 * @param y coordenada Y del jugador
	 * @param w ancho del jugador
	 * @param h alto del jugador
	 * @return {@code true} si algún punto del borde derecho está bloqueado
	 */
	protected boolean isCollidingRight(int x, int y, int w, int h) {
		return tileManager.isBlocked(x + w, y) || tileManager.isBlocked(x + w, y + h/2) || tileManager.isBlocked(x + w, y + h);
	}

	/**
	 * Dibuja el jugador escalado según su tamaño efectivo.
	 * Durante la invencibilidad aplica un parpadeo semi-transparente.
	 *
	 * @param g contexto gráfico
	 */
	@Override
	public void draw(Graphics g) {
		if (invincibleTicks > 0 && (invincibleTicks / BLINK_INTERVAL) % 2 == 0) {
			Graphics2D g2 = (Graphics2D) g;
			Composite old = g2.getComposite();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
			g2.drawImage(texture, (int) position.getX(), (int) position.getY(), getWidth(), getHeight(), null);
			g2.setComposite(old);
		} else {
			g.drawImage(texture, (int) position.getX(), (int) position.getY(), getWidth(), getHeight(), null);
		}
	}
}
