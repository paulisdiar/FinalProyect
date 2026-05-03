package states;

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

import controlador.Window;

public class PreGameState {

	private Window window;

	public PreGameState(Window window) {
		this.window = window;
		show();
	}

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

		JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 10));
		panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 200, 20, 200));

		JButton bJugar = new JButton("¡Jugar!");
		JButton bVolver = new JButton("Volver");

		bJugar.setFont(new Font("Arial", Font.BOLD, 16));
		bVolver.setFont(new Font("Arial", Font.PLAIN, 16));

		bJugar.addActionListener(e -> window.startGame());
		bVolver.addActionListener(e -> window.goToMenu());

		panelBotones.add(bVolver);
		panelBotones.add(bJugar);
		window.add(panelBotones, BorderLayout.SOUTH);

		window.revalidate();
		window.repaint();
	}
}
