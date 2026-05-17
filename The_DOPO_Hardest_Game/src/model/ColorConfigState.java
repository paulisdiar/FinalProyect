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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import Controller.Window;
import View.Assets;

public class ColorConfigState {

	private static final String[] NAMES = {
		"Rojo", "Azul", "Verde", "Amarillo",
		"Naranja", "Morado", "Cian", "Rosa"
	};

	private Window window;
	private GameMode mode;
	private int selectedIndex1 = 0;
	private int selectedIndex2 = 1;
	private int machineIndex;

	public ColorConfigState(Window window, GameMode mode) {
		this.window = window;
		this.mode = mode;
		this.machineIndex = new Random().nextInt(Assets.playerColors.length);
		show();
	}

	private void show() {
		window.getContentPane().removeAll();
		window.setLayout(new BorderLayout(10, 10));

		JLabel title = new JLabel("Selección de Colores", SwingConstants.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 26));
		title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
		window.add(title, BorderLayout.NORTH);

		JPanel center = new JPanel();
		if (mode == GameMode.PVP) {
			center.setLayout(new GridLayout(1, 2, 30, 0));
			center.setBorder(BorderFactory.createEmptyBorder(10, 80, 10, 80));
			center.add(buildPicker("Jugador 1", 1));
			center.add(buildPicker("Jugador 2", 2));
		} else if (mode == GameMode.PVM) {
			center.setLayout(new GridLayout(1, 2, 30, 0));
			center.setBorder(BorderFactory.createEmptyBorder(10, 80, 10, 80));
			center.add(buildPicker("Jugador", 1));
			center.add(buildMachinePanel());
		} else {
			center.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
			center.add(buildPicker("Jugador", 1));
		}
		window.add(center, BorderLayout.CENTER);

		JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		south.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

		JButton bVolver = new JButton("Volver");
		bVolver.setFont(new Font("Arial", Font.PLAIN, 15));
		bVolver.addActionListener(e -> window.goToMenu());

		JButton bJugar = new JButton("¡Jugar!");
		bJugar.setFont(new Font("Arial", Font.BOLD, 16));
		bJugar.addActionListener(e -> {
			BufferedImage t2 = (mode == GameMode.PVM)
				? Assets.playerColors[machineIndex]
				: Assets.playerColors[selectedIndex2];
			window.startGame(mode, Assets.playerColors[selectedIndex1], t2);
		});

		south.add(bVolver);
		south.add(bJugar);
		window.add(south, BorderLayout.SOUTH);

		window.revalidate();
		window.repaint();
	}

	private JPanel buildPicker(String label, int playerNum) {
		JPanel panel = new JPanel(new BorderLayout(5, 8));
		panel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), label,
			TitledBorder.CENTER, TitledBorder.TOP,
			new Font("Arial", Font.BOLD, 14)
		));

		int idx = (playerNum == 1) ? selectedIndex1 : selectedIndex2;
		JLabel preview = new JLabel(scaledIcon(Assets.playerColors[idx], 48, 48), SwingConstants.CENTER);
		preview.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));

		JPanel swatches = new JPanel(new GridLayout(2, 4, 6, 6));
		swatches.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));

		for (int i = 0; i < Assets.playerColors.length; i++) {
			final int fi = i;
			JButton btn = new JButton(scaledIcon(Assets.playerColors[i], 36, 36));
			btn.setToolTipText(NAMES[i]);
			btn.setPreferredSize(new Dimension(44, 44));
			btn.setFocusPainted(false);
			btn.addActionListener(e -> {
				if (playerNum == 1) selectedIndex1 = fi;
				else selectedIndex2 = fi;
				preview.setIcon(scaledIcon(Assets.playerColors[fi], 48, 48));
				preview.repaint();
			});
			swatches.add(btn);
		}

		panel.add(preview, BorderLayout.NORTH);
		panel.add(swatches, BorderLayout.CENTER);
		return panel;
	}

	private JPanel buildMachinePanel() {
		JPanel panel = new JPanel(new BorderLayout(5, 8));
		panel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), "Máquina",
			TitledBorder.CENTER, TitledBorder.TOP,
			new Font("Arial", Font.BOLD, 14)
		));

		JLabel preview = new JLabel(scaledIcon(Assets.playerColors[machineIndex], 48, 48), SwingConstants.CENTER);
		preview.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));

		JLabel lbl = new JLabel("<html><center>Color aleatorio<br>(generado automáticamente)</center></html>", SwingConstants.CENTER);
		lbl.setFont(new Font("Arial", Font.ITALIC, 13));

		panel.add(preview, BorderLayout.NORTH);
		panel.add(lbl, BorderLayout.CENTER);
		return panel;
	}

	private ImageIcon scaledIcon(BufferedImage img, int w, int h) {
		return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}
}
