package Utility;

import java.sql.*;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import project.Project;

/**
 * DatabaseUtility is a utility class that provides static methods
 * to manipulate data in the database, create the table or connect to it.
 */
public class DatabaseUtility {
    private static final String URL = "jdbc:mysql://localhost:3306/PhotoAppDB";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static Statement statement;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Verifies the existence of the table in the database.
     * 
     * @param con The Connection to the database.
     */
    public static void verifyTableExistance(Connection con) {
    	String createTableSQL = "CREATE TABLE IF NOT EXISTS Projects (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "path VARCHAR(255) NOT NULL," +
                "file_type VARCHAR(255) NOT NULL" +
                ")";
		try {
			statement = con.createStatement();
			statement.executeUpdate(createTableSQL);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			 try {
	                if (statement != null) {
	                    statement.close();
	                }
	                if (con != null) {
	                    con.close();
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	         }
		}
		
    }
    
    /**
     * Loads all saved projects from the database and adds them to the project list.
     *
     * @param projectListModel The model for the JList to populate with project names.
     * @param projectList The component from the frame.
     */
    public static void loadProjectsFromDatabase(DefaultListModel<String> projectListModel, JList<String> projectList) {
        String query = "SELECT name, path, file_type FROM projects";
        boolean isEmpty = true;
        try (Connection connection = DatabaseUtility.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                isEmpty = false;
                String name = resultSet.getString("name");
                String path = resultSet.getString("path");
                String type = resultSet.getString("file_type");
                projectListModel.addElement(name + " - " + path + " - " + type);
            }
             
            if (isEmpty) {
                projectListModel.addElement("No projects found. Create a new project to get started!");
                projectList.setEnabled(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading projects from database!", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Adds a project's name, path and type to the database.
     * 
     * @param p The current project that contains all the information needed.
     */
    public static void addProject(Project p) {
        String query = "INSERT INTO Projects (name, path, file_type) VALUES (?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, p.getName());
            preparedStatement.setString(2, p.getPath());
            preparedStatement.setString(3, p.getType());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Removes a project from the database based on the path.
     * 
     * @param p The current project's path to the project's image.
     */
    public static void deleteProject(String p) {
    	String query = "DELETE FROM Projects WHERE path = (?)";
    	try (Connection connection = getConnection();
    		 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    		preparedStatement.setString(1, p);
    		preparedStatement.executeUpdate();
    		preparedStatement.close();
    		connection.close();
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
}
