package ui;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import db.MongoDBUtil;
import models.Product;
import org.bson.Document;
import services.AuthService;
import services.ChatService;
import services.ProductService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AdminPanelUI extends JFrame {
    private JTable customerTable;
    private JTable productTable;
    private JPanel contentPanel;
    private JPanel homePanel;
    private JPanel usersPanel;
    private JPanel editPanel;

    private AuthService authService;
    private ProductService productService;
    private ChatService chatService;
    private MongoDatabase mongoDatabase;

    public AdminPanelUI(AuthService authService, ProductService productService, ChatService chatService) {
        this.authService = authService;
        this.productService = productService;
        this.chatService = chatService;

        MongoDBUtil.connect();
        this.mongoDatabase = MongoDBUtil.getDatabase();

        setTitle("SACKCircle - Admin Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel("👑 Admin Dashboard - SACKCircle", JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setOpaque(true);
        header.setBackground(new Color(45, 45, 80));
        header.setForeground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 10, 20, 10));
        add(header, BorderLayout.NORTH);

        // Content Panel
        contentPanel = new JPanel(new CardLayout());
        add(contentPanel, BorderLayout.CENTER);

        initHomePanel();
        initUsersPanel();
        initEditPanel();

        contentPanel.add(homePanel, "HOME");
        contentPanel.add(usersPanel, "USERS");
        contentPanel.add(editPanel, "EDIT");

        showPanel("HOME");

        setVisible(true);
    }

    private void initHomePanel() {
        homePanel = new JPanel(new GridBagLayout());
        homePanel.setBackground(new Color(240, 245, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        JLabel welcome = new JLabel("Welcome Admin 👋");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        gbc.gridx = 0;
        gbc.gridy = 0;
        homePanel.add(welcome, gbc);

        JButton viewUsersBtn = new JButton("👥 View Users");
        viewUsersBtn.setPreferredSize(new Dimension(200, 40));
        viewUsersBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        viewUsersBtn.addActionListener(e -> {
            loadCustomerData();
            showPanel("USERS");
        });

        gbc.gridy = 1;
        homePanel.add(viewUsersBtn, gbc);

        JButton editUsersBtn = new JButton("🛠️ Manage Users");
        editUsersBtn.setPreferredSize(new Dimension(200, 40));
        editUsersBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        editUsersBtn.addActionListener(e -> {
            // Placeholder for future features
            showPanel("EDIT");
        });

        gbc.gridy = 2;
        homePanel.add(editUsersBtn, gbc);
    }

    private void initUsersPanel() {
        usersPanel = new JPanel(new BorderLayout());

        customerTable = new JTable();
        productTable = new JTable();

        JScrollPane customerScrollPane = new JScrollPane(customerTable);
        JScrollPane productScrollPane = new JScrollPane(productTable);

        customerScrollPane.setBorder(BorderFactory.createTitledBorder("👥 Customers"));
        productScrollPane.setBorder(BorderFactory.createTitledBorder("📦 Products"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, customerScrollPane, productScrollPane);
        splitPane.setResizeWeight(0.4);
        usersPanel.add(splitPane, BorderLayout.CENTER);

        // Event: click customer to view products
        customerTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow != -1) {
                    String username = customerTable.getValueAt(selectedRow, 0).toString();
                    showProductsForCustomer(username);
                }
            }
        });

        JButton backBtn = new JButton("⬅️ Back");
        backBtn.addActionListener(e -> showPanel("HOME"));
        usersPanel.add(backBtn, BorderLayout.SOUTH);
    }

    private void initEditPanel() {
        editPanel = new JPanel(new BorderLayout());
        editPanel.setBackground(Color.LIGHT_GRAY);

        JLabel comingSoon = new JLabel("🔧 User Management coming soon...", JLabel.CENTER);
        comingSoon.setFont(new Font("Segoe UI", Font.ITALIC, 22));
        editPanel.add(comingSoon, BorderLayout.CENTER);

        JButton backBtn = new JButton("⬅️ Back");
        backBtn.addActionListener(e -> showPanel("HOME"));
        editPanel.add(backBtn, BorderLayout.SOUTH);
    }

    private void showPanel(String name) {
        CardLayout cl = (CardLayout) (contentPanel.getLayout());
        cl.show(contentPanel, name);
    }

    private void loadCustomerData() {
        MongoCollection<Document> customersCollection = mongoDatabase.getCollection("users");
        List<Document> customers = customersCollection.find().into(new java.util.ArrayList<>());

        String[] columnNames = {"Username", "Email", "University", "Products Count", "Phone"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Document customer : customers) {
            String username = customer.getString("username") != null ? customer.getString("username") : "N/A";
            String email = customer.getString("email") != null ? customer.getString("email") : "N/A";
            String university = customer.getString("university") != null ? customer.getString("university") : "N/A";
            String phone = customer.getString("phone") != null ? customer.getString("phone") : "—";
            int productsCount = getProductsCountForCustomer(username);
            model.addRow(new Object[]{username, email, university, productsCount, phone});
        }

        customerTable.setModel(model);
    }

    private int getProductsCountForCustomer(String username) {
        MongoCollection<Document> productsCollection = MongoDBUtil.getProductCollection();
        List<Document> products = productsCollection.find(new Document("sellerUsername", username)).into(new java.util.ArrayList<>());
        return products.size();
    }

    private void showProductsForCustomer(String username) {
        MongoCollection<Document> productsCollection = MongoDBUtil.getProductCollection();
        List<Document> products = productsCollection.find(new Document("sellerUsername", username)).into(new java.util.ArrayList<>());

        String[] columnNames = {"Name", "Price (₹)", "Description"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Document product : products) {
            String productName = product.getString("name");
            Double price = product.getDouble("price");
            String description = product.getString("description");
            model.addRow(new Object[]{productName, price, description});
        }

        productTable.setModel(model);
    }

    public static void main(String[] args) {
        MongoDBUtil.connect();
        MongoDatabase mongoDatabase = MongoDBUtil.getDatabase();

        AuthService authService = new AuthService(mongoDatabase);
        ProductService productService = new ProductService(mongoDatabase);
        ChatService chatService = new ChatService(mongoDatabase);

        new AdminPanelUI(authService, productService, chatService);
    }
}
