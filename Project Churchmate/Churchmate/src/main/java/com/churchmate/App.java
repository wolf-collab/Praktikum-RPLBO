package com.churchmate;

import com.churchmate.controller.ManageDataController;
import com.churchmate.service.AuthService;
import com.churchmate.service.DatabaseService;
import com.churchmate.controller.ChatManager;
import com.churchmate.service.ChatService;
import com.churchmate.service.ManageDataService;
import com.churchmate.ui.LoginUi;
import com.churchmate.ui.UserUI;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        DatabaseService db = new DatabaseService();

        ManageDataService manageDataService = new ManageDataService(db);
        ManageDataController manageDataController = new ManageDataController(manageDataService);

        AuthService authService = new AuthService(db.getUserDAO());
        LoginUi loginUi = new LoginUi(authService, manageDataController);

        ChatService chatService = new ChatService(db);
        ChatManager chatManager = new ChatManager(chatService);

        // Langsung buka chatbot, bukan halaman login
        UserUI userUI = new UserUI(chatManager, authService, loginUi);
        userUI.showChatInterface(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}