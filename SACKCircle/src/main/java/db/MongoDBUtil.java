package db;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class MongoDBUtil {
    private static final String URI = "mongodb://localhost:27017";
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static void connect() {
        try {
            mongoClient = MongoClients.create(URI);
            database = mongoClient.getDatabase("SACKCircleDB");
            System.out.println("Connected to MongoDB successfully!");
        } catch (Exception e) {
            System.err.println("Error connecting to MongoDB: " + e.getMessage());
        }
    }

    public static MongoDatabase getDatabase() {
        if (database == null) {
            connect(); // auto-reconnect if not already
        }
        return database;
    }

    public static MongoCollection<Document> getProductCollection() {
        if (database == null) {
            connect();
        }
        return database.getCollection("products");
    }


    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("MongoDB connection closed.");
        }
    }
}
