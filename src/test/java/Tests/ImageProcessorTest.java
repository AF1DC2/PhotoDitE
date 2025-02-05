package Tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Utility.ImageProcessorUtility;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Unit test class for the ImageProcessor class.
 * This class contains tests to ensure the methods in ImageProcessor function as expected.
 */
public class ImageProcessorTest {

    private BufferedImage testImage;

    /**
     * Set up the test environment by initializing a sample image.
     * This method is executed before each test method.
     */
    @BeforeEach
    public void setUp() throws IOException {
        testImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                testImage.setRGB(x, y, 0xFF0000);
            }
        }
    }

    /**
     * Test the toGrayscale method of the ImageProcessor class.
     * This test checks that all pixels in the output image have equal red, green, and blue values,
     * ensuring the image has been properly converted to grayscale.
     */
    @Test
    public void testToGrayscale() {
        BufferedImage grayscaleImage = ImageProcessorUtility.toGrayscale(testImage);

        for (int x = 0; x < grayscaleImage.getWidth(); x++) {
            for (int y = 0; y < grayscaleImage.getHeight(); y++) {
                int rgb = grayscaleImage.getRGB(x, y) & 0xFFFFFF;
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                assertEquals(red, green);
                assertEquals(green, blue);
            }
        }
    }

    /**
     * Test the toSepia method of the ImageProcessor class.
     * This test checks that all pixels in the output image have equal red, green, and blue values,
     * ensuring the image has been properly converted to sepia.
     */
    @Test
    public void testToSepia() {
        BufferedImage sepiaImage = ImageProcessorUtility.toSepia(testImage);

        for (int x = 0; x < sepiaImage.getWidth(); x++) {
            for (int y = 0; y < sepiaImage.getHeight(); y++) {
                int rgb = sepiaImage.getRGB(x, y);
                Color color = new Color(rgb);
                assertTrue(color.getRed() >= color.getGreen());
                assertTrue(color.getGreen() >= color.getBlue());
            }
        }
    }

    /**
     * Test the toNegative method of the ImageProcessor class.
     * This test checks that all pixels in the output image have equal red, green, and blue values,
     * ensuring the image has been properly converted to negative.
     */
    @Test
    public void testToNegative() {
        BufferedImage negativeImage = ImageProcessorUtility.toNegative(testImage);

        for (int x = 0; x < negativeImage.getWidth(); x++) {
            for (int y = 0; y < negativeImage.getHeight(); y++) {
                Color originalColor = new Color(testImage.getRGB(x, y));
                Color negativeColor = new Color(negativeImage.getRGB(x, y));
                assertEquals(255 - originalColor.getRed(), negativeColor.getRed());
                assertEquals(255 - originalColor.getGreen(), negativeColor.getGreen());
                assertEquals(255 - originalColor.getBlue(), negativeColor.getBlue());
            }
        }
    }
    
    /**
     * Test the rotateImageLeft method of the ImageProcessor class.
     * This test checks that the rotated image has dimensions that are swapped compared to the original image.
     */
    @Test
    public void testRotateImageLeft() {
        BufferedImage rotatedImage = ImageProcessorUtility.rotateImageLeft(testImage);

        assertEquals(testImage.getHeight(), rotatedImage.getWidth());
        assertEquals(testImage.getWidth(), rotatedImage.getHeight());

        for (int x = 0; x < testImage.getWidth(); x++) {
            for (int y = 0; y < testImage.getHeight(); y++) {
                assertEquals(testImage.getRGB(x, y), rotatedImage.getRGB(y, testImage.getWidth() - 1 - x));
            }
        }
    }

    /**
     * Test the rotateImageRight method of the ImageProcessor class.
     * This test checks that the rotated image has dimensions that are swapped compared to the original image.
     */
    @Test
    public void testRotateImageRight() {
        BufferedImage rotatedImage = ImageProcessorUtility.rotateImageRight(testImage);

        assertEquals(testImage.getHeight(), rotatedImage.getWidth());
        assertEquals(testImage.getWidth(), rotatedImage.getHeight());

        for (int x = 0; x < testImage.getWidth(); x++) {
            for (int y = 0; y < testImage.getHeight(); y++) {
                assertEquals(testImage.getRGB(x, y), rotatedImage.getRGB(testImage.getHeight() - 1 - y, x));
            }
        }
    }

    /**
     * Test the adjustBrightness method of the ImageProcessor class.
     * This test checks that the brightness adjustment produces pixels with values within the expected range.
     */
    @Test
    public void testAdjustBrightness() {
        BufferedImage brightenedImage = ImageProcessorUtility.adjustBrightness(testImage, 50);

        for (int x = 0; x < brightenedImage.getWidth(); x++) {
            for (int y = 0; y < brightenedImage.getHeight(); y++) {
                int rgb = brightenedImage.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                assertTrue(red >= 50 && red <= 255);
            }
        }
    }

    /**
     * Test the adjustContrast method of the ImageProcessor class.
     * This test checks that the brightness adjustment produces pixels with values within the expected range.
     */
    @Test
    public void testAdjustContrast() {
        BufferedImage contrastedImage = ImageProcessorUtility.adjustContrast(testImage, 50);

        for (int x = 0; x < contrastedImage.getWidth(); x++) {
            for (int y = 0; y < contrastedImage.getHeight(); y++) {
                int rgb = contrastedImage.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                assertTrue(red >= 0 && red <= 255);
            }
        }
    }
    
    /**
     * Test the zoomImage method of the ImageProcessor class.
     * This test checks that the zoom operation correctly scales the image dimensions.
     */
    @Test
    public void testZoomImage() {
        BufferedImage zoomedImage = ImageProcessorUtility.zoomImage(testImage, 2.0);

        assertEquals(20, zoomedImage.getWidth());
        assertEquals(20, zoomedImage.getHeight());
    }

    /**
     * Test the flipImage method of the ImageProcessor class.
     * This test checks that the flipped image correctly mirrors the original image.
     */
    @Test
    void testFlipImage() {
    	
    	BufferedImage originalImage = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                originalImage.setRGB(x, y, new Color(x * 85, y * 85, 0).getRGB());
            	}
        	}
    	
        BufferedImage flippedImage = ImageProcessorUtility.flipImage(originalImage);

        assertEquals(originalImage.getRGB(0, 0), flippedImage.getRGB(2, 0));
        assertEquals(originalImage.getRGB(1, 0), flippedImage.getRGB(1, 0));
        assertEquals(originalImage.getRGB(2, 0), flippedImage.getRGB(0, 0)); 
    }
    
}
