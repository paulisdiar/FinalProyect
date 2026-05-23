package editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Editor visual de niveles. Permite pintar tiles en una grilla 33x25,
 * cargar y guardar archivos .txt compatibles con el juego.
 */
public class LevelEditor extends JFrame {

    private static final int COLS      = 33;
    private static final int ROWS      = 25;
    private static final int CELL_SIZE = 24;

    private final String[][] grid = new String[ROWS][COLS];

    private String selectedToken = "0";
    private String groupNumber   = "1";

    private GridPanel gridPanel;

    // ── Tokens disponibles ──────────────────────────────────────────────────
    private static final Map<String, String> TOKENS = new LinkedHashMap<>();
    static {
        TOKENS.put("0",  "Camino A");
        TOKENS.put("1",  "Camino B");
        TOKENS.put("3",  "Pared");
        TOKENS.put("2",  "Meta");
        TOKENS.put("4",  "Checkpoint");
        TOKENS.put("5",  "Moneda");
        TOKENS.put("6",  "Spawn J1");
        TOKENS.put("7",  "Spawn J2");
        TOKENS.put("8",  "Enemigo H→");
        TOKENS.put("9",  "Enemigo H←");
        TOKENS.put("A",  "Enemigo V↓");
        TOKENS.put("B",  "Enemigo V↑");
        TOKENS.put("C",  "Diagonal");
        TOKENS.put("D",  "Acelerado H");
        TOKENS.put("E",  "Acelerado V");
        TOKENS.put("F",  "Skin Rojo");
        TOKENS.put("G",  "Skin Azul");
        TOKENS.put("H",  "Skin Verde");
        TOKENS.put("I",  "Fuente Vida");
        TOKENS.put("J",  "Bomba");
        TOKENS.put("Cn", "Patrullero");
        TOKENS.put("Pn", "Waypoint");
    }

    // ── Sprites ─────────────────────────────────────────────────────────────
    private BufferedImage imgPath1, imgPath2, imgWall, imgGoal, imgEnemy, imgCoin;
    private BufferedImage imgCoinRed, imgCoinBlue, imgCoinGreen;

    public LevelEditor() {
        super("Editor de Niveles — The DOPO Hardest Game");
        loadAssets();
        initGrid();
        buildUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadAssets() {
        imgPath1     = loadImg("res/tiles/PathTile1.png");
        imgPath2     = loadImg("res/tiles/PathTile2.png");
        imgGoal      = loadImg("res/tiles/Finish-Checkpoint_Tile.png");
        imgEnemy     = loadImg("res/enemies/Enemy.png");
        imgCoin      = loadImg("res/coin/Coin.png");
        imgCoinRed   = loadImg("res/coin/red_coin.png");
        imgCoinBlue  = loadImg("res/coin/blue_coin.png");
        imgCoinGreen = loadImg("res/coin/green_coin.png");
    }

    private BufferedImage loadImg(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (Exception e) {
            return null;
        }
    }

    private void initGrid() {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                grid[r][c] = "3";
    }

    private void buildUI() {
        setLayout(new BorderLayout(4, 4));

        // ── Grilla central ──────────────────────────────────────────────────
        gridPanel = new GridPanel();
        gridPanel.setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
        JScrollPane scroll = new JScrollPane(gridPanel);
        add(scroll, BorderLayout.CENTER);

        // ── Paleta lateral ──────────────────────────────────────────────────
        JPanel palette = buildPalette();
        add(palette, BorderLayout.EAST);

        // ── Barra inferior ──────────────────────────────────────────────────
        JPanel bar = buildBar();
        add(bar, BorderLayout.SOUTH);
    }

    private JPanel buildPalette() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBorder(BorderFactory.createTitledBorder("Tiles"));
        outer.setPreferredSize(new Dimension(160, 0));

        JPanel grid = new JPanel(new GridLayout(0, 1, 2, 2));
        ButtonGroup group = new ButtonGroup();

        for (Map.Entry<String, String> entry : TOKENS.entrySet()) {
            String token = entry.getKey();
            String label = entry.getValue();

            JToggleButton btn = new JToggleButton();
            btn.setLayout(new BorderLayout(4, 0));
            btn.setFont(new Font("Monospaced", Font.BOLD, 11));

            JLabel iconLbl = new JLabel();
            iconLbl.setPreferredSize(new Dimension(20, 20));
            BufferedImage sprite = spriteFor(token);
            if (sprite != null) {
                Image scaled = sprite.getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                iconLbl.setIcon(new javax.swing.ImageIcon(scaled));
            } else {
                iconLbl.setBackground(colorFor(token));
                iconLbl.setOpaque(true);
            }

            btn.add(iconLbl, BorderLayout.WEST);
            btn.add(new JLabel(label), BorderLayout.CENTER);

            if (token.equals("0")) btn.setSelected(true);

            btn.addActionListener(e -> {
                if (token.equals("Cn") || token.equals("Pn")) {
                    String num = JOptionPane.showInputDialog(this, "Número de grupo:", groupNumber);
                    if (num != null && !num.trim().isEmpty()) groupNumber = num.trim();
                    selectedToken = (token.equals("Cn") ? "C" : "P") + groupNumber;
                } else {
                    selectedToken = token;
                }
            });

            group.add(btn);
            grid.add(btn);
        }

        outer.add(new JScrollPane(grid), BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildBar() {
        JPanel bar = new JPanel();

        JButton bNuevo  = new JButton("Nuevo");
        JButton bCargar = new JButton("Cargar");
        JButton bGuardar = new JButton("Guardar");
        JLabel  lGroup  = new JLabel("  Grupo nº:");
        JTextField tGroup = new JTextField(groupNumber, 3);

        tGroup.addActionListener(e -> groupNumber = tGroup.getText().trim());

        bNuevo.addActionListener(e -> {
            initGrid();
            gridPanel.repaint();
        });

        bCargar.addActionListener(e -> cargar());
        bGuardar.addActionListener(e -> guardar());

        bar.add(bNuevo);
        bar.add(bCargar);
        bar.add(bGuardar);
        bar.add(lGroup);
        bar.add(tGroup);
        return bar;
    }

    // ── Lógica de carga ─────────────────────────────────────────────────────

    private void cargar() {
        JFileChooser fc = new JFileChooser("res/maps");
        fc.setFileFilter(new FileNameExtensionFilter("Mapas (*.txt)", "txt"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try (BufferedReader br = new BufferedReader(new FileReader(fc.getSelectedFile()))) {
            for (int r = 0; r < ROWS; r++) {
                String line = br.readLine();
                if (line == null) break;
                String[] tokens = line.trim().split("\\s+");
                for (int c = 0; c < COLS && c < tokens.length; c++) {
                    grid[r][c] = tokens[c];
                }
            }
            gridPanel.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar: " + ex.getMessage());
        }
    }

    // ── Lógica de guardado ──────────────────────────────────────────────────

    private void guardar() {
        JFileChooser fc = new JFileChooser("res/maps");
        fc.setFileFilter(new FileNameExtensionFilter("Mapas (*.txt)", "txt"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = fc.getSelectedFile();
        if (!file.getName().endsWith(".txt")) file = new File(file.getAbsolutePath() + ".txt");
        try (PrintWriter pw = new PrintWriter(file, "UTF-8")) {
            for (int r = 0; r < ROWS; r++) {
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < COLS; c++) {
                    if (c > 0) sb.append(' ');
                    sb.append(grid[r][c]);
                }
                pw.println(sb);
            }
            JOptionPane.showMessageDialog(this, "Guardado en: " + file.getName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }

    // ── Panel de grilla ─────────────────────────────────────────────────────

    private class GridPanel extends JPanel {

        GridPanel() {
            MouseAdapter ma = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e)  { paint(e); }
                @Override
                public void mouseDragged(MouseEvent e)  { paint(e); }

                private void paint(MouseEvent e) {
                    int col = e.getX() / CELL_SIZE;
                    int row = e.getY() / CELL_SIZE;
                    if (row < 0 || row >= ROWS || col < 0 || col >= COLS) return;
                    if (SwingUtilities.isRightMouseButton(e)) {
                        grid[row][col] = "3";
                    } else {
                        grid[row][col] = selectedToken;
                    }
                    repaint();
                }
            };
            addMouseListener(ma);
            addMouseMotionListener(ma);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    int x = c * CELL_SIZE;
                    int y = r * CELL_SIZE;
                    drawCell(g, x, y, grid[r][c], r, c);
                }
            }
            // Líneas de grilla
            g.setColor(new Color(0, 0, 0, 40));
            for (int c = 0; c <= COLS; c++) g.drawLine(c * CELL_SIZE, 0, c * CELL_SIZE, ROWS * CELL_SIZE);
            for (int r = 0; r <= ROWS; r++) g.drawLine(0, r * CELL_SIZE, COLS * CELL_SIZE, r * CELL_SIZE);
        }

        private void drawCell(Graphics g, int x, int y, String token, int row, int col) {
            BufferedImage sprite = spriteFor(token);
            if (sprite != null) {
                g.drawImage(sprite, x, y, CELL_SIZE, CELL_SIZE, null);
            } else {
                Color bg = colorFor(token);
                g.setColor(bg);
                g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
            }
            // Etiqueta para tokens especiales
            if (needsLabel(token)) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Monospaced", Font.BOLD, 8));
                g.drawString(token.length() > 3 ? token.substring(0, 3) : token, x + 2, y + CELL_SIZE - 4);
            }
        }
    }

    // ── Helpers visuales ────────────────────────────────────────────────────

    private BufferedImage spriteFor(String token) {
        return switch (token) {
            case "0"  -> imgPath1;
            case "1"  -> imgPath2;
            case "2"  -> imgGoal;
            case "4", "6", "7" -> imgGoal;
            case "5"  -> imgCoin;
            case "F"  -> imgCoinRed;
            case "G"  -> imgCoinBlue;
            case "H"  -> imgCoinGreen;
            case "I"  -> imgCoinGreen;
            case "8", "9", "A", "B", "C", "D", "E" -> imgEnemy;
            default -> null;
        };
    }

    private Color colorFor(String token) {
        if (token.equals("3"))           return new Color(173, 181, 217);
        if (token.equals("J"))           return new Color(200, 30, 30);
        if (token.startsWith("C"))       return new Color(255, 165, 0);
        if (token.startsWith("P"))       return new Color(255, 200, 100);
        return new Color(100, 100, 100);
    }

    private boolean needsLabel(String token) {
        return token.equals("J") || token.equals("I") || token.equals("6")
            || token.equals("7") || token.startsWith("C") || token.startsWith("P");
    }

    // ── Entry point ─────────────────────────────────────────────────────────

    /**
     * Punto de entrada del editor de niveles.
     *
     * @param args argumentos de línea de comandos (no usados)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LevelEditor::new);
    }
}
