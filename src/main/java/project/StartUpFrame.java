package project;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import Utility.DatabaseUtility;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.io.FilenameUtils;

/**
 * StartupFrame is the initial window displayed to the user.
 * It contains a scrollable list of projects and buttons to proceed or create a new project.
 */
public class StartUpFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private DefaultListModel<String> projectListModel;

    /**
     * Constructs the StartupFrame with a project list and options to create or open projects.
     */
    public StartUpFrame() {
    	
        setTitle("PhotoDitE - Startup");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 300);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(0x2C2C2C));
        setLocationRelativeTo(null);

        JLabel welcomeLabel = new JLabel("Welcome to PhotoDitE!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(0xD4D4D4));
        getContentPane().add(welcomeLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBackground(new Color(0x2C2C2C));
        getContentPane().add(centerPanel, BorderLayout.CENTER);

        projectListModel = new DefaultListModel<>();
        JList<String> projectList = new JList<>(projectListModel);
        projectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectList.setBackground(new Color(0x3C3F41));
        projectList.setForeground(new Color(0xD4D4D4));
        projectList.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(projectList);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0xD4D4D4), 1), "Your Projects", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.PLAIN, 12), new Color(0xD4D4D4)));
        scrollPane.setBackground(new Color(0x3C3F41));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        DatabaseUtility.loadProjectsFromDatabase(projectListModel, projectList);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.setBackground(new Color(0x2C2C2C));
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        JButton openButton = new JButton("Open Project");
        openButton.setBackground(new Color(0x3C3F41));
        openButton.setForeground(new Color(0xD4D4D4));
        openButton.setBorder(BorderFactory.createLineBorder(new Color(0x1E1E1E)));
        openButton.setFocusPainted(false);
        openButton.setFont(new Font("Arial", Font.PLAIN, 14));
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(!new File(projectList.getSelectedValue().substring(projectList.getSelectedValue().indexOf('-') + 1, projectList.getSelectedValue().lastIndexOf('-')).strip()).exists()) {
            		JOptionPane.showMessageDialog(StartUpFrame.this, 
                            "The selected project doesn't exist!", "Error", JOptionPane.ERROR_MESSAGE);
            		DatabaseUtility.deleteProject(projectList.getSelectedValue().substring(projectList.getSelectedValue().indexOf('-') + 1, projectList.getSelectedValue().lastIndexOf('-')).strip());
            		projectList.clearSelection();
            		projectListModel.clear();
            		DatabaseUtility.loadProjectsFromDatabase(projectListModel, projectList);
            		openButton.setEnabled(false);
            		return;
            	}
            	else{
            		Project project = new Project(projectList.getSelectedValue().substring(0, projectList.getSelectedValue().indexOf('-')).strip(), 
                							  	  projectList.getSelectedValue().substring(projectList.getSelectedValue().indexOf('-') + 1, projectList.getSelectedValue().lastIndexOf('-')).strip(),
                							  	  projectList.getSelectedValue().substring(projectList.getSelectedValue().lastIndexOf('-') + 1, projectList.getSelectedValue().length()).strip());
            		MainFrame mainFrame = new MainFrame(project);
            		mainFrame.setVisible(true);
            		dispose();
            	}
            }
        });
        buttonPanel.add(openButton);
        openButton.setEnabled(false);

        JButton newProjectButton = new JButton("New Project");
        newProjectButton.setBackground(new Color(0x3C3F41));
        newProjectButton.setForeground(new Color(0xD4D4D4));
        newProjectButton.setBorder(BorderFactory.createLineBorder(new Color(0x1E1E1E)));
        newProjectButton.setFocusPainted(false);
        newProjectButton.setFont(new Font("Arial", Font.PLAIN, 14));
        newProjectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            	String name = JOptionPane.showInputDialog(StartUpFrame.this, 
                        "Enter a name for the new project:", "New Project", JOptionPane.PLAIN_MESSAGE);
                    
                if (name == null || name.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(StartUpFrame.this, 
                        "Project name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            	
                String path;
                String type;
                
            	JFileChooser fileChooser = new JFileChooser();
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setFileFilter(new FileNameExtensionFilter("Images (.png, .jpg, .jpeg)", "png", "jpg", "jpeg"));
                int result = fileChooser.showOpenDialog(StartUpFrame.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    path = selectedFile.getAbsolutePath();
                    type = FilenameUtils.getExtension(path);
                }
                else {
                	JOptionPane.showMessageDialog(StartUpFrame.this, 
                    "You must select an image for the project!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            	Project project = new Project(name, path, type);
            	DatabaseUtility.addProject(project);
                MainFrame mainFrame = new MainFrame(project);
                mainFrame.setVisible(true);
                dispose();
            }
        });
        buttonPanel.add(newProjectButton);
        
        projectList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				openButton.setEnabled(true);
			}
        });
    }

    /**
     * Launches the startup window.
     */
    public static void main(String[] args) {
    	
    	try {
			Connection connection = DatabaseUtility.getConnection();
			DatabaseUtility.verifyTableExistance(connection);
		
    	} catch (SQLException e) {
			e.printStackTrace();
		}
    	
        EventQueue.invokeLater(() -> {
            try {
                StartUpFrame frame = new StartUpFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
