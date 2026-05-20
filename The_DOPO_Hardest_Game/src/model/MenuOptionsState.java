package model;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import Controller.Window;
import View.Assets;

public class MenuOptionsState {

	private Window window;
	private boolean cargar     = false;
	private boolean volverMenu = false;

	public MenuOptionsState(Window window) {
		this.window = window;
		show();
	}

	private void show() {
		window.getContentPane().removeAll();
		window.setLayout(new BorderLayout());

		JLabel titulo = new JLabel("Opciones", SwingConstants.CENTER);
		titulo.setFont(new Font("Arial", Font.BOLD, 36));
		window.add(titulo, BorderLayout.CENTER);

		JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 10));

		JButton bCargar = new JButton("Cargar Partida");
		JButton bVolver = new JButton("Volver al Menú");

		bCargar.addActionListener(e -> cargar     = true);
		bVolver.addActionListener(e -> volverMenu = true);

		panelBotones.add(bCargar);
		panelBotones.add(bVolver);

		window.add(panelBotones, BorderLayout.SOUTH);
		window.revalidate();
		window.repaint();
	}

	public void update() {
		if (cargar) {
			cargar = false;
			load();
		}
		if (volverMenu) {
			volverMenu = false;
			window.goToMenu();
		}
	}

	private void load() {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("save.dat"))) {
			SaveData data = (SaveData) ois.readObject();
			window.startGame(
				data.mode,
				Assets.playerColors[data.textureIndex1],
				Assets.playerColors[data.textureIndex2],
				data.type1,
				data.type2
			);
			JOptionPane.showMessageDialog(null, "Partida cargada.", "Cargar", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException | ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "No se encontró partida guardada.", "Cargar", JOptionPane.WARNING_MESSAGE);
		}
	}
}
