package model;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import View.Vector2D;

/**
 * Clase abstracta que representa obstáculos estáticos del nivel.
 * Extiende {@link GameEntity}. Subclase: {@link Bomb}.
 */
public abstract class Obstacle extends GameEntity {

    /**
     * @param position posición inicial del obstáculo
     * @param texture  sprite del obstáculo
     */
    public Obstacle(Vector2D position, BufferedImage texture) {
        super(position, texture);
    }

    /**
     * Sin movimiento por defecto; los obstáculos son estáticos.
     */
    @Override
    public void update() {
    	// No-op: Los coleccionables estáticos no requieren lógica en cada tick por defecto
    }

    /**
     * Dibuja el obstáculo en su posición actual.
     *
     * @param g contexto gráfico
     */
    @Override
    public void draw(Graphics g) {
        g.drawImage(texture, (int) position.getX(), (int) position.getY(), null);
    }
}
