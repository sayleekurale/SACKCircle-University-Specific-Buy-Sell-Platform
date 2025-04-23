package ui;

import services.AuthService;
import services.ProductService;
import services.ChatService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class RegisterUI extends JFrame {
    private JTextField usernameField, emailField, universityField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JRadioButton customerRadioButton, adminRadioButton;
    private ButtonGroup userTypeGroup;
    private AuthService authService;
    private ProductService productService;
    private ChatService chatService;
    private Image backgroundImg = getBlurredImage(new ImageIcon("resources/rg-bg.jpeg").getImage());

    public RegisterUI(AuthService authService, ProductService productService, ChatService chatService) {
        this.authService = authService;
        this.productService = productService;
        this.chatService = chatService;

        setTitle("Register");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setContentPane(new BackgroundPanel());
        setLayout(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // 🏷️ Caption Heading
        JLabel headingLabel = new JLabel("Join the SACKCircle");
        headingLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headingLabel.setForeground(Color.WHITE);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(headingLabel, gbc);

        gbc.gridwidth = 1;

        // 📌 Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setForeground(Color.WHITE);
        usernameField = new JTextField(20);
        styleTextField(usernameField);
        addComponent(usernameLabel, 0, 1, gbc);
        addComponent(usernameField, 1, 1, gbc);

        // 📧 Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setForeground(Color.WHITE);
        emailField = new JTextField(20);
        styleTextField(emailField);
        addComponent(emailLabel, 0, 2, gbc);
        addComponent(emailField, 1, 2, gbc);

        // 🔑 Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(Color.WHITE);
        passwordField = new JPasswordField(20);
        styleTextField(passwordField);
        addComponent(passwordLabel, 0, 3, gbc);
        addComponent(passwordField, 1, 3, gbc);

        // 🎓 University
        JLabel universityLabel = new JLabel("University:");
        universityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        universityLabel.setForeground(Color.WHITE);
        universityField = new JTextField(20);
        styleTextField(universityField);
        addComponent(universityLabel, 0, 4, gbc);
        addComponent(universityField, 1, 4, gbc);

        // 🧑‍💼 User Type Selection
        JLabel userTypeLabel = new JLabel("User Type:");
        userTypeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userTypeLabel.setForeground(Color.WHITE);
        customerRadioButton = new JRadioButton("Customer", true);
        adminRadioButton = new JRadioButton("Admin");
        customerRadioButton.setFont(new Font("Arial", Font.PLAIN, 14));
        adminRadioButton.setFont(new Font("Arial", Font.PLAIN, 14));
        userTypeGroup = new ButtonGroup();
        userTypeGroup.add(customerRadioButton);
        userTypeGroup.add(adminRadioButton);

        JPanel userTypePanel = new JPanel(new FlowLayout());
        userTypePanel.setOpaque(false);
        userTypePanel.add(customerRadioButton);
        userTypePanel.add(adminRadioButton);

        addComponent(userTypeLabel, 0, 5, gbc);
        addComponent(userTypePanel, 1, 5, gbc);

        // ✅ Register Button
        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(76, 175, 80));
        registerButton.setPreferredSize(new Dimension(200, 40));
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(registerButton);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 6;
        add(buttonPanel, gbc);

        setVisible(true);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createLineBorder(new Color(120, 120, 120), 1));
        field.setBackground(new Color(255, 255, 255, 230));
    }

    private void addComponent(Component component, int gridx, int gridy, GridBagConstraints gbc) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        add(component, gbc);
    }

    private void handleRegistration() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String university = universityField.getText().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || university.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean isAdmin = adminRadioButton.isSelected();

        boolean registrationSuccess;
        if (isAdmin) {
            registrationSuccess = authService.registerAdmin(username, email, password, university);
        } else {
            registrationSuccess = authService.registerUser(username, email, password, university);
        }

        if (registrationSuccess) {
            JOptionPane.showMessageDialog(this, (isAdmin ? "Admin" : "User") + " registration successful! Please login.");
            dispose();
            new LoginUI(authService, productService, chatService);
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed! User may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

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

    class BackgroundPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        new RegisterUI(new AuthService(null), new ProductService(null), new ChatService(null));
    }
}
