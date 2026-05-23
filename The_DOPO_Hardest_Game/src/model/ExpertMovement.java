package model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import Controller.Window;
import View.Vector2D;

/**
 * Lógica de movimiento experta para la máquina (R11b).
 * Usa BFS en la grilla de tiles para trazar el camino más corto hacia:
 *   1) La moneda más cercana aún no recogida.
 *   2) Si no hay monedas, la zona de meta.
 * Evita tiles de pared y celdas ocupadas por enemigos.
 */
public class ExpertMovement implements MovementLogic {

    private static final int ROWS = 25;
    private static final int COLS = 33;

    private final Level level;

    /** Siguientes pasos BFS pendientes; se recalcula cuando se vacía. */
    private final Deque<int[]> path = new ArrayDeque<>();

    /**
     * @param level nivel activo; necesario para consultar monedas y enemigos en tiempo real
     */
    public ExpertMovement(Level level) {
        this.level = level;
    }

    /**
     * Devuelve el desplazamiento de un paso BFS hacia el objetivo.
     * Recalcula la ruta cuando el camino actual se agota.
     */
    @Override
    public int[] getDirection(Vector2D position, int w, int h, TileManager tileManager) {
        int tileW = Window.WIDTH  / COLS;
        int tileH = Window.HEIGHT / ROWS;

        int col = (int) position.getX() / tileW;
        int row = (int) position.getY() / tileH;

        if (path.isEmpty()) {
            computePath(row, col, tileManager, tileW, tileH);
        }

        if (path.isEmpty()) {
            return new int[]{0, 0};
        }

        int[] next = path.peek();
        int targetX = next[1] * tileW;
        int targetY = next[0] * tileH;

        int dx = 0;
        int dy = 0;
        int speed = 2;

        int cx = (int) position.getX();
        int cy = (int) position.getY();

        if (Math.abs(cx - targetX) <= speed && Math.abs(cy - targetY) <= speed) {
            path.poll();
            dx = targetX - cx;
            dy = targetY - cy;
        } else {
            if      (targetX > cx) dx =  speed;
            else if (targetX < cx) dx = -speed;
            if      (targetY > cy) dy =  speed;
            else if (targetY < cy) dy = -speed;
        }

        return new int[]{dx, dy};
    }

    /**
     * BFS desde la celda actual hacia el objetivo más cercano.
     * Objetivo: moneda más próxima no recogida; si no hay, la meta.
     */
    private void computePath(int startRow, int startCol, TileManager tileManager, int tileW, int tileH) {
        int[] goal = findGoal(tileManager, tileW, tileH);
        if (goal == null) {
            return;
        }

        boolean[][] visited  = new boolean[ROWS][COLS];
        int[][]     parentR  = new int[ROWS][COLS];
        int[][]     parentC  = new int[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                parentR[r][c] = -1;
                parentC[r][c] = -1;
            }
        }

        Deque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;

        int[] dr = {-1, 1, 0, 0};
        int[] dc = { 0, 0,-1, 1};

        boolean found = false;

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0];
            int c = cur[1];

            if (r == goal[0] && c == goal[1]) {
                found = true;
                break;
            }

            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d];
                int nc = c + dc[d];
                if (nr < 0 || nr >= ROWS || nc < 0 || nc >= COLS) continue;
                if (visited[nr][nc]) continue;
                if (tileManager.isBlocked(nc * tileW, nr * tileH)) continue;
                if (hasEnemyAt(nr, nc, tileW, tileH)) continue;
                visited[nr][nc] = true;
                parentR[nr][nc] = r;
                parentC[nr][nc] = c;
                queue.add(new int[]{nr, nc});
            }
        }

        if (!found) {
            return;
        }

        List<int[]> steps = new ArrayList<>();
        int r = goal[0];
        int c = goal[1];
        while (!(r == startRow && c == startCol)) {
            steps.add(new int[]{r, c});
            int pr = parentR[r][c];
            int pc = parentC[r][c];
            r = pr;
            c = pc;
        }
        Collections.reverse(steps);
        path.addAll(steps);
    }

    /**
     * Elige el objetivo: moneda no recogida más cercana en distancia Manhattan,
     * o la celda de meta si no quedan monedas.
     */
    private int[] findGoal(TileManager tileManager, int tileW, int tileH) {
        List<Coin> coins = level.getActiveCoins();
        if (!coins.isEmpty()) {
            Vector2D machinePos = level.getMachinePosition();
            int mr = (int) machinePos.getY() / tileH;
            int mc = (int) machinePos.getX() / tileW;
            int bestDist = Integer.MAX_VALUE;
            int[] best = null;
            for (Coin coin : coins) {
                if (coin.isCollected()) continue;
                int cr = (int) coin.getPosition().getY() / tileH;
                int cc = (int) coin.getPosition().getX() / tileW;
                int dist = Math.abs(cr - mr) + Math.abs(cc - mc);
                if (dist < bestDist) {
                    bestDist = dist;
                    best = new int[]{cr, cc};
                }
            }
            if (best != null) {
                return best;
            }
        }

        // Sin monedas: ir a la meta
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (tileManager.isGoal(col * tileW, row * tileH)) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    /**
     * Indica si hay algún enemigo activo en la celda dada.
     */
    private boolean hasEnemyAt(int row, int col, int tileW, int tileH) {
        int px = col * tileW;
        int py = row * tileH;
        for (Enemy e : level.getEnemies()) {
            int er = (int) e.getPosition().getY() / tileH;
            int ec = (int) e.getPosition().getX() / tileW;
            if (er == row && ec == col) return true;
            // También evita la celda inmediatamente adyacente al enemigo
            if (Math.abs(er - row) + Math.abs(ec - col) <= 1) return true;
        }
        return false;
    }
}
