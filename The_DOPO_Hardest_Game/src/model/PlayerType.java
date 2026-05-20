package model;

public enum PlayerType {

    ROJO (1.0f, 1.0f, false),
    AZUL (1.5f, 1.5f, false),
    VERDE(1.0f, 1.0f, true);

    public final float speedMult;
    public final float sizeMult;
    public final boolean hasShield;

    PlayerType(float speedMult, float sizeMult, boolean hasShield) {
        this.speedMult = speedMult;
        this.sizeMult  = sizeMult;
        this.hasShield = hasShield;
    }
}
