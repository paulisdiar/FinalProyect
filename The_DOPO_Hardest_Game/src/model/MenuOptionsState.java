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

/**
 * Estado del menú de opciones accesible desde el menú principal.
 * Ofrece cargar una partida guardada o volver al menú.
 */
public class MenuOptionsState {

	private Window window;
	private boolean cargar     = false;
	private boolean volverMenu = false;

	/**
	 * Crea y muestra la pantalla de opciones del menú principal.
	 *
	 * @param window ventana principal de la aplicación
	 */
	public MenuOptionsState(Window window) {
		this.window = window;
		show();
	}

	/**
	 * Construye y muestra los componentes Swing del menú de opciones.
	 */
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

	/**
	 * Procesa los flags de botón y ejecuta la acción correspondiente.
	 * Debe llamarse una vez por frame desde el hilo del bucle de juego.
	 */
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

	/**
	 * Deserializa un archivo de partida y delega la carga a la ventana.
	 *
	 * @param archivo archivo .dat a abrir
	 * @throws GameException si el archivo no puede leerse o no es un SaveData válido
	 */
	private void openSaveFile(File archivo) throws GameException {
<<<<<<< HEAD
		try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(archivo.toPath()))) {
		    SaveData data = (SaveData) ois.readObject();
		    window.startGameFromSave(data);
=======
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
			SaveData data = (SaveData) ois.readObject();
			window.startGameFromSave(data);
			GameLogger.logInfo("CARGAR_MENU", "Partida cargada desde menú: " + archivo.getAbsolutePath());
>>>>>>> branch 'main' of https://github.com/paulisdiar/FinalProyect.git
		} catch (IOException | ClassNotFoundException e) {
<<<<<<< HEAD
		    throw new GameException("Error al abrir el archivo");
=======
			GameLogger.logError("CARGAR_MENU", "Fallo al cargar desde menú: " + archivo.getAbsolutePath() + " — " + e.getMessage());
			throw new GameException("Error al abrir el archivo");
>>>>>>> branch 'main' of https://github.com/paulisdiar/FinalProyect.git
		}
	}

	/**
	 * Abre un {@link JFileChooser} para que el usuario elija un archivo .dat
	 * y carga la partida seleccionada.
	 */
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
