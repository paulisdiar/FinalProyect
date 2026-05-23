 package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import View.Vector2D;

/**
 * Bomba: obstáculo estático que destruye a CUALQUIER entidad (jugador o enemigo)
 * al contacto. Al activarse desaparece del nivel (token {@code J} en el mapa).
 * Elemento configurable en el archivo .txt del nivel (R17b).
 */
public class Bomb extends Obstacle {

    private boolean exploded = false;

    /**
     * @param position posición inicial de la bomba
     * @param texture  sprite de la bomba
     */
    public Bomb(Vector2D position, BufferedImage texture) {
        super(position, texture);
    }

    /**
     * @return {@code true} si la bomba ya explotó y no debe detectarse más
     */
    public boolean hasExploded() {
        return exploded;
    }

    /**
     * Marca la bomba como explotada; deja de dibujarse y de colisionar.
     */
    public void explode() {
        exploded = true;
    }

    /**
     * Sin lógica de movimiento; hereda comportamiento estático de {@link Obstacle}.
     */
    @Override
    public void update() {
    }

    /**
     * Dibuja la bomba si aún no ha explotado.
     * Si no hay textura disponible dibuja un círculo rojo como respaldo.
     *
     * @param g contexto gráfico
     */
    @Override
    public void draw(Graphics g) {
        if (exploded) {
            return;
        }
        if (texture != null) {
            g.drawImage(texture, (int) position.getX(), (int) position.getY(), null);
        } else {
            g.setColor(new Color(200, 30, 30));
            g.fillOval((int) position.getX(), (int) position.getY(), 14, 14);
            g.setColor(Color.BLACK);
            g.drawOval((int) position.getX(), (int) position.getY(), 14, 14);
        }
    }
}
