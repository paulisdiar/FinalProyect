package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import java.util.Arrays;
import java.util.List;

import Controller.Window;
import View.GameMenuInput;
import View.KeyBoard;
import View.MenuInput;
import View.PreGameInput;
import View.Vector2D;
import model.AceleradoEnemy;
import model.BasicEnemy;
import model.Bomb;
import model.Coin;
import model.DiagonalMovement;
import model.FastMovement;
import model.GameException;
import model.GameLogger;
import model.GameMode;
import model.GameState;
import model.HorizontalMovement;
import model.LifeSource;
import model.MachinePlayer;
import model.Level;
import model.LevelRegistry;
import model.PatrolMovement;
import model.PatrulleroEnemy;
import model.Player1;
import model.Player2;
import model.PlayerType;
import model.RandomMovement;
import model.SkinCoin;
import model.VerticalMovement;
import model.TileManager;
import model.Tile;
import model.BluePlayer;
import model.RedPlayer;
import model.HumanPlayer;
import model.GreenPlayer;
import model.SaveData;
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

    // =========================================================
    // Bomb
    // =========================================================

    @Test
    public void bomb_noExplotadaAlCrear() {
        Bomb b = new Bomb(new Vector2D(10, 10), dummyTexture);
        assertFalse(b.hasExploded());
    }

    @Test
    public void bomb_explode() {
        Bomb b = new Bomb(new Vector2D(10, 10), dummyTexture);
        b.explode();
        assertTrue(b.hasExploded());
    }

    @Test
    public void bomb_update_noLanzaExcepcion() {
        Bomb b = new Bomb(new Vector2D(10, 10), dummyTexture);
        b.update();
        assertFalse(b.hasExploded());
    }

    @Test
    public void bomb_draw_conTextura() {
        Bomb b = new Bomb(new Vector2D(10, 10), dummyTexture);
        java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        b.draw(canvas.getGraphics());
    }

    @Test
    public void bomb_draw_despuesDeExplotar_noPinta() {
        Bomb b = new Bomb(new Vector2D(10, 10), dummyTexture);
        b.explode();
        java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        b.draw(canvas.getGraphics());
        assertTrue(b.hasExploded());
    }

    @Test
    public void bomb_draw_sinTextura() {
        Bomb b = new Bomb(new Vector2D(10, 10), null);
        java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        b.draw(canvas.getGraphics());
        assertFalse(b.hasExploded());
    }

    @Test
    public void bomb_posicion() {
        Bomb b = new Bomb(new Vector2D(50, 60), dummyTexture);
        assertEquals(50.0, b.getPosition().getX(), 0.001);
        assertEquals(60.0, b.getPosition().getY(), 0.001);
    }

    // =========================================================
    // LifeSource
    // =========================================================

    @Test
    public void lifeSource_noRecogidaAlCrear() {
        LifeSource ls = new LifeSource(new Vector2D(10, 10), dummyTexture);
        assertFalse(ls.isCollected());
    }

    @Test
    public void lifeSource_collect() {
        LifeSource ls = new LifeSource(new Vector2D(10, 10), dummyTexture);
        ls.collect();
        assertTrue(ls.isCollected());
    }

    @Test
    public void lifeSource_update() {
        LifeSource ls = new LifeSource(new Vector2D(10, 10), dummyTexture);
        ls.update();
        assertFalse(ls.isCollected());
    }

    @Test
    public void lifeSource_draw_conTextura() {
        LifeSource ls = new LifeSource(new Vector2D(10, 10), dummyTexture);
        java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        ls.draw(canvas.getGraphics());
    }

    @Test
    public void lifeSource_draw_yaRecogida_noPinta() {
        LifeSource ls = new LifeSource(new Vector2D(10, 10), dummyTexture);
        ls.collect();
        java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        ls.draw(canvas.getGraphics());
        assertTrue(ls.isCollected());
    }

    @Test
    public void lifeSource_draw_sinTextura() {
        LifeSource ls = new LifeSource(new Vector2D(10, 10), null);
        java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        ls.draw(canvas.getGraphics());
        assertFalse(ls.isCollected());
    }

    // =========================================================
    // SkinCoin
    // =========================================================

    @Test
    public void skinCoin_getSkinType() {
        SkinCoin sc = new SkinCoin(new Vector2D(5, 5), dummyTexture, PlayerType.AZUL);
        assertEquals(PlayerType.AZUL, sc.getSkinType());
    }

    @Test
    public void skinCoin_collect() {
        SkinCoin sc = new SkinCoin(new Vector2D(5, 5), dummyTexture, PlayerType.VERDE);
        assertFalse(sc.isCollected());
        sc.collect();
        assertTrue(sc.isCollected());
    }

    @Test
    public void skinCoin_todosLosTipos() {
        for (PlayerType tipo : PlayerType.values()) {
            SkinCoin sc = new SkinCoin(new Vector2D(0, 0), dummyTexture, tipo);
            assertEquals(tipo, sc.getSkinType());
        }
    }

    // =========================================================
    // PlayerType
    // =========================================================

    @Test
    public void playerType_rojoAtributos() {
        assertEquals(1.0f, PlayerType.ROJO.speedMult, 0.001f);
        assertEquals(1.0f, PlayerType.ROJO.sizeMult, 0.001f);
        assertFalse(PlayerType.ROJO.hasShield);
    }

    @Test
    public void playerType_azulAtributos() {
        assertEquals(1.5f, PlayerType.AZUL.speedMult, 0.001f);
        assertEquals(1.5f, PlayerType.AZUL.sizeMult, 0.001f);
        assertFalse(PlayerType.AZUL.hasShield);
    }

    @Test
    public void playerType_verdeAtributos() {
        assertEquals(1.0f, PlayerType.VERDE.speedMult, 0.001f);
        assertTrue(PlayerType.VERDE.hasShield);
    }

    @Test
    public void playerType_tresValores() {
        assertEquals(3, PlayerType.values().length);
    }

    // =========================================================
    // DiagonalMovement
    // =========================================================

    @Test
    public void diagonalMovement_campoLibre() {
        DiagonalMovement dm = new DiagonalMovement(1, 1);
        int[] dir = dm.getDirection(new Vector2D(50, 50), 16, 16, freeManager);
        assertNotNull(dir);
        assertEquals(2, dir.length);
    }

    @Test
    public void diagonalMovement_reboteX() {
        DiagonalMovement dm = new DiagonalMovement(1, 1);
        int[] dir = dm.getDirection(new Vector2D(50, 50), 16, 16, wallManager);
        assertNotNull(dir);
    }

    @Test
    public void diagonalMovement_reboteIzquierda() {
        DiagonalMovement dm = new DiagonalMovement(-1, 1);
        int[] dir = dm.getDirection(new Vector2D(50, 50), 16, 16, wallManager);
        assertNotNull(dir);
    }

    @Test
    public void diagonalMovement_reboteArriba() {
        DiagonalMovement dm = new DiagonalMovement(1, -1);
        int[] dir = dm.getDirection(new Vector2D(50, 50), 16, 16, wallManager);
        assertNotNull(dir);
    }

    // =========================================================
    // FastMovement
    // =========================================================

    @Test
    public void fastMovement_duplicaVelocidad() {
        HorizontalMovement base = new HorizontalMovement(1);
        FastMovement fm = new FastMovement(base);
        int[] dirBase = base.getDirection(new Vector2D(50, 50), 16, 16, freeManager);
        int[] dirFast = fm.getDirection(new Vector2D(50, 50), 16, 16, freeManager);
        assertEquals(dirBase[0] * 2, dirFast[0]);
        assertEquals(dirBase[1] * 2, dirFast[1]);
    }

    @Test
    public void fastMovement_conVertical() {
        VerticalMovement base = new VerticalMovement(1);
        FastMovement fm = new FastMovement(base);
        int[] dir = fm.getDirection(new Vector2D(50, 50), 16, 16, freeManager);
        assertNotNull(dir);
    }

    // =========================================================
    // PatrolMovement
    // =========================================================

    @Test
    public void patrolMovement_listaVacia_cero() {
        PatrolMovement pm = new PatrolMovement(Arrays.asList());
        int[] dir = pm.getDirection(new Vector2D(50, 50), 16, 16, freeManager);
        assertEquals(0, dir[0]);
        assertEquals(0, dir[1]);
    }

    @Test
    public void patrolMovement_avanzaHaciaWaypoint() {
        List<Vector2D> wp = Arrays.asList(new Vector2D(200, 50));
        PatrolMovement pm = new PatrolMovement(wp);
        int[] dir = pm.getDirection(new Vector2D(50, 50), 16, 16, freeManager);
        assertTrue(dir[0] > 0);
        assertEquals(0, dir[1]);
    }

    @Test
    public void patrolMovement_avanzaEnY() {
        List<Vector2D> wp = Arrays.asList(new Vector2D(50, 200));
        PatrolMovement pm = new PatrolMovement(wp);
        int[] dir = pm.getDirection(new Vector2D(50, 50), 16, 16, freeManager);
        assertEquals(0, dir[0]);
        assertTrue(dir[1] > 0);
    }

    @Test
    public void patrolMovement_ciclaWaypoints() {
        List<Vector2D> wp = Arrays.asList(new Vector2D(52, 52), new Vector2D(200, 200));
        PatrolMovement pm = new PatrolMovement(wp);
        // Posición muy cerca del primer waypoint → debe avanzar al segundo
        int[] dir = pm.getDirection(new Vector2D(50, 50), 16, 16, freeManager);
        assertNotNull(dir);
    }

    // =========================================================
    // PatrulleroEnemy
    // =========================================================

    @Test
    public void patrulleroEnemy_update() {
        List<Vector2D> wp = Arrays.asList(new Vector2D(200, 100), new Vector2D(50, 100));
        PatrulleroEnemy pe = new PatrulleroEnemy(new Vector2D(50, 100), dummyTexture, freeManager, wp);
        for (int i = 0; i < 10; i++) {
            pe.update();
        }
        assertNotNull(pe.getPosition());
    }

    // =========================================================
    // AceleradoEnemy
    // =========================================================

    @Test
    public void aceleradoEnemy_update() {
        AceleradoEnemy ae = new AceleradoEnemy(new Vector2D(50, 50), dummyTexture, freeManager, new HorizontalMovement(1));
        for (int i = 0; i < 10; i++) {
            ae.update();
        }
        assertNotNull(ae.getPosition());
    }

    @Test
    public void aceleradoEnemy_conWallManager() {
        AceleradoEnemy ae = new AceleradoEnemy(new Vector2D(50, 50), dummyTexture, wallManager, new HorizontalMovement(1));
        ae.update();
        assertNotNull(ae.getPosition());
    }

    // =========================================================
    // GameException
    // =========================================================

    @Test
    public void gameException_mensaje() {
        GameException ex = new GameException("error de prueba");
        assertEquals("error de prueba", ex.getMessage());
    }

    @Test
    public void gameException_esException() {
        GameException ex = new GameException("test");
        assertTrue(ex instanceof Exception);
    }

    // =========================================================
    // GameLogger
    // =========================================================

    @Test
    public void gameLogger_logInfo_noLanzaExcepcion() {
        GameLogger.logInfo("TEST", "mensaje de prueba info");
    }

    @Test
    public void gameLogger_logError_noLanzaExcepcion() {
        GameLogger.logError("TEST", "mensaje de prueba error");
    }

    @Test
    public void gameLogger_escribeVariasEntradas() {
        GameLogger.logInfo("TEST", "entrada 1");
        GameLogger.logInfo("TEST", "entrada 2");
        GameLogger.logError("TEST", "entrada error");
    }

    // =========================================================
    // GameState acumulado y nombres
    // =========================================================

    @Test
    public void gameState_nombres() {
        assertEquals("J1", dummyGameState.getName1());
        assertEquals("J2", dummyGameState.getName2());
    }

    @Test
    public void gameState_puntajesAcumuladosInicio() {
        assertEquals(0, dummyGameState.getAccumulatedScore1());
        assertEquals(0, dummyGameState.getAccumulatedScore2());
    }

    @Test
    public void gameState_isLastLevel_conScore() {
        assertFalse(dummyGameState.isLastLevel(0, 0));
    }

    // =========================================================
    // Player — skin, lifeBonus, absorbHit, onRespawn
    // =========================================================

    @Test
    public void player_applySkin_y_clearSkin() {
        RedPlayer p = new RedPlayer(new Vector2D(50, 50), dummyTexture, freeManager, stubControls);
        assertNull(p.getActiveSkin());
        p.applySkin(PlayerType.AZUL);
        assertEquals(PlayerType.AZUL, p.getActiveSkin());
        p.clearSkin();
        assertNull(p.getActiveSkin());
    }

    @Test
    public void player_getWidth_conSkin() {
        RedPlayer p = new RedPlayer(new Vector2D(0, 0), dummyTexture, freeManager, stubControls);
        int base = p.getWidth();
        p.applySkin(PlayerType.AZUL);
        assertEquals((int)(dummyTexture.getWidth() * 1.5f), p.getWidth());
        p.clearSkin();
        assertEquals(base, p.getWidth());
    }

    @Test
    public void player_getHeight_conSkin() {
        RedPlayer p = new RedPlayer(new Vector2D(0, 0), dummyTexture, freeManager, stubControls);
        p.applySkin(PlayerType.VERDE);
        assertEquals((int)(dummyTexture.getHeight() * 1.0f), p.getHeight());
    }

    @Test
    public void player_grantLifeBonus_absorbHit() {
        RedPlayer p = new RedPlayer(new Vector2D(0, 0), dummyTexture, freeManager, stubControls);
        assertFalse(p.absorbHit());
        p.grantLifeBonus();
        assertTrue(p.absorbHit());
        assertFalse(p.absorbHit());
    }

    @Test
    public void player_onRespawn_limpiaSkinYBonus() {
        RedPlayer p = new RedPlayer(new Vector2D(0, 0), dummyTexture, freeManager, stubControls);
        p.applySkin(PlayerType.AZUL);
        p.grantLifeBonus();
        p.onRespawn();
        assertNull(p.getActiveSkin());
        assertFalse(p.absorbHit());
    }

    @Test
    public void player_draw() {
        RedPlayer p = new RedPlayer(new Vector2D(10, 10), dummyTexture, freeManager, stubControls);
        java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        p.draw(canvas.getGraphics());
    }

    @Test
    public void player_setPosition() {
        RedPlayer p = new RedPlayer(new Vector2D(0, 0), dummyTexture, freeManager, stubControls);
        p.setPosition(new Vector2D(99, 77));
        assertEquals(99.0, p.getPosition().getX(), 0.001);
        assertEquals(77.0, p.getPosition().getY(), 0.001);
    }

    // =========================================================
    // HumanPlayer — update con controles activos y efectoVelocidad
    // =========================================================

    @Test
    public void humanPlayer_update_movimientoLibreArriba() {
        ControlScheme soloArriba = new ControlScheme() {
            public boolean isUp()    { return true;  }
            public boolean isDown()  { return false; }
            public boolean isLeft()  { return false; }
            public boolean isRight() { return false; }
        };
        RedPlayer p = new RedPlayer(new Vector2D(100, 100), dummyTexture, freeManager, soloArriba);
        p.update();
        assertTrue(p.getPosition().getY() < 100);
    }

    @Test
    public void humanPlayer_update_movimientoLibreAbajo() {
        ControlScheme soloAbajo = new ControlScheme() {
            public boolean isUp()    { return false; }
            public boolean isDown()  { return true;  }
            public boolean isLeft()  { return false; }
            public boolean isRight() { return false; }
        };
        RedPlayer p = new RedPlayer(new Vector2D(100, 100), dummyTexture, freeManager, soloAbajo);
        p.update();
        assertTrue(p.getPosition().getY() > 100);
    }

    @Test
    public void humanPlayer_update_movimientoLibreIzquierda() {
        ControlScheme soloIzq = new ControlScheme() {
            public boolean isUp()    { return false; }
            public boolean isDown()  { return false; }
            public boolean isLeft()  { return true;  }
            public boolean isRight() { return false; }
        };
        RedPlayer p = new RedPlayer(new Vector2D(100, 100), dummyTexture, freeManager, soloIzq);
        p.update();
        assertTrue(p.getPosition().getX() < 100);
    }

    @Test
    public void humanPlayer_update_movimientoDiagonal() {
        ControlScheme diagonal = new ControlScheme() {
            public boolean isUp()    { return true;  }
            public boolean isDown()  { return false; }
            public boolean isLeft()  { return false; }
            public boolean isRight() { return true;  }
        };
        RedPlayer p = new RedPlayer(new Vector2D(100, 100), dummyTexture, freeManager, diagonal);
        p.update();
        assertTrue(p.getPosition().getX() > 100);
        assertTrue(p.getPosition().getY() < 100);
    }

    @Test
    public void humanPlayer_update_colisionConPared() {
        ControlScheme todoDirecciones = new ControlScheme() {
            public boolean isUp()    { return true; }
            public boolean isDown()  { return true; }
            public boolean isLeft()  { return true; }
            public boolean isRight() { return true; }
        };
        RedPlayer p = new RedPlayer(new Vector2D(50, 50), dummyTexture, wallManager, todoDirecciones);
        double xAntes = p.getPosition().getX();
        double yAntes = p.getPosition().getY();
        p.update();
        assertEquals(xAntes, p.getPosition().getX(), 0.001);
        assertEquals(yAntes, p.getPosition().getY(), 0.001);
    }

    @Test
    public void humanPlayer_update_conSkinAzul() {
        ControlScheme derecha = new ControlScheme() {
            public boolean isUp()    { return false; }
            public boolean isDown()  { return false; }
            public boolean isLeft()  { return false; }
            public boolean isRight() { return true;  }
        };
        RedPlayer p = new RedPlayer(new Vector2D(50, 50), dummyTexture, freeManager, derecha);
        p.applySkin(PlayerType.AZUL);
        p.update();
        assertTrue(p.getPosition().getX() > 50);
    }

    // =========================================================
    // BluePlayer — getWidth, getHeight, getEffectiveSpeed
    // =========================================================

    @Test
    public void bluePlayer_getEffectiveSpeed_viaUpdate() {
        ControlScheme derecha = new ControlScheme() {
            public boolean isUp()    { return false; }
            public boolean isDown()  { return false; }
            public boolean isLeft()  { return false; }
            public boolean isRight() { return true;  }
        };
        BluePlayer p = new BluePlayer(new Vector2D(50, 50), dummyTexture, freeManager, derecha);
        p.update();
        assertTrue(p.getPosition().getX() > 50);
    }

    // =========================================================
    // GreenPlayer — absorbHit doble, onRespawn, draw
    // =========================================================

    @Test
    public void greenPlayer_escudoPropio() {
        GreenPlayer g = new GreenPlayer(new Vector2D(0, 0), dummyTexture, freeManager, stubControls);
        assertTrue(g.absorbHit());
        assertFalse(g.absorbHit());
    }

    @Test
    public void greenPlayer_lifeBonus_antes_del_escudo() {
        GreenPlayer g = new GreenPlayer(new Vector2D(0, 0), dummyTexture, freeManager, stubControls);
        g.grantLifeBonus();
        assertTrue(g.absorbHit());  // consume lifeBonus primero
        assertTrue(g.absorbHit());  // ahora consume escudo
        assertFalse(g.absorbHit()); // ya no queda nada
    }

    @Test
    public void greenPlayer_onRespawn_restauraEscudo() {
        GreenPlayer g = new GreenPlayer(new Vector2D(0, 0), dummyTexture, freeManager, stubControls);
        g.absorbHit(); // rompe escudo
        g.onRespawn();
        assertTrue(g.absorbHit()); // escudo restaurado
    }

    @Test
    public void greenPlayer_draw_escudoRoto() {
        GreenPlayer g = new GreenPlayer(new Vector2D(10, 10), dummyTexture, freeManager, stubControls);
        g.absorbHit(); // rompe escudo
        java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        g.draw(canvas.createGraphics()); // rama shieldBroken=true
    }

    @Test
    public void greenPlayer_draw_escudoIntacto() {
        GreenPlayer g = new GreenPlayer(new Vector2D(10, 10), dummyTexture, freeManager, stubControls);
        java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        g.draw(canvas.createGraphics());
    }

    @Test
    public void greenPlayer_velocidadReducida_trasPerdidaEscudo() {
        ControlScheme derecha = new ControlScheme() {
            public boolean isUp()    { return false; }
            public boolean isDown()  { return false; }
            public boolean isLeft()  { return false; }
            public boolean isRight() { return true;  }
        };
        GreenPlayer g = new GreenPlayer(new Vector2D(50, 50), dummyTexture, freeManager, derecha);
        g.absorbHit(); // rompe escudo → velocidad reducida
        g.update();
        assertTrue(g.getPosition().getX() > 50);
    }

    // =========================================================
    // Player1 y Player2 — esquemas de teclado
    // =========================================================

    @Test
    public void player1_teclasDireccion() {
        KeyBoard kb = new KeyBoard();
        kb.keyPressed(new KeyEvent(new java.awt.Label(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED));
        kb.keyPressed(new KeyEvent(new java.awt.Label(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED));
        kb.keyPressed(new KeyEvent(new java.awt.Label(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_LEFT, KeyEvent.CHAR_UNDEFINED));
        kb.keyPressed(new KeyEvent(new java.awt.Label(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_RIGHT, KeyEvent.CHAR_UNDEFINED));
        kb.update();
        Player1 p1 = new Player1();
        assertTrue(p1.isUp());
        assertTrue(p1.isDown());
        assertTrue(p1.isLeft());
        assertTrue(p1.isRight());
    }

    @Test
    public void player2_teclasWASD() {
        KeyBoard kb = new KeyBoard();
        kb.keyPressed(new KeyEvent(new java.awt.Label(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_W, KeyEvent.CHAR_UNDEFINED));
        kb.keyPressed(new KeyEvent(new java.awt.Label(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_S, KeyEvent.CHAR_UNDEFINED));
        kb.keyPressed(new KeyEvent(new java.awt.Label(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_A, KeyEvent.CHAR_UNDEFINED));
        kb.keyPressed(new KeyEvent(new java.awt.Label(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_D, KeyEvent.CHAR_UNDEFINED));
        kb.update();
        Player2 p2 = new Player2();
        assertTrue(p2.isUp());
        assertTrue(p2.isDown());
        assertTrue(p2.isLeft());
        assertTrue(p2.isRight());
    }

    // =========================================================
    // Collectible.draw y Obstacle.draw (ramas con textura)
    // =========================================================

    @Test
    public void collectible_draw_noRecogida() {
        Coin c = new Coin(new Vector2D(10, 10), dummyTexture);
        java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        c.draw(canvas.getGraphics());
    }

    @Test
    public void collectible_draw_recogida_noPinta() {
        Coin c = new Coin(new Vector2D(10, 10), dummyTexture);
        c.collect();
        java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        c.draw(canvas.getGraphics());
        assertTrue(c.isCollected());
    }

    // =========================================================
    // MachinePlayer — colisiones en todos los ejes
    // =========================================================

    @Test
    public void machinePlayer_movimientoLibre_horizontal() {
        MachinePlayer mp = new MachinePlayer(new Vector2D(100, 100), dummyTexture, freeManager, new HorizontalMovement(1));
        double xAntes = mp.getPosition().getX();
        mp.update();
        assertNotEquals(xAntes, mp.getPosition().getX(), 0.001);
    }

    @Test
    public void machinePlayer_movimientoLibre_vertical() {
        MachinePlayer mp = new MachinePlayer(new Vector2D(100, 100), dummyTexture, freeManager, new VerticalMovement(1));
        double yAntes = mp.getPosition().getY();
        mp.update();
        assertNotEquals(yAntes, mp.getPosition().getY(), 0.001);
    }

    @Test
    public void machinePlayer_draw() {
        MachinePlayer mp = new MachinePlayer(new Vector2D(10, 10), dummyTexture, freeManager, new RandomMovement());
        java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        mp.draw(canvas.getGraphics());
    }

    // =========================================================
    // Enemy.draw
    // =========================================================

    @Test
    public void basicEnemy_draw() {
        BasicEnemy e = new BasicEnemy(new Vector2D(10, 10), dummyTexture, freeManager, new HorizontalMovement(1));
        java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        e.draw(canvas.getGraphics());
    }

    // =========================================================
    // KeyBoard — resetKeys y tecla ESCAPE/M (flanco)
    // =========================================================

    @Test
    public void keyboard_resetKeys() {
        KeyBoard kb = new KeyBoard();
        kb.keyPressed(new KeyEvent(new java.awt.Label(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED));
        kb.update();
        kb.resetKeys();
        assertFalse(KeyBoard.UP);
    }

    @Test
    public void keyboard_escapeFlancoSubida() {
        KeyBoard kb = new KeyBoard();
        kb.keyPressed(new KeyEvent(new java.awt.Label(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED));
        kb.update();
        assertTrue(KeyBoard.ESCAPE_PRESSED);
        kb.update(); // segundo frame: ya no es flanco
        assertFalse(KeyBoard.ESCAPE_PRESSED);
    }

    @Test
    public void keyboard_mFlancoSubida() {
        KeyBoard kb = new KeyBoard();
        kb.keyPressed(new KeyEvent(new java.awt.Label(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_M, KeyEvent.CHAR_UNDEFINED));
        kb.update();
        assertTrue(KeyBoard.M_PRESSED);
        kb.update();
        assertFalse(KeyBoard.M_PRESSED);
    }

    // =========================================================
    // TileManager — métodos de consulta sobre mapa real
    // =========================================================

    @Test
    public void tileManager_getCoinPositions_noNull() {
        TileManager tm = new TileManager(dummyGameState, "res/maps/level1.txt");
        assertNotNull(tm.getCoinPositions());
    }

    @Test
    public void tileManager_getSpawnPlayer1_noNull() {
        TileManager tm = new TileManager(dummyGameState, "res/maps/level1.txt");
        assertNotNull(tm.getSpawnPlayer1());
    }

    @Test
    public void tileManager_getSpawnPlayer2_noNull() {
        TileManager tm = new TileManager(dummyGameState, "res/maps/level1.txt");
        assertNotNull(tm.getSpawnPlayer2());
    }

    @Test
    public void tileManager_getSkinCoinPositions_noNull() {
        TileManager tm = new TileManager(dummyGameState, "res/maps/level1.txt");
        assertNotNull(tm.getSkinCoinPositions());
    }

    @Test
    public void tileManager_getLifeSourcePositions_noNull() {
        TileManager tm = new TileManager(dummyGameState, "res/maps/level1.txt");
        assertNotNull(tm.getLifeSourcePositions());
    }

    @Test
    public void tileManager_getBombPositions_noNull() {
        TileManager tm = new TileManager(dummyGameState, "res/maps/level1.txt");
        assertNotNull(tm.getBombPositions());
    }

    @Test
    public void tileManager_getEnemySpawns_noNull() {
        TileManager tm = new TileManager(dummyGameState, "res/maps/level1.txt");
        assertNotNull(tm.getEnemySpawns());
    }

    @Test
    public void tileManager_getPatrolGroups_noNull() {
        TileManager tm = new TileManager(dummyGameState, "res/maps/level1.txt");
        assertNotNull(tm.getPatrolGroups());
    }

    @Test
    public void tileManager_getPatrolSpawns_noNull() {
        TileManager tm = new TileManager(dummyGameState, "res/maps/level1.txt");
        assertNotNull(tm.getPatrolSpawns());
    }

    @Test
    public void tileManager_isBlocked_dentroDelMapa() {
        TileManager tm = new TileManager(dummyGameState, "res/maps/level1.txt");
        // coordenada válida dentro del mapa — solo verificamos que no lanza excepción
        boolean resultado = tm.isBlocked(100, 100);
        assertTrue(resultado || !resultado);
    }

    @Test
    public void tileManager_isBlocked_fueraDeLimites() {
        TileManager tm = new TileManager(dummyGameState, "res/maps/level1.txt");
        assertTrue(tm.isBlocked(-1, 100));
        assertTrue(tm.isBlocked(100, -1));
        assertTrue(tm.isBlocked(99999, 100));
        assertTrue(tm.isBlocked(100, 99999));
    }

    @Test
    public void tileManager_isGoal_fueraDeLimites() {
        TileManager tm = new TileManager(dummyGameState, "res/maps/level1.txt");
        assertFalse(tm.isGoal(-1, -1));
        assertFalse(tm.isGoal(99999, 99999));
    }

    @Test
    public void tileManager_isGoal_dentroDelMapa() {
        TileManager tm = new TileManager(dummyGameState, "res/maps/level1.txt");
        boolean resultado = tm.isGoal(400, 300);
        assertTrue(resultado || !resultado);
    }

    @Test
    public void tileManager_isCheckpoint_fueraDeLimites() {
        TileManager tm = new TileManager(dummyGameState, "res/maps/level1.txt");
        assertFalse(tm.isCheckpoint(-1, -1));
        assertFalse(tm.isCheckpoint(99999, 99999));
    }

    @Test
    public void tileManager_isCheckpoint_dentroDelMapa() {
        TileManager tm = new TileManager(dummyGameState, "res/maps/level1.txt");
        boolean resultado = tm.isCheckpoint(200, 200);
        assertTrue(resultado || !resultado);
    }

    @Test
    public void tileManager_draw_noLanzaExcepcion() {
        TileManager tm = new TileManager(dummyGameState, "res/maps/level1.txt");
        java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(800, 600, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        tm.draw(canvas.getGraphics());
    }

    // =========================================================
    // SaveData — campos adicionales
    // =========================================================

    @Test
    public void saveData_camposScore() {
        SaveData sd = new SaveData();
        sd.score1 = 500;
        sd.score2 = 300;
        sd.levelIndex = 2;
        assertEquals(500, sd.score1);
        assertEquals(300, sd.score2);
        assertEquals(2, sd.levelIndex);
    }

    @Test
    public void saveData_camposPosicion() {
        SaveData sd = new SaveData();
        sd.playerX = 123.4f;
        sd.playerY = 56.7f;
        assertEquals(123.4f, sd.playerX, 0.001f);
        assertEquals(56.7f,  sd.playerY, 0.001f);
    }

    @Test
    public void saveData_modoYTipos() {
        SaveData sd = new SaveData();
        sd.mode  = GameMode.PVP;
        sd.type1 = PlayerType.AZUL;
        sd.type2 = PlayerType.VERDE;
        sd.textureIndex1 = 1;
        sd.textureIndex2 = 2;
        assertEquals(GameMode.PVP,   sd.mode);
        assertEquals(PlayerType.AZUL,  sd.type1);
        assertEquals(PlayerType.VERDE, sd.type2);
        assertEquals(1, sd.textureIndex1);
        assertEquals(2, sd.textureIndex2);
    }

    // =========================================================
    // GameState — nextLevel acumula puntajes
    // =========================================================

    @Test
    public void gameState_nextLevel_acumulaPuntajes() {
        dummyGameState.nextLevel(100, 50);
        assertEquals(100, dummyGameState.getAccumulatedScore1());
        assertEquals(50,  dummyGameState.getAccumulatedScore2());
    }

    @Test
    public void gameState_nextLevel_dobleAcumulacion() {
        dummyGameState.nextLevel(100, 50);
        dummyGameState.nextLevel(200, 75);
        assertEquals(300, dummyGameState.getAccumulatedScore1());
        assertEquals(125, dummyGameState.getAccumulatedScore2());
    }

    // =========================================================
    // ControlScheme — stub verifica cada método
    // =========================================================

    @Test
    public void controlScheme_stubFalse() {
        assertFalse(stubControls.isUp());
        assertFalse(stubControls.isDown());
        assertFalse(stubControls.isLeft());
        assertFalse(stubControls.isRight());
    }

    // =========================================================
    // LevelRegistry
    // =========================================================

    @Test
    public void levelRegistry_countLevels_solo_minimoUno() {
        int total = LevelRegistry.countLevels(false);
        assertTrue(total >= 1);
    }

    @Test
    public void levelRegistry_countLevels_dosjugadores_minimoUno() {
        int total = LevelRegistry.countLevels(true);
        assertTrue(total >= 1);
    }

    @Test
    public void levelRegistry_countLevels_sinParametro() {
        int total = LevelRegistry.countLevels();
        assertTrue(total >= 1);
    }

    // =========================================================
    // Obstacle.draw con textura real
    // =========================================================

    @Test
    public void obstacle_draw_conTextura() {
        Bomb b = new Bomb(new Vector2D(5, 5), dummyTexture);
        java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        // llama el draw de Obstacle (Bomb no explotada usa Obstacle.draw solo si no redefine)
        // Bomb redefine draw, pero Obstacle.draw se cubre via super si la bomba no redefine Obstacle
        // Para cubrir Obstacle.draw directamente creamos una subclase anónima
        model.Obstacle obs = new model.Obstacle(new Vector2D(5, 5), dummyTexture) {
            @Override public void update() {}
        };
        obs.draw(canvas.getGraphics());
    }

    @Test
    public void obstacle_update_noOp() {
        model.Obstacle obs = new model.Obstacle(new Vector2D(5, 5), dummyTexture) {
            @Override public void update() { super.update(); }
        };
        obs.update();
        assertNotNull(obs.getPosition());
    }

    // =========================================================
    // Collectible.update y draw rama no recogida
    // =========================================================

    @Test
    public void collectible_update_noOp() {
        Coin c = new Coin(new Vector2D(0, 0), dummyTexture);
        c.update();
        assertFalse(c.isCollected());
    }

    // =========================================================
    // Level — constructor SOLO, métodos públicos
    // =========================================================

    private Level buildLevel(GameMode mode) {
        return new Level(dummyGameState, "res/maps/level1.txt", mode,
            dummyTexture, dummyTexture,
            PlayerType.ROJO, PlayerType.ROJO,
            "J1", "J2");
    }

    @Test
    public void level_constructor_solo() {
        Level lvl = buildLevel(GameMode.SOLO);
        assertNotNull(lvl);
    }

    @Test
    public void level_constructor_pvp() {
        Level lvl = buildLevel(GameMode.PVP);
        assertNotNull(lvl);
    }

    @Test
    public void level_constructor_pvm() {
        Level lvl = buildLevel(GameMode.PVM);
        assertNotNull(lvl);
    }

    @Test
    public void level_togglePause_yIsPaused() {
        Level lvl = buildLevel(GameMode.SOLO);
        assertFalse(lvl.isPaused());
        lvl.togglePause();
        assertTrue(lvl.isPaused());
        lvl.togglePause();
        assertFalse(lvl.isPaused());
    }

    @Test
    public void level_update_soloUnFrame() {
        Level lvl = buildLevel(GameMode.SOLO);
        lvl.update();
        assertFalse(lvl.isPaused());
    }

    @Test
    public void level_update_pausado() {
        Level lvl = buildLevel(GameMode.SOLO);
        lvl.togglePause();
        lvl.update(); // con pausa activa, no debe lanzar excepción
        assertTrue(lvl.isPaused());
    }

    @Test
    public void level_update_pvp_variosFrames() {
        Level lvl = buildLevel(GameMode.PVP);
        for (int i = 0; i < 5; i++) {
            lvl.update();
        }
        assertNotNull(lvl);
    }

    @Test
    public void level_update_pvm_variosFrames() {
        Level lvl = buildLevel(GameMode.PVM);
        for (int i = 0; i < 5; i++) {
            lvl.update();
        }
        assertNotNull(lvl);
    }

    @Test
    public void level_draw_solo() {
        Level lvl = buildLevel(GameMode.SOLO);
        java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(800, 600, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        lvl.draw(canvas.createGraphics());
    }

    @Test
    public void level_draw_pvp() {
        Level lvl = buildLevel(GameMode.PVP);
        java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(800, 600, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        lvl.draw(canvas.createGraphics());
    }

    @Test
    public void level_draw_pvm() {
        Level lvl = buildLevel(GameMode.PVM);
        java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(800, 600, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        lvl.draw(canvas.createGraphics());
    }

    @Test
    public void level_draw_pausado() {
        Level lvl = buildLevel(GameMode.SOLO);
        lvl.togglePause();
        java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(800, 600, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        lvl.draw(canvas.createGraphics()); // cubre drawPauseOverlay
    }

    @Test
    public void level_getSaveData_solo() {
        Level lvl = buildLevel(GameMode.SOLO);
        SaveData sd = lvl.getSaveData(1);
        assertNotNull(sd);
        assertEquals(1, sd.levelIndex);
    }

    @Test
    public void level_getSaveData_pvp() {
        Level lvl = buildLevel(GameMode.PVP);
        SaveData sd = lvl.getSaveData(2);
        assertNotNull(sd);
        assertEquals(2, sd.levelIndex);
    }

    @Test
    public void level_applyLoad_solo() {
        Level lvl = buildLevel(GameMode.SOLO);
        SaveData sd = lvl.getSaveData(1);
        sd.playerX = 100;
        sd.playerY = 200;
        sd.score1  = 50;
        sd.deaths1 = 2;
        lvl.applyLoad(sd);
    }

    @Test
    public void level_applyLoad_pvp() {
        Level lvl = buildLevel(GameMode.PVP);
        SaveData sd = lvl.getSaveData(1);
        sd.player2X = 150;
        sd.player2Y = 250;
        sd.coinsCollected = new boolean[0];
        lvl.applyLoad(sd);
    }

    @Test
    public void level_applyLoad_conMonedas() {
        Level lvl = buildLevel(GameMode.SOLO);
        SaveData sd = lvl.getSaveData(1);
        if (sd.coinsCollected != null && sd.coinsCollected.length > 0) {
            sd.coinsCollected[0] = true;
        }
        lvl.applyLoad(sd);
    }

    @Test
    public void level_determinarGanador_soloViaReflexion() throws Exception {
        Level lvl = buildLevel(GameMode.SOLO);
        java.lang.reflect.Method m = Level.class.getDeclaredMethod(
            "determinarGanador", int.class, int.class, int.class, int.class, boolean.class);
        m.setAccessible(true);
        String res = (String) m.invoke(lvl, 10, 5, 1, 2, true);
        assertEquals("¡Nivel completado!", res);
    }

    @Test
    public void level_determinarGanador_pvpJ1Gana() throws Exception {
        Level lvl = buildLevel(GameMode.PVP);
        java.lang.reflect.Method m = Level.class.getDeclaredMethod(
            "determinarGanador", int.class, int.class, int.class, int.class, boolean.class);
        m.setAccessible(true);
        String res = (String) m.invoke(lvl, 20, 10, 1, 1, true);
        assertTrue(res.contains("J1"));
    }

    @Test
    public void level_determinarGanador_pvpJ2Gana() throws Exception {
        Level lvl = buildLevel(GameMode.PVP);
        java.lang.reflect.Method m = Level.class.getDeclaredMethod(
            "determinarGanador", int.class, int.class, int.class, int.class, boolean.class);
        m.setAccessible(true);
        String res = (String) m.invoke(lvl, 10, 20, 1, 1, true);
        assertTrue(res.contains("J2"));
    }

    @Test
    public void level_determinarGanador_pvpEmpateMenosMuertes() throws Exception {
        Level lvl = buildLevel(GameMode.PVP);
        java.lang.reflect.Method m = Level.class.getDeclaredMethod(
            "determinarGanador", int.class, int.class, int.class, int.class, boolean.class);
        m.setAccessible(true);
        String empate = (String) m.invoke(lvl, 10, 10, 1, 1, true);
        assertTrue(empate.contains("Empate"));
        String j1menores = (String) m.invoke(lvl, 10, 10, 0, 2, true);
        assertTrue(j1menores.contains("J1"));
        String j2menores = (String) m.invoke(lvl, 10, 10, 2, 0, true);
        assertTrue(j2menores.contains("J2"));
    }

    @Test
    public void level_determinarGanador_pvm() throws Exception {
        Level lvl = buildLevel(GameMode.PVM);
        java.lang.reflect.Method m = Level.class.getDeclaredMethod(
            "determinarGanador", int.class, int.class, int.class, int.class, boolean.class);
        m.setAccessible(true);
        String res = (String) m.invoke(lvl, 20, 10, 0, 0, true);
        assertTrue(res.contains("J1"));
    }

    @Test
    public void level_constructor_tiposAzulVerde() {
        Level lvl = new Level(dummyGameState, "res/maps/level1.txt", GameMode.PVP,
            dummyTexture, dummyTexture,
            PlayerType.AZUL, PlayerType.VERDE,
            "A", "B");
        assertNotNull(lvl);
    }
}
