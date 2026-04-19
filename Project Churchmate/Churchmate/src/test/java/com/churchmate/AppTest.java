package com.churchmate;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

// Import class yang akan diuji
import com.churchmate.model.Gereja;
import com.churchmate.controller.ChatManager;

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
     * Memastikan string yang dikembalikan formatnya sesuai.
     */
    public void testGerejaModel() {
        // Persiapan Data (Arrange)
        Gereja gereja = new Gereja(1, "GKJ Gondokusuman", "Jl. Dr. Wahidin", "Gereja bersejarah di Yogyakarta");

        // Aksi (Act)
        String info = gereja.getInfo();

        // Verifikasi (Assert)
        String expectedOutput = "Gereja: GKJ Gondokusuman, Alamat: Jl. Dr. Wahidin";
        assertEquals("Format info gereja tidak sesuai!", expectedOutput, info);
    }

    /**
     * Menguji logika pembuatan sesi pada ChatManager.
     * Memastikan sessionId berhasil di-generate (tidak null).
     */
    public void testChatManagerSession() {
        // Persiapan Data
        ChatManager chatManager = new ChatManager();

        // Aksi
        String sessionId = chatManager.startSession();

        // Verifikasi
        assertNotNull("Session ID seharusnya tidak null setelah startSession() dipanggil!", sessionId);
        assertTrue("Session ID harus berupa string yang tidak kosong", !sessionId.isEmpty());
    }
}