package com.churchmate;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.churchmate.model.Gereja;
import com.churchmate.model.Ibadah;
import com.churchmate.controller.ChatManager;
import com.churchmate.service.ChatService;
import com.churchmate.service.DatabaseService;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Unit test untuk logika utama aplikasi Churchmate.
 */
public class AppTest extends TestCase {

    public AppTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Menguji method getInfo() pada model Gereja.
     */
    public void testGerejaModel() {
        Gereja gereja = new Gereja(
                1,
                "GKJ Gondokusuman",
                "Jl. Dr. Wahidin",
                "08123456789",
                "Gereja bersejarah di Yogyakarta",
                "www.gkjgondokusuman.org",
                "info@gkjgondokusuman.org"
        );

        String info = gereja.getInfo();

        assertNotNull("Info gereja seharusnya tidak null", info);
        assertTrue("Info gereja harus memuat nama gereja", info.contains("GKJ Gondokusuman"));
        assertTrue("Info gereja harus memuat alamat", info.contains("Jl. Dr. Wahidin"));
        assertTrue("Info gereja harus memuat email", info.contains("info@gkjgondokusuman.org"));
    }

    /**
     * Menguji bahwa ChatManager dapat meneruskan pesan ke ChatService
     * dan menghasilkan jawaban tentang ibadah.
     */
    public void testChatManagerSendMessage() {
        DatabaseService db = new DatabaseService();

        db.save(new Ibadah(
                1,
                1,
                "Ibadah Minggu",
                LocalDate.of(2026, 2, 15),
                LocalTime.of(8, 0),
                "Pdt. Yohanes",
                "Kasih Tuhan",
                "Gereja Utama"
        ));

        ChatService chatService = new ChatService(db);
        ChatManager chatManager = new ChatManager(chatService);

        String response = chatManager.sendMessage("kapan ibadah minggu?");

        assertNotNull("Response chatbot seharusnya tidak null", response);
        assertTrue("Response harus memuat informasi ibadah", response.toLowerCase().contains("ibadah"));
    }
}