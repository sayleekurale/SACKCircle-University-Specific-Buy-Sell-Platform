package ui;

import models.Product;
import services.ProductService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WishlistUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel wishlistPanel;
    private ProductService productService;
    private String userId;

    public WishlistUI(ProductService productService, String userId) {
        this.productService = productService;
        this.userId = userId;

        setTitle("💖 Wishlist - SACKCircle");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        wishlistPanel = new JPanel();
        wishlistPanel.setLayout(new BoxLayout(wishlistPanel, BoxLayout.Y_AXIS));
        wishlistPanel.setBackground(new Color(245, 248, 255));

        JScrollPane scrollPane = new JScrollPane(wishlistPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        loadWishlist();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window
        setVisible(true);
    }

    private void loadWishlist() {
        wishlistPanel.removeAll();

        if (userId == null || userId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "User ID is missing. Cannot load wishlist.");
            return;
        }

        List<Product> wishlist = productService.getWishlist(userId);

        if (wishlist.isEmpty()) {
            JLabel emptyLabel = new JLabel("✨ Your wishlist is empty!", JLabel.CENTER);
            emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            wishlistPanel.add(Box.createVerticalGlue());
            wishlistPanel.add(emptyLabel);
            wishlistPanel.add(Box.createVerticalGlue());
        } else {
            for (Product product : wishlist) {
                JPanel productCard = new JPanel(new BorderLayout(10, 10));
                productCard.setBackground(Color.WHITE);
                productCard.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(10, 15, 10, 15),
                        BorderFactory.createLineBorder(new Color(200, 200, 255), 1)
                ));
                productCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

                JLabel nameLabel = new JLabel(product.getName());
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

                JLabel priceLabel = new JLabel("₹" + product.getPrice());
                priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                priceLabel.setForeground(new Color(0, 102, 51));

                JButton removeButton = new JButton("Remove");
                removeButton.setBackground(new Color(255, 102, 102));
                removeButton.setForeground(Color.WHITE);
                removeButton.setFocusPainted(false);
                removeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                removeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                removeButton.addActionListener(e -> {
                    productService.removeFromWishlist(userId, product.getId().toString());
                    loadWishlist();
                });

                JPanel left = new JPanel(new GridLayout(2, 1));
                left.setBackground(Color.WHITE);
                left.add(nameLabel);
                left.add(priceLabel);

                productCard.add(left, BorderLayout.CENTER);
                productCard.add(removeButton, BorderLayout.EAST);

                wishlistPanel.add(Box.createVerticalStrut(8));
                wishlistPanel.add(productCard);
            }
        }

        wishlistPanel.revalidate();
        wishlistPanel.repaint();
    }
}
