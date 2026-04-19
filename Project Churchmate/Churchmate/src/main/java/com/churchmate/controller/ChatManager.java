package com.churchmate.controller;

import java.util.Map;
import java.util.HashMap;

public class ChatManager {
    private Map<String, String> activeSessions = new HashMap<>();
    private String sessionId;

    public String startSession() {
        this.sessionId = java.util.UUID.randomUUID().toString();
        return this.sessionId;
    }

    public void endSession(String id) {
        activeSessions.remove(id);
    }
}