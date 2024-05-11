package net.rockscience.util.image;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import lombok.Data;

public class ImageUtil {
	private ImageUtil() {}

	public static int MAX_IMG_WIDTH = 1500;
	public static int THUMBNAIL_WIDTH = 300;

	public static BufferedImage bufferedImageFromStream(InputStream is) throws IOException {
		return ImageIO.read(is);
	}

	public static BufferedImage scaleImageToMaxTargetWidth(BufferedImage originalImage, int targetWidth) {
		int originalWidth = originalImage.getWidth();
		int originalHeight = originalImage.getHeight();

		if(originalWidth <= targetWidth) {
			return originalImage;
		}

		float scaleFactor = (float)targetWidth/originalWidth;
		int targetHeight = (int)(originalHeight * scaleFactor);

		BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);

		Graphics2D graphics = resizedImage.createGraphics();
		graphics.drawImage(originalImage, 0, 0, targetWidth,targetHeight, null);
		graphics.dispose();

		return resizedImage;
	}

	public static InputStreamAndSize streamFromBufferedImage(BufferedImage bi, String imageType) throws IOException {
		InputStreamAndSize sas = new InputStreamAndSize();

		ByteArrayOutputStream  os = new ByteArrayOutputStream();
		ImageIO.write(bi, imageType, os);
		sas.setBytes(os.size());
		sas.setStream(new ByteArrayInputStream(os.toByteArray()));

		return sas;
	}

	@Data
	public static class InputStreamAndSize {
		private InputStream stream;
		private long bytes;
	}


}
