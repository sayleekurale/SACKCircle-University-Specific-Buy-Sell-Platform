package ui;

import services.AuthService;
import db.DBUtil;
import services.ProductService;
import services.ChatService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class LoginUI extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private JComboBox<String> roleComboBox;

    private AuthService authService;
    private ProductService productService;
    private ChatService chatService;

    private Image getBlurredImage(Image original) {
        int radius = 10;
        float weight = 1.0f / (radius * radius);
        float[] matrix = new float[radius * radius];

        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = weight;
        }

        BufferedImage src = new BufferedImage(original.getWidth(null), original.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = src.createGraphics();
        g2.drawImage(original, 0, 0, null);
        g2.dispose();

        BufferedImageOp op = new ConvolveOp(new Kernel(radius, radius, matrix), ConvolveOp.EDGE_NO_OP, null);
        BufferedImage dest = op.filter(src, null);

        return dest;
    }

    private Image backgroundImg = getBlurredImage(new ImageIcon("resources/bg.jpg").getImage());

    public LoginUI(AuthService authService, ProductService productService, ChatService chatService) {
        this.authService = authService;
        this.productService = productService;
        this.chatService = chatService;

        setTitle("SACKCircle - Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setContentPane(new BackgroundPanel());
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel formPanel = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(new Color(255, 255, 255, 180));
            }
        };
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        UIManager.put("Label.font", font);

        // 🏷️ Welcome Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel welcomeLabel = new JLabel("Welcome to SACKCircle", SwingConstants.CENTER);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formPanel.add(welcomeLabel, gbc);

        gbc.gridwidth = 1;

        // 🎯 Email
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel emailLabel = new JLabel("Email/Username:");
        emailLabel.setForeground(Color.WHITE); // ← White text
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        emailField = new JTextField(15);
        styleTextField(emailField);
        formPanel.add(emailField, gbc);

        // 🔑 Password
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE); // ← White text
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        styleTextField(passwordField);
        formPanel.add(passwordField, gbc);

        // 👥 Role Dropdown
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel roleLabel = new JLabel("Login as:");
        roleLabel.setForeground(Color.WHITE); // ← White text
        formPanel.add(roleLabel, gbc);
        gbc.gridx = 1;
        roleComboBox = new JComboBox<>(new String[]{"Customer", "Admin"});
        styleComboBox(roleComboBox);
        formPanel.add(roleComboBox, gbc);

        // 🔐 Login Button
        gbc.gridx = 0; gbc.gridy = 4;
        loginButton = new JButton("Login");
        styleButton(loginButton, new Color(76, 175, 80));
        formPanel.add(loginButton, gbc);

        // 📝 Register Button
        gbc.gridx = 1;
        registerButton = new JButton("Register");
        styleButton(registerButton, new Color(33, 150, 243));
        formPanel.add(registerButton, gbc);

        add(formPanel);

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> {
            dispose();
            new RegisterUI(authService, productService, chatService);
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    if (main.Main.mongoClient != null) {
                        main.Main.mongoClient.close();
                        System.out.println("MongoDB connection closed from LoginUI.");
                    }
                } catch (Exception ex) {
                    System.err.println("Error while closing MongoDB connection: " + ex.getMessage());
                }
                System.exit(0);
            }
        });

        setVisible(true);
    }

    class BackgroundPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createLineBorder(new Color(120, 120, 120), 1));
        field.setBackground(new Color(255, 255, 255, 230));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(new Color(255, 255, 255, 220));
    }

    private void styleButton(JButton button, Color color) {
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = String.valueOf(passwordField.getPassword()).trim();
        String role = (String) roleComboBox.getSelectedItem();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (role.equals("Admin")) {
            handleAdminLogin(email, password);
        } else {
            handleCustomerLogin(email, password);
        }
    }

    private void handleAdminLogin(String username, String password) {
        try (Connection conn = new DBUtil().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM admin WHERE username=?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                if (BCrypt.checkpw(password, storedHashedPassword)) {
                    JOptionPane.showMessageDialog(this, "✅ Admin Login Successful");
                    dispose();
                    new AdminPanelUI(authService, productService, chatService).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Invalid Admin Credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "❌ Invalid Admin Credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "⚠️ Error connecting to DB.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleCustomerLogin(String email, String password) {
        String userId = authService.loginUser(email, password);
        if (userId != null) {
            JOptionPane.showMessageDialog(this, "✅ Customer Login Successful");
            dispose();
            new DashboardUI(productService, chatService, userId).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "❌ Invalid Customer Credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
