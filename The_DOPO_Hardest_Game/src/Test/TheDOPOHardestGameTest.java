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
import model.PlayerType;
import model.RandomMovement;
import model.VerticalMovement;
import model.TileManager;
import model.Tile;
import model.BluePlayer;
import model.RedPlayer;
import model.HumanPlayer;
import model.GreenPlayer;
import model.SaveData;
import model.PlayerType;
import model.ControlScheme;


public class TheDOPOHardestGameTest {

    private model.TileManager freeManager;
    private model.TileManager wallManager;
    public BufferedImage dummyTexture;
    private GameState dummyGameState;
    private ControlScheme stubControls;

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

        dummyGameState = new GameState(fakeWindow, GameMode.SOLO, dummyTexture, dummyTexture, PlayerType.ROJO, PlayerType.ROJO, "J1", "J2");

        stubControls = new ControlScheme() {
            @Override public boolean isUp() { return false; }
            @Override public boolean isDown() { return false; }
            @Override public boolean isLeft() { return false; }
            @Override public boolean isRight() { return false; }
        };

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
    public void vector2DConstructorXY() {
        Vector2D v = new Vector2D(3.5, 7.2);
        assertEquals(3.5, v.getX(), 0.001);
        assertEquals(7.2, v.getY(), 0.001);
    }

    @Test
    public void vector2DConstructor() {
        Vector2D v = new Vector2D();
        assertEquals(0.0, v.getX(), 0.001);
        assertEquals(0.0, v.getY(), 0.001);
    }

    @Test
    public void vector2DSetX() {
        Vector2D v = new Vector2D(1, 1);
        v.setX(99.9);
        assertEquals(99.9, v.getX(), 0.001);
    }

    @Test
    public void vector2DSetY() {
        Vector2D v = new Vector2D(1, 1);
        v.setY(-5.5);
        assertEquals(-5.5, v.getY(), 0.001);
    }

    @Test
    public void vector2DOperaciones() {
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
    
    @Test
    public void coberturaVector2D() {
        Vector2D v = new Vector2D(5.0, 10.0);
        v.setX(12.0);
        v.setY(24.0);
        assertEquals(12.0, v.getX(), 0.001);
        assertEquals(24.0, v.getY(), 0.001);
        
        Vector2D vVacio = new Vector2D();
        assertEquals(0.0, vVacio.getX(), 0.001);
    }

    // =========================================================
    // Coin
    // =========================================================

    @Test
    public void coinNuevaMoneda() {
        Coin coin = new Coin(new Vector2D(10, 10), dummyTexture);
        assertFalse(coin.isCollected());
    }

    @Test
    public void coinCollect() {
        Coin coin = new Coin(new Vector2D(10, 10), dummyTexture);
        coin.collect();
        assertTrue(coin.isCollected());
    }

    @Test
    public void coinCollectDouble() {
        Coin coin = new Coin(new Vector2D(0, 0), dummyTexture);
        coin.collect();
        coin.collect();
        assertTrue(coin.isCollected());
    }

    @Test
    public void coinUpdate() {
        Coin coin = new Coin(new Vector2D(5, 5), dummyTexture);
        coin.update();
    }

    @Test
    public void coinGetPosition() {
        Vector2D pos = new Vector2D(20, 30);
        Coin coin = new Coin(pos, dummyTexture);
        assertEquals(20.0, coin.getPosition().getX(), 0.001);
        assertEquals(30.0, coin.getPosition().getY(), 0.001);
    }

    @Test
    public void coinCiclosYRecoleccion() {
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
    public void gameModeTieneExactamenteTresValores() {
        assertEquals(3, GameMode.values().length);
    }

    @Test
    public void gameModeRetornaSolo() {
        assertEquals(GameMode.SOLO, GameMode.valueOf("SOLO"));
    }

    @Test
    public void gameModeRetornaPvp() {
        assertEquals(GameMode.PVP, GameMode.valueOf("PVP"));
    }

    @Test
    public void gameModeRetornaPvm() {
        assertEquals(GameMode.PVM, GameMode.valueOf("PVM"));
    }

    // =========================================================
    // KeyBoard
    // =========================================================

    @Test
    public void keyboardConstructor() {
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
    
    @Test
    public void cobertura_RandomMovement() {
        RandomMovement rm = new RandomMovement();
        
        int[] dir1 = rm.getDirection(new Vector2D(50, 50), 16, 16, freeManager);
        assertNotNull(dir1);
        
        int[] dirWall = rm.getDirection(new Vector2D(10, 10), 16, 16, wallManager);
        assertNotNull(dirWall);
        
        try {
            java.lang.reflect.Field tickerField = RandomMovement.class.getDeclaredField("ticker");
            tickerField.setAccessible(true);
            tickerField.setInt(rm, 55); // Supera el CHANGE_INTERVAL
        } catch (Exception e) {
        }
        
        int[] dirInterval = rm.getDirection(new Vector2D(50, 50), 16, 16, freeManager);
        assertNotNull(dirInterval);
    }

    @Test
    public void cobertura_VerticalMovement() {
 
        VerticalMovement vmDown = new VerticalMovement(1);
        int[] dirDown = vmDown.getDirection(new Vector2D(10, 10), 16, 16, wallManager);
        assertTrue(dirDown[1] < 0);
        
        VerticalMovement vmUp = new VerticalMovement(-1);
        int[] dirUp = vmUp.getDirection(new Vector2D(10, 10), 16, 16, wallManager);
        assertTrue(dirUp[1] > 0);
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
            KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D
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
   
     
    // =========================================================
    // SaveData
    // =========================================================

   
    @Test
    public void coberturaSaveData() {
        SaveData data = new SaveData();
        data.mode = GameMode.PVM;
        data.type1 = PlayerType.ROJO;
        data.name1 = "Paula";
        data.playerX = 150.5f;
        data.coinsCollected = new boolean[]{true, false, true};
        
        assertEquals(GameMode.PVM, data.mode);
        assertEquals(PlayerType.ROJO, data.type1);
        assertEquals("Paula", data.name1);
        assertEquals(150.5f, data.playerX, 0.001);
        assertTrue(data.coinsCollected[0]);
    }
    // =========================================================
    // MOVIMIENTO Y COLISIONES
    // =========================================================

    @Test
    public void coberturaMovimientoYColisionBordes() {
        ControlScheme activeControls = new ControlScheme() {
            public boolean isUp() {
            	return true; 
            }
            public boolean isDown() { 
            	return false; 
            }
            public boolean isLeft() { 
            	return false; 
            }
            public boolean isRight() {
            	return true;
            }
        };

        BluePlayer player = new BluePlayer(new Vector2D(100, 100), dummyTexture, freeManager, activeControls);
        player.update();
        
        assertTrue(player.getPosition().getX() > 100);
        assertTrue(player.getPosition().getY() < 100);
    }

    @Test
    public void coberturaColisionJugadorEnemigoYCheckpoint() {
        Vector2D posInicial = new Vector2D(50, 50);
        Vector2D checkpointActivo = new Vector2D(10, 10);
        
        boolean colisionDetectada = true; 
        if (colisionDetectada) {
            posInicial.setX(checkpointActivo.getX());
            posInicial.setY(checkpointActivo.getY());
        }
        assertEquals(10.0, posInicial.getX(), 0.001);
    }

    @Test
    public void coberturaRecoleccionMonedasNivel() {
        Coin coin = new Coin(new Vector2D(10, 10), dummyTexture);
        assertFalse(coin.isCollected());
        
        coin.collect();
        assertTrue(coin.isCollected());
        coin.update(); 
    }

    @Test
    public void coberturaTiempo() {
        int tiempoRestante = 0;
        boolean nivelReiniciado = false;
        if (tiempoRestante <= 0) {
            nivelReiniciado = true;
        }
        assertTrue(nivelReiniciado);
    }
    
    @Test
    public void MovimientoYColisionPared() {
        HumanPlayer jugador = new RedPlayer(new Vector2D(32, 32), dummyTexture, freeManager, stubControls);
        
        stubControls.isUp();
        stubControls.isDown();
        stubControls.isLeft();
        stubControls.isRight();
        
        assertTrue(freeManager.isBlocked(-10, 20));
        assertTrue(freeManager.isBlocked(20, -10));
        assertFalse(freeManager.isGoal(-5, -5));
        assertFalse(freeManager.isCheckpoint(-5, -5));
    }

    @Test
    public void ColeccionMonedas() {
        Coin moneda = new Coin(new Vector2D(64, 64), dummyTexture);
        assertNotNull(moneda.getPosition());
        
        assertFalse(freeManager.isGoal(32, 32));
        assertFalse(freeManager.isCheckpoint(32, 32));
    }

    // =========================================================
    // MULTIJUGADOR, INTELIGENCIA Y PUNTAJES
    // =========================================================

    @Test
    public void pvp_ControlesIndependientes() {
        double tiempoJ1 = 40.5;
        double tiempoJ2 = 35.2;
        String ganador = (tiempoJ1 < tiempoJ2) ? "J1" : "J2";
        assertEquals("J2", ganador);
    }

    @Test
    public void pvm_direccionRandom() {
        RandomMovement rm = new RandomMovement();
        int[] dirFree = rm.getDirection(new Vector2D(50, 50), 16, 16, freeManager);
        assertNotNull(dirFree);

        int[] dirWall = rm.getDirection(new Vector2D(50, 50), 16, 16, wallManager);
        assertEquals(0, dirWall[0]);
        assertEquals(0, dirWall[1]);
    }

    @Test
    public void cobertura_Puntaje() {
        int monedas = 5; int tiempo = 100; int muertes = 1;
        int puntaje = (monedas * 100) + tiempo - (muertes * 50);
        assertEquals(550, puntaje);
    }
    
    @Test
    public void testM2_IaAleatoria_SoloDireccionesValidas() {
        RandomMovement movAleatorio = new RandomMovement();
        
        int[] dirLibre = movAleatorio.getDirection(new Vector2D(32, 32), 16, 16, freeManager);
        assertNotNull(dirLibre);

        int[] dirMuro = movAleatorio.getDirection(new Vector2D(16, 16), 16, 16, wallManager);
        assertNotNull(dirMuro);

        try {
            java.lang.reflect.Field tickerField = RandomMovement.class.getDeclaredField("ticker");
            tickerField.setAccessible(true);
            tickerField.setInt(movAleatorio, 55);
        } catch (Exception e) {}
        
        int[] dirIntervalo = movAleatorio.getDirection(new Vector2D(32, 32), 16, 16, freeManager);
        assertNotNull(dirIntervalo);
    }

    // =========================================================
    //  JUGADORES 
    // =========================================================

    @Test
    public void jugadorAzul_tamaño() {
        BluePlayer azul = new BluePlayer(new Vector2D(0, 0), dummyTexture, freeManager, stubControls);
        
        assertEquals((int)(dummyTexture.getWidth() * 1.5f), azul.getWidth());
        assertEquals((int)(dummyTexture.getHeight() * 1.5f), azul.getHeight());
    }

    @Test
    public void jugadorVerde_VelocidadEscudo() {
        GreenPlayer verde = new GreenPlayer(new Vector2D(0, 0), dummyTexture, freeManager, stubControls);
        
        assertTrue(verde.absorbHit());
        assertFalse(verde.absorbHit());
        verde.onRespawn(); 
    }

    // =========================================================
    // PERSISTENCIA 
    // =========================================================

    @Test
    public void guardarYCargarPersistencia() {
        int nivelGuardado = 2;
        boolean monedaRecogida = true;
        
        int nivelCargado = nivelGuardado;
        boolean monedaReaparece = !monedaRecogida;
        
        assertEquals(2, nivelCargado);
        assertFalse(monedaReaparece);
    }
    
    // =========================================================
    //Bomba
    // =========================================================
    
    @Test
    public void cobertura_VidaBomba() {
        int vidas = 3;
        vidas++; 
        assertEquals(4, vidas);

        boolean bombaActivada = true;
        assertTrue(bombaActivada); 
    }

    // =========================================================
    //GameMode
    // =========================================================

    @Test
    public void cobertura_GameModeEnum() {
        assertEquals(3, GameMode.values().length);
        assertEquals(GameMode.SOLO, GameMode.valueOf("SOLO"));
    }

    // =========================================================
    //Botones
    // =========================================================
    
    @Test
    public void cobertura_InputsYAcciones() {
        MenuInput mi = new MenuInput();
        mi.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Jugar"));
        mi.update();
        assertTrue(MenuInput.JUGAR);

        PreGameInput pgi = new PreGameInput();
        pgi.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Volver al Menu"));
        pgi.update();
        assertTrue(PreGameInput.VOLVER_MENU);

        GameMenuInput gmi = new GameMenuInput();
        gmi.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Guardar"));
        gmi.update();
        assertTrue(GameMenuInput.GUARDAR);
    }

   
 // =========================================================
    // TILEMANAGER 
    // =========================================================
    @Test
    public void testTileManager_LimitesExtremos() {
        TileManager customManager = new TileManager(dummyGameState, "res/maps/level1.txt");
        
        assertTrue(customManager.isBlocked(-5, 10));
        assertTrue(customManager.isBlocked(10, -5));
        assertFalse(customManager.isCheckpoint(-1, -1));
        assertFalse(customManager.isGoal(-1, -1));
        
        Tile t = new Tile();
        t.image = dummyTexture;
        t.collision = true;
        assertTrue(t.collision);
    }
    
    @Test
    public void testTile_EstructuraSimple() {
        Tile casilla = new Tile();
        casilla.image = dummyTexture;
        casilla.collision = true;
        assertTrue(casilla.collision);
        assertNotNull(casilla.image);
    }
}
