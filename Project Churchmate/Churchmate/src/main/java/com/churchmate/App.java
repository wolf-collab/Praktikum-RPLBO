package com.churchmate;

import com.churchmate.model.Gereja;
import com.churchmate.model.Ibadah;
import com.churchmate.model.Kegiatan;
import com.churchmate.service.DatabaseService;

import java.time.LocalDateTime;
import java.util.Date; // Pakai java.util.Date untuk Kegiatan

public class App {
    public static void main(String[] args) {
        DatabaseService db = new DatabaseService();

        // 1. Model Gereja (4 Parameter: id, nama, alamat, deskripsi)
        db.save(new Gereja(
                1,
                "Gereja ABC",
                "Jl. Mawar",
                "Gereja umum di pusat kota"
        ));

        // 2. Model Ibadah (4 Parameter: id, nama, jadwal(LocalDateTime), lokasi)
        db.save(new Ibadah(
                1,
                "Ibadah Minggu",
                LocalDateTime.of(2026, 4, 19, 8, 0),
                "Gereja Utama"
        ));

        // 3. Model Kegiatan (4 Parameter: id, judul, tanggal(Date), kategori)
        db.save(new Kegiatan(
                1,
                "Persekutuan Pemuda",
                new Date(), // Mengambil tanggal hari ini
                "Pemuda"
        ));

        System.out.println("Data berhasil disimpan ke DatabaseService!");
    }
}