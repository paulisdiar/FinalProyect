package model;

import java.awt.Graphics;

import java.awt.image.BufferedImage;

import View.Vector2D;

/**
 * Clase abstracta raíz de toda la jerarquía de entidades del juego.
 * Define posición, textura y el contrato de actualización y dibujado.
 * Sustituye a {@link GameObject} como base de la jerarquía completa:
 * Player → Enemy → Collectible → Obstacle.
 */
public abstract class GameEntity {

    protected BufferedImage texture;
    protected Vector2D position;

    /**
     * @param position posición inicial de la entidad
     * @param texture  sprite de la entidad
     */
    public GameEntity(Vector2D position, BufferedImage texture) {
        this.position = position;
        this.texture  = texture;
    }

    /**
     * Actualiza el estado de la entidad en cada frame.
     */
    public abstract void update();

    /**
     * Dibuja la entidad en el contexto gráfico dado.
     *
     * @param g contexto gráfico sobre el que dibujar
     */
    public abstract void draw(Graphics g);

    /**
     * @return posición actual de la entidad
     */
    public Vector2D getPosition() {
        return position;
    }

    /**
     * @param position nueva posición de la entidad
     */
    public void setPosition(Vector2D position) {
        this.position = position;
    }
}
