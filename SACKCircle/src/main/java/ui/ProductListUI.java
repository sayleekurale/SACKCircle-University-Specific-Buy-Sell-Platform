package ui;

import models.Product;
import services.ProductService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProductListUI extends JFrame {
    private JPanel productPanel;
    private ProductService productService;

    public ProductListUI(ProductService productService) {
        this.productService = productService;
        setTitle("Product List");
        setSize(500, 400);
        setLayout(new BorderLayout());

        productPanel = new JPanel();
        productPanel.setLayout(new GridLayout(0, 1)); // Adjust layout to display products
        JScrollPane scrollPane = new JScrollPane(productPanel);
        add(scrollPane, BorderLayout.CENTER);

        loadProducts();

        setVisible(true);
    }

    private void loadProducts() {
        productPanel.removeAll(); // Clear existing components
        List<Product> products = productService.getAllProducts();

        for (Product product : products) {
            JPanel productCard = new JPanel(new BorderLayout());
            productCard.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            productCard.setBackground(Color.LIGHT_GRAY);
            
            JLabel nameLabel = new JLabel("Name: " + product.getName());
            JLabel categoryLabel = new JLabel("Category: " + product.getCategory());
            JLabel priceLabel = new JLabel("Price: $" + product.getPrice());

            productCard.add(nameLabel, BorderLayout.NORTH);
            productCard.add(categoryLabel, BorderLayout.CENTER);
            productCard.add(priceLabel, BorderLayout.SOUTH);

            productPanel.add(productCard);
        }
        productPanel.revalidate();
        productPanel.repaint();
    }
}
