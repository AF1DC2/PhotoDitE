package Utility;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;


/**
 * ImageProcessor is a utility class that provides static methods for various image manipulation operations,
 * including grayscale conversion, image rotation, brightness adjustment, image zooming, and flipping.
 */
public class ImageProcessorUtility {

	/**
     * Converts a given BufferedImage to grayscale.
     * The grayscale image is created using the BYTE_GRAY color model.
     * 
     * @param image The BufferedImage to be converted to grayscale.
     * @return A new BufferedImage in grayscale.
     */
    public static BufferedImage toGrayscale(BufferedImage image) {
        BufferedImage grayscale = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = grayscale.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return grayscale;
    }

    /**
     * Applies a sepia filter to the image.
     * The sepia filter gives the image a warm, reddish-brown tone, mimicking old photographs.
     * 
     * @param image The BufferedImage to apply the sepia filter to.
     * @return A new BufferedImage with the sepia filter applied.
     */
    public static BufferedImage toSepia(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage sepiaImage = new BufferedImage(width, height, image.getType());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));

                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                int tr = (int) (0.393 * red + 0.769 * green + 0.189 * blue);
                int tg = (int) (0.349 * red + 0.686 * green + 0.168 * blue);
                int tb = (int) (0.272 * red + 0.534 * green + 0.131 * blue);

                tr = clamp(tr);
                tg = clamp(tg);
                tb = clamp(tb);

                sepiaImage.setRGB(x, y, new Color(tr, tg, tb).getRGB());
            }
        }
        return sepiaImage;
    }

    /**
     * Applies a negative filter to the image.
     * The negative filter inverts all colors by subtracting the RGB values from 255.
     * 
     * @param image The BufferedImage to apply the negative filter to.
     * @return A new BufferedImage with the negative filter applied.
     */
    public static BufferedImage toNegative(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage negativeImage = new BufferedImage(width, height, image.getType());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));

                int red = 255 - color.getRed();
                int green = 255 - color.getGreen();
                int blue = 255 - color.getBlue();

                negativeImage.setRGB(x, y, new Color(clamp(red), clamp(green), clamp(blue)).getRGB());
            }
        }
        return negativeImage;
    }
    
    /**
     * Rotates the given image by 90 degrees counter-clockwise.
     * This method creates a new BufferedImage where the height and width are swapped,
     * and the pixels are rearranged accordingly to achieve the rotation.
     * 
     * @param image The BufferedImage to be rotated.
     * @return A new BufferedImage that is rotated 90 degrees clockwise.
     */
    public static BufferedImage rotateImageLeft(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage rotated = new BufferedImage(height, width, image.getType());
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rotated.setRGB(y, width - 1 - x, image.getRGB(x, y));
            }
        }
        return rotated;
    }

    /**
     * Rotates the given image by 90 degrees clockwise.
     * This method creates a new BufferedImage where the height and width are swapped,
     * and the pixels are rearranged accordingly to achieve the rotation.
     * 
     * @param image The BufferedImage to be rotated.
     * @return A new BufferedImage that is rotated 90 degrees clockwise.
     */
    public static BufferedImage rotateImageRight(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage rotated = new BufferedImage(height, width, image.getType());
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rotated.setRGB(height - 1 - y, x, image.getRGB(x, y));
            }
        }
        return rotated;
    }
    
    /**
     * Adjusts the brightness of an image based on a given adjustment value.
     * A positive adjustment value brightens the image, and a negative value darkens it.
     * The adjustment is applied using a RescaleOp operation.
     * 
     * @param image The BufferedImage whose brightness is to be adjusted.
     * @param adjustment The brightness adjustment value (positive to brighten, negative to darken).
     * @return A new BufferedImage with the adjusted brightness.
     */
    public static BufferedImage adjustBrightness(BufferedImage image, int adjustment) {
        BufferedImage brightened = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        float scaleFactor = 1 + (adjustment / 100.0f);
        RescaleOp bright = new RescaleOp(scaleFactor, adjustment, null);
        brightened = bright.filter(image, null);
        return brightened;
    }
    
    /**
     * Adjusts the contrast of an image by scaling pixel values around a midpoint.
     * Positive values increase contrast, while negative values decrease it.
     * 
     * @param image The BufferedImage whose contrast is to be adjusted.
     * @param contrast The contrast adjustment value (-100 to 100).
     * @return A new BufferedImage with the adjusted contrast.
     */
    public static BufferedImage adjustContrast(BufferedImage image, int contrast) {
        BufferedImage contrasted = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        float scaleFactor = 1 + (contrast / 100.0f);
        float offset = 128 * (1 - scaleFactor);
        RescaleOp contrastOp = new RescaleOp(scaleFactor, offset, null);
        contrasted = contrastOp.filter(image, null);
        return contrasted;
    }
    
    /**
     * Zooms the image by a given factor. A zoom factor of 1.0 keeps the image size unchanged.
     * Values greater than 1.0 enlarge the image, and values between 0 and 1 shrink the image.
     * 
     * @param image The BufferedImage to be zoomed.
     * @param zoomFactor The factor by which the image is to be zoomed (e.g., 1.2 to zoom in).
     * @return A new BufferedImage that is zoomed according to the specified zoom factor.
     */
    public static BufferedImage zoomImage(BufferedImage image, double zoomFactor) {
        int newWidth = (int) (image.getWidth() * zoomFactor);
        int newHeight = (int) (image.getHeight() * zoomFactor);
        BufferedImage zoomedImage = new BufferedImage(newWidth, newHeight, image.getType());
        Graphics2D g = zoomedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(image, 0, 0, newWidth, newHeight, null);
        g.dispose();
        return zoomedImage;
    }
    
    /**
     * Flips the given image horizontally.
     * The flipping is performed by applying an AffineTransform with a scale factor of -1 for the x-axis,
     * which results in a mirror image of the original.
     * 
     * @param image The BufferedImage to be flipped.
     * @return A new BufferedImage that is the horizontal flip of the original.
     */
    public static BufferedImage flipImage(BufferedImage image) {
    	BufferedImage fliped = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
    	AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
    	tx.translate(-image.getWidth(null), 0);
    	AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    	fliped = op.filter(image, null);
    	return fliped;
    }
    
    /**
     * Helper method to clamp pixel values between 0 and 255.
     * 
     * @param value The pixel's value.
     */
    private static int clamp(int value) {
        return Math.min(255, Math.max(0, value));
    }
    
}
