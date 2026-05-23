package View;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Utilidad estática para cargar imágenes desde el sistema de archivos.
 */
public class Loader {

	/**
	 * Carga una imagen desde la ruta indicada.
	 *
	 * @param path ruta al archivo de imagen
	 * @return imagen cargada, o {@code null} si ocurrió un error de E/S
	 */
	public static BufferedImage imageLoader(String path) {
		try {
			return ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
