package model;

import java.awt.BorderLayout;

import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.nio.file.Files;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import controller.Window;

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

	private void openSaveFile(File archivo) throws GameException {
		try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(archivo.toPath()))) {
		    SaveData data = (SaveData) ois.readObject();
		    window.startGameFromSave(data);
		} catch (IOException | ClassNotFoundException e) {
		    throw new GameException("Error al abrir el archivo");
		}
	}

	private void load() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("Archivos de partida (*.dat)", "dat"));
		int result = chooser.showOpenDialog(window);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (!file.getName().toLowerCase().endsWith(".dat")) {
				JOptionPane.showMessageDialog(window, "El archivo debe tener extensión .dat", "Archivo inválido", JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				openSaveFile(file);
				JOptionPane.showMessageDialog(window, "Partida cargada.", "Cargar", JOptionPane.INFORMATION_MESSAGE);
			} catch (GameException e) {
				JOptionPane.showMessageDialog(window, e.getMessage(), "Error al cargar", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
}
