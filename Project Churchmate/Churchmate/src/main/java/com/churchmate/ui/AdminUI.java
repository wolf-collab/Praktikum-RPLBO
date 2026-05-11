package com.churchmate.ui;

import com.churchmate.controller.ManageDataController;
import com.churchmate.model.Gereja;
import com.churchmate.model.Ibadah;
import com.churchmate.model.Kegiatan;
import com.churchmate.model.Renungan;
import com.churchmate.service.DatabaseService;
import com.churchmate.service.ManageDataService;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AdminUI {
    private Object loginForm; // Sesuai UML
    private BorderPane dashboardPanel; // Sesuai UML, diganti ke BorderPane
    private ManageDataController controller;
    private BorderPane mainContent;
    private String currentView = "GEREJA";
    private com.churchmate.service.AuthService authService;
    private LoginUi loginUi;
    private UserUI userUI;

    public AdminUI() {
    }

    public void setController(ManageDataController controller) {
        this.controller = controller;
    }

    public void setAuthService(com.churchmate.service.AuthService authService) {
        this.authService = authService;
    }

    public void setLoginUi(LoginUi loginUi) {
        this.loginUi = loginUi;
    }

    public void setUserUI(UserUI userUI) {
        this.userUI = userUI;
    }

    // Method sesuai UML
    public void showLoginForm() {
    }

    // Method sesuai UML
    public void showDashboard(Stage primaryStage) {
        primaryStage.setTitle("CHURCHMATE PANEL ADMIN");

        dashboardPanel = new BorderPane();

        // Header (Warna Biru)
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #4b3cc8;");
        header.setPrefHeight(60);
        header.setPadding(new Insets(10, 20, 10, 20));
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("CHURCHMATE   PANEL ADMIN");
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button btnLogout = new Button("LOGOUT");
        btnLogout.setStyle(
                "-fx-background-color: #ffffff; -fx-text-fill: #4b3cc8; -fx-font-weight: bold; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> {
            if (authService != null) {
                authService.logout();
            }
            // Tutup hanya jendela admin; UserUI tetap berjalan di background
            primaryStage.close();
        });

        header.getChildren().addAll(titleLabel, headerSpacer, btnLogout);

        // Sidebar (Kiri)
        VBox sidebar = new VBox(10);
        sidebar.setStyle("-fx-background-color: #f0f0f5;");
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(20, 10, 10, 10));

        String[] menus = { "DATA GEREJA", "DATA IBADAH", "DATA KEGIATAN", "DATA RENUNGAN" }; // , "PENGATURAN" };
        for (String menu : menus) {
            Button menuBtn = new Button(menu);
            menuBtn.setMaxWidth(Double.MAX_VALUE);
            menuBtn.setPrefHeight(40);
            menuBtn.setStyle("-fx-background-color: white; -fx-cursor: hand; -fx-background-radius: 5;");

            menuBtn.setOnAction(e -> {
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
                    case "DATA RENUNGAN":
                        currentView = "RENUNGAN";
                        showRenunganTable();
                        break;
                    // case "PENGATURAN":
                    // currentView = "PENGATURAN";
                    // showPengaturanPanel();
                    // break;
                }
            });
            sidebar.getChildren().add(menuBtn);
        }

        // Main Content Area (Kanan - Tabel Data)
        mainContent = new BorderPane();
        mainContent.setStyle("-fx-background-color: white;");

        // Menyusun Panel
        dashboardPanel.setTop(header);
        dashboardPanel.setLeft(sidebar);
        dashboardPanel.setCenter(mainContent);

        // Tampilkan tabel Gereja sebagai default
        if (controller != null) {
            showGerejaTable();
        } else {
            // Fallback jika tidak ada controller
            Label contentTitle = new Label(" DATA GEREJA");
            contentTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            contentTitle.setPadding(new Insets(10));
            mainContent.setTop(contentTitle);

            TableView<Gereja> table = new TableView<>();
            mainContent.setCenter(table);
        }

        Scene scene = new Scene(dashboardPanel, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ==========================================
    // TAMPILKAN TABEL DATA GEREJA
    // ==========================================
    private void showGerejaTable() {
        BorderPane topPanel = new BorderPane();
        topPanel.setStyle("-fx-background-color: white;");
        topPanel.setPadding(new Insets(10));

        Label contentTitle = new Label(" DATA GEREJA");
        contentTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        topPanel.setLeft(contentTitle);

        Button btnTambah = new Button("+ TAMBAH GEREJA");
        btnTambah.setStyle(
                "-fx-background-color: #4b3cc8; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        btnTambah.setOnAction(e -> showAddGerejaDialog());
        topPanel.setRight(btnTambah);

        mainContent.setTop(topPanel);

        TableView<Gereja> table = new TableView<>();

        TableColumn<Gereja, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("gerejaId"));
        colId.setPrefWidth(50);

        TableColumn<Gereja, String> colNama = new TableColumn<>("Nama Gereja");
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colNama.setPrefWidth(150);

        TableColumn<Gereja, String> colAlamat = new TableColumn<>("Alamat");
        colAlamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));
        colAlamat.setPrefWidth(200);

        TableColumn<Gereja, String> colNoTelp = new TableColumn<>("No. Telp");
        colNoTelp.setCellValueFactory(new PropertyValueFactory<>("noTelp"));
        colNoTelp.setPrefWidth(100);

        TableColumn<Gereja, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(150);

        TableColumn<Gereja, Void> colAksi = new TableColumn<>("Aksi");
        colAksi.setPrefWidth(150);
        colAksi.setCellFactory(param -> new TableCell<>() {
            private final Button btnUbah = new Button("Ubah");
            private final Button btnHapus = new Button("Hapus");
            private final HBox pane = new HBox(5, btnUbah, btnHapus);

            {
                btnUbah.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");
                btnHapus.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand;");

                btnUbah.setOnAction(event -> {
                    Gereja g = getTableView().getItems().get(getIndex());
                    showEditGerejaDialog(g);
                });

                btnHapus.setOnAction(event -> {
                    Gereja g = getTableView().getItems().get(getIndex());
                    confirmDeleteGereja(g);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        table.getColumns().addAll(colId, colNama, colAlamat, colNoTelp, colEmail, colAksi);

        List<Gereja> list = controller.getAllGereja();
        ObservableList<Gereja> data = FXCollections.observableArrayList(list);
        table.setItems(data);

        mainContent.setCenter(table);
    }

    private void showAddGerejaDialog() {
        Dialog<Gereja> dialog = new Dialog<>();
        dialog.setTitle("Tambah Gereja");
        dialog.setHeaderText("Masukkan detail gereja baru.");

        ButtonType simpanButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(simpanButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfNama = new TextField();
        TextField tfAlamat = new TextField();
        TextField tfNoTelp = new TextField();
        TextField tfDeskripsi = new TextField();
        TextField tfWebsite = new TextField();
        TextField tfEmail = new TextField();

        grid.add(new Label("Nama Gereja:"), 0, 0);
        grid.add(tfNama, 1, 0);
        grid.add(new Label("Alamat:"), 0, 1);
        grid.add(tfAlamat, 1, 1);
        grid.add(new Label("No. Telp:"), 0, 2);
        grid.add(tfNoTelp, 1, 2);
        grid.add(new Label("Deskripsi:"), 0, 3);
        grid.add(tfDeskripsi, 1, 3);
        grid.add(new Label("Website:"), 0, 4);
        grid.add(tfWebsite, 1, 4);
        grid.add(new Label("Email:"), 0, 5);
        grid.add(tfEmail, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == simpanButtonType) {
                int newId = controller.getNextGerejaId();
                return new Gereja(newId, tfNama.getText(), tfAlamat.getText(),
                        tfNoTelp.getText(), tfDeskripsi.getText(), tfWebsite.getText(), tfEmail.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(g -> {
            controller.addGereja(g);
            showGerejaTable(); // Refresh
        });
    }

    private void showEditGerejaDialog(Gereja existing) {
        Dialog<Gereja> dialog = new Dialog<>();
        dialog.setTitle("Ubah Gereja");
        dialog.setHeaderText("Ubah detail gereja.");

        ButtonType simpanButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(simpanButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfNama = new TextField(existing.getNama());
        TextField tfAlamat = new TextField(existing.getAlamat());
        TextField tfNoTelp = new TextField(existing.getNoTelp());
        TextField tfDeskripsi = new TextField(existing.getDeskripsi());
        TextField tfWebsite = new TextField(existing.getWebsite());
        TextField tfEmail = new TextField(existing.getEmail());

        grid.add(new Label("Nama Gereja:"), 0, 0);
        grid.add(tfNama, 1, 0);
        grid.add(new Label("Alamat:"), 0, 1);
        grid.add(tfAlamat, 1, 1);
        grid.add(new Label("No. Telp:"), 0, 2);
        grid.add(tfNoTelp, 1, 2);
        grid.add(new Label("Deskripsi:"), 0, 3);
        grid.add(tfDeskripsi, 1, 3);
        grid.add(new Label("Website:"), 0, 4);
        grid.add(tfWebsite, 1, 4);
        grid.add(new Label("Email:"), 0, 5);
        grid.add(tfEmail, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == simpanButtonType) {
                return new Gereja(existing.getGerejaId(), tfNama.getText(), tfAlamat.getText(),
                        tfNoTelp.getText(), tfDeskripsi.getText(), tfWebsite.getText(), tfEmail.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updated -> {
            controller.updateGereja(updated);
            showGerejaTable(); // Refresh
        });
    }

    private void confirmDeleteGereja(Gereja g) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus Gereja");
        alert.setContentText("Apakah Anda yakin ingin menghapus data gereja ini?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                controller.deleteGereja(g.getGerejaId());
                showGerejaTable();
            }
        });
    }

    // ==========================================
    // TAMPILKAN TABEL DATA IBADAH
    // ==========================================
    private void showIbadahTable() {
        BorderPane topPanel = new BorderPane();
        topPanel.setStyle("-fx-background-color: white;");
        topPanel.setPadding(new Insets(10));

        Label contentTitle = new Label(" DATA IBADAH");
        contentTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        topPanel.setLeft(contentTitle);

        Button btnTambah = new Button("+ TAMBAH IBADAH");
        btnTambah.setStyle(
                "-fx-background-color: #4b3cc8; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        btnTambah.setOnAction(e -> showAddIbadahDialog());
        topPanel.setRight(btnTambah);

        mainContent.setTop(topPanel);

        TableView<Ibadah> table = new TableView<>();

        TableColumn<Ibadah, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("ibadahId"));
        colId.setPrefWidth(50);

        TableColumn<Ibadah, String> colNama = new TableColumn<>("Nama Ibadah");
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaibadah"));
        colNama.setPrefWidth(150);

        TableColumn<Ibadah, String> colTanggal = new TableColumn<>("Tanggal");
        colTanggal.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getTglIbadah() != null ? cellData.getValue().getTglIbadah().toString() : ""));
        colTanggal.setPrefWidth(100);

        TableColumn<Ibadah, String> colJam = new TableColumn<>("Jam");
        colJam.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getJam() != null ? cellData.getValue().getJam().toString() : ""));
        colJam.setPrefWidth(100);

        TableColumn<Ibadah, String> colPendeta = new TableColumn<>("Pendeta");
        colPendeta.setCellValueFactory(new PropertyValueFactory<>("pendeta"));
        colPendeta.setPrefWidth(150);

        TableColumn<Ibadah, String> colTema = new TableColumn<>("Tema");
        colTema.setCellValueFactory(new PropertyValueFactory<>("tema"));
        colTema.setPrefWidth(150);

        TableColumn<Ibadah, String> colLokasi = new TableColumn<>("Lokasi");
        colLokasi.setCellValueFactory(new PropertyValueFactory<>("lokasi"));
        colLokasi.setPrefWidth(150);

        TableColumn<Ibadah, Void> colAksi = new TableColumn<>("Aksi");
        colAksi.setPrefWidth(150);
        colAksi.setCellFactory(param -> new TableCell<>() {
            private final Button btnUbah = new Button("Ubah");
            private final Button btnHapus = new Button("Hapus");
            private final HBox pane = new HBox(5, btnUbah, btnHapus);

            {
                btnUbah.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");
                btnHapus.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand;");

                btnUbah.setOnAction(event -> {
                    Ibadah ib = getTableView().getItems().get(getIndex());
                    showEditIbadahDialog(ib);
                });

                btnHapus.setOnAction(event -> {
                    Ibadah ib = getTableView().getItems().get(getIndex());
                    confirmDeleteIbadah(ib);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        table.getColumns().addAll(colId, colNama, colTanggal, colJam, colPendeta, colTema, colLokasi, colAksi);

        List<Ibadah> list = controller.getAllIbadah();
        ObservableList<Ibadah> data = FXCollections.observableArrayList(list);
        table.setItems(data);

        mainContent.setCenter(table);
    }

    private void showAddIbadahDialog() {
        Dialog<Ibadah> dialog = new Dialog<>();
        dialog.setTitle("Tambah Ibadah");
        dialog.setHeaderText("Masukkan detail ibadah baru.");

        ButtonType simpanButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(simpanButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfGerejaId = new TextField("1");
        TextField tfNama = new TextField();
        TextField tfTanggal = new TextField("2026-02-15");
        TextField tfJam = new TextField("08:00");
        TextField tfPendeta = new TextField();
        TextField tfTema = new TextField();
        TextField tfLokasi = new TextField();

        grid.add(new Label("Gereja ID:"), 0, 0);
        grid.add(tfGerejaId, 1, 0);
        grid.add(new Label("Nama Ibadah:"), 0, 1);
        grid.add(tfNama, 1, 1);
        grid.add(new Label("Tanggal (yyyy-MM-dd):"), 0, 2);
        grid.add(tfTanggal, 1, 2);
        grid.add(new Label("Jam (HH:mm):"), 0, 3);
        grid.add(tfJam, 1, 3);
        grid.add(new Label("Pendeta:"), 0, 4);
        grid.add(tfPendeta, 1, 4);
        grid.add(new Label("Tema:"), 0, 5);
        grid.add(tfTema, 1, 5);
        grid.add(new Label("Lokasi:"), 0, 6);
        grid.add(tfLokasi, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == simpanButtonType) {
                try {
                    int newId = controller.getNextIbadahId();
                    int gerejaId = Integer.parseInt(tfGerejaId.getText().trim());
                    LocalDate tanggal = LocalDate.parse(tfTanggal.getText().trim());
                    LocalTime jam = LocalTime.parse(tfJam.getText().trim());

                    return new Ibadah(gerejaId, newId, tfNama.getText(), tanggal, jam,
                            tfPendeta.getText(), tfTema.getText(), tfLokasi.getText());
                } catch (Exception ex) {
                    showErrorAlert("Format input tidak valid: " + ex.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ib -> {
            controller.addIbadah(ib);
            showIbadahTable();
        });
    }

    private void showEditIbadahDialog(Ibadah existing) {
        Dialog<Ibadah> dialog = new Dialog<>();
        dialog.setTitle("Ubah Ibadah");
        dialog.setHeaderText("Ubah detail ibadah.");

        ButtonType simpanButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(simpanButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfGerejaId = new TextField(String.valueOf(existing.getGerejaid()));
        TextField tfNama = new TextField(existing.getNamaibadah());
        TextField tfTanggal = new TextField(existing.getTglIbadah() != null ? existing.getTglIbadah().toString() : "");
        TextField tfJam = new TextField(existing.getJam() != null ? existing.getJam().toString() : "");
        TextField tfPendeta = new TextField(existing.getPendeta());
        TextField tfTema = new TextField(existing.getTema());
        TextField tfLokasi = new TextField(existing.getLokasi());

        grid.add(new Label("Gereja ID:"), 0, 0);
        grid.add(tfGerejaId, 1, 0);
        grid.add(new Label("Nama Ibadah:"), 0, 1);
        grid.add(tfNama, 1, 1);
        grid.add(new Label("Tanggal (yyyy-MM-dd):"), 0, 2);
        grid.add(tfTanggal, 1, 2);
        grid.add(new Label("Jam (HH:mm):"), 0, 3);
        grid.add(tfJam, 1, 3);
        grid.add(new Label("Pendeta:"), 0, 4);
        grid.add(tfPendeta, 1, 4);
        grid.add(new Label("Tema:"), 0, 5);
        grid.add(tfTema, 1, 5);
        grid.add(new Label("Lokasi:"), 0, 6);
        grid.add(tfLokasi, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == simpanButtonType) {
                try {
                    int gerejaId = Integer.parseInt(tfGerejaId.getText().trim());
                    LocalDate tanggal = LocalDate.parse(tfTanggal.getText().trim());
                    LocalTime jam = LocalTime.parse(tfJam.getText().trim());

                    return new Ibadah(gerejaId, existing.getIbadahId(), tfNama.getText(), tanggal, jam,
                            tfPendeta.getText(), tfTema.getText(), tfLokasi.getText());
                } catch (Exception ex) {
                    showErrorAlert("Format input tidak valid: " + ex.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updated -> {
            controller.updateIbadah(updated);
            showIbadahTable();
        });
    }

    private void confirmDeleteIbadah(Ibadah ib) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus Ibadah");
        alert.setContentText("Apakah Anda yakin ingin menghapus data ibadah ini?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                controller.deleteIbadah(ib.getIbadahId());
                showIbadahTable();
            }
        });
    }

    // ==========================================
    // TAMPILKAN TABEL DATA KEGIATAN
    // ==========================================
    private void showKegiatanTable() {
        BorderPane topPanel = new BorderPane();
        topPanel.setStyle("-fx-background-color: white;");
        topPanel.setPadding(new Insets(10));

        Label contentTitle = new Label(" DATA KEGIATAN");
        contentTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        topPanel.setLeft(contentTitle);

        Button btnTambah = new Button("+ TAMBAH KEGIATAN");
        btnTambah.setStyle(
                "-fx-background-color: #4b3cc8; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        btnTambah.setOnAction(e -> showAddKegiatanDialog());
        topPanel.setRight(btnTambah);

        mainContent.setTop(topPanel);

        TableView<Kegiatan> table = new TableView<>();

        TableColumn<Kegiatan, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("kegiatanId"));
        colId.setPrefWidth(50);

        TableColumn<Kegiatan, String> colJudul = new TableColumn<>("Judul");
        colJudul.setCellValueFactory(new PropertyValueFactory<>("judul"));
        colJudul.setPrefWidth(150);

        TableColumn<Kegiatan, String> colTanggal = new TableColumn<>("Tanggal");
        colTanggal.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getTanggal() != null ? cellData.getValue().getTanggal().toString() : ""));
        colTanggal.setPrefWidth(100);

        TableColumn<Kegiatan, String> colKategori = new TableColumn<>("Kategori");
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colKategori.setPrefWidth(150);

        TableColumn<Kegiatan, String> colLokasi = new TableColumn<>("Lokasi");
        colLokasi.setCellValueFactory(new PropertyValueFactory<>("lokasi"));
        colLokasi.setPrefWidth(150);

        TableColumn<Kegiatan, String> colDeskripsi = new TableColumn<>("Deskripsi");
        colDeskripsi.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        colDeskripsi.setPrefWidth(200);

        TableColumn<Kegiatan, Void> colAksi = new TableColumn<>("Aksi");
        colAksi.setPrefWidth(150);
        colAksi.setCellFactory(param -> new TableCell<>() {
            private final Button btnUbah = new Button("Ubah");
            private final Button btnHapus = new Button("Hapus");
            private final HBox pane = new HBox(5, btnUbah, btnHapus);

            {
                btnUbah.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");
                btnHapus.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand;");

                btnUbah.setOnAction(event -> {
                    Kegiatan kg = getTableView().getItems().get(getIndex());
                    showEditKegiatanDialog(kg);
                });

                btnHapus.setOnAction(event -> {
                    Kegiatan kg = getTableView().getItems().get(getIndex());
                    confirmDeleteKegiatan(kg);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        table.getColumns().addAll(colId, colJudul, colTanggal, colKategori, colLokasi, colDeskripsi, colAksi);

        List<Kegiatan> list = controller.getAllKegiatan();
        ObservableList<Kegiatan> data = FXCollections.observableArrayList(list);
        table.setItems(data);

        mainContent.setCenter(table);
    }

    private void showAddKegiatanDialog() {
        Dialog<Kegiatan> dialog = new Dialog<>();
        dialog.setTitle("Tambah Kegiatan");
        dialog.setHeaderText("Masukkan detail kegiatan baru.");

        ButtonType simpanButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(simpanButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfGerejaId = new TextField("1");
        TextField tfJudul = new TextField();
        TextField tfTanggal = new TextField("2026-02-16");
        TextField tfKategori = new TextField();
        TextField tfLokasi = new TextField();
        TextField tfDeskripsi = new TextField();

        grid.add(new Label("Gereja ID:"), 0, 0);
        grid.add(tfGerejaId, 1, 0);
        grid.add(new Label("Judul:"), 0, 1);
        grid.add(tfJudul, 1, 1);
        grid.add(new Label("Tanggal (yyyy-MM-dd):"), 0, 2);
        grid.add(tfTanggal, 1, 2);
        grid.add(new Label("Kategori:"), 0, 3);
        grid.add(tfKategori, 1, 3);
        grid.add(new Label("Lokasi:"), 0, 4);
        grid.add(tfLokasi, 1, 4);
        grid.add(new Label("Deskripsi:"), 0, 5);
        grid.add(tfDeskripsi, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == simpanButtonType) {
                try {
                    int newId = controller.getNextKegiatanId();
                    int gerejaId = Integer.parseInt(tfGerejaId.getText().trim());
                    LocalDate tanggal = LocalDate.parse(tfTanggal.getText().trim());

                    return new Kegiatan(gerejaId, newId, tfJudul.getText(), tanggal,
                            tfKategori.getText(), tfLokasi.getText(), tfDeskripsi.getText());
                } catch (Exception ex) {
                    showErrorAlert("Format input tidak valid: " + ex.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(kg -> {
            controller.addKegiatan(kg);
            showKegiatanTable();
        });
    }

    private void showEditKegiatanDialog(Kegiatan existing) {
        Dialog<Kegiatan> dialog = new Dialog<>();
        dialog.setTitle("Ubah Kegiatan");
        dialog.setHeaderText("Ubah detail kegiatan.");

        ButtonType simpanButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(simpanButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfGerejaId = new TextField(String.valueOf(existing.getGerejaid()));
        TextField tfJudul = new TextField(existing.getJudul());
        TextField tfTanggal = new TextField(existing.getTanggal() != null ? existing.getTanggal().toString() : "");
        TextField tfKategori = new TextField(existing.getKategori());
        TextField tfLokasi = new TextField(existing.getLokasi());
        TextField tfDeskripsi = new TextField(existing.getDeskripsi());

        grid.add(new Label("Gereja ID:"), 0, 0);
        grid.add(tfGerejaId, 1, 0);
        grid.add(new Label("Judul:"), 0, 1);
        grid.add(tfJudul, 1, 1);
        grid.add(new Label("Tanggal (yyyy-MM-dd):"), 0, 2);
        grid.add(tfTanggal, 1, 2);
        grid.add(new Label("Kategori:"), 0, 3);
        grid.add(tfKategori, 1, 3);
        grid.add(new Label("Lokasi:"), 0, 4);
        grid.add(tfLokasi, 1, 4);
        grid.add(new Label("Deskripsi:"), 0, 5);
        grid.add(tfDeskripsi, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == simpanButtonType) {
                try {
                    int gerejaId = Integer.parseInt(tfGerejaId.getText().trim());
                    LocalDate tanggal = LocalDate.parse(tfTanggal.getText().trim());

                    return new Kegiatan(gerejaId, existing.getKegiatanId(), tfJudul.getText(), tanggal,
                            tfKategori.getText(), tfLokasi.getText(), tfDeskripsi.getText());
                } catch (Exception ex) {
                    showErrorAlert("Format input tidak valid: " + ex.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updated -> {
            controller.updateKegiatan(updated);
            showKegiatanTable();
        });
    }

    private void confirmDeleteKegiatan(Kegiatan kg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus Kegiatan");
        alert.setContentText("Apakah Anda yakin ingin menghapus data kegiatan ini?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                controller.deleteKegiatan(kg.getKegiatanId());
                showKegiatanTable();
            }
        });
    }

    // ==========================================
    // PANEL PENGATURAN
    // ==========================================
    // private void showPengaturanPanel() {
    // BorderPane panel = new BorderPane();
    // panel.setStyle("-fx-background-color: white;");
    // panel.setPadding(new Insets(20));
    //
    // Label label = new Label("PENGATURAN");
    // label.setFont(Font.font("Arial", FontWeight.BOLD, 18));
    // panel.setTop(label);
    //
    // Label info = new Label("\nHalaman pengaturan akan tersedia di versi
    // berikutnya.");
    // info.setFont(Font.font("Arial", 14));
    // panel.setCenter(info);
    //
    // mainContent.setCenter(panel);
    // mainContent.setTop(null); // Clear header for this page
    // }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ==========================================
    // TAMPILKAN TABEL DATA RENUNGAN
    // ==========================================
    private void showRenunganTable() {
        BorderPane topPanel = new BorderPane();
        topPanel.setStyle("-fx-background-color: white;");
        topPanel.setPadding(new Insets(10));

        Label contentTitle = new Label(" DATA RENUNGAN");
        contentTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        topPanel.setLeft(contentTitle);

        Button btnTambah = new Button("+ TAMBAH RENUNGAN");
        btnTambah.setStyle("-fx-background-color: #4b3cc8; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        btnTambah.setOnAction(e -> showAddRenunganDialog());
        topPanel.setRight(btnTambah);

        mainContent.setTop(topPanel);

        TableView<Renungan> table = new TableView<>();

        TableColumn<Renungan, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Renungan, String> colTanggal = new TableColumn<>("Tanggal");
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colTanggal.setPrefWidth(100);

        TableColumn<Renungan, String> colJudul = new TableColumn<>("Judul");
        colJudul.setCellValueFactory(new PropertyValueFactory<>("judul"));
        colJudul.setPrefWidth(150);

        TableColumn<Renungan, String> colAyat = new TableColumn<>("Ayat Referensi");
        colAyat.setCellValueFactory(new PropertyValueFactory<>("ayatReferensi"));
        colAyat.setPrefWidth(150);

        TableColumn<Renungan, String> colIsi = new TableColumn<>("Isi Renungan");
        colIsi.setCellValueFactory(new PropertyValueFactory<>("isiRenungan"));
        colIsi.setPrefWidth(200);

        TableColumn<Renungan, Void> colAksi = new TableColumn<>("Aksi");
        colAksi.setPrefWidth(150);
        colAksi.setCellFactory(param -> new TableCell<>() {
            private final Button btnUbah = new Button("Ubah");
            private final Button btnHapus = new Button("Hapus");
            private final HBox pane = new HBox(5, btnUbah, btnHapus);

            {
                btnUbah.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");
                btnHapus.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand;");

                btnUbah.setOnAction(event -> {
                    Renungan r = getTableView().getItems().get(getIndex());
                    showEditRenunganDialog(r);
                });

                btnHapus.setOnAction(event -> {
                    Renungan r = getTableView().getItems().get(getIndex());
                    confirmDeleteRenungan(r);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        table.getColumns().addAll(colId, colTanggal, colJudul, colAyat, colIsi, colAksi);

        // Memanggil data dari controller
        List<Renungan> list = controller.getAllRenungan();
        ObservableList<Renungan> data = FXCollections.observableArrayList(list);
        table.setItems(data);

        mainContent.setCenter(table);
    }

    private void showAddRenunganDialog() {
        Dialog<Renungan> dialog = new Dialog<>();
        dialog.setTitle("Tambah Renungan");
        dialog.setHeaderText("Masukkan detail renungan baru.");

        ButtonType simpanButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(simpanButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfTanggal = new TextField(LocalDate.now().toString()); // Default hari ini
        TextField tfJudul = new TextField();
        TextField tfAyat = new TextField();
        TextArea tfIsi = new TextArea();
        tfIsi.setPrefRowCount(5);
        tfIsi.setWrapText(true);

        grid.add(new Label("Tanggal (yyyy-MM-dd):"), 0, 0);
        grid.add(tfTanggal, 1, 0);
        grid.add(new Label("Judul:"), 0, 1);
        grid.add(tfJudul, 1, 1);
        grid.add(new Label("Ayat Referensi:"), 0, 2);
        grid.add(tfAyat, 1, 2);
        grid.add(new Label("Isi Renungan:"), 0, 3);
        grid.add(tfIsi, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == simpanButtonType) {
                // Return objek baru (ID diset 0 dulu karena AutoIncrement di DB)
                return new Renungan(tfTanggal.getText(), tfJudul.getText(), tfAyat.getText(), tfIsi.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(r -> {
            controller.addRenungan(r);
            showRenunganTable(); // Refresh tabel
        });
    }

    private void showEditRenunganDialog(Renungan existing) {
        Dialog<Renungan> dialog = new Dialog<>();
        dialog.setTitle("Ubah Renungan");
        dialog.setHeaderText("Ubah detail renungan.");

        ButtonType simpanButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(simpanButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfTanggal = new TextField(existing.getTanggal());
        TextField tfJudul = new TextField(existing.getJudul());
        TextField tfAyat = new TextField(existing.getAyatReferensi());
        TextArea tfIsi = new TextArea(existing.getIsiRenungan());
        tfIsi.setPrefRowCount(5);
        tfIsi.setWrapText(true);

        grid.add(new Label("Tanggal (yyyy-MM-dd):"), 0, 0);
        grid.add(tfTanggal, 1, 0);
        grid.add(new Label("Judul:"), 0, 1);
        grid.add(tfJudul, 1, 1);
        grid.add(new Label("Ayat Referensi:"), 0, 2);
        grid.add(tfAyat, 1, 2);
        grid.add(new Label("Isi Renungan:"), 0, 3);
        grid.add(tfIsi, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == simpanButtonType) {
                return new Renungan(existing.getId(), tfTanggal.getText(), tfJudul.getText(), tfAyat.getText(), tfIsi.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updated -> {
            controller.updateRenungan(updated);
            showRenunganTable(); // Refresh tabel
        });
    }

    private void confirmDeleteRenungan(Renungan r) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus Renungan");
        alert.setContentText("Apakah Anda yakin ingin menghapus data renungan ini?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                controller.deleteRenungan(r.getId());
                showRenunganTable();
            }
        });
    }
}
