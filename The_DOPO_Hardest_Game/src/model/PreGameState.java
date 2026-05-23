package model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import Controller.Window;

/**
 * Estado de instrucciones previas al juego. Muestra las reglas básicas
 * y permite al usuario elegir el modo de juego o volver al menú.
 */
public class PreGameState {

	private Window window;

	/**
	 * Crea y muestra la pantalla de instrucciones.
	 *
	 * @param window ventana principal de la aplicación
	 */
	public PreGameState(Window window) {
		this.window = window;
		show();
	}

	/**
	 * Construye y muestra los componentes Swing de la pantalla de instrucciones.
	 */
	private void show() {
		window.getContentPane().removeAll();
		window.setLayout(new BorderLayout(10, 10));

		JLabel titulo = new JLabel("INSTRUCCIONES", SwingConstants.CENTER);
		titulo.setFont(new Font("Arial", Font.BOLD, 28));
		titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
		window.add(titulo, BorderLayout.NORTH);

		JTextPane texto = new JTextPane();
		texto.setFont(new Font("Arial", Font.PLAIN, 16));
		texto.setEditable(false);
		texto.setFocusable(false);
		texto.setOpaque(false);
		texto.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));

		StyledDocument doc = texto.getStyledDocument();

		Style normal = texto.addStyle("normal", null);
		StyleConstants.setFontFamily(normal, "Arial");
		StyleConstants.setFontSize(normal, 16);

		Style rojo = texto.addStyle("rojo", normal);
		StyleConstants.setForeground(rojo, new Color(200, 0, 0));
		StyleConstants.setBold(rojo, true);

		Style azul = texto.addStyle("azul", normal);
		StyleConstants.setForeground(azul, new Color(0, 80, 200));
		StyleConstants.setBold(azul, true);

		Style amarillo = texto.addStyle("amarillo", normal);
		StyleConstants.setForeground(amarillo, new Color(180, 140, 0));
		StyleConstants.setBold(amarillo, true);

		Object[][] partes = {
			{"Eres el cuadrado ", normal},
			{"rojo", rojo},
			{".\n\nEvita los círculos ", normal},
			{"azules", azul},
			{" y recoge los círculos ", normal},
			{"amarillos", amarillo},
			{".\n\nUna vez que hayas recogido todos los círculos ", normal},
			{"amarillos", amarillo},
			{", dirígete\nal faro verde para completar el nivel.\n\n" +
			 "Algunos niveles tienen más de un faro; los faros intermedios\n" +
			 "actúan como puntos de control.\n\n" +
			 "Debes completar todos los niveles para enviar tu puntuación.\n\n" +
			 "Tu puntuación refleja cuántas veces has muerto;\n" +
			 "cuanto menos, mejor.", normal}
		};

		try {
			for (Object[] parte : partes)
				doc.insertString(doc.getLength(), (String) parte[0], (Style) parte[1]);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		window.add(texto, BorderLayout.CENTER);

		JPanel panelBotones = new JPanel(new GridLayout(1, 4, 10, 10));
		panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 60, 20, 60));

		JButton bVolver  = new JButton("Volver");
		JButton bJugar   = new JButton("¡Jugar!");
		JButton bJugar2p = new JButton("2 Jugadores");
		JButton bJugarVM = new JButton("vs Máquina");

		bVolver.setFont(new Font("Arial", Font.PLAIN, 16));
		bJugar.setFont(new Font("Arial", Font.BOLD, 16));
		bJugar2p.setFont(new Font("Arial", Font.BOLD, 16));
		bJugarVM.setFont(new Font("Arial", Font.BOLD, 16));

		bVolver.addActionListener(e -> window.goToMenu());
		bJugar.addActionListener(e -> window.showColorConfig(GameMode.SOLO));
		bJugar2p.addActionListener(e -> window.showColorConfig(GameMode.PVP));
		bJugarVM.addActionListener(e -> window.showColorConfig(GameMode.PVM));

		panelBotones.add(bVolver);
		panelBotones.add(bJugar);
		panelBotones.add(bJugar2p);
		panelBotones.add(bJugarVM);
		window.add(panelBotones, BorderLayout.SOUTH);

		window.revalidate();
		window.repaint();
	}
}
