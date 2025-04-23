package models;

import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;

public class User {
    private String username;
    private String email;
    private String password; // This should always be stored as a hash
    private String university;

    public User(String username, String email, String password, String university) {
        this.username = username;
        this.email = email;
        this.password = password; // Already hashed in AuthService
        this.university = university;
    }

    // Validate password using BCrypt
    public boolean validatePassword(String enteredPassword) {
        return BCrypt.checkpw(enteredPassword, this.password);
    }

    // Convert user data to MongoDB Document
    public Document toDocument() {
        return new Document("username", username)
                .append("email", email)
                .append("password", password) // Already hashed
                .append("university", university);
    }
}
