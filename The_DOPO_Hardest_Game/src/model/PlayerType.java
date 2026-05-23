package model;

/**
 * Tipos de jugador disponibles, cada uno con multiplicadores de
 * velocidad, tamaño y capacidad de escudo.
 */
public enum PlayerType {

    ROJO (1.0f, 1.0f, false),
    AZUL (1.5f, 1.5f, false),
    VERDE(1.0f, 1.0f, true);

    public final float speedMult;
    public final float sizeMult;
    public final boolean hasShield;

    /**
     * @param speedMult multiplicador de velocidad respecto al base
     * @param sizeMult  multiplicador de tamaño respecto al sprite base
     * @param hasShield {@code true} si este tipo puede absorber un golpe
     */
    PlayerType(float speedMult, float sizeMult, boolean hasShield) {
        this.speedMult = speedMult;
        this.sizeMult  = sizeMult;
        this.hasShield = hasShield;
    }
}
