package services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;

import db.DBUtil; // MySQL connection utility
import models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * AuthService handles both MongoDB-based customer registration/login
 * and MySQL-based admin registration for the SACKCircle application.
 */
public class AuthService {

    private static MongoCollection<Document> usersCollection;
    private static String loggedInUserId = null;

    private final DBUtil dbUtil; // MySQL utility for admin operations

    // Constructor initializes MongoDB user collection and MySQL utility
    public AuthService(MongoDatabase database) {
        usersCollection = database.getCollection("users");
        dbUtil = new DBUtil();
    }

    /** -------------------- ADMIN REGISTRATION (MySQL) -------------------- **/

    public boolean registerAdmin(String username, String email, String password, String role) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

        try (Connection conn = dbUtil.getConnection()) {
            String query = "INSERT INTO admin (username, email, password, role) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, hashedPassword);
                stmt.setString(4, role);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("✅ Admin registered successfully!");
                    return true;
                } else {
                    System.out.println("❌ Admin registration failed!");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error during admin registration: " + e.getMessage());
        }
        return false;
    }

    /** -------------------- CUSTOMER REGISTRATION (MongoDB) -------------------- **/

    public boolean registerUser(String username, String email, String password, String university) {
        // Check for existing user
        Document existingUser = usersCollection.find(new Document("email", email)).first();
        if (existingUser != null) {
            System.out.println("❌ User already exists!");
            return false;
        }

        // Hash the password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

        // Insert new user document
        User newUser = new User(username, email, hashedPassword, university);
        try {
            usersCollection.insertOne(newUser.toDocument());
            System.out.println("✅ Customer registered successfully!");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error during user registration: " + e.getMessage());
            return false;
        }
    }

    /** -------------------- USER LOGIN -------------------- **/

    public String loginUser(String email, String password) {
        Document userDoc = usersCollection.find(new Document("email", email)).first();
        if (userDoc != null) {
            String storedHashedPassword = userDoc.getString("password");
            if (BCrypt.checkpw(password, storedHashedPassword)) {
                loggedInUserId = userDoc.getObjectId("_id").toHexString();
                System.out.println("✅ Login successful! UserID: " + loggedInUserId);
                return loggedInUserId;
            } else {
                System.out.println("❌ Incorrect password!");
            }
        } else {
            System.out.println("❌ User not found!");
        }
        return null;
    }

    /** -------------------- GET LOGGED-IN USER ID -------------------- **/

    public String getLoggedInUserId() {
        return loggedInUserId;
    }
}
