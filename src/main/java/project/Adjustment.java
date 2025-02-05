package project;

import java.awt.image.BufferedImage;

/**
 * Adjustment is a class that holds information about an image's adjustment and it's used
 * for the undoStack to keep track of an image progression through editing.
 * It holds the new image and the type of the adjustment.
 */
public class Adjustment {

	public enum type {
		FILTER,
		BRIGHTNESS,
		CONTRAST,
		FLIP,
		ROTATE,
		DEFAULT, 
	}
	
	private BufferedImage image;
	private type adjustment;
	
	Adjustment(BufferedImage image, type adjustment){
		this.image = image;
		this.adjustment = adjustment;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	public type getAdjustment() {
		return adjustment;
	}
	public void setAdjustment(type adjustment) {
		this.adjustment = adjustment;
	}
	
}
