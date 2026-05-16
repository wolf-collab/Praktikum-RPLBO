package com.churchmate.ui;

import com.churchmate.controller.ChatManager;
import com.churchmate.controller.ManageDataController;
import com.churchmate.dao.AlkitabDAO;
import com.churchmate.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class UserUI {

    private BorderPane mainPanel;
    private VBox chatPanel;
    private ScrollPane scrollPane;
    private VBox bottomPanel;
    private TextField inputField;

    private final ChatManager chatManager;
    private final AuthService authService;
    private final LoginUI loginUi;
    // Buat satu instance DAO agar CSV hanya dibaca satu kali saat aplikasi buka
    private final AlkitabDAO alkitabDAO = new AlkitabDAO();

    /** Constructor lama (tanpa fitur admin login) */
    public UserUI(ChatManager chatManager) {
        this(chatManager, null, null);
    }

    /** Constructor baru dengan dukungan admin login dari dalam chatbot */
    public UserUI(ChatManager chatManager, AuthService authService, LoginUI loginUi) {
        this.chatManager = chatManager;
        this.authService = authService;
        this.loginUi = loginUi;
    }

    public void showChatInterface(Stage primaryStage) {
        primaryStage.setTitle("Churchmate - Chatbot Gereja");

        mainPanel = new BorderPane();
        // 1. HEADER
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #4a3bcc;");
        header.setPrefHeight(50);
        header.setPadding(new Insets(10, 20, 10, 20));
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("CHURCHMATE   CHATBOT GEREJA");
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Label statusLabel = new Label("STATUS: TERHUBUNG  🟢");
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        header.getChildren().addAll(titleLabel, headerSpacer, statusLabel);
        // 2. CHAT AREA (Tengah)
        chatPanel = new VBox(10);
        chatPanel.setStyle("-fx-background-color: white;");
        chatPanel.setPadding(new Insets(10));

        scrollPane = new ScrollPane(chatPanel);
        scrollPane.setStyle("-fx-background: white; -fx-border-color: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        chatPanel.heightProperty().addListener((obs, oldVal, newVal) -> scrollPane.setVvalue(1.0));

        addChatBubble("Halo! Saya Churchmate Bot.\nMau tahu informasi apa hari ini?", false);
        // 3. INPUT AREA (Bawah)
        bottomPanel = new VBox();
        bottomPanel.setStyle("-fx-background-color: white;");
        bottomPanel.setPadding(new Insets(15, 20, 20, 20));

        HBox inputWrapper = new HBox(10);
        inputWrapper.setAlignment(Pos.CENTER);
        inputWrapper.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5; -fx-background-radius: 5;");
        inputWrapper.setPadding(new Insets(5));
        inputWrapper.setPrefHeight(45);

        Button btnMenu = new Button("≡ MENU");
        btnMenu.setStyle("-fx-background-color: #4a3bcc; -fx-text-fill: white; -fx-cursor: hand;");

        ContextMenu faqMenu = new ContextMenu();
        MenuItem faq1 = new MenuItem("Jadwal ibadah?");
        MenuItem faq2 = new MenuItem("Agenda kegiatan?");
        MenuItem faq3 = new MenuItem("Baca Renungan Firman Hari Ini");
        faqMenu.getItems().addAll(faq1, faq2, faq3);
        btnMenu.setOnAction(e -> faqMenu.show(btnMenu, Side.TOP, 0, 0));

        faq1.setOnAction(e -> processUserInput("jadwal ibadah"));
        faq2.setOnAction(e -> processUserInput("kegiatan"));
        faq3.setOnAction(e -> processUserInput("renungan"));

        inputField = new TextField();
        inputField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        inputField.setFont(Font.font("Segoe UI", 14));
        HBox.setHgrow(inputField, Priority.ALWAYS);

        Button btnSend = new Button("↑");
        btnSend.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        btnSend.setStyle("-fx-background-color: #e6e6e6; -fx-cursor: hand;");

        inputWrapper.getChildren().addAll(btnMenu, inputField, btnSend);
        bottomPanel.getChildren().add(inputWrapper);

        btnSend.setOnAction(e -> processUserInput(inputField.getText()));
        inputField.setOnAction(e -> processUserInput(inputField.getText()));
        // 4. SIDEBAR (Kiri)
        VBox sidebar = new VBox(10);
        sidebar.setStyle("-fx-background-color: #f5f5f5;");
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(20, 10, 10, 10));

        Button btnNewChat = createSidebarButton("PERCAKAPAN BARU", true);
        Button btnSearchChat = createSidebarButton("BACA ALKITAB", false);

        btnSearchChat.setOnAction(e -> {
            btnSearchChat.setStyle("-fx-background-color: #c8d2f0; -fx-background-radius: 5;");
            btnNewChat.setStyle("-fx-background-color: white; -fx-background-radius: 5;");
            mainPanel.setCenter(createBibleView());
            mainPanel.setBottom(null);
        });

        btnNewChat.setOnAction(e -> {
            btnNewChat.setStyle("-fx-background-color: #c8d2f0; -fx-background-radius: 5;");
            btnSearchChat.setStyle("-fx-background-color: white; -fx-background-radius: 5;");
            mainPanel.setCenter(scrollPane);
            mainPanel.setBottom(bottomPanel);
        });

        sidebar.getChildren().addAll(btnNewChat, btnSearchChat);

        // 5. PANEL LOGIN ADMIN (hanya muncul jika authService tersedia)
        if (authService != null) {
            Region spacer = new Region();
            VBox.setVgrow(spacer, Priority.ALWAYS);

            // Panel login yang tersembunyi, muncul saat tombol diklik
            VBox loginPanel = new VBox(8);
            loginPanel.setStyle(
                "-fx-background-color: #eef0ff; " +
                "-fx-border-color: #4a3bcc; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8;"
            );
            loginPanel.setPadding(new Insets(12));
            loginPanel.setVisible(false);
            loginPanel.setManaged(false);

            Label loginTitle = new Label("🔐 Login Admin");
            loginTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            loginTitle.setTextFill(Color.web("#4a3bcc"));

            TextField usernameField = new TextField();
            usernameField.setPromptText("Username");
            usernameField.setStyle(
                "-fx-background-color: white; " +
                "-fx-border-color: #c0c8f0; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 6;"
            );

            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Password");
            passwordField.setStyle(
                "-fx-background-color: white; " +
                "-fx-border-color: #c0c8f0; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 6;"
            );

            Label errorLabel = new Label("");
            errorLabel.setTextFill(Color.RED);
            errorLabel.setFont(Font.font("Segoe UI", 11));
            errorLabel.setWrapText(true);

            Button btnMasuk = new Button("Masuk");
            btnMasuk.setMaxWidth(Double.MAX_VALUE);
            btnMasuk.setStyle(
                "-fx-background-color: #4a3bcc; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand; " +
                "-fx-padding: 7 0 7 0;"
            );

            btnMasuk.setOnAction(e -> {
                String username = usernameField.getText().trim();
                String password = passwordField.getText();
                if (username.isEmpty() || password.isEmpty()) {
                    errorLabel.setText("Username dan password tidak boleh kosong.");
                    return;
                }
                if (authService.login(username, password)) {
                    // Buka AdminUI di stage yang sama
                    AdminUI adminUI = new AdminUI();
                    ManageDataController ctrl = loginUi.getManageDataController();
                    adminUI.setController(ctrl);
                    adminUI.setAuthService(authService);
                    adminUI.setLoginUi(loginUi);
                    adminUI.setUserUI(this);

                    // Buat stage baru untuk admin agar chatbot bisa tetap tersedia
                    Stage adminStage = new Stage();
                    adminUI.showDashboard(adminStage);

                    // Reset form login setelah berhasil
                    usernameField.clear();
                    passwordField.clear();
                    errorLabel.setText("");
                    loginPanel.setVisible(false);
                    loginPanel.setManaged(false);
                } else {
                    errorLabel.setText("❌ Username atau password salah.");
                    passwordField.clear();
                }
            });

            loginPanel.getChildren().addAll(loginTitle, usernameField, passwordField, errorLabel, btnMasuk);

            // Tombol toggle login admin di bagian bawah sidebar
            Button btnAdminLogin = new Button("🔑  Login Admin");
            btnAdminLogin.setMaxWidth(Double.MAX_VALUE);
            btnAdminLogin.setPrefHeight(35);
            btnAdminLogin.setStyle(
                "-fx-background-color: #4a3bcc; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;"
            );

            btnAdminLogin.setOnAction(e -> {
                boolean nowVisible = !loginPanel.isVisible();
                loginPanel.setVisible(nowVisible);
                loginPanel.setManaged(nowVisible);
                if (nowVisible) {
                    btnAdminLogin.setText("✕  Tutup Login");
                    btnAdminLogin.setStyle(
                        "-fx-background-color: #8a7de6; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;"
                    );
                } else {
                    btnAdminLogin.setText("🔑  Login Admin");
                    btnAdminLogin.setStyle(
                        "-fx-background-color: #4a3bcc; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;"
                    );
                }
            });

            sidebar.getChildren().addAll(spacer, loginPanel, btnAdminLogin);
        }

        // MENGGABUNGKAN
        mainPanel.setTop(header);
        mainPanel.setLeft(sidebar);
        mainPanel.setCenter(scrollPane);
        mainPanel.setBottom(bottomPanel);

        Scene scene = new Scene(mainPanel, 950, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createBibleView() {
        VBox bibleLayout = new VBox(15);
        bibleLayout.setPadding(new Insets(20));
        bibleLayout.setStyle("-fx-background-color: white;");

        Label title = new Label("📖 Baca Alkitab");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));

        // Baris 1: Pilih Kitab
        HBox row1 = new HBox(10);
        row1.setAlignment(Pos.CENTER_LEFT);

        Label lblKitab = new Label("Kitab:");
        lblKitab.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lblKitab.setPrefWidth(60);

        ComboBox<String> comboKitab = new ComboBox<>();
        comboKitab.getItems().addAll(alkitabDAO.getAllKitab());
        comboKitab.setPromptText("Pilih Kitab");
        comboKitab.setPrefWidth(180);

        row1.getChildren().addAll(lblKitab, comboKitab);

        // Baris 2: Pilih Pasal (dari - sampai)
        HBox row2 = new HBox(10);
        row2.setAlignment(Pos.CENTER_LEFT);

        Label lblPasal = new Label("Pasal:");
        lblPasal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lblPasal.setPrefWidth(60);

        ComboBox<Integer> comboPasalDari = new ComboBox<>();
        comboPasalDari.setPromptText("Dari");
        comboPasalDari.setPrefWidth(80);

        Label lblSampaiPasal = new Label("s/d");
        lblSampaiPasal.setFont(Font.font("Segoe UI", 13));

        ComboBox<Integer> comboPasalSampai = new ComboBox<>();
        comboPasalSampai.setPromptText("Sampai");
        comboPasalSampai.setPrefWidth(80);
        comboPasalSampai.setDisable(true);

        row2.getChildren().addAll(lblPasal, comboPasalDari, lblSampaiPasal, comboPasalSampai);

        // Baris 3: Pilih Ayat (dari - sampai), hanya aktif jika 1 pasal dipilih
        HBox row3 = new HBox(10);
        row3.setAlignment(Pos.CENTER_LEFT);

        Label lblAyat = new Label("Ayat:");
        lblAyat.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lblAyat.setPrefWidth(60);

        ComboBox<String> comboAyatDari = new ComboBox<>();
        comboAyatDari.setPromptText("Semua");
        comboAyatDari.setPrefWidth(90);
        comboAyatDari.setDisable(true);

        Label lblSampaiAyat = new Label("s/d");
        lblSampaiAyat.setFont(Font.font("Segoe UI", 13));

        ComboBox<String> comboAyatSampai = new ComboBox<>();
        comboAyatSampai.setPromptText("Semua");
        comboAyatSampai.setPrefWidth(90);
        comboAyatSampai.setDisable(true);

        Label lblAyatNote = new Label("(kosongkan = tampilkan semua ayat)");
        lblAyatNote.setFont(Font.font("Segoe UI", 11));
        lblAyatNote.setStyle("-fx-text-fill: gray;");

        row3.getChildren().addAll(lblAyat, comboAyatDari, lblSampaiAyat, comboAyatSampai, lblAyatNote);

        // Tombol Tampilkan
        Button btnCari = new Button("▶  Tampilkan");
        btnCari.setStyle(
            "-fx-background-color: #4a3bcc; -fx-text-fill: white; "
            + "-fx-cursor: hand; -fx-background-radius: 6; -fx-padding: 7 16 7 16;"
        );
        btnCari.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

        // Area teks
        TextArea txtAyat = new TextArea();
        txtAyat.setEditable(false);
        txtAyat.setWrapText(true);
        txtAyat.setFont(Font.font("Segoe UI", 14));
        VBox.setVgrow(txtAyat, Priority.ALWAYS);

        // ── Helper: reset ayat controls ──
        Runnable resetAyat = () -> {
            comboAyatDari.getItems().clear();
            comboAyatDari.setValue(null);
            comboAyatSampai.getItems().clear();
            comboAyatSampai.setValue(null);
            comboAyatDari.setDisable(true);
            comboAyatSampai.setDisable(true);
        };

        // ── Listener Kitab → isi Pasal Dari, reset lainnya ──
        comboKitab.setOnAction(e -> {
            String selected = comboKitab.getValue();
            if (selected != null) {
                comboPasalDari.getItems().setAll(alkitabDAO.getPasalByKitab(selected));
                comboPasalDari.setValue(null);
                comboPasalSampai.getItems().clear();
                comboPasalSampai.setValue(null);
                comboPasalSampai.setDisable(true);
                resetAyat.run();
                txtAyat.clear();
            }
        });

        // ── Listener Pasal Dari → isi Pasal Sampai dan Ayat Dari ──
        comboPasalDari.setOnAction(e -> {
            String kit = comboKitab.getValue();
            Integer pas = comboPasalDari.getValue();
            if (kit != null && pas != null) {
                // Isi Pasal Sampai mulai dari pasal yang dipilih
                List<Integer> semuaPasal = alkitabDAO.getPasalByKitab(kit);
                List<Integer> pasalSampaiList = semuaPasal.stream()
                        .filter(p -> p >= pas)
                        .collect(java.util.stream.Collectors.toList());
                comboPasalSampai.getItems().setAll(pasalSampaiList);
                comboPasalSampai.setValue(pas);  // default: pasal yang sama
                comboPasalSampai.setDisable(false);

                // Isi Ayat Dari berdasarkan pasal dari (+ opsi kosong = semua)
                List<Integer> ayatList = alkitabDAO.getAyatByKitabAndPasal(kit, pas);
                List<String> ayatOptions = new java.util.ArrayList<>();
                ayatOptions.add("");  // kosong = semua
                ayatList.forEach(a -> ayatOptions.add(String.valueOf(a)));
                comboAyatDari.getItems().setAll(ayatOptions);
                comboAyatDari.setValue("");
                comboAyatDari.setDisable(false);

                comboAyatSampai.getItems().setAll(ayatOptions);
                comboAyatSampai.setValue("");
                comboAyatSampai.setDisable(false);

                lblAyatNote.setText("(kosongkan = tampilkan semua ayat)");
                txtAyat.clear();
            }
        });

        // ── Listener Pasal Sampai → jika beda pasal, nonaktifkan ayat ──
        comboPasalSampai.setOnAction(e -> {
            Integer dari = comboPasalDari.getValue();
            Integer sampai = comboPasalSampai.getValue();
            if (dari != null && sampai != null && !sampai.equals(dari)) {
                // Range multi-pasal: pemilihan ayat tidak relevan
                resetAyat.run();
                lblAyatNote.setText("(pemilihan ayat hanya untuk 1 pasal)");
            } else if (dari != null && sampai != null && sampai.equals(dari)) {
                // 1 pasal: aktifkan kembali
                String kit = comboKitab.getValue();
                if (kit != null) {
                    List<Integer> ayatList = alkitabDAO.getAyatByKitabAndPasal(kit, dari);
                    List<String> ayatOptions = new java.util.ArrayList<>();
                    ayatOptions.add("");
                    ayatList.forEach(a -> ayatOptions.add(String.valueOf(a)));
                    comboAyatDari.getItems().setAll(ayatOptions);
                    comboAyatDari.setValue("");
                    comboAyatDari.setDisable(false);
                    comboAyatSampai.getItems().setAll(ayatOptions);
                    comboAyatSampai.setValue("");
                    comboAyatSampai.setDisable(false);
                }
                lblAyatNote.setText("(kosongkan = tampilkan semua ayat)");
            }
        });

        // ── Listener Ayat Dari → sesuaikan pilihan Ayat Sampai ──
        comboAyatDari.setOnAction(e -> {
            String ayatDariVal = comboAyatDari.getValue();
            String kit = comboKitab.getValue();
            Integer pas = comboPasalDari.getValue();
            if (kit != null && pas != null && ayatDariVal != null && !ayatDariVal.isEmpty()) {
                int ayatMin = Integer.parseInt(ayatDariVal);
                List<Integer> ayatList = alkitabDAO.getAyatByKitabAndPasal(kit, pas);
                List<String> filtered = new java.util.ArrayList<>();
                filtered.add("");
                ayatList.stream().filter(a -> a >= ayatMin)
                        .forEach(a -> filtered.add(String.valueOf(a)));
                comboAyatSampai.getItems().setAll(filtered);
                // Set default sampai = dari (ayat tunggal)
                comboAyatSampai.setValue(ayatDariVal);
            }
        });

        // ── Aksi Tampilkan ──
        btnCari.setOnAction(e -> {
            String k = comboKitab.getValue();
            Integer pasalDari = comboPasalDari.getValue();
            Integer pasalSampai = comboPasalSampai.getValue();
            String ayatDariVal = comboAyatDari.getValue();
            String ayatSampaiVal = comboAyatSampai.getValue();

            if (k == null || pasalDari == null) {
                txtAyat.setText("Pilih Kitab dan Pasal terlebih dahulu.");
                return;
            }

            int pSampai = (pasalSampai != null) ? pasalSampai : pasalDari;
            StringBuilder sb = new StringBuilder();

            // Cek apakah ada range ayat spesifik (hanya berlaku untuk 1 pasal)
            boolean ayatDipilih = ayatDariVal != null && !ayatDariVal.isEmpty();
            boolean rangeSatuPasal = pasalDari.equals(pSampai);

            if (ayatDipilih && rangeSatuPasal) {
                int aDari = Integer.parseInt(ayatDariVal);
                int aSampai = (ayatSampaiVal != null && !ayatSampaiVal.isEmpty())
                        ? Integer.parseInt(ayatSampaiVal)
                        : aDari;
                // Pastikan urutan benar
                if (aSampai < aDari) aSampai = aDari;

                sb.append("── ").append(k).append(" ").append(pasalDari)
                  .append(":").append(aDari)
                  .append(aDari == aSampai ? "" : "–" + aSampai)
                  .append(" ──\n");

                List<Integer> semuaAyat = alkitabDAO.getAyatByKitabAndPasal(k, pasalDari);
                for (int a : semuaAyat) {
                    if (a >= aDari && a <= aSampai) {
                        sb.append(a).append("  ").append(alkitabDAO.getFirman(k, pasalDari, a)).append("\n");
                    }
                }
            } else {
                // Tampilkan satu atau lebih pasal penuh
                for (int p = pasalDari; p <= pSampai; p++) {
                    sb.append("── ").append(k).append(" Pasal ").append(p).append(" ──\n");
                    List<Integer> ayatList = alkitabDAO.getAyatByKitabAndPasal(k, p);
                    for (int a : ayatList) {
                        sb.append(a).append("  ").append(alkitabDAO.getFirman(k, p, a)).append("\n");
                    }
                    sb.append("\n");
                }
            }

            txtAyat.setText(sb.toString().trim());
        });

        bibleLayout.getChildren().addAll(title, row1, row2, row3, btnCari, txtAyat);
        return bibleLayout;
    }

    private void processUserInput(String text) {
        if (!text.trim().isEmpty()) {
            addChatBubble(text, true);
            inputField.clear();
            addChatBubble(chatManager.sendMessage(text), false);
        }
    }

    private void addChatBubble(String message, boolean isUser) {
        HBox row = new HBox();
        row.setPadding(new Insets(5));
        Label label = new Label(message);
        label.setWrapText(true);
        label.setMaxWidth(500);
        label.setPadding(new Insets(10, 15, 10, 15));
        label.setFont(Font.font("Segoe UI", 14));

        if (isUser) {
            label.setStyle("-fx-background-color: #c8dcff; -fx-background-radius: 15;");
            row.setAlignment(Pos.CENTER_RIGHT);
        } else {
            label.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 15;");
            row.setAlignment(Pos.CENTER_LEFT);
        }
        row.getChildren().add(label);
        chatPanel.getChildren().add(row);
    }

    private Button createSidebarButton(String text, boolean isActive) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(35);
        btn.setStyle("-fx-background-color: " + (isActive ? "#c8d2f0" : "white") + "; -fx-background-radius: 5;");
        return btn;
    }
}

