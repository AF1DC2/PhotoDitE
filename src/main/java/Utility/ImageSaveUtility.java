package Utility;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import project.Project;

/**
 * ImageSave is a utility class for saving images to disk.
 * It allows the user to choose a file location and format for saving the current image.
 */
public class ImageSaveUtility {

	/**
     * Saves the current image to a file.
     * Opens a file chooser dialog to allow the user to select the destination and file format (PNG, JPG, or JPEG).
     * If the image format is not specified by the user, it defaults to PNG.
     * If no image is available, it shows an error message.
     * 
     * @param currentImage The BufferedImage to be saved.
     * @param MainFrame The JFrame that is used to show error messages.
     */
	public static void saveAsImage(BufferedImage currentImage, JFrame MainFrame) {
        if (currentImage == null) {
            JOptionPane.showMessageDialog(null, "No image to save!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Image");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images (.png, .jpg, .jpeg)", "png", "jpg", "jpeg"));
        
        int selected = fileChooser.showSaveDialog(MainFrame);

        if (selected == JFileChooser.APPROVE_OPTION) {
            File image = fileChooser.getSelectedFile();

            String fileName = image.getAbsolutePath();
            if (!fileName.toLowerCase().endsWith(".png") && !fileName.toLowerCase().endsWith(".jpg") && !fileName.toLowerCase().endsWith(".jpeg")) {
                image = new File(fileName + ".png");
            }

            try {
            	if(image.getAbsolutePath().toLowerCase().endsWith(".png"))
            		ImageIO.write(currentImage, "png", image);
            	if(image.getAbsolutePath().toLowerCase().endsWith(".jpg"))
            		ImageIO.write(currentImage, "jpg", image);
            	if(image.getAbsolutePath().toLowerCase().endsWith(".jpeg"))
            		ImageIO.write(currentImage, "jpeg", image);
            	
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(MainFrame, "Failed to save the image!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
	
	/**
     * Saves the current image to the file at the path of the project.
     * 
     * @param project The current project that contains all the information needed for the save.
     */
	public static void saveImage(Project project) {
		try {
			ImageIO.write(project.getImage(), project.getType().toLowerCase(), new File(project.getPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
