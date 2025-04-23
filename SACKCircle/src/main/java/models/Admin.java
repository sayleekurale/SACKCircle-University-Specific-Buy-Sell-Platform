package models;

public class Admin {
    private int id;
    private String username;
    private String email;
    private String passwordHash;

    // Default constructor
    public Admin() {}

    // Parameterized constructor
    public Admin(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // Full constructor including ID
    public Admin(int id, String username, String email, String passwordHash) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // Optional: toString() for easier debugging
    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
