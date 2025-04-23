package ui;

import models.Product;
import services.ProductService;
import services.ChatService;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.bson.types.ObjectId;

public class DashboardUI extends JFrame {
    private JPanel productPanel;
    private ProductService productService;
    private ChatService chatService;
    private String currentUserId;

    public DashboardUI(ProductService productService, ChatService chatService, String userId) {
        this.productService = productService;
        this.chatService = chatService;
        this.currentUserId = userId;

        setTitle("SACKCircle - Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createTopPanel(), BorderLayout.NORTH);
        add(createProductListPanel(), BorderLayout.CENTER);

        loadProducts(productService.getAllProducts());
        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());

        // 📸 Banner
        JLabel banner = new JLabel() {
            @Override
            public void setIcon(Icon icon) {
                // Override setIcon to resize the image when the label is resized
                super.setIcon(icon);
            }
        };

        // Load the image and scale it to fit the panel
        ImageIcon originalIcon = new ImageIcon("resources/neavy.jpg");
        Image img = originalIcon.getImage(); // Get the image from the icon

        // Scale the image dynamically to fit the width and height of the container
        Image scaledImg = img.getScaledInstance(1300, 250, Image.SCALE_SMOOTH); // Scale to preferred size
        ImageIcon scaledIcon = new ImageIcon(scaledImg); // Create a new ImageIcon with the scaled image

        // Set the scaled icon to the label
        banner.setIcon(scaledIcon);

        // Set preferred size for the panel where the image should appear
        banner.setPreferredSize(new Dimension(800, 150));

        // Add the banner to the top panel
        topPanel.add(banner, BorderLayout.NORTH);

        // 🔍 Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        searchPanel.setBackground(new Color(245, 245, 245));

        JTextField searchField = new JTextField(25);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JButton searchBtn = new JButton("🔍 Search");
        searchBtn.setFocusPainted(false);
        searchBtn.setBackground(new Color(70, 130, 180));
        searchBtn.setForeground(Color.BLACK);
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 13));

        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        topPanel.add(searchPanel, BorderLayout.SOUTH);

        return topPanel;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(260, getHeight()));
        sidebar.setBackground(new Color(25, 25, 40));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        Font categoryFont = new Font("Segoe UI", Font.BOLD, 15);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 16);

        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createSidebarCategory("📚 Books", "books", categoryFont));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createSidebarCategory("🛋️ Furniture", "Furniture", categoryFont));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createSidebarCategory("🧥 Clothing", "clothing", categoryFont));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createSidebarCategory("💻 Electronics", "electronics", categoryFont));

        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        sidebar.add(createStyledSidebarButton("🏠 Home", () -> loadProducts(productService.getAllProducts()), buttonFont));
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(createStyledSidebarButton("➕ Add Product", this::openAddProductUI, buttonFont));
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(createStyledSidebarButton("❤️ Wishlist", this::openWishlistUI, buttonFont));
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(createStyledSidebarButton("💬 Chat", () -> openChatUI(currentUserId), buttonFont));

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(createStyledSidebarButton("🚪 Logout", this::logout, buttonFont));

        return sidebar;
    }

    private JLabel createSidebarCategory(String name, String category, Font font) {
        JLabel label = new JLabel(name);
        label.setFont(font);
        label.setForeground(new Color(173, 216, 230));
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loadCategoryProducts(category);
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                label.setForeground(new Color(173, 216, 230));
            }
        });
        return label;
    }

    private JButton createStyledSidebarButton(String text, Runnable action, Font font) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setFont(font);
        button.setFocusPainted(false);
        button.setBackground(new Color(60, 60, 90));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setOpaque(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 60, 90));
            }
        });

        button.addActionListener(e -> action.run());
        return button;
    }

    private JScrollPane createProductListPanel() {
        productPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        productPanel.setBackground(new Color(250, 250, 250));

        JScrollPane scrollPane = new JScrollPane(productPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        return scrollPane;
    }

    public void loadProducts(List<Product> products) {
        productPanel.removeAll();

        for (Product product : products) {
            productPanel.add(createProductCard(product));
        }

        productPanel.revalidate();
        productPanel.repaint();
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(250, 230));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel imageLabel = new JLabel(getImageIconFromBinary(product.getImage()));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(imageLabel);

        card.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel nameLabel = new JLabel(product.getName(), JLabel.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nameLabel);

        JLabel priceLabel = new JLabel("₹" + product.getPrice(), JLabel.CENTER);
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        priceLabel.setForeground(new Color(34, 139, 34));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(priceLabel);

        card.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        JButton detailsButton = new JButton("Chat with Seller");
        detailsButton.setPreferredSize(new Dimension(130, 30));
        detailsButton.setBackground(new Color(65, 105, 225));
        detailsButton.setForeground(Color.BLACK);
        detailsButton.setFocusPainted(false);
        detailsButton.setFont(new Font("SansSerif", Font.PLAIN, 13));
        detailsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        detailsButton.addActionListener(e -> openChatUI(product.getSellerId()));

        JButton wishlistButton = new JButton("❤️");
        wishlistButton.setPreferredSize(new Dimension(50, 30));
        wishlistButton.setBackground(Color.WHITE);
        wishlistButton.setFocusPainted(false);
        wishlistButton.setToolTipText("Add to Wishlist");
        wishlistButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        wishlistButton.addActionListener(e -> {
            productService.addToWishlist(currentUserId, product.getId().toHexString());
            JOptionPane.showMessageDialog(this, "Added to Wishlist!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        buttonPanel.add(detailsButton);
        buttonPanel.add(wishlistButton);

        card.add(buttonPanel);
        return card;
    }

    private ImageIcon getImageIconFromBinary(org.bson.types.Binary binaryImage) {
        if (binaryImage == null) return new ImageIcon();

        try (ByteArrayInputStream bis = new ByteArrayInputStream(binaryImage.getData())) {
            BufferedImage bufferedImage = ImageIO.read(bis);
            if (bufferedImage != null) {
                Image scaledImage = bufferedImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ImageIcon();
    }

    private void openWishlistUI() {
        new WishlistUI(productService, currentUserId).setVisible(true);
    }

    private void openAddProductUI() {
        new AddProductUI(productService, currentUserId, this).setVisible(true);
    }

    private void openChatUI(String sellerId) {
        if (sellerId == null || sellerId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Invalid seller ID!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new ChatUI(chatService, currentUserId, sellerId);
    }

    private void loadCategoryProducts(String category) {
        List<Product> categoryProducts = productService.getProductsByCategory(category);
        loadProducts(categoryProducts);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginUI(null, productService, chatService).setVisible(true);
        }
    }
}