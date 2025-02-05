package project;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Project {

	protected String name;
	protected String path;
	protected String type;
	protected BufferedImage image;
	
	public Project(String n, String p, String t) {
		name = n;
		path = p;
		type = t;
		try {
			if(path != null) {
				image = ImageIO.read(new File(path));
			}
			else {
				image = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getImage() {
		return image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
