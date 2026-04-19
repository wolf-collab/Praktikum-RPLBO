package com.churchmate;

import com.churchmate.controller.ChatManager;
import com.churchmate.model.Gereja;
import com.churchmate.model.User;
import com.churchmate.model.Ibadah;
import com.churchmate.model.Kegiatan;
import com.churchmate.service.ChatService;
import com.churchmate.service.DatabaseService;
import com.churchmate.ui.UserUI;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


public class App {
    public static void main(String[] args) {
        DatabaseService db = new DatabaseService();

        db.save(new Gereja(
                1,
                "Gereja ABC",
                "Jl. Mawar",
                "08123456789",
                "Gereja umum",
                "www.gereja.com",
                "gereja@mail.com"
        ));
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
        db.save(new Kegiatan(
                1,
                1,
                "Persekutuan Pemuda",
                LocalDate.of(2026, 2, 16),
                "Pemuda",
                "Aula Gereja",
                "Kegiatan persekutuan rutin pemuda"
        ));
    }
}