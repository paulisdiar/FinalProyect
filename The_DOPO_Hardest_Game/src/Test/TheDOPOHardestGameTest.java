package Test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import Controller.Window;
import View.GameMenuInput;
import View.KeyBoard;
import View.MenuInput;
import View.PreGameInput;
import View.Vector2D;
import model.BasicEnemy;
import model.Coin;
import model.GameMode;
import model.GameState;
import model.HorizontalMovement;
import model.MachinePlayer;
import model.RandomMovement;
import model.VerticalMovement;

public class TheDOPOHardestGameTest {

    private model.TileManager freeManager;
    private model.TileManager wallManager;
    public BufferedImage dummyTexture;
    private GameState dummyGameState;

    @Before
    public void setUp() {
        dummyTexture = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        Window fakeWindow = new Window() {
            @Override public void setJMenuBar(javax.swing.JMenuBar menuBar) {}
            @Override public void revalidate() {}
            @Override public void goToMenu() {}
            @Override public void setVisible(boolean b) {}
            @Override public void pack() {}
            @Override public void repaint() {}
        };

        dummyGameState = new GameState(fakeWindow, GameMode.SOLO, dummyTexture, dummyTexture, null, null, "J1", "J2");

        freeManager = new model.TileManager(dummyGameState, "res/maps/level1.txt") {
            @Override public boolean isBlocked(int x, int y)    { return false; }
            @Override public boolean isGoal(int x, int y)       { return false; }
            @Override public boolean isCheckpoint(int x, int y) { return false; }
        };

        wallManager = new model.TileManager(dummyGameState, "res/maps/level1.txt") {
        	@Override public boolean isBlocked(int x, int y)    { return true; }
            @Override public boolean isGoal(int x, int y)       { return false; }
            @Override public boolean isCheckpoint(int x, int y) { return false; }
        };
    }

    // =========================================================
    // Vector2D
    // =========================================================

    @Test
    public void vector2D_constructorXY() {
        Vector2D v = new Vector2D(3.5, 7.2);
        assertEquals(3.5, v.getX(), 0.001);
        assertEquals(7.2, v.getY(), 0.001);
    }

    @Test
    public void vector2D_constructor() {
        Vector2D v = new Vector2D();
        assertEquals(0.0, v.getX(), 0.001);
        assertEquals(0.0, v.getY(), 0.001);
    }

    @Test
    public void vector2D_setX() {
        Vector2D v = new Vector2D(1, 1);
        v.setX(99.9);
        assertEquals(99.9, v.getX(), 0.001);
    }

    @Test
    public void vector2D_setY() {
        Vector2D v = new Vector2D(1, 1);
        v.setY(-5.5);
        assertEquals(-5.5, v.getY(), 0.001);
    }

    @Test
    public void vector2D_operaciones() {
        Vector2D v = new Vector2D(3.5, 7.2);
        assertEquals(3.5, v.getX(), 0.001);
        assertEquals(7.2, v.getY(), 0.001);

        v.setX(10.0);
        v.setY(20.0);
        assertEquals(10.0, v.getX(), 0.001);
        assertEquals(20.0, v.getY(), 0.001);

        Vector2D vacio = new Vector2D();
        assertEquals(0.0, vacio.getX(), 0.001);
    }

    // =========================================================
    // Coin
    // =========================================================

    @Test
    public void coin_nuevaMoneda() {
        Coin coin = new Coin(new Vector2D(10, 10), dummyTexture);
        assertFalse(coin.isCollected());
    }

    @Test
    public void coin_collect() {
        Coin coin = new Coin(new Vector2D(10, 10), dummyTexture);
        coin.collect();
        assertTrue(coin.isCollected());
    }

    @Test
    public void coin_collectDouble() {
        Coin coin = new Coin(new Vector2D(0, 0), dummyTexture);
        coin.collect();
        coin.collect();
        assertTrue(coin.isCollected());
    }

    @Test
    public void coin_update() {
        Coin coin = new Coin(new Vector2D(5, 5), dummyTexture);
        coin.update();
    }

    @Test
    public void coin_getPosition() {
        Vector2D pos = new Vector2D(20, 30);
        Coin coin = new Coin(pos, dummyTexture);
        assertEquals(20.0, coin.getPosition().getX(), 0.001);
        assertEquals(30.0, coin.getPosition().getY(), 0.001);
    }

    @Test
    public void coin_CiclosYRecoleccion() {
        Coin c = new Coin(new Vector2D(5, 5), dummyTexture);
        c.update();
        assertFalse(c.isCollected());
        c.collect();
        assertTrue(c.isCollected());
        c.update();
    }

    // =========================================================
    // GameMode
    // =========================================================

    @Test
    public void gameMode_tieneExactamenteTresValores() {
        assertEquals(3, GameMode.values().length);
    }

    @Test
    public void gameMode_retornaSolo() {
        assertEquals(GameMode.SOLO, GameMode.valueOf("SOLO"));
    }

    @Test
    public void gameMode_retornaPvp() {
        assertEquals(GameMode.PVP, GameMode.valueOf("PVP"));
    }

    @Test
    public void gameMode_retornaPvm() {
        assertEquals(GameMode.PVM, GameMode.valueOf("PVM"));
    }

    // =========================================================
    // KeyBoard
    // =========================================================

    @Test
    public void keyboard_constructor() {
        new KeyBoard();
        assertFalse(KeyBoard.UP);
        assertFalse(KeyBoard.DOWN);
        assertFalse(KeyBoard.LEFT);
        assertFalse(KeyBoard.RIGHT);
    }

    @Test
    public void keyboard_activaTeclaUp() {
        KeyBoard kb = new KeyBoard();
        kb.keyPressed(new KeyEvent(new java.awt.Label(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED));
        kb.update();
        assertTrue(KeyBoard.UP);
    }

    @Test
    public void keyboard_desactivaTeclaDown() {
        KeyBoard kb = new KeyBoard();
        kb.keyPressed(new KeyEvent(new java.awt.Label(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED));
        kb.keyReleased(new KeyEvent(new java.awt.Label(), KeyEvent.KEY_RELEASED, 0, 0, KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED));
        kb.update();
        assertFalse(KeyBoard.DOWN);
    }

    @Test
    public void keyboard_TodasLasDirecciones() {
        KeyBoard kb = new KeyBoard();
        int[] keys = {KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT};
        for (int key : keys) {
            kb.keyPressed(new KeyEvent(new java.awt.Label(), KeyEvent.KEY_PRESSED, 0, 0, key, KeyEvent.CHAR_UNDEFINED));
        }
        kb.update();
        assertTrue(KeyBoard.W && KeyBoard.A && KeyBoard.S && KeyBoard.D && KeyBoard.LEFT && KeyBoard.RIGHT);
    }

    @Test
    public void keyboard_keyTyped() {
        KeyBoard kb = new KeyBoard();
        kb.keyTyped(new KeyEvent(new java.awt.Label(), KeyEvent.KEY_TYPED, 0, 0, KeyEvent.VK_UNDEFINED, 'a'));
    }

    // =========================================================
    // MenuInput
    // =========================================================

    @Test
    public void menuInput_constructor() {
        new MenuInput();
        assertFalse(MenuInput.JUGAR);
        assertFalse(MenuInput.SALIR);
    }

    @Test
    public void menuInput_actionJugar() {
        MenuInput mi = new MenuInput();
        mi.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Jugar"));
        mi.update();
        assertTrue(MenuInput.JUGAR);
    }

    @Test
    public void menuInput_actionSalir() {
        MenuInput mi = new MenuInput();
        mi.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Salir"));
        mi.update();
        assertTrue(MenuInput.SALIR);
    }

    @Test
    public void menuInput_actionOpciones() {
        MenuInput mi = new MenuInput();
        mi.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Opciones"));
        mi.update();
        assertTrue(MenuInput.OPCIONES);
    }

    @Test
    public void menuInput_update() {
        MenuInput mi = new MenuInput();
        mi.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Jugar"));
        mi.update();
        mi.update();
        assertFalse(MenuInput.JUGAR);
    }

    @Test
    public void menuInput_accionesGraficas() {
        MenuInput mi = new MenuInput();
        java.awt.Button btn = new java.awt.Button();

        mi.actionPerformed(new ActionEvent(btn, ActionEvent.ACTION_PERFORMED, "Jugar"));
        mi.update();
        assertTrue(MenuInput.JUGAR);

        mi.actionPerformed(new ActionEvent(btn, ActionEvent.ACTION_PERFORMED, "Salir"));
        mi.update();
        assertTrue(MenuInput.SALIR);
    }

    // =========================================================
    // PreGameInput
    // =========================================================

    @Test
    public void preGameInput_actionJugar() {
        PreGameInput pgi = new PreGameInput();
        pgi.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Jugar"));
        pgi.update();
        assertTrue(PreGameInput.JUGAR);
    }

    @Test
    public void preGameInput_actionVolverMenu() {
        PreGameInput pgi = new PreGameInput();
        pgi.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Volver al Menu"));
        pgi.update();
        assertTrue(PreGameInput.VOLVER_MENU);
    }

    // =========================================================
    // GameMenuInput
    // =========================================================

    @Test
    public void gameMenuInput_todasLasAcciones() {
        GameMenuInput gmi = new GameMenuInput();
        String[] acciones = {"Guardar", "Cargar", "Volver al Menu", "Salir"};

        for (String accion : acciones) {
            gmi.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, accion));
        }
        gmi.update();
        assertTrue(GameMenuInput.GUARDAR && GameMenuInput.CARGAR && GameMenuInput.VOLVER_MENU && GameMenuInput.SALIR);
    }

    // =========================================================
    // Movimientos
    // =========================================================

    @Test
    public void horizontalMovement_paredAlFrenteCambia() {
        HorizontalMovement hm = new HorizontalMovement(1);
        int[] dir = hm.getDirection(new Vector2D(100, 100), 16, 16, wallManager);
        assertEquals(0, dir[1]);
    }

    @Test
    public void horizontalMovement_paredAlFrente() {
        HorizontalMovement hm = new HorizontalMovement(1);
        int[] dir = hm.getDirection(new Vector2D(100, 100), 16, 16, wallManager);
        assertNotNull(dir);
    }

    @Test
    public void randomMovement_campoLibre() {
        RandomMovement rm = new RandomMovement();
        int[] dirFree = rm.getDirection(new Vector2D(50, 50), 16, 16, freeManager);
        assertNotNull(dirFree);

        int[] dirWall = rm.getDirection(new Vector2D(50, 50), 16, 16, wallManager);
        assertEquals(0, dirWall[0]);
        assertEquals(0, dirWall[1]);
    }

    // =========================================================
    // GameState
    // =========================================================

    @Test
    public void gameState_propiedades() {
        assertNotNull(dummyGameState.getWindow());
        assertFalse(dummyGameState.isLastLevel());
    }

    // =========================================================
    // Cobertura enemigos y máquina
    // =========================================================

    @Test
    public void cobertura_BasicEnemy() {
        Vector2D posicionInicial = new Vector2D(100, 100);
        BasicEnemy enemigo = new BasicEnemy(posicionInicial, dummyTexture, freeManager, new HorizontalMovement(1));
        assertNotNull(enemigo.getPosition());
        for (int i = 0; i < 10; i++) {
            enemigo.update();
        }
        assertNotNull(enemigo.getPosition());
    }

    @Test
    public void cobertura_MachinePlayer() {
        Vector2D posicionInicial = new Vector2D(50, 50);
        MachinePlayer maquina = new MachinePlayer(posicionInicial, dummyTexture, freeManager, new RandomMovement());
        for (int i = 0; i < 5; i++) {
            maquina.update();
        }
        assertNotNull(maquina.getPosition());
    }

    @Test
    public void cobertura_Movimientos() {
        HorizontalMovement movHoriz = new HorizontalMovement(-1);
        VerticalMovement movVert = new VerticalMovement(1);
        Vector2D posEstres = new Vector2D(20, 20);

        int[] dirH = movHoriz.getDirection(posEstres, 16, 16, wallManager);
        int[] dirV = movVert.getDirection(posEstres, 16, 16, wallManager);

        assertNotNull(dirH);
        assertNotNull(dirV);
    }

    @Test
    public void cobertura_SimulacionTeclas() {
        KeyBoard kb = new KeyBoard();
        java.awt.Label componenteOrigen = new java.awt.Label();

        int[] codigosTeclas = {
            KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
            KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D
        };

        for (int codigo : codigosTeclas) {
            kb.keyPressed(new KeyEvent(componenteOrigen, KeyEvent.KEY_PRESSED, 0, 0, codigo, KeyEvent.CHAR_UNDEFINED));
            kb.update();
            kb.keyReleased(new KeyEvent(componenteOrigen, KeyEvent.KEY_RELEASED, 0, 0, codigo, KeyEvent.CHAR_UNDEFINED));
            kb.update();
        }

        assertFalse(KeyBoard.UP);
        assertFalse(KeyBoard.DOWN);
    }

    @Test
    public void cobertura_GameMenuInput() {
        GameMenuInput inputMenu = new GameMenuInput();
        java.awt.Button botonOrigen = new java.awt.Button();

        String[] comandos = {"Guardar", "Cargar", "Volver al Menu", "Salir"};
        for (String cmd : comandos) {
            inputMenu.actionPerformed(new ActionEvent(botonOrigen, ActionEvent.ACTION_PERFORMED, cmd));
            inputMenu.update();
        }

        assertTrue(GameMenuInput.SALIR);
    }
}
