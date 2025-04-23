package services;

import db.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminLogDAO {
    public void logAction(int adminId, String actionType, String description) {
        String sql = "INSERT INTO admin_logs (admin_id, action_type, description) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, adminId);
            stmt.setString(2, actionType);
            stmt.setString(3, description);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

