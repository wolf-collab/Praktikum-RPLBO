package com.churchmate.ui;

import com.churchmate.controller.ManageDataController;
import com.churchmate.model.Gereja;
import com.churchmate.model.Ibadah;
import com.churchmate.model.Kegiatan;
import com.churchmate.service.DatabaseService;
import com.churchmate.service.ManageDataService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AdminUI extends JFrame {
    private Object loginForm; // Sesuai UML (bisa diisi referensi ke JPanel login)
    private JPanel dashboardPanel; // Sesuai UML
    private ManageDataController controller;
    private JPanel mainContent;
    private String currentView = "GEREJA";

    public AdminUI() {
        setTitle("CHURCHMATE PANEL ADMIN");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void setController(ManageDataController controller) {
        this.controller = controller;
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

            // Tambahkan action listener untuk setiap menu
            menuBtn.addActionListener(e -> {
                switch (menu) {
                    case "DATA GEREJA":
                        currentView = "GEREJA";
                        showGerejaTable();
                        break;
                    case "DATA IBADAH":
                        currentView = "IBADAH";
                        showIbadahTable();
                        break;
                    case "DATA KEGIATAN":
                        currentView = "KEGIATAN";
                        showKegiatanTable();
                        break;
                    case "PENGATURAN":
                        currentView = "PENGATURAN";
                        showPengaturanPanel();
                        break;
                }
            });

            sidebar.add(menuBtn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        // Main Content Area (Kanan - Tabel Data)
        mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(Color.WHITE);

        // Menyusun Panel
        dashboardPanel.add(header, BorderLayout.NORTH);
        dashboardPanel.add(sidebar, BorderLayout.WEST);
        dashboardPanel.add(mainContent, BorderLayout.CENTER);

        setContentPane(dashboardPanel);

        // Tampilkan tabel Gereja sebagai default
        if (controller != null) {
            showGerejaTable();
        } else {
            // Fallback jika tidak ada controller (kompatibel dengan kode lama)
            JLabel contentTitle = new JLabel(" DATA GEREJA");
            contentTitle.setFont(new Font("Arial", Font.BOLD, 18));
            mainContent.add(contentTitle, BorderLayout.NORTH);

            String[] columnNames = {"No.", "Nama Gereja", "Jadwal", "Lokasi", "Aksi"};
            Object[][] data = {
                    {"1", "Ibadah Kreatif", "Setiap Sabtu", "Gedung GKJ", "Ubah | Hapus"}
            };
            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            mainContent.add(scrollPane, BorderLayout.CENTER);
        }

        setVisible(true);
    }

    // ==========================================
    // TAMPILKAN TABEL DATA GEREJA
    // ==========================================
    private void showGerejaTable() {
        mainContent.removeAll();

        // Header panel dengan judul dan tombol tambah
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel contentTitle = new JLabel(" DATA GEREJA");
        contentTitle.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(contentTitle, BorderLayout.WEST);

        JButton btnTambah = new JButton("+ TAMBAH GEREJA");
        btnTambah.setBackground(new Color(75, 60, 200));
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFocusPainted(false);
        btnTambah.addActionListener(e -> showAddGerejaDialog());
        topPanel.add(btnTambah, BorderLayout.EAST);

        mainContent.add(topPanel, BorderLayout.NORTH);

        // Tabel
        String[] columnNames = {"No.", "ID", "Nama Gereja", "Alamat", "No. Telp", "Email", "Aksi"};
        List<Gereja> list = controller.getAllGereja();
        Object[][] data = new Object[list.size()][7];

        for (int i = 0; i < list.size(); i++) {
            Gereja g = list.get(i);
            data[i][0] = String.valueOf(i + 1);
            data[i][1] = String.valueOf(g.getGerejaId());
            data[i][2] = g.getNama();
            data[i][3] = g.getAlamat();
            data[i][4] = g.getNoTelp();
            data[i][5] = g.getEmail();
            data[i][6] = "Ubah | Hapus";
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(40);

        // Klik pada baris tabel untuk Ubah/Hapus
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (row >= 0 && col == 6) {
                    int gerejaId = Integer.parseInt(table.getValueAt(row, 1).toString());
                    showGerejaActionDialog(gerejaId);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        mainContent.add(scrollPane, BorderLayout.CENTER);

        mainContent.revalidate();
        mainContent.repaint();
    }

    private void showAddGerejaDialog() {
        JTextField tfNama = new JTextField();
        JTextField tfAlamat = new JTextField();
        JTextField tfNoTelp = new JTextField();
        JTextField tfDeskripsi = new JTextField();
        JTextField tfWebsite = new JTextField();
        JTextField tfEmail = new JTextField();

        Object[] fields = {
                "Nama Gereja:", tfNama,
                "Alamat:", tfAlamat,
                "No. Telp:", tfNoTelp,
                "Deskripsi:", tfDeskripsi,
                "Website:", tfWebsite,
                "Email:", tfEmail
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Tambah Gereja", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int newId = controller.getNextGerejaId();
            Gereja g = new Gereja(newId, tfNama.getText(), tfAlamat.getText(),
                    tfNoTelp.getText(), tfDeskripsi.getText(), tfWebsite.getText(), tfEmail.getText());
            controller.addGereja(g);
            showGerejaTable(); // Refresh
        }
    }

    private void showGerejaActionDialog(int gerejaId) {
        String[] options = {"Ubah", "Hapus", "Batal"};
        int choice = JOptionPane.showOptionDialog(this,
                "Pilih aksi untuk Gereja ID: " + gerejaId,
                "Aksi", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[2]);

        if (choice == 0) { // Ubah
            showEditGerejaDialog(gerejaId);
        } else if (choice == 1) { // Hapus
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.deleteGereja(gerejaId);
                showGerejaTable(); // Refresh
            }
        }
    }

    private void showEditGerejaDialog(int gerejaId) {
        // Cari data gereja yang akan di-edit
        List<Gereja> list = controller.getAllGereja();
        Gereja existing = null;
        for (Gereja g : list) {
            if (g.getGerejaId() == gerejaId) {
                existing = g;
                break;
            }
        }
        if (existing == null) return;

        JTextField tfNama = new JTextField(existing.getNama());
        JTextField tfAlamat = new JTextField(existing.getAlamat());
        JTextField tfNoTelp = new JTextField(existing.getNoTelp());
        JTextField tfDeskripsi = new JTextField(existing.getDeskripsi());
        JTextField tfWebsite = new JTextField(existing.getWebsite());
        JTextField tfEmail = new JTextField(existing.getEmail());

        Object[] fields = {
                "Nama Gereja:", tfNama,
                "Alamat:", tfAlamat,
                "No. Telp:", tfNoTelp,
                "Deskripsi:", tfDeskripsi,
                "Website:", tfWebsite,
                "Email:", tfEmail
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Ubah Gereja", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Gereja updated = new Gereja(gerejaId, tfNama.getText(), tfAlamat.getText(),
                    tfNoTelp.getText(), tfDeskripsi.getText(), tfWebsite.getText(), tfEmail.getText());
            controller.updateGereja(updated);
            showGerejaTable(); // Refresh
        }
    }

    // ==========================================
    // TAMPILKAN TABEL DATA IBADAH
    // ==========================================
    private void showIbadahTable() {
        mainContent.removeAll();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel contentTitle = new JLabel(" DATA IBADAH");
        contentTitle.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(contentTitle, BorderLayout.WEST);

        JButton btnTambah = new JButton("+ TAMBAH IBADAH");
        btnTambah.setBackground(new Color(75, 60, 200));
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFocusPainted(false);
        btnTambah.addActionListener(e -> showAddIbadahDialog());
        topPanel.add(btnTambah, BorderLayout.EAST);

        mainContent.add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"No.", "ID", "Nama Ibadah", "Tanggal", "Jam", "Pendeta", "Tema", "Lokasi", "Aksi"};
        List<Ibadah> list = controller.getAllIbadah();
        Object[][] data = new Object[list.size()][9];

        for (int i = 0; i < list.size(); i++) {
            Ibadah ib = list.get(i);
            data[i][0] = String.valueOf(i + 1);
            data[i][1] = String.valueOf(ib.getIbadahId());
            data[i][2] = ib.getNamaibadah();
            data[i][3] = ib.getTglIbadah() != null ? ib.getTglIbadah().toString() : "";
            data[i][4] = ib.getJam() != null ? ib.getJam().toString() : "";
            data[i][5] = ib.getPendeta();
            data[i][6] = ib.getTema();
            data[i][7] = ib.getLokasi();
            data[i][8] = "Ubah | Hapus";
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(40);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (row >= 0 && col == 8) {
                    int ibadahId = Integer.parseInt(table.getValueAt(row, 1).toString());
                    showIbadahActionDialog(ibadahId);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        mainContent.add(scrollPane, BorderLayout.CENTER);

        mainContent.revalidate();
        mainContent.repaint();
    }

    private void showAddIbadahDialog() {
        JTextField tfNama = new JTextField();
        JTextField tfTanggal = new JTextField("2026-02-15");
        JTextField tfJam = new JTextField("08:00");
        JTextField tfPendeta = new JTextField();
        JTextField tfTema = new JTextField();
        JTextField tfLokasi = new JTextField();
        JTextField tfGerejaId = new JTextField("1");

        Object[] fields = {
                "Gereja ID:", tfGerejaId,
                "Nama Ibadah:", tfNama,
                "Tanggal (yyyy-MM-dd):", tfTanggal,
                "Jam (HH:mm):", tfJam,
                "Pendeta:", tfPendeta,
                "Tema:", tfTema,
                "Lokasi:", tfLokasi
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Tambah Ibadah", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int newId = controller.getNextIbadahId();
                int gerejaId = Integer.parseInt(tfGerejaId.getText().trim());
                LocalDate tanggal = LocalDate.parse(tfTanggal.getText().trim());
                LocalTime jam = LocalTime.parse(tfJam.getText().trim());

                Ibadah ib = new Ibadah(gerejaId, newId, tfNama.getText(), tanggal, jam,
                        tfPendeta.getText(), tfTema.getText(), tfLokasi.getText());
                controller.addIbadah(ib);
                showIbadahTable(); // Refresh
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Format input tidak valid: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showIbadahActionDialog(int ibadahId) {
        String[] options = {"Ubah", "Hapus", "Batal"};
        int choice = JOptionPane.showOptionDialog(this,
                "Pilih aksi untuk Ibadah ID: " + ibadahId,
                "Aksi", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[2]);

        if (choice == 0) {
            showEditIbadahDialog(ibadahId);
        } else if (choice == 1) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.deleteIbadah(ibadahId);
                showIbadahTable(); // Refresh
            }
        }
    }

    private void showEditIbadahDialog(int ibadahId) {
        List<Ibadah> list = controller.getAllIbadah();
        Ibadah existing = null;
        for (Ibadah ib : list) {
            if (ib.getIbadahId() == ibadahId) {
                existing = ib;
                break;
            }
        }
        if (existing == null) return;

        JTextField tfNama = new JTextField(existing.getNamaibadah());
        JTextField tfTanggal = new JTextField(existing.getTglIbadah() != null ? existing.getTglIbadah().toString() : "");
        JTextField tfJam = new JTextField(existing.getJam() != null ? existing.getJam().toString() : "");
        JTextField tfPendeta = new JTextField(existing.getPendeta());
        JTextField tfTema = new JTextField(existing.getTema());
        JTextField tfLokasi = new JTextField(existing.getLokasi());
        JTextField tfGerejaId = new JTextField(String.valueOf(existing.getGerejaid()));

        Object[] fields = {
                "Gereja ID:", tfGerejaId,
                "Nama Ibadah:", tfNama,
                "Tanggal (yyyy-MM-dd):", tfTanggal,
                "Jam (HH:mm):", tfJam,
                "Pendeta:", tfPendeta,
                "Tema:", tfTema,
                "Lokasi:", tfLokasi
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Ubah Ibadah", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int gerejaId = Integer.parseInt(tfGerejaId.getText().trim());
                LocalDate tanggal = LocalDate.parse(tfTanggal.getText().trim());
                LocalTime jam = LocalTime.parse(tfJam.getText().trim());

                Ibadah updated = new Ibadah(gerejaId, ibadahId, tfNama.getText(), tanggal, jam,
                        tfPendeta.getText(), tfTema.getText(), tfLokasi.getText());
                controller.updateIbadah(updated);
                showIbadahTable(); // Refresh
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Format input tidak valid: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==========================================
    // TAMPILKAN TABEL DATA KEGIATAN
    // ==========================================
    private void showKegiatanTable() {
        mainContent.removeAll();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel contentTitle = new JLabel(" DATA KEGIATAN");
        contentTitle.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(contentTitle, BorderLayout.WEST);

        JButton btnTambah = new JButton("+ TAMBAH KEGIATAN");
        btnTambah.setBackground(new Color(75, 60, 200));
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFocusPainted(false);
        btnTambah.addActionListener(e -> showAddKegiatanDialog());
        topPanel.add(btnTambah, BorderLayout.EAST);

        mainContent.add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"No.", "ID", "Judul", "Tanggal", "Kategori", "Lokasi", "Deskripsi", "Aksi"};
        List<Kegiatan> list = controller.getAllKegiatan();
        Object[][] data = new Object[list.size()][8];

        for (int i = 0; i < list.size(); i++) {
            Kegiatan kg = list.get(i);
            data[i][0] = String.valueOf(i + 1);
            data[i][1] = String.valueOf(kg.getKegiatanId());
            data[i][2] = kg.getJudul();
            data[i][3] = kg.getTanggal() != null ? kg.getTanggal().toString() : "";
            data[i][4] = kg.getKategori();
            data[i][5] = kg.getLokasi();
            data[i][6] = kg.getDeskripsi();
            data[i][7] = "Ubah | Hapus";
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(40);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (row >= 0 && col == 7) {
                    int kegiatanId = Integer.parseInt(table.getValueAt(row, 1).toString());
                    showKegiatanActionDialog(kegiatanId);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        mainContent.add(scrollPane, BorderLayout.CENTER);

        mainContent.revalidate();
        mainContent.repaint();
    }

    private void showAddKegiatanDialog() {
        JTextField tfJudul = new JTextField();
        JTextField tfTanggal = new JTextField("2026-02-16");
        JTextField tfKategori = new JTextField();
        JTextField tfLokasi = new JTextField();
        JTextField tfDeskripsi = new JTextField();
        JTextField tfGerejaId = new JTextField("1");

        Object[] fields = {
                "Gereja ID:", tfGerejaId,
                "Judul:", tfJudul,
                "Tanggal (yyyy-MM-dd):", tfTanggal,
                "Kategori:", tfKategori,
                "Lokasi:", tfLokasi,
                "Deskripsi:", tfDeskripsi
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Tambah Kegiatan", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int newId = controller.getNextKegiatanId();
                int gerejaId = Integer.parseInt(tfGerejaId.getText().trim());
                LocalDate tanggal = LocalDate.parse(tfTanggal.getText().trim());

                Kegiatan kg = new Kegiatan(gerejaId, newId, tfJudul.getText(), tanggal,
                        tfKategori.getText(), tfLokasi.getText(), tfDeskripsi.getText());
                controller.addKegiatan(kg);
                showKegiatanTable(); // Refresh
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Format input tidak valid: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showKegiatanActionDialog(int kegiatanId) {
        String[] options = {"Ubah", "Hapus", "Batal"};
        int choice = JOptionPane.showOptionDialog(this,
                "Pilih aksi untuk Kegiatan ID: " + kegiatanId,
                "Aksi", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[2]);

        if (choice == 0) {
            showEditKegiatanDialog(kegiatanId);
        } else if (choice == 1) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.deleteKegiatan(kegiatanId);
                showKegiatanTable(); // Refresh
            }
        }
    }

    private void showEditKegiatanDialog(int kegiatanId) {
        List<Kegiatan> list = controller.getAllKegiatan();
        Kegiatan existing = null;
        for (Kegiatan kg : list) {
            if (kg.getKegiatanId() == kegiatanId) {
                existing = kg;
                break;
            }
        }
        if (existing == null) return;

        JTextField tfJudul = new JTextField(existing.getJudul());
        JTextField tfTanggal = new JTextField(existing.getTanggal() != null ? existing.getTanggal().toString() : "");
        JTextField tfKategori = new JTextField(existing.getKategori());
        JTextField tfLokasi = new JTextField(existing.getLokasi());
        JTextField tfDeskripsi = new JTextField(existing.getDeskripsi());
        JTextField tfGerejaId = new JTextField(String.valueOf(existing.getGerejaid()));

        Object[] fields = {
                "Gereja ID:", tfGerejaId,
                "Judul:", tfJudul,
                "Tanggal (yyyy-MM-dd):", tfTanggal,
                "Kategori:", tfKategori,
                "Lokasi:", tfLokasi,
                "Deskripsi:", tfDeskripsi
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Ubah Kegiatan", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int gerejaId = Integer.parseInt(tfGerejaId.getText().trim());
                LocalDate tanggal = LocalDate.parse(tfTanggal.getText().trim());

                Kegiatan updated = new Kegiatan(gerejaId, kegiatanId, tfJudul.getText(), tanggal,
                        tfKategori.getText(), tfLokasi.getText(), tfDeskripsi.getText());
                controller.updateKegiatan(updated);
                showKegiatanTable(); // Refresh
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Format input tidak valid: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==========================================
    // PANEL PENGATURAN (Placeholder)
    // ==========================================
    private void showPengaturanPanel() {
        mainContent.removeAll();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("PENGATURAN");
        label.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(label, BorderLayout.NORTH);

        JLabel info = new JLabel("<html><br>Halaman pengaturan akan tersedia di versi berikutnya.</html>");
        info.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(info, BorderLayout.CENTER);

        mainContent.add(panel, BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseService db = new DatabaseService();
            ManageDataService service = new ManageDataService(db);
            ManageDataController controller = new ManageDataController(service);

            AdminUI ui = new AdminUI();
            ui.setController(controller);
            ui.showDashboard();
        });
    }
}