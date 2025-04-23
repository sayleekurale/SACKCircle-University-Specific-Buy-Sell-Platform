package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/sackcircle";
    private static final String USER = "new_user";  // The new user you created
    private static final String PASSWORD = "new_password";   // 🔁 Change to your MySQL password

    // Method to establish and return a database connection
    public Connection getConnection() throws SQLException {
        try {
            // Load the MySQL JDBC driver (if necessary)
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL driver not found.");
        }
    }

    // Save Admin Method
    public Boolean saveAdmin(String username, String email, String password, String university, String role) {
        String query = "INSERT INTO admin (username, email, password, university, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, university);
            stmt.setString(5, role); // Added role parameter
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;  // Return true if admin saved successfully
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Return false if any error occurs
        }
    }

}
