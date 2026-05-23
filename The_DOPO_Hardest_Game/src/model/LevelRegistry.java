package model;

import java.io.File;

/**
 * Utilidad estática que detecta cuántos niveles existen en {@code res/maps}
 * buscando archivos con el patrón {@code level1.txt}, {@code level2.txt}, etc.
 * No requiere modificar código al agregar nuevos niveles con el editor.
 */
public class LevelRegistry {

    private static final String MAP_DIR    = "res/maps";
    private static final String PREFIX     = "level";
    private static final String SUFFIX_1P  = ".txt";
    private static final String SUFFIX_2P  = "_2p.txt";

    /**
     * Cuenta los niveles disponibles para el modo indicado.
     * Busca {@code level1.txt}, {@code level2.txt}… hasta que no encuentre el siguiente.
     *
     * @param twoPlayer {@code true} para contar archivos {@code _2p.txt}, {@code false} para {@code .txt}
     * @return número de niveles encontrados (mínimo 1)
     */
    public static int countLevels(boolean twoPlayer) {
        String suffix = twoPlayer ? SUFFIX_2P : SUFFIX_1P;
        int count = 0;
        while (new File(MAP_DIR + "/" + PREFIX + (count + 1) + suffix).exists()) {
            count++;
        }
        return Math.max(count, 1);
    }

    /**
     * Devuelve el número total de niveles disponibles considerando ambos modos.
     * Un nivel se cuenta si existe al menos el archivo de un jugador.
     *
     * @return número de niveles encontrados
     */
    public static int countLevels() {
        int count = 0;
        while (new File(MAP_DIR + "/" + PREFIX + (count + 1) + SUFFIX_1P).exists()
            || new File(MAP_DIR + "/" + PREFIX + (count + 1) + SUFFIX_2P).exists()) {
            count++;
        }
        return Math.max(count, 1);
    }
}
