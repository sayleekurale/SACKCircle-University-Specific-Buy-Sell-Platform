package services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import models.Product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private final MongoCollection<Document> productCollection;
    private final MongoCollection<Document> wishlistCollection;

    public ProductService(MongoDatabase database) {
        this.productCollection = database.getCollection("products");
        this.wishlistCollection = database.getCollection("wishlist");
        ensureUniqueIndex();
    }

    private void ensureUniqueIndex() {
        boolean indexExists = wishlistCollection.listIndexes()
            .into(new ArrayList<>())
            .stream()
            .anyMatch(index -> index.get("key").equals(new Document("userId", 1).append("productId", 1)));

        if (!indexExists) {
            wishlistCollection.createIndex(
                new Document("userId", 1).append("productId", 1),
                new IndexOptions().unique(true)
            );
        }
    }

    public boolean addProduct(String name, double price, String category, String description, String imagePath, String sellerUsername) {
        try {
            byte[] imageData = Files.readAllBytes(Paths.get(imagePath));
            Binary imageBinary = new Binary(imageData);

            Product product = new Product(name, price, category, description, imageBinary, sellerUsername);
            productCollection.insertOne(product.toDocument());

            System.out.println("✅ Product added successfully!");
            return true;
        } catch (IOException e) {
            System.out.println("❌ Error reading image file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error adding product: " + e.getMessage());
        }
        return false;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        for (Document doc : productCollection.find()) {
            products.add(Product.fromDocument(doc));
        }
        return products;
    }

    public List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        for (Document doc : productCollection.find(Filters.eq("category", category))) {
            products.add(Product.fromDocument(doc));
        }
        return products;
    }

    public Product getProductById(String productId) {
        if (productId == null || productId.trim().isEmpty()) return null;

        Document doc = productCollection.find(Filters.eq("_id", new ObjectId(productId))).first();
        return (doc != null) ? Product.fromDocument(doc) : null;
    }

    public List<Product> getUserProducts(String sellerUsername) {
        if (sellerUsername == null || sellerUsername.trim().isEmpty()) return new ArrayList<>();

        List<Product> products = new ArrayList<>();
        for (Document doc : productCollection.find(Filters.eq("sellerUsername", sellerUsername))) {
            products.add(Product.fromDocument(doc));
        }
        return products;
    }


    public boolean addProduct(String name, double price, String category, String description, String imagePath, String sellerUsername) {
        try {
            byte[] imageData = Files.readAllBytes(Paths.get(imagePath));
            Binary imageBinary = new Binary(imageData);

            Product product = new Product(name, price, category, description, imageBinary, sellerUsername);
            productCollection.insertOne(product.toDocument());

            System.out.println("✅ Product added successfully!");
            return true;
        } catch (IOException e) {
            System.out.println("❌ Error reading image file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error adding product: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteProduct(String productId) {
        if (productId == null || productId.trim().isEmpty()) return false;

        try {
            ObjectId objectId = new ObjectId(productId);
            return productCollection.deleteOne(Filters.eq("_id", objectId)).getDeletedCount() > 0;
        } catch (Exception e) {
            System.out.println("❌ Error deleting product: " + e.getMessage());
        }
        return false;
    }

    public boolean addToWishlist(String userId, String productId) {
        if (userId == null || productId == null) return false;

        try {
            wishlistCollection.insertOne(new Document("userId", userId).append("productId", new ObjectId(productId)));
            return true;
        } catch (Exception e) {
            if (e.getMessage().contains("duplicate key")) {
                System.out.println("⚠️ Product already in wishlist!");
            } else {
                System.out.println("❌ Error adding to wishlist: " + e.getMessage());
            }
        }
        return false;
    }

    public boolean removeFromWishlist(String userId, String productId) {
        if (userId == null || productId == null) return false;

        try {
            return wishlistCollection.deleteOne(Filters.and(
                Filters.eq("userId", userId), 
                Filters.eq("productId", new ObjectId(productId))
            )).getDeletedCount() > 0;
        } catch (Exception e) {
            System.out.println("❌ Error removing from wishlist: " + e.getMessage());
        }
        return false;
    }

    public List<Product> getWishlist(String userId) {
        if (userId == null || userId.trim().isEmpty()) return new ArrayList<>();

        List<Product> wishlistItems = new ArrayList<>();
        for (Document doc : wishlistCollection.find(Filters.eq("userId", userId))) {
            ObjectId productId = doc.getObjectId("productId");
            Document productDoc = productCollection.find(Filters.eq("_id", productId)).first();
            if (productDoc != null) wishlistItems.add(Product.fromDocument(productDoc));
        }
        return wishlistItems;
    }

    public List<Product> searchProducts(String keyword, String category, double minPrice, double maxPrice) {
        List<Product> filteredProducts = new ArrayList<>();
        List<Bson> filters = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            filters.add(Filters.regex("name", ".*" + keyword + ".*", "i"));
        }

        if (category != null && !category.equalsIgnoreCase("All")) {
            filters.add(Filters.eq("category", category));
        }

        if (minPrice > 0 || maxPrice > 0) {
            Bson priceFilter = (minPrice > 0 && maxPrice > 0) ? 
                Filters.and(Filters.gte("price", minPrice), Filters.lte("price", maxPrice)) :
                (minPrice > 0 ? Filters.gte("price", minPrice) : Filters.lte("price", maxPrice));

            filters.add(priceFilter);
        }

        Bson finalFilter = filters.isEmpty() ? new Document() : Filters.and(filters);
        for (Document doc : productCollection.find(finalFilter)) {
            filteredProducts.add(Product.fromDocument(doc));
        }

        return filteredProducts;
    }
}
