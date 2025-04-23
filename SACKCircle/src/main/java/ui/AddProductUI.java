package ui;

import models.Product;
import services.ProductService;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AddProductUI extends JFrame {
    private JTextField nameField, priceField, descriptionField;
    private JComboBox<String> categoryDropdown;
    private JButton addButton, cancelButton, selectImageButton;
    private JLabel imagePreview;
    private File selectedImageFile = null;
    private ProductService productService;
    private String sellerId;
    private DashboardUI dashboardUI;

    public AddProductUI(ProductService productService, String userId, DashboardUI dashboardUI) {
        this.productService = productService;
        this.sellerId = userId;
        this.dashboardUI = dashboardUI;

        setTitle("➕ Add New Product");
        setSize(600, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        mainPanel.setBackground(new Color(245, 245, 245));

        // Create and position components
        gbc.insets = new Insets(10, 10, 10, 10); // Add space between components
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(createLabeledField("Product Name:", nameField = new JTextField()), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(createLabeledComponent("Category:", categoryDropdown = new JComboBox<>(new String[]{"Electronics", "Books", "Clothing", "Other"})), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(createLabeledField("Price (₹):", priceField = new JTextField()), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(createLabeledField("Description:", descriptionField = new JTextField()), gbc);

        // Image selection section
        gbc.gridx = 0;
        gbc.gridy = 4;
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BorderLayout());
        selectImageButton = new JButton("📸 Choose Image");
        selectImageButton.setBackground(new Color(0, 150, 136));
        selectImageButton.setForeground(Color.BLACK);
        selectImageButton.setFont(new Font("Arial", Font.BOLD, 14));
        selectImageButton.addActionListener(e -> selectImage());
        imagePanel.add(selectImageButton, BorderLayout.NORTH);

        imagePreview = new JLabel("No Image Selected", SwingConstants.CENTER);
        imagePreview.setPreferredSize(new Dimension(150, 150));
        imagePreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        imagePanel.add(imagePreview, BorderLayout.CENTER);

        mainPanel.add(imagePanel, gbc);

        // Buttons panel
        gbc.gridx = 0;
        gbc.gridy = 5;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        addButton = new JButton("✅ Add Product");
        addButton.setForeground(Color.BLACK);
        cancelButton = new JButton("❌ Cancel");
        cancelButton.setForeground(Color.BLACK);

        addButton.addActionListener(e -> addProduct());
        cancelButton.addActionListener(e -> dispose());

        // Styling buttons
        styleButton(addButton, new Color(0, 128, 0)); // Green for add button
        styleButton(cancelButton, new Color(255, 0, 0)); // Red for cancel button

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createLabeledField(String label, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.add(new JLabel(label), BorderLayout.WEST);
        textField.setPreferredSize(new Dimension(200, 25));
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLabeledComponent(String label, JComboBox<String> comboBox) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.add(new JLabel(label), BorderLayout.WEST);
        panel.add(comboBox, BorderLayout.CENTER);
        return panel;
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(180, 40));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
    }

    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Product Image");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            ImageIcon imageIcon = new ImageIcon(selectedImageFile.getAbsolutePath());
            Image img = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            imagePreview.setIcon(new ImageIcon(img));
            imagePreview.setText("");
        }
    }

    private void addProduct() {
        String name = nameField.getText().trim();
        String category = (String) categoryDropdown.getSelectedItem();
        String priceText = priceField.getText().trim();
        String description = descriptionField.getText().trim();

        if (name.isEmpty() || category.isEmpty() || priceText.isEmpty() || description.isEmpty() || selectedImageFile == null) {
            JOptionPane.showMessageDialog(this, "⚠️ All fields (including image) are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "⚠️ Enter a valid price!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Save the image locally
        File imageDir = new File("images");
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }
        String newImagePath = "images/" + selectedImageFile.getName();
        File newImageFile = new File(newImagePath);
        try {
            Files.copy(selectedImageFile.toPath(), newImageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "❌ Failed to save image!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = productService.addProduct(name, price, category, description, newImagePath, sellerId);
        if (success) {
            JOptionPane.showMessageDialog(this, "🎉 Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            if (dashboardUI != null) {
                dashboardUI.loadProducts(productService.getAllProducts());
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Failed to add product!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
