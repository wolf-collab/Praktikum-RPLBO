package com.churchmate;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.churchmate.model.Gereja;
import com.churchmate.controller.ChatManager;
import com.churchmate.service.ChatService;
import com.churchmate.service.DatabaseService;

public class AppTest extends TestCase {

    public AppTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    public void testGerejaModel() {
        // Parameter disesuaikan dengan 7 argumen model Gereja yang baru
        Gereja gereja = new Gereja(1, "GKJ Gondokusuman", "Jl. Dr. Wahidin", "0812345", "Gereja bersejarah", "gkj.com", "email@gkj.com");

        String info = gereja.getInfo();
        assertNotNull("Info gereja tidak boleh null", info);
        assertTrue("Info harus mengandung nama gereja", info.contains("GKJ Gondokusuman"));
    }

    public void testChatManagerSession() {
        // Inisialisasi sesuai struktur Dependency Injection yang baru
        DatabaseService db = new DatabaseService();
        ChatService chatService = new ChatService(db);
        ChatManager chatManager = new ChatManager(chatService);

        // Tes apakah chatbot merespons jika tidak ada data
        String response = chatManager.sendMessage("jadwal ibadah");
        assertNotNull("Response chatbot tidak boleh null", response);
    }
}