package com.churchmate.controller;

import com.churchmate.service.ChatService;

public class ChatManager {

    private final ChatService chatService;

    public ChatManager(ChatService chatService) {
        this.chatService = chatService;
    }

    public String sendMessage(String message) {
        return chatService.processMessage(message);
    }
}