package com.churchmate.controller;

import com.churchmate.service.ChatService;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatManager {
    private static ChatManager instance;
    public static ChatManager getInstance() {
        if (instance == null) instance = new ChatManager();
        return instance;
    }

    private final ChatService chatService = ChatService.getInstance();

    // sessionId → username
    private final Map<String, String> activeSessions = new HashMap<>();
    private String sessionId;

    private ChatManager() {}

    /** Mulai sesi baru, kembalikan sessionId */
    public String startSession(String username) {
        sessionId = UUID.randomUUID().toString();
        activeSessions.put(sessionId, username);
        return sessionId;
    }

    /** Akhiri sesi */
    public void endSession(String sid) {
        activeSessions.remove(sid);
    }

    /** Kirim pesan dan dapatkan balasan */
    public String sendMessage(String sid, String message) {
        if (!activeSessions.containsKey(sid)) return "Sesi tidak valid.";
        return chatService.processMessage(message);
    }

    /** Kirim pesan dengan sessionId aktif */
    public String sendMessage(String message) {
        return chatService.processMessage(message);
    }

    public String[] getFaqMenu() {
        return chatService.getFaqMenu();
    }

    public String getActiveSessionId() { return sessionId; }
}
