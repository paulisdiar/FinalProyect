package model;

import java.awt.Graphics;

import java.awt.image.BufferedImage;

import View.Vector2D;

/**
 * Clase abstracta que representa cualquier objeto coleccionable del nivel.
 * Extiende {@link GameEntity} y agrega el estado de recolección.
 * Subclases: {@link Coin}, {@link SkinCoin}, {@link LifeSource}.
 */
public abstract class Collectible extends GameEntity {

    private boolean collected = false;

    /**
     * @param position posición inicial del coleccionable
     * @param texture  sprite del coleccionable
     */
    public Collectible(Vector2D position, BufferedImage texture) {
        super(position, texture);
    }

    /**
     * @return {@code true} si ya fue recogido
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Marca el coleccionable como recogido; no tiene efecto si ya lo estaba.
     */
    public void collect() {
        collected = true;
    }

    /**
     * Sin lógica de actualización por defecto; las subclases pueden sobreescribir.
     */
    @Override
    public void update() {
    	// No-op: Los coleccionables estáticos no requieren lógica en cada tick por defecto
    }

    /**
     * Dibuja el coleccionable solo si aún no ha sido recogido.
     *
     * @param g contexto gráfico
     */
    @Override
    public void draw(Graphics g) {
        if (!collected) {
            g.drawImage(texture, (int) position.getX(), (int) position.getY(), null);
        }
    }
}
