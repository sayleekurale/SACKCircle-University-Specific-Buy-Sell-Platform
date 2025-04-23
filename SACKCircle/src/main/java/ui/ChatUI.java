package ui;

import models.Chatmessages;
import services.ChatService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;


public class ChatUI extends JFrame {
    private ChatService chatService;
    private String userId, receiverId;
    private JPanel chatPanel;
    private JTextField messageField;
    private JScrollPane scrollPane;

    public ChatUI(ChatService chatService, String userId, String receiverId) {
        if (chatService == null || userId == null || receiverId == null) {
            JOptionPane.showMessageDialog(null, "Chat service or User ID is null!", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("ChatService, userId, or receiverId cannot be null");
        }

        this.chatService = chatService;
        this.userId = userId;
        this.receiverId = receiverId;

        setTitle("\uD83D\uDCAC Chat - SACKCircle");
        setSize(420, 650);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Chat History Panel
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(245, 248, 255));
        chatPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(new Color(230, 245, 255));
        add(scrollPane, BorderLayout.CENTER);

        JPanel suggestionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        suggestionsPanel.setBackground(new Color(240, 245, 250));
        suggestionsPanel.setBorder(BorderFactory.createTitledBorder("Quick Messages"));
        suggestionsPanel.setPreferredSize(new Dimension(getWidth(), 80));  // Adjust height as needed


        String[] suggestions = {"What's the price?", "Is it still available?", "Can you share more photos?", "Can we negotiate?", "My bid is ₹500", "I can pay via UPI"};
        for (String suggestion : suggestions) {
            JButton suggestionBtn = new JButton(suggestion);
            suggestionBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
            suggestionBtn.setBackground(new Color(220, 230, 250));
            suggestionBtn.setBorder(BorderFactory.createLineBorder(new Color(150, 180, 220)));
            suggestionBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            suggestionBtn.addActionListener(e -> {
                messageField.setText(suggestion);
                messageField.requestFocus();
            });
            suggestionsPanel.add(suggestionBtn);
        }

        add(suggestionsPanel, BorderLayout.NORTH);

        // Input Section
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBackground(new Color(250, 250, 255));
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)));

        messageField = new JTextField();
        messageField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        messageField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                new EmptyBorder(8, 12, 8, 12)));
        inputPanel.add(messageField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        sendButton.setBackground(new Color(0, 120, 215));
        sendButton.setForeground(Color.BLACK);
        sendButton.setFocusPainted(false);
        sendButton.setPreferredSize(new Dimension(80, 38));
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendButton.addActionListener(e -> sendMessage());
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        loadChatHistory();
        setVisible(true);
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            chatService.sendMessage(userId, receiverId, message);
            addMessageToUI("Me: " + message, true);
            messageField.setText("");
        }
    }

    private void loadChatHistory() {
        chatPanel.removeAll();
        List<String> messages = chatService.getChatHistory(userId, receiverId);

        for (String msg : messages) {
            boolean isMine = msg.startsWith("Me:");
            addMessageToUI(msg, isMine);
        }

        chatPanel.revalidate();
        chatPanel.repaint();
        scrollToBottom();
    }

    private void addMessageToUI(String message, boolean isMine) {
        JPanel bubbleWrapper = new JPanel(new FlowLayout(isMine ? FlowLayout.RIGHT : FlowLayout.LEFT, 10, 4));
        bubbleWrapper.setOpaque(false);

        JLabel messageLabel = new JLabel("<html><div style='padding: 10px 15px; border-radius: 18px; box-shadow: 2px 2px 5px rgba(0,0,0,0.1); background-color:" +
                (isMine ? "#0078D7" : "#ECECEC") + "; color: " + (isMine ? "white" : "black") + "; max-width: 240px;'>" + message + "</div></html>");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        bubbleWrapper.setBorder(new EmptyBorder(4, 10, 4, 10));
        bubbleWrapper.add(messageLabel);
        chatPanel.add(bubbleWrapper);
        chatPanel.revalidate();
        scrollToBottom();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum()));
    }
}