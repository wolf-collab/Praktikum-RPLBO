package com.churchmate.ui;

import com.churchmate.controller.ChatManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class UserUI extends JFrame {

    private JPanel chatPanel; // Mengganti JTextArea menjadi JPanel
    private JScrollPane scrollPane;
    private JTextField inputField;
    private final ChatManager chatManager;

    public UserUI(ChatManager chatManager) {
        this.chatManager = chatManager;

        setTitle("Churchmate - User Chatbot");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void showChatInterface() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // ==========================================
        // 1. HEADER & SIDEBAR (Tetap sama)
        // ==========================================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(74, 59, 204));
        header.setPreferredSize(new Dimension(getWidth(), 50));
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("CHURCHMATE   CHATBOT GEREJA");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        header.add(titleLabel, BorderLayout.WEST);

        JLabel statusLabel = new JLabel("STATUS: TERHUBUNG  🟢");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        header.add(statusLabel, BorderLayout.EAST);

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(new Color(245, 245, 245));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(20, 10, 10, 10));

        JButton btnNewChat = createSidebarButton("PERCAKAPAN BARU", true);
        JButton btnSearchChat = createSidebarButton("CARI PERCAKAPAN", false);

        navPanel.add(btnNewChat);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(btnSearchChat);

        JPanel bottomSidebar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomSidebar.setOpaque(false);
        bottomSidebar.setBorder(new EmptyBorder(0, 0, 20, 0));
        JButton btnLogout = new JButton("LOGOUT");
        bottomSidebar.add(btnLogout);

        sidebar.add(navPanel, BorderLayout.NORTH);
        sidebar.add(bottomSidebar, BorderLayout.SOUTH);

        // ==========================================
        // 3. CHAT AREA (Diubah menjadi Panel dinamis)
        // ==========================================
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Color.WHITE);
        chatPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        // Mengatur agar scroll lebih mulus
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Menampilkan pesan sambutan awal (Kiri - Bot)
        addChatBubble("Halo! Saya Churchmate Bot.\nMau tahu informasi apa hari ini? (Misal: 'jadwal ibadah', 'kegiatan', 'alamat')", false);

        // ==========================================
        // 4. INPUT AREA (Tetap sama)
        // ==========================================
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(15, 20, 20, 20));

        JPanel inputWrapper = new JPanel(new BorderLayout(10, 0));
        inputWrapper.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        inputWrapper.setBackground(Color.WHITE);
        inputWrapper.setPreferredSize(new Dimension(0, 45));

        JButton btnMenu = new JButton("≡ MENU");
        btnMenu.setBackground(new Color(74, 59, 204));
        btnMenu.setForeground(Color.WHITE);
        btnMenu.setFocusPainted(false);

        JPopupMenu faqMenu = new JPopupMenu();
        JMenuItem faq1 = new JMenuItem("Jadwal ibadah?");
        JMenuItem faq2 = new JMenuItem("Agenda kegiatan?");
        JMenuItem faq3 = new JMenuItem("Alamat gereja?");
        faqMenu.add(faq1); faqMenu.add(faq2); faqMenu.add(faq3);

        btnMenu.addActionListener(e -> faqMenu.show(btnMenu, 0, -faqMenu.getPreferredSize().height));

        faq1.addActionListener(e -> processUserInput("jadwal ibadah"));
        faq2.addActionListener(e -> processUserInput("kegiatan"));
        faq3.addActionListener(e -> processUserInput("alamat gereja"));

        inputField = new JTextField();
        inputField.setBorder(new EmptyBorder(0, 10, 0, 10));
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnSend = new JButton("↑");
        btnSend.setFont(new Font("Arial", Font.BOLD, 16));
        btnSend.setBackground(new Color(230, 230, 230));

        inputWrapper.add(btnMenu, BorderLayout.WEST);
        inputWrapper.add(inputField, BorderLayout.CENTER);
        inputWrapper.add(btnSend, BorderLayout.EAST);

        bottomPanel.add(inputWrapper, BorderLayout.CENTER);

        btnSend.addActionListener(e -> processUserInput(inputField.getText()));
        inputField.addActionListener(e -> processUserInput(inputField.getText()));

        // ==========================================
        // GABUNGKAN SEMUA
        // ==========================================
        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    // ==========================================
    // LOGIKA CHAT KIRI-KANAN (CHAT BUBBLE)
    // ==========================================
    private void processUserInput(String text) {
        text = text.trim();
        if (!text.isEmpty()) {
            // 1. Tampilkan pesan User di KANAN
            addChatBubble(text, true);
            inputField.setText("");

            // 2. Panggil ChatService
            String balasan = chatManager.sendMessage(text);

            // 3. Tampilkan pesan Bot di KIRI
            addChatBubble(balasan, false);
        }
    }

    private void addChatBubble(String message, boolean isUser) {
        // Baris wadah untuk satu bubble
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Area teks bubble
        JTextArea bubbleText = new JTextArea(message);
        bubbleText.setEditable(false);
        bubbleText.setLineWrap(true);
        bubbleText.setWrapStyleWord(true);
        bubbleText.setMargin(new Insets(10, 15, 10, 15));
        bubbleText.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Styling dan Posisi Kiri/Kanan
        if (isUser) {
            bubbleText.setBackground(new Color(200, 220, 255)); // Biru muda untuk User
            rowPanel.add(Box.createHorizontalGlue()); // Mendorong bubble ke kanan
            rowPanel.add(bubbleText);
        } else {
            bubbleText.setBackground(new Color(240, 240, 240)); // Abu-abu muda untuk Bot
            rowPanel.add(bubbleText);
            rowPanel.add(Box.createHorizontalGlue()); // Mendorong bubble ke kiri
        }

        // Membatasi lebar maksimum bubble agar teksnya bisa "turun/wrap" ke bawah
        int maxWidth = 500;
        bubbleText.setSize(new Dimension(maxWidth, Short.MAX_VALUE));
        int preferredHeight = bubbleText.getPreferredSize().height;
        int preferredWidth = Math.min(maxWidth, bubbleText.getPreferredSize().width + 30);
        bubbleText.setMaximumSize(new Dimension(preferredWidth, preferredHeight));

        // Masukkan ke Panel Utama
        chatPanel.add(rowPanel);
        chatPanel.revalidate();
        chatPanel.repaint();

        // Otomatis scroll ke bagian paling bawah
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private JButton createSidebarButton(String text, boolean isActive) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 35));
        btn.setFocusPainted(false);
        btn.setBackground(isActive ? new Color(200, 210, 240) : Color.WHITE);
        return btn;
    }
}