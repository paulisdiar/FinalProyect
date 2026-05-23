package model;

import view.Vector2D;

<<<<<<< HEAD
@FunctionalInterface
=======
/**
 * Estrategia de movimiento para entidades del juego (enemigos, máquina).
 * Cada implementación encapsula un comportamiento distinto.
 */
>>>>>>> branch 'main' of https://github.com/paulisdiar/FinalProyect.git
public interface MovementLogic {

<<<<<<< HEAD
	int[] getDirection(Vector2D position, int width, int height, TileManager tileManager);
=======
	/**
	 * Calcula el desplazamiento a aplicar en este frame.
	 *
	 * @param position   posición actual de la entidad
	 * @param w          ancho de la entidad en píxeles
	 * @param h          alto de la entidad en píxeles
	 * @param tileManager mapa de tiles para consultar colisiones
	 * @return arreglo {@code [dx, dy]} con el desplazamiento horizontal y vertical
	 */
	int[] getDirection(Vector2D position, int w, int h, TileManager tileManager);
>>>>>>> branch 'main' of https://github.com/paulisdiar/FinalProyect.git
}
