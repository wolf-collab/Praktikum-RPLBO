package com.churchmate.ui;

import com.churchmate.controller.ManageDataController;
import javax.swing.*;
import java.awt.*;

public class AdminUI extends JFrame {
    private Object loginForm; // Sesuai UML (bisa diisi referensi ke JPanel login)
    private JPanel dashboardPanel; // Sesuai UML

    public AdminUI() {
        setTitle("CHURCHMATE PANEL ADMIN");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // Method sesuai UML
    public void showLoginForm() {
        // Logika menampilkan panel login
    }

    // Method sesuai UML
    public void showDashboard() {
        dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new BorderLayout());

        // Header (Warna Biru)
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(75, 60, 200)); // Menyesuaikan warna mockup
        header.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel titleLabel = new JLabel("  CHURCHMATE   PANEL ADMIN");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        header.add(titleLabel, BorderLayout.WEST);

        JButton btnLogout = new JButton("LOGOUT");
        header.add(btnLogout, BorderLayout.EAST);

        // Sidebar (Kiri)
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(240, 240, 245));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        String[] menus = {"PENGATURAN", "DATA GEREJA", "DATA IBADAH", "DATA KEGIATAN"};
        for (String menu : menus) {
            JButton menuBtn = new JButton(menu);
            menuBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            menuBtn.setMaximumSize(new Dimension(200, 40));
            sidebar.add(menuBtn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        // Main Content Area (Kanan - Tabel Data)
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(Color.WHITE);

        JLabel contentTitle = new JLabel(" DATA GEREJA");
        contentTitle.setFont(new Font("Arial", Font.BOLD, 18));
        mainContent.add(contentTitle, BorderLayout.NORTH);

        // Dummy Tabel (Bisa diganti dengan data dari Controller)
        String[] columnNames = {"No.", "Nama Gereja", "Jadwal", "Lokasi", "Aksi"};
        Object[][] data = {
                {"1", "Ibadah Kreatif", "Setiap Sabtu", "Gedung GKJ", "Ubah | Hapus"}
        };
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        mainContent.add(scrollPane, BorderLayout.CENTER);

        // Menyusun Panel
        dashboardPanel.add(header, BorderLayout.NORTH);
        dashboardPanel.add(sidebar, BorderLayout.WEST);
        dashboardPanel.add(mainContent, BorderLayout.CENTER);

        setContentPane(dashboardPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminUI ui = new AdminUI();
            ui.showDashboard();
        });
    }
}