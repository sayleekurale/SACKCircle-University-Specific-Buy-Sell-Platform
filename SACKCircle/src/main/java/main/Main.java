package main;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import services.AuthService;
import services.ProductService;
import services.ChatService;
import ui.LoginUI;

public class Main {
    public static MongoClient mongoClient;  // 👈 make it accessible if needed

    public static void main(String[] args) {
        try {
            // Establish MongoDB connection
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase database = mongoClient.getDatabase("SACKCircleDB");
            System.out.println("Connected to MongoDB successfully!");

            // Initialize services
            ProductService productService = new ProductService(database);
            AuthService authService = new AuthService(database);
            ChatService chatService = new ChatService(database);

            // Launch UI
            LoginUI loginUI = new LoginUI(authService, productService, chatService);

            // 👇 Add this block in LoginUI or Main Frame UI (next step)
            // Add shutdown hook in the LoginUI or Main UI window (see next message)

        } catch (Exception e) {
            System.err.println("Error while connecting to MongoDB: " + e.getMessage());
        }
    }
}
