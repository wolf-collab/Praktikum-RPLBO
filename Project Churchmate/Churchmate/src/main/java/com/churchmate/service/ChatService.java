package com.churchmate.service;

import java.util.List;
import java.util.ArrayList;

public class ChatService {
    private List<String> responseHistory = new ArrayList<>();

    public void processMessage(String msg) {
        // Proses pesan
    }

    public String searchKnowledge(String query) {
        return "Knowledge result for " + query;
    }

    public String generateResponse(String query) {
        return "Response to " + query;
    }

    public void saveMessage(String msg) {
        responseHistory.add(msg);
    }
}