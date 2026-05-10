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
    private final LoginUi loginUi;
    // Buat satu instance DAO agar CSV hanya dibaca satu kali saat aplikasi buka
    private final AlkitabDAO alkitabDAO = new AlkitabDAO();

    /** Constructor lama (tanpa fitur admin login) */
    public UserUI(ChatManager chatManager) {
        this(chatManager, null, null);
    }

    /** Constructor baru dengan dukungan admin login dari dalam chatbot */
    public UserUI(ChatManager chatManager, AuthService authService, LoginUi loginUi) {
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
        faqMenu.getItems().addAll(faq1, faq2);
        btnMenu.setOnAction(e -> faqMenu.show(btnMenu, Side.TOP, 0, 0));

        faq1.setOnAction(e -> processUserInput("jadwal ibadah"));
        faq2.setOnAction(e -> processUserInput("kegiatan"));

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

        Label title = new Label("Baca Alkitab");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));

        HBox comboContainer = new HBox(10);

        ComboBox<String> comboKitab = new ComboBox<>();
        // Memuat data kitab langsung saat panel dibuat
        List<String> listKitab = alkitabDAO.getAllKitab();
        comboKitab.getItems().addAll(listKitab);
        comboKitab.setPromptText("Pilih Kitab");

        ComboBox<Integer> comboPasal = new ComboBox<>();
        comboPasal.setPromptText("Pasal");

        ComboBox<Integer> comboAyat = new ComboBox<>();
        comboAyat.setPromptText("Ayat");

        // Listener Kitab -> Pasal
        comboKitab.setOnAction(e -> {
            String selected = comboKitab.getValue();
            if (selected != null) {
                comboPasal.getItems().setAll(alkitabDAO.getPasalByKitab(selected));
                comboAyat.getItems().clear();
            }
        });

        // Listener Pasal -> Ayat
        comboPasal.setOnAction(e -> {
            String kit = comboKitab.getValue();
            Integer pas = comboPasal.getValue();
            if (kit != null && pas != null) {
                comboAyat.getItems().setAll(alkitabDAO.getAyatByKitabAndPasal(kit, pas));
            }
        });

        Button btnCari = new Button("Tampilkan");
        btnCari.setStyle("-fx-background-color: #4a3bcc; -fx-text-fill: white; -fx-cursor: hand;");

        comboContainer.getChildren().addAll(comboKitab, comboPasal, comboAyat, btnCari);

        TextArea txtAyat = new TextArea();
        txtAyat.setEditable(false);
        txtAyat.setWrapText(true);
        txtAyat.setFont(Font.font("Segoe UI", 16));
        VBox.setVgrow(txtAyat, Priority.ALWAYS);

        btnCari.setOnAction(e -> {
            String k = comboKitab.getValue();
            Integer p = comboPasal.getValue();
            Integer a = comboAyat.getValue();
            if (k != null && p != null && a != null) {
                txtAyat.setText(alkitabDAO.getFirman(k, p, a));
            } else {
                txtAyat.setText("Pilih Kitab, Pasal, dan Ayat!");
            }
        });

        bibleLayout.getChildren().addAll(title, comboContainer, txtAyat);
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
