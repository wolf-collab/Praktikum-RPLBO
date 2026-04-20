package com.churchmate.ui;

import com.churchmate.controller.ChatManager;

import java.util.Scanner;

public class UserUI {

    private final ChatManager chatManager;

    public UserUI(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    public void showChatInterface() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== ChurchMate Chatbot ===");

        while (true) {
            System.out.print("Anda: ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            String response = chatManager.sendMessage(input);
            System.out.println("Bot: " + response);
        }
    }
}