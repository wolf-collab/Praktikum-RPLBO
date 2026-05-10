package com.churchmate.ui;

import com.churchmate.controller.ManageDataController;
import com.churchmate.service.AuthService;

import com.churchmate.service.ManageDataService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginUi {
    private final AuthService authService;
    private final ManageDataController manageDataController;

    public LoginUi(AuthService authService, ManageDataController manageDataController) {
        this.authService = authService;
        this.manageDataController = manageDataController;
    }

    public ManageDataController getManageDataController() {
        return manageDataController;
    }

    public void showLoginfForm(Stage stage) {
        Label title = new Label("Login Admin");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (authService.login(username, password)) {
                AdminUI adminUI = new AdminUI();
                adminUI.setController(manageDataController);
                adminUI.setAuthService(authService);
                adminUI.setLoginUi(this);
                adminUI.showDashboard(stage);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Gagal");
                alert.setHeaderText(null);
                alert.setContentText("Username atau password salah.");
                alert.showAndWait();
            }
        });

        VBox root = new VBox(10, title, usernameField, passwordField, loginButton);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Login Admin");
        stage.show();
    }
}
