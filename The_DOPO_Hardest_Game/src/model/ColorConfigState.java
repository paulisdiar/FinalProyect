package model;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import controller.Window;
import view.Assets;

/**
 * Pantalla de selección de tipo y nombre de jugador antes de iniciar
 * una partida. Adapta la interfaz según el modo de juego (SOLO, PVP, PVM).
 */
public class ColorConfigState {

	private static final PlayerType[] TYPES = PlayerType.values();

	private static final String[] TYPE_LABELS = { "Rojo", "Azul", "Verde" };

	private static final String[] TYPE_TOOLTIPS = {
		"Velocidad normal, tamaño normal",
		"1.5x velocidad y tamaño",
		"Absorbe el primer golpe sin morir"
	};

	private Window window;
	private GameMode mode;

	private PlayerType selectedType1 = PlayerType.ROJO;
	private PlayerType selectedType2 = PlayerType.ROJO;
	private PlayerType machineType;
	private int machineColorIndex;

	private JTextField nameField1;
	private JTextField nameField2;

	/**
	 * Crea y muestra la pantalla de selección de tipo.
	 * En modo PVM genera automáticamente el tipo y color de la máquina.
	 *
	 * @param window ventana principal de la aplicación
	 * @param mode   modo de juego seleccionado
	 */
	public ColorConfigState(Window window, GameMode mode) {
		this.window = window;
		this.mode = mode;
		Random rnd = new Random();
		this.machineType = TYPES[rnd.nextInt(TYPES.length)];
		this.machineColorIndex = typeToColorIndex(machineType);
		show();
	}

	/**
	 * Convierte un {@link PlayerType} en el índice del array {@code Assets.playerColors}.
	 *
	 * @param type tipo de jugador
	 * @return índice de color correspondiente
	 */
	private int typeToColorIndex(PlayerType type) {
		return switch (type) {
			case ROJO  -> 0;
			case AZUL  -> 1;
			case VERDE -> 2;
		};
	}

	/**
	 * @param type tipo de jugador
	 * @return textura correspondiente a ese tipo desde {@link View.Assets#playerColors}
	 */
	private BufferedImage textureFor(PlayerType type) {
		return Assets.playerColors[typeToColorIndex(type)];
	}

	/**
	 * Construye y muestra los paneles de selección según el modo de juego activo.
	 */
	private void show() {
		window.getContentPane().removeAll();
		window.setLayout(new BorderLayout(10, 10));

		JLabel title = new JLabel("Selección de Tipo", SwingConstants.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 26));
		title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
		window.add(title, BorderLayout.NORTH);

		JPanel center = new JPanel();
		if (mode == GameMode.PVP) {
			center.setLayout(new GridLayout(1, 2, 30, 0));
			center.setBorder(BorderFactory.createEmptyBorder(10, 80, 10, 80));
			center.add(buildTypePanel("Jugador 1", 1));
			center.add(buildTypePanel("Jugador 2", 2));
		} else if (mode == GameMode.PVM) {
			center.setLayout(new GridLayout(1, 2, 30, 0));
			center.setBorder(BorderFactory.createEmptyBorder(10, 80, 10, 80));
			center.add(buildTypePanel("Jugador", 1));
			center.add(buildMachinePanel());
		} else {
			center.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
			center.add(buildTypePanel("Jugador", 1));
		}
		window.add(center, BorderLayout.CENTER);

		JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		south.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

		JButton bVolver = new JButton("Volver");
		bVolver.setFont(new Font("Arial", Font.PLAIN, 15));
		bVolver.addActionListener(e -> window.goToMenu());

		int totalLevels = LevelRegistry.countLevels(mode != GameMode.SOLO);
		String[] levelNames = new String[totalLevels];
		for (int i = 0; i < totalLevels; i++) {
			levelNames[i] = "Nivel " + (i + 1);
		}
		javax.swing.JComboBox<String> levelSelector = new javax.swing.JComboBox<>(levelNames);
		levelSelector.setFont(new Font("Arial", Font.PLAIN, 14));

		JButton bJugar = new JButton("¡Jugar!");
		bJugar.setFont(new Font("Arial", Font.BOLD, 16));
		bJugar.addActionListener(e -> {
			PlayerType pt2   = (mode == GameMode.PVM) ? machineType : selectedType2;
			BufferedImage t1 = textureFor(selectedType1);
			BufferedImage t2 = textureFor(pt2);
			String n1 = nameField1.getText().trim().isEmpty() ? "Jugador 1" : nameField1.getText().trim();
			String n2;
			if (mode == GameMode.PVM) {
				n2 = "Máquina";
			} else if (nameField2 != null && !nameField2.getText().trim().isEmpty()) {
				n2 = nameField2.getText().trim();
			} else {
				n2 = "Jugador 2";
			}
			int startLevel = levelSelector.getSelectedIndex() + 1;
			window.startGame(mode, t1, t2, selectedType1, pt2, n1, n2, startLevel);
		});

		south.add(bVolver);
		south.add(levelSelector);
		south.add(bJugar);
		window.add(south, BorderLayout.SOUTH);

		window.revalidate();
		window.repaint();
	}

	/**
	 * Construye el panel de selección de tipo para un jugador humano,
	 * incluyendo campo de nombre, vista previa y botones de tipo.
	 *
	 * @param label     etiqueta del panel (p. ej. "Jugador 1")
	 * @param playerNum número de jugador (1 o 2) para asociar el campo de nombre correcto
	 * @return panel Swing listo para añadir a la ventana
	 */
	private JPanel buildTypePanel(String label, int playerNum) {
		JPanel panel = new JPanel(new BorderLayout(5, 8));
		panel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), label,
			TitledBorder.CENTER, TitledBorder.TOP,
			new Font("Arial", Font.BOLD, 14)
		));

		JTextField nameField = new JTextField(playerNum == 1 ? "Jugador 1" : "Jugador 2", 12);
		nameField.setFont(new Font("Arial", Font.PLAIN, 13));
		nameField.setHorizontalAlignment(JTextField.CENTER);
		JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
		namePanel.add(new JLabel("Nombre: "));
		namePanel.add(nameField);
		if (playerNum == 1) {
			nameField1 = nameField;
		} else {
			nameField2 = nameField;
		}

		JLabel preview = new JLabel(scaledIcon(textureFor(PlayerType.ROJO), 64, 64), SwingConstants.CENTER);
		preview.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(namePanel, BorderLayout.NORTH);
		topPanel.add(preview, BorderLayout.CENTER);

		JPanel buttons = new JPanel(new GridLayout(1, 3, 8, 0));
		buttons.setBorder(BorderFactory.createEmptyBorder(4, 12, 16, 12));

		ButtonGroup group = new ButtonGroup();

		for (int i = 0; i < TYPES.length; i++) {
			final PlayerType pt = TYPES[i];
			final int ci = typeToColorIndex(pt);

			JToggleButton btn = new JToggleButton();
			btn.setLayout(new BorderLayout(2, 4));
			btn.setToolTipText(TYPE_TOOLTIPS[i]);
			btn.setFocusPainted(false);
			btn.setPreferredSize(new Dimension(80, 90));
			btn.setSelected(i == 0);

			JLabel icon = new JLabel(scaledIcon(Assets.playerColors[ci], 40, 40), SwingConstants.CENTER);
			JLabel name = new JLabel(TYPE_LABELS[i], SwingConstants.CENTER);
			name.setFont(new Font("Arial", Font.BOLD, 12));

			btn.add(icon, BorderLayout.CENTER);
			btn.add(name, BorderLayout.SOUTH);

			btn.addActionListener(e -> {
				if (playerNum == 1) selectedType1 = pt;
				else selectedType2 = pt;
				preview.setIcon(scaledIcon(Assets.playerColors[ci], 64, 64));
				preview.repaint();
			});

			group.add(btn);
			buttons.add(btn);
		}

		panel.add(topPanel, BorderLayout.CENTER);
		panel.add(buttons, BorderLayout.SOUTH);
		return panel;
	}

	/**
	 * Construye el panel informativo de la máquina en modo PVM,
	 * mostrando su tipo y color generados aleatoriamente.
	 *
	 * @return panel Swing con la vista previa de la máquina
	 */
	private JPanel buildMachinePanel() {
		JPanel panel = new JPanel(new BorderLayout(5, 12));
		panel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), "Máquina",
			TitledBorder.CENTER, TitledBorder.TOP,
			new Font("Arial", Font.BOLD, 14)
		));

		JLabel preview = new JLabel(scaledIcon(Assets.playerColors[machineColorIndex], 64, 64), SwingConstants.CENTER);
		preview.setBorder(BorderFactory.createEmptyBorder(12, 0, 8, 0));

		JLabel lbl = new JLabel(
			"<html><center>Tipo y color aleatorio<br>(generados automáticamente)</center></html>",
			SwingConstants.CENTER
		);
		lbl.setFont(new Font("Arial", Font.ITALIC, 13));
		lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

		panel.add(preview, BorderLayout.CENTER);
		panel.add(lbl, BorderLayout.SOUTH);
		return panel;
	}

	/**
	 * Crea un icono escalado suavemente a las dimensiones indicadas.
	 *
	 * @param img imagen fuente
	 * @param w   ancho deseado en píxeles
	 * @param h   alto deseado en píxeles
	 * @return icono escalado
	 */
	private ImageIcon scaledIcon(BufferedImage img, int w, int h) {
		return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}
}
