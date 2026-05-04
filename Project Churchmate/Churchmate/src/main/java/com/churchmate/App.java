package com.churchmate;

import com.churchmate.service.DatabaseService;
import com.churchmate.controller.ChatManager;
import com.churchmate.service.ChatService;
import com.churchmate.ui.UserUI;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        DatabaseService db = new DatabaseService();
        ChatService chatService = new ChatService(db);
        ChatManager chatManager = new ChatManager(chatService);
        UserUI userUI = new UserUI(chatManager);
        userUI.showChatInterface(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}