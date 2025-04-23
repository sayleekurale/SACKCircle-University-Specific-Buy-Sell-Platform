package models;

import org.bson.Document;

public class Chatmessages {
    private String senderId;
    private String receiverId;
    private String message;
    private long timestamp;

    // ✅ Constructor
    public Chatmessages(String senderId, String receiverId, String message, long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
    }

    // ✅ Convert to MongoDB Document
    public Document toDocument() {
        return new Document("senderId", senderId)
                .append("receiverId", receiverId)
                .append("message", message)
                .append("timestamp", timestamp);
    }

    // ✅ Convert MongoDB Document to ChatMessage Object
    public static Chatmessages fromDocument(Document doc) {
        return new Chatmessages(
                doc.getString("senderId"),
                doc.getString("receiverId"),
                doc.getString("message"),
                doc.getLong("timestamp")
        );
    }

    // ✅ Getters
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }

    // ✅ Setters (Optional, if needed)
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public void setMessage(String message) { this.message = message; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
