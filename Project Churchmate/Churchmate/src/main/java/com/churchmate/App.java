package com.churchmate;

import com.churchmate.service.DatabaseService;
import com.churchmate.controller.ChatManager;
import com.churchmate.service.ChatService;
import com.churchmate.ui.UserUI;


public class App {
    public static void main(String[] args) {
        DatabaseService db = new DatabaseService();
        ChatService chatService = new ChatService(db);
        ChatManager chatManager = new ChatManager(chatService);
        UserUI userUI = new UserUI(chatManager);

        userUI.showChatInterface();
    }
}