package services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatService {
    private final MongoCollection<Document> chatCollection;

    public ChatService(MongoDatabase database) {
        this.chatCollection = database.getCollection("chats"); // ✅ MongoDB Chat Collection
    }

    // 📩 Send message (Buyer -> Seller)
    public void sendMessage(String senderId, String receiverId, String message) {
        Document chatMessage = new Document("senderId", senderId)
                .append("receiverId", receiverId)
                .append("message", "Me: " + message)
                .append("timestamp", System.currentTimeMillis());

        chatCollection.insertOne(chatMessage);
    }

    // 📩 Seller replies
    public void receiveMessage(String senderId, String receiverId, String message) {
        Document chatMessage = new Document("senderId", senderId)
                .append("receiverId", receiverId)
                .append("message", "Seller: " + message)
                .append("timestamp", System.currentTimeMillis());

        chatCollection.insertOne(chatMessage);
    }

    // 📜 Fetch chat history between two users
    public List<String> getChatHistory(String userId, String otherUserId) {
        List<String> messages = new ArrayList<>();

        chatCollection.find(Filters.or(
                Filters.and(Filters.eq("senderId", userId), Filters.eq("receiverId", otherUserId)),
                Filters.and(Filters.eq("senderId", otherUserId), Filters.eq("receiverId", userId))
        )).forEach(document -> messages.add(document.getString("message")));

        return messages;
    }

    // 💰 Make an Offer
    public void makeOffer(String senderId, String receiverId, double offerPrice) {
        String offerMessage = "Me: 💰 Offered ₹" + offerPrice;
        sendMessage(senderId, receiverId, offerMessage);
        receiveMessage(receiverId, senderId, "Seller: 📢 I’ll consider your offer.");
    }

    // 🔥 Place a Bid
    public void placeBid(String senderId, String receiverId, double bidPrice) {
        String bidMessage = "Me: 🔥 Placed a bid of ₹" + bidPrice;
        sendMessage(senderId, receiverId, bidMessage);
        receiveMessage(receiverId, senderId, "Seller: ✅ Your bid has been recorded.");
    }

    // ✅ Common OLX-style Quick Responses
    public List<String> getCommonResponses() {
        return Arrays.asList(
            "Is this still available?",
            "What’s the final price?",
            "Can you share more details?",
            "Where is the pickup location?",
            "Is there a discount available?"
        );
    }
}
