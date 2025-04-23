package models;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.bson.types.Binary;

public class Product {
    private ObjectId id;
    private String name;
    private double price;
    private String category;
    private String description;
    private Binary image;  // 🖼️ Store image as binary data
    private String sellerId;

    // ✅ Constructor for new products
    public Product(String name, double price, String category, String description, Binary image, String sellerId) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.image = image;
        this.sellerId = sellerId;
    }

    // ✅ Constructor for products retrieved from DB
    public Product(ObjectId id, String name, double price, String category, String description, Binary image, String sellerId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.image = image;
        this.sellerId = sellerId;
    }

    // ✅ Convert to MongoDB Document
    public Document toDocument() {
        Document doc = new Document()
                .append("name", name)
                .append("category", category)
                .append("price", price)
                .append("description", description)
                .append("image", image)  // 🖼️ Store image as binary
                .append("sellerId", sellerId);

        if (id != null) {
            doc.append("_id", id);
        }
        return doc;
    }

    // ✅ Convert MongoDB Document to Product Object
    public static Product fromDocument(Document doc) {
        return new Product(
                doc.getObjectId("_id"),
                doc.getString("name"),
                doc.getDouble("price"),
                doc.getString("category"),
                doc.getString("description"),
                doc.get("image", Binary.class),  // 🖼️ Retrieve image binary
                doc.getString("sellerId")
        );
    }

    // ✅ Getters
    public ObjectId getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public Binary getImage() { return image; } // 🖼️ Get image binary
    public String getSellerId() { return sellerId; }

    // ✅ Setters
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setImage(Binary image) { this.image = image; } // 🖼️ Set image binary
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }
}
