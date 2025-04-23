package services;

import db.DBUtil;
import models.Admin;

import java.sql.*;

public class AdminDAO {

    public void registerAdmin(Admin admin) {
        String sql = "INSERT INTO admins (username, email, password_hash) VALUES (?, ?, ?)";

        try (Connection conn = MySQLUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, admin.getUsername());
            stmt.setString(2, admin.getEmail());
            stmt.setString(3, admin.getPasswordHash());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Admin loginAdmin(String username, String passwordHash) {
        String sql = "SELECT * FROM admins WHERE username = ? AND password_hash = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Admin admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setUsername(rs.getString("username"));
                admin.setEmail(rs.getString("email"));
                return admin;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
