package com.churchmate.ui;

import com.churchmate.controller.ChatManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserUI extends JFrame {

    // Atribut sesuai Class Diagram
    private JTextArea chatWindow;
    private JTextField inputField;

    // Relasi ke ChatManager sesuai panah di diagram
    private ChatManager chatManager;

    public UserUI() {
        chatManager = new ChatManager();

        setTitle("CHURCHMATE - CHATBOT GEREJA");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // Method sesuai Class Diagram
    public void showChatInterface() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header (Warna Biru)
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(75, 60, 200));
        header.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel titleLabel = new JLabel("  CHURCHMATE   CHATBOT GEREJA");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        header.add(titleLabel, BorderLayout.WEST);

        // Sidebar (Kiri)
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(240, 240, 245));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnNewChat = new JButton("PERCAKAPAN BARU");
        btnNewChat.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnNewChat.setMaximumSize(new Dimension(180, 35));

        JButton btnSearchChat = new JButton("CARI PERCAKAPAN");
        btnSearchChat.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSearchChat.setMaximumSize(new Dimension(180, 35));

        sidebar.add(btnNewChat);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnSearchChat);

        // Chat Area (Tengah) -> Ini adalah atribut -chatWindow
        chatWindow = new JTextArea();
        chatWindow.setEditable(false);
        chatWindow.setLineWrap(true);
        chatWindow.setWrapStyleWord(true);
        chatWindow.setFont(new Font("SansSerif", Font.PLAIN, 14));
        chatWindow.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(chatWindow);

        // Sapaan awal Chatbot
        displayMessage("Churchmate Bot:\nHalo, mau tahu informasi apa hari ini?");

        // Input Area (Bawah) -> Ini adalah atribut -inputField
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        inputField = new JTextField();
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JButton btnSend = new JButton("Kirim ↑");
        btnSend.setBackground(new Color(220, 220, 220));

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(btnSend, BorderLayout.EAST);

        // Logika ketika tombol kirim ditekan
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = inputField.getText();
                if (!text.trim().isEmpty()) {
                    displayMessage("User:\n" + text);
                    inputField.setText(""); // Kosongkan field

                    // TODO: Hubungkan dengan chatManager.sendMessage() nanti
                    displayMessage("Churchmate Bot:\nMaaf, sistem AI balasan sedang dikembangkan.");
                }
            }
        });

        // Agar bisa kirim pesan dengan menekan tombol 'Enter' di keyboard
        inputField.addActionListener(e -> btnSend.doClick());

        // Gabungkan semua komponen
        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    // Method sesuai Class Diagram
    public void displayMessage(String msg) {
        if (chatWindow != null) {
            chatWindow.append(msg + "\n\n");
            // Otomatis scroll ke bawah saat ada pesan baru
            chatWindow.setCaretPosition(chatWindow.getDocument().getLength());
        }
    }

    // Main method untuk menjalankan khusus UI ini (Testing)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserUI ui = new UserUI();
            ui.showChatInterface();
        });
    }
}