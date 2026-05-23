package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import View.Vector2D;

/**
 * Fuente de vida: coleccionable estático que otorga al jugador una vida extra,
 * es decir, la capacidad de absorber el siguiente golpe sin morir.
 * Desaparece al ser recogida (token {@code I} en el mapa).
 * Elemento nuevo propuesto por el equipo (R24).
 */
public class LifeSource extends Collectible {

    /**
     * @param position posición inicial de la fuente de vida
     * @param texture  sprite de la fuente de vida
     */
    public LifeSource(Vector2D position, BufferedImage texture) {
        super(position, texture);
    }

    /**
     * Sin lógica de actualización adicional; hereda comportamiento de {@link Collectible}.
     */
    @Override
    public void update() {
    }

    /**
     * Dibuja la fuente de vida si aún no ha sido recogida.
     * Si no hay textura disponible dibuja un círculo verde como respaldo.
     *
     * @param g contexto gráfico
     */
    @Override
    public void draw(Graphics g) {
        if (isCollected()) {
            return;
        }
        if (texture != null) {
            g.drawImage(texture, (int) position.getX(), (int) position.getY(), null);
        } else {
            g.setColor(new Color(0, 200, 80));
            g.fillOval((int) position.getX(), (int) position.getY(), 14, 14);
            g.setColor(Color.WHITE);
            g.drawString("+", (int) position.getX() + 4, (int) position.getY() + 11);
        }
    }
}
