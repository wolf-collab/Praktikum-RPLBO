package com.churchmate.model;

import java.time.LocalDate;
import java.util.Date;
import java.time.LocalTime;

public class Kegiatan {
    private int kegiatanId;
    private int gerejaId;
    private String judul;
    private LocalDate tanggal;
    private String kategori;
    private String lokasi;
    private String deskripsi;

    public Kegiatan(int kegiatanId, int gerejaId, String judul, LocalDate tanggal,
                    String kategori, String lokasi, String deskripsi) {
        this.kegiatanId = kegiatanId;
        this.gerejaId = gerejaId;
        this.judul = judul;
        this.tanggal = tanggal;
        this.kategori = kategori;
        this.lokasi = lokasi;
        this.deskripsi = deskripsi;
    }

    public String getJudul() {
        return judul;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public String getKategori() {
        return kategori;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getLokasi() {
        return lokasi;
    }
}
