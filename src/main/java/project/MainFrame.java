package project;

import javax.swing.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;

import Utility.ImageProcessorUtility;
import Utility.ImageSaveUtility;
import project.Adjustment.type;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Stack;

/**
 * MainFrame is the main graphical user interface (GUI) for the photo editing application.
 * It allows the user to edit, save, and perform basic image transformations such as grayscale,
 * rotation, flip, and brightness adjustment.
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
    private BufferedImage currentImage = null; 
    private BufferedImage brightnessImage = null;
    private BufferedImage contrastImage = null;
    private BufferedImage noFilterImage = null;
    private JLabel imageLabel;
    private double zoomFactor = 1.0;
    private Stack<Adjustment> undoStack = new Stack<>();
    private boolean isModified = false;
    
    /**
     * Constructs the MainFrame with necessary components.
     * Sets up the layout, buttons, and event listeners.
     */
    public MainFrame(Project selectedProject) {
    	
    	currentImage = selectedProject.getImage();
    	brightnessImage = selectedProject.getImage();
    	contrastImage = selectedProject.getImage();
    	noFilterImage = selectedProject.getImage();
    	undoStack.push(new Adjustment(currentImage, type.DEFAULT));
        getContentPane().setBackground(new Color(0, 0, 0));
        setTitle("PhotoDitE - " + selectedProject.getName());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        BorderLayout borderLayout = new BorderLayout();
        getContentPane().setLayout(borderLayout);
        getContentPane().setBackground(new Color(0x2C2C2C));
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
                if (isModified) {
                    int option = JOptionPane.showConfirmDialog(null,
                        "You have unsaved changes. Do you want to save before exiting?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                    if (option == JOptionPane.YES_OPTION) {
                    	selectedProject.setImage(currentImage);
						ImageSaveUtility.saveImage(selectedProject); 
						undoStack.clear(); 
						undoStack.push(new Adjustment(currentImage, type.DEFAULT));
						isModified = false;
		                setTitle(getTitle().substring(0, getTitle().lastIndexOf('*')));
                        System.exit(0);
                    } 
                    else {
                    	if (option == JOptionPane.NO_OPTION) {
                        System.exit(0);
                    	}
                    }
                } 
                else {
                    System.exit(0);
                }
        	}
        });

        imageLabel = new JLabel();
        imageLabel.setBackground(new Color(0x2C2C2C));
        imageLabel.setOpaque(true);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(imageLabel);
        scrollPane.setBackground(new Color(0x2C2C2C));
        scrollPane.getViewport().setBackground(new Color(0x2C2C2C));
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        imageLabel.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (currentImage != null) {
                    int rotation = e.getWheelRotation();
                    zoomFactor += (rotation > 0) ? -0.1 : 0.1;
                    zoomFactor = Math.max(0.1, zoomFactor);
                    updateImageLabel(ImageProcessorUtility.zoomImage(currentImage, zoomFactor));
                }
            }
        });
        if(currentImage != null) {
        	updateImageLabel(currentImage);
        }
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        getContentPane().add(buttonPanel, BorderLayout.WEST);
        buttonPanel.setBackground(new Color(0x3C3F41)); 
        buttonPanel.setBorder(BorderFactory.createLineBorder(new Color(0x1E1E1E)));
        
        JButton undoButton = new JButton("Undo");
        
        Component verticalStrut = Box.createVerticalStrut(20);
        buttonPanel.add(verticalStrut);
        
        Box horizontalBox_1 = Box.createHorizontalBox();
        buttonPanel.add(horizontalBox_1);
    
        JComboBox<String> filterComboBox = new JComboBox<>();
        filterComboBox.setBackground(new Color(0x3C3F41));
        filterComboBox.setForeground(new Color(0xD4D4D4));
        filterComboBox.setBorder(BorderFactory.createLineBorder(new Color(0x1E1E1E)));
        filterComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    filterComboBox.setBackground(new Color(0x444444));
                } else {
                    filterComboBox.setBackground(new Color(0x3C3F41));
                }
            }
        });
        horizontalBox_1.add(filterComboBox);
        filterComboBox.setToolTipText("Filters");
        filterComboBox.addItem("No Filter");
        filterComboBox.addItem("Grayscale");
        filterComboBox.addItem("Sepia");
        filterComboBox.addItem("Negative");
        filterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentImage != null) {
                	if(isModified == false) {
                		isModified = true;
                		setTitle(getTitle() + "*");
                	}
                	
                    String selectedFilter = (String) filterComboBox.getSelectedItem();
                    BufferedImage filteredImage = null;

                    switch (selectedFilter) {
                    	case "No Filter":
                    		filteredImage = noFilterImage;
                    		if(isModified && undoStack.size() == 1) {
                    			isModified = false;
                    			setTitle(getTitle().substring(0, getTitle().lastIndexOf('*')));
                    		}
                    		break;
                        case "Grayscale":
                            filteredImage = ImageProcessorUtility.toGrayscale(noFilterImage);
                            break;
                        case "Sepia":
                            filteredImage = ImageProcessorUtility.toSepia(noFilterImage);
                            break;
                        case "Negative":
                            filteredImage = ImageProcessorUtility.toNegative(noFilterImage);
                            break;
                    }

                    if (filteredImage != null) {
                        updateImage(filteredImage, type.FILTER, (String) filterComboBox.getSelectedItem());
                    }
                }
            }
        });
        
        Component verticalStrut_1 = Box.createVerticalStrut(20);
        buttonPanel.add(verticalStrut_1);
        
        Box horizontalBox = Box.createHorizontalBox();
        buttonPanel.add(horizontalBox);
        
                JButton rotateLeftButton = new JButton("Left Rotate");
                rotateLeftButton.setFont(new Font("Open Sans", Font.TRUETYPE_FONT, 16));
                rotateLeftButton.setPreferredSize(new Dimension(200, 50));
                rotateLeftButton.setBackground(new Color(0x3C3F41));
                rotateLeftButton.setForeground(new Color(0xD4D4D4));
                rotateLeftButton.setBorder(BorderFactory.createLineBorder(new Color(0x1E1E1E)));
                rotateLeftButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        rotateLeftButton.setBackground(new Color(0x444444));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        rotateLeftButton.setBackground(new Color(0x3C3F41));
                    }
                });
                horizontalBox.add(rotateLeftButton);
                rotateLeftButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    	if(isModified == false) {
                    		isModified = true;
                    		setTitle(getTitle() + "*");
                    	}
                    	
                    	if(undoStack.size() == 1) {
                    		undoButton.setEnabled(true);
                    	}
                    	
                        if (currentImage != null) {
                            BufferedImage rotatedImage = ImageProcessorUtility.rotateImageLeft(noFilterImage);
                            updateImage(rotatedImage, type.ROTATE, (String) filterComboBox.getSelectedItem());
                        }
                    }
                });
                
                JButton rotateRightButton = new JButton("Right Rotate");
                rotateRightButton.setFont(new Font("Open Sans", Font.TRUETYPE_FONT, 16));
                rotateRightButton.setPreferredSize(new Dimension(200, 50));
                rotateRightButton.setBackground(new Color(0x3C3F41));
                rotateRightButton.setForeground(new Color(0xD4D4D4));
                rotateRightButton.setBorder(BorderFactory.createLineBorder(new Color(0x1E1E1E)));
                rotateRightButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        rotateRightButton.setBackground(new Color(0x444444));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        rotateRightButton.setBackground(new Color(0x3C3F41));
                    }
                });
                horizontalBox.add(rotateRightButton);
                rotateRightButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    	if(isModified == false) {
                    		isModified = true;
                    		setTitle(getTitle() + "*");
                    	}
                    	
                    	if(undoStack.size() == 1) {
                    		undoButton.setEnabled(true);
                    	}
                    	
                        if (currentImage != null) {
                            BufferedImage rotatedImage = ImageProcessorUtility.rotateImageRight(noFilterImage);
                            updateImage(rotatedImage, type.ROTATE, (String) filterComboBox.getSelectedItem());
                        }
                    }
                });
        
        Component verticalStrut_2 = Box.createVerticalStrut(20);
        buttonPanel.add(verticalStrut_2);
        
        Box horizontalBox_2 = Box.createHorizontalBox();
        buttonPanel.add(horizontalBox_2);
        
        JButton flipButton = new JButton("Flip");
        flipButton.setFont(new Font("Open Sans", Font.TRUETYPE_FONT, 16));
        flipButton.setPreferredSize(new Dimension(200, 50));
        flipButton.setBackground(new Color(0x3C3F41));
        flipButton.setForeground(new Color(0xD4D4D4));
        flipButton.setFocusPainted(false);
        flipButton.setBorder(BorderFactory.createLineBorder(new Color(0x1E1E1E)));
        flipButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                flipButton.setBackground(new Color(0x444444));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                flipButton.setBackground(new Color(0x3C3F41));
            }
        });
        horizontalBox_2.add(flipButton);
        flipButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(isModified == false) {
            		isModified = true;
            		setTitle(getTitle() + "*");
            	}
        		
        		if(undoStack.size() == 1) {
            		undoButton.setEnabled(true);
            	}
        		
                if (currentImage != null) {
                    BufferedImage flipImage = ImageProcessorUtility.flipImage(noFilterImage);
                    updateImage(flipImage, type.FLIP, (String) filterComboBox.getSelectedItem());
                }
            }
        });
        
        JSlider brightnessSlider = new JSlider();
        brightnessSlider.setBackground(new Color(0x2C2C2C));
        brightnessSlider.setForeground(new Color(0xD4D4D4));
        brightnessSlider.setUI(new BasicSliderUI(brightnessSlider) {
            @Override
			public void paintThumb(Graphics g) {
                g.setColor(new Color(0x007ACC));
                super.paintThumb(g);
            }
        });
        brightnessSlider.setValue(0);
        brightnessSlider.setMaximum(20);
        brightnessSlider.setMinimum(-20);
        brightnessSlider.setToolTipText("Brighten");
        if(currentImage == null) {
        	brightnessSlider.setEnabled(false);
        }
        brightnessSlider.addChangeListener(new ChangeListener() {
        	
            @Override
            public void stateChanged(ChangeEvent e) {
            	if(isModified == false) {
            		isModified = true;
            		setTitle(getTitle() + "*");
            	}
            	
            	if(undoStack.size() == 1) {
            		undoButton.setEnabled(true);
            	}
            	
                if (currentImage != null) {
                	JSlider source = (JSlider) e.getSource();
                	
                    BufferedImage brightenedImage = ImageProcessorUtility.adjustBrightness(brightnessImage, brightnessSlider.getValue());
                    updateImageLabel(ImageProcessorUtility.zoomImage(brightenedImage, zoomFactor));
                    
                    if(!source.getValueIsAdjusting()) {
                    	updateImage(brightenedImage, type.BRIGHTNESS, (String) filterComboBox.getSelectedItem());
                    }
                    
                }
            }

        });
        
        Component verticalStrut_4 = Box.createVerticalStrut(20);
        buttonPanel.add(verticalStrut_4);
        
        Box horizontalBox_3 = Box.createHorizontalBox();
        buttonPanel.add(horizontalBox_3);
        
        JLabel brightnessLabel = new JLabel("Brightness");
        brightnessLabel.setForeground(new Color(0xD4D4D4));
        horizontalBox_3.add(brightnessLabel);
        buttonPanel.add(brightnessSlider);
        
        Component verticalStrut_3 = Box.createVerticalStrut(20);
        buttonPanel.add(verticalStrut_3);
        
        Box horizontalBox_4 = Box.createHorizontalBox();
        buttonPanel.add(horizontalBox_4);
        
        JLabel contrastLabel = new JLabel("Contrast");
        contrastLabel.setForeground(new Color(0xD4D4D4));
        horizontalBox_4.add(contrastLabel);
        
        JSlider contrastSlider = new JSlider();
        contrastSlider.setBackground(new Color(0x2C2C2C));
        contrastSlider.setForeground(new Color(0xD4D4D4));
        contrastSlider.setUI(new BasicSliderUI(contrastSlider) {
            @Override
			public void paintThumb(Graphics g) {
                g.setColor(new Color(0x007ACC));
                super.paintThumb(g);
            }
        });
        contrastSlider.setValue(0);
        contrastSlider.setMaximum(75);
        contrastSlider.setMinimum(-75);
        contrastSlider.setToolTipText("Adjust Contrast");
        if (currentImage == null) {
            contrastSlider.setEnabled(false);
        }
        contrastSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
            	if(isModified == false) {
            		isModified = true;
            		setTitle(getTitle() + "*");
            	}
            	
            	if(undoStack.size() == 1) {
            		undoButton.setEnabled(true);
            	}
            	
                if (currentImage != null) {
                    JSlider source = (JSlider) e.getSource();
                    
                    BufferedImage contrastedImage = ImageProcessorUtility.adjustContrast(contrastImage, source.getValue());
                    updateImageLabel(ImageProcessorUtility.zoomImage(contrastedImage, zoomFactor));

                    if (!source.getValueIsAdjusting()) {
                    	updateImage(contrastedImage, type.CONTRAST, (String) filterComboBox.getSelectedItem());
                    }
                }
            }
        });
        buttonPanel.add(contrastSlider);
        
        Component verticalGlue = Box.createVerticalGlue();
        buttonPanel.add(verticalGlue);
        
        Box horizontalBox_5 = Box.createHorizontalBox();
        buttonPanel.add(horizontalBox_5);
        
        undoButton.setFont(new Font("Open Sans", Font.TRUETYPE_FONT, 16));
        undoButton.setPreferredSize(new Dimension(200, 50));
        undoButton.setBackground(new Color(0x3C3F41));
        undoButton.setForeground(new Color(0xD4D4D4));
        undoButton.setFocusPainted(false);
        undoButton.setBorder(BorderFactory.createLineBorder(new Color(0x1E1E1E)));
        undoButton.setEnabled(false);
        undoButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                undoButton.setBackground(new Color(0x444444));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                undoButton.setBackground(new Color(0x3C3F41));
            }
        });
        horizontalBox_5.add(undoButton);
        undoButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if (undoStack.size() > 1) {
                    undoStack.pop();
                    Adjustment lastAdjustment = undoStack.peek();

                    if(undoStack.size() == 1) {
                    	if(filterComboBox.getSelectedItem().toString().equals("No Filter")) {
                    		isModified = false;
                    		setTitle(getTitle().substring(0, getTitle().lastIndexOf('*')));
                    	}
                        undoButton.setEnabled(false);
                    }
                    
                    currentImage = lastAdjustment.getImage();
                    brightnessImage = lastAdjustment.getImage();
                	contrastImage  = lastAdjustment.getImage();
                	noFilterImage = lastAdjustment.getImage();
                	
                	currentImage = applyFilter((String) filterComboBox.getSelectedItem());
                    updateImageLabel(ImageProcessorUtility.zoomImage(currentImage, zoomFactor));
                }
        	}
        });
        
        Component verticalStrut_5 = Box.createVerticalStrut(20);
        buttonPanel.add(verticalStrut_5);
        
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(0x2C2C2C));
        menuBar.setForeground(new Color(0xD4D4D4));
        getContentPane().add(menuBar, BorderLayout.NORTH);
        
        JMenu menu = new JMenu("File");
        menu.setBackground(new Color(0x2C2C2C));
        menu.setForeground(new Color(0xD4D4D4));
        menuBar.add(menu);
        
        JMenuItem saveButton = new JMenuItem("Save");
        saveButton.setBackground(new Color(0x3C3F41));
        saveButton.setForeground(new Color(0xD4D4D4));
        saveButton.setToolTipText("Saves image at the path of the project");
        saveButton.addActionListener(e -> {selectedProject.setImage(currentImage);
        								   ImageSaveUtility.saveImage(selectedProject); 
        								   undoStack.clear(); 
        								   undoStack.push(new Adjustment(currentImage, type.DEFAULT));
        								   isModified = false;
        				                   setTitle(getTitle().substring(0, getTitle().lastIndexOf('*')));
        							});
        menu.add(saveButton);
        
        JMenuItem saveAsButton = new JMenuItem("Save As");
        saveAsButton.setBackground(new Color(0x3C3F41));
        saveAsButton.setForeground(new Color(0xD4D4D4));
        saveAsButton.setToolTipText("Save as a different format at a specific path");
        saveAsButton.addActionListener(e -> {ImageSaveUtility.saveAsImage(currentImage, MainFrame.this);
        								     undoStack.clear(); 
        								     undoStack.push(new Adjustment(currentImage, type.DEFAULT));
        							});
        menu.add(saveAsButton);
        
    }
    
    /**
     * Updates all the BufferedImages with the modified Image and pushes the Adjustment
     * down the undoStack.
     * 
     * @param modifiedImage The BufferedImage after the adjustment applied.
     * @param a The type of the adjustment.
     * @param filter The current filter that is applied over the image.
     */
    private void updateImage(BufferedImage modifiedImage, type a, String filter) {
    	if(a != type.FILTER) {
    		noFilterImage = modifiedImage;
    		if(a != type.BRIGHTNESS)
        		brightnessImage = modifiedImage;
        	if(a != type.CONTRAST)
        		contrastImage  = modifiedImage;
    		undoStack.push(new Adjustment(copyImage(noFilterImage), a));
    	}
    	currentImage = applyFilter(filter);
        updateImageLabel(ImageProcessorUtility.zoomImage(currentImage, zoomFactor));
    }
    
    /**
     * Creates a copy of a BufferedImage.
     * 
     * @param original The original image to be copied.
     * @return A new BufferedImage that is a copy of the original.
     */
    private BufferedImage copyImage(BufferedImage original) {
    	BufferedImage copy = original.getSubimage(0, 0, original.getWidth(), original.getHeight());
        return copy;
    }
    
    
    /**
     * Updates the image displayed in the JLabel with a new BufferedImage.
     * 
     * @param image The image to be displayed in the JLabel.
     */
    private void updateImageLabel(BufferedImage image) {
        ImageIcon icon = new ImageIcon(image);
        imageLabel.setIcon(icon);
    }
    
    private BufferedImage applyFilter(String filter) {
    	BufferedImage image = null;
    	switch (filter) {
    	case "No Filter":
    		image = noFilterImage;
    		break;
        case "Grayscale":
            image = ImageProcessorUtility.toGrayscale(noFilterImage);
            break;
        case "Sepia":
            image = ImageProcessorUtility.toSepia(noFilterImage);
            break;
        case "Negative":
            image = ImageProcessorUtility.toNegative(noFilterImage);
            break;
    	}
    	return image;
    }

}
