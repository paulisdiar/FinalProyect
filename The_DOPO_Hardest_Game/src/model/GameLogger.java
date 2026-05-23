package model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger de errores del juego. Escribe entradas con timestamp en
 * {@code logs/game.log}. Usado en los escenarios de guardado y carga
 * de partida para cumplir R20.
 */
public class GameLogger {

    private static final String LOG_FILE = "logs/game.log";
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Registra un mensaje de error con timestamp en el archivo de log.
     * Si el directorio o archivo no existen los crea automáticamente.
     * Si no puede escribir, imprime en stderr sin lanzar excepción.
     *
     * @param scenario descripción corta del escenario (p. ej. "GUARDAR", "CARGAR")
     * @param message  detalle del error
     */
    public static void logError(String scenario, String message) {
        new java.io.File("logs").mkdirs();
        String entry = "[" + LocalDateTime.now().format(FMT) + "] [ERROR] [" + scenario + "] " + message;
        try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            pw.println(entry);
        } catch (IOException e) {
            System.err.println("GameLogger: no se pudo escribir en " + LOG_FILE + " — " + e.getMessage());
        }
    }

    /**
     * Registra un mensaje informativo con timestamp en el archivo de log.
     *
     * @param scenario descripción corta del escenario
     * @param message  detalle del evento
     */
    public static void logInfo(String scenario, String message) {
        new java.io.File("logs").mkdirs();
        String entry = "[" + LocalDateTime.now().format(FMT) + "] [INFO]  [" + scenario + "] " + message;
        try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            pw.println(entry);
        } catch (IOException e) {
            System.err.println("GameLogger: no se pudo escribir en " + LOG_FILE + " — " + e.getMessage());
        }
    }
}
