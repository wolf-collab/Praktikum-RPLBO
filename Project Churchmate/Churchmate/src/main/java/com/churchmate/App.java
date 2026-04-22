package com.churchmate;

import com.churchmate.model.Gereja;
import com.churchmate.model.Ibadah;
import com.churchmate.model.Kegiatan;
import com.churchmate.service.DatabaseService;
import com.churchmate.controller.ChatManager;
import com.churchmate.service.ChatService;
import com.churchmate.ui.UserUI;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date; // Pakai java.util.Date untuk Kegiatan

public class App {
    public static void main(String[] args) {
        DatabaseService db = new DatabaseService();

        // Hanya seed data jika database masih kosong
//        if (db.isEmpty()) {
//            db.save(new Gereja(
//                    1,
//                    "Gereja ABC",
//                    "Jl. Mawar No. 10",
//                    "08123456789",
//                    "Gereja umum",
//                    "www.gereja.com",
//                    "gereja@mail.com"
//            ));
//
//            db.save(new Ibadah(
//                    1,
//                    1,
//                    "Ibadah Minggu",
//                    LocalDate.of(2026, 2, 15),
//                    LocalTime.of(8, 0),
//                    "Pdt. Yohanes",
//                    "Kasih Tuhan",
//                    "Gereja Utama"
//            ));
//
//            db.save(new Kegiatan(
//                    1,
//                    1,
//                    "Persekutuan Pemuda",
//                    LocalDate.of(2026, 2, 16),
//                    "Pemuda",
//                    "Aula Gereja",
//                    "Kegiatan rutin pemuda"
//            ));
//
//            db.save(new Ibadah(
//                    1,
//                    2,
//                    "Ibadah Pemuda",
//                    LocalDate.of(2026, 2, 21),
//                    LocalTime.of(17, 0),
//                    "Pdt. Markus",
//                    "Masa Muda Untuk Kristus",
//                    "Ruang Ibadah Lt. 2"
//            ));
//
//            db.save(new Ibadah(
//                    1,
//                    3,
//                    "Sekolah Minggu",
//                    LocalDate.of(2026, 2, 15),
//                    LocalTime.of(8, 0),
//                    "Kak Lidia",
//                    "Mengenal Kasih Tuhan",
//                    "Ruang Anak"
//            ));
//
//            db.save(new Kegiatan(
//                    1,
//                    2,
//                    "Pendalaman Alkitab",
//                    LocalDate.of(2026, 2, 18),
//                    "Umum",
//                    "Ruang Serbaguna",
//                    "Membahas kitab Roma"
//            ));
//
//            db.save(new Kegiatan(
//                    1,
//                    3,
//                    "Latihan Paduan Suara",
//                    LocalDate.of(2026, 2, 20),
//                    "Musik",
//                    "Aula Gereja",
//                    "Persiapan pujian hari Minggu"
//            ));
//
//            System.out.println("Data awal berhasil disimpan ke database.");
//        } else {
//            System.out.println("Database sudah berisi data, melewati proses seeding.");
//        }

        ChatService chatService = new ChatService(db);
        ChatManager chatManager = new ChatManager(chatService);
        UserUI userUI = new UserUI(chatManager);

        userUI.showChatInterface();
    }
}