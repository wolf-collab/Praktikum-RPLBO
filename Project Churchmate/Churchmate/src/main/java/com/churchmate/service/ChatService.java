package com.churchmate.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ChatService {
    private final DatabaseService databaseService;
    private final List<String> responseHistory = new ArrayList<>();

    public ChatService() {
        this(new DatabaseService());
    }

    public ChatService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public String processMessage(String msg) {
        if (msg == null || msg.trim().isEmpty()) {
            return "Pesan tidak boleh kosong.";
        }

        String cleanMessage = msg.trim();
        saveMessage("User", cleanMessage);

        String knowledgeResult = searchKnowledge(cleanMessage);
        String response = generateResponse(knowledgeResult);

        saveMessage("Bot", response);
        responseHistory.add(response);

        return response;
    }

    public String searchKnowledge(String query) {
        List<String> matches = new ArrayList<>();
        String normalizedQuery = query.toLowerCase();

        for (Object data : databaseService.findAll()) {
            if (data instanceof ChatMessageRecord) {
                continue;
            }

            String description = describeData(data);
            if (description.toLowerCase().contains(normalizedQuery)) {
                matches.add(description);
            }
        }

        if (matches.isEmpty()) {
            return "";
        }

        return String.join(" | ", matches);
    }

    public String generateResponse(String knowledgeResult) {
        if (knowledgeResult == null || knowledgeResult.isBlank()) {
            return "Maaf, data yang Anda cari belum tersedia di database gereja.";
        }

        return "Saya menemukan informasi berikut: " + knowledgeResult;
    }

    public void saveMessage(String sender, String msg) {
        databaseService.save(new ChatMessageRecord(sender, msg));
    }

    public List<String> getResponseHistory() {
        return new ArrayList<>(responseHistory);
    }

    public List<String> getConversationHistory() {
        List<String> conversation = new ArrayList<>();

        for (Object data : databaseService.findAll()) {
            if (data instanceof ChatMessageRecord record) {
                conversation.add(record.format());
            }
        }

        return conversation;
    }

    public void saveKnowledge(Object data) {
        databaseService.save(data);
    }

    private String describeData(Object data) {
        String[] preferredMethods = {"getInfo", "getUpcoming", "getDetail", "getProfile", "getJadwal"};

        for (String methodName : preferredMethods) {
            try {
                Method method = data.getClass().getMethod(methodName);
                Object result = method.invoke(data);
                if (result instanceof String text) {
                    return text;
                }
            } catch (ReflectiveOperationException ignored) {
                // Coba method lain yang lebih cocok.
            }
        }

        return data.toString();
    }

    private static class ChatMessageRecord {
        private final String sender;
        private final String message;

        private ChatMessageRecord(String sender, String message) {
            this.sender = sender;
            this.message = message;
        }

        private String format() {
            return sender + ": " + message;
        }
    }
}
