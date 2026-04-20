package com.churchmate.model;

import java.time.LocalDate;
import java.util.Date;

public class Kegiatan {
    private int gerejaid;
    private int kegiatanId;
    private String judul;
    private LocalDate tanggal;
    private String kategori;
    private String lokasi;
    private String deskripsi;

    public Kegiatan(int gerejaid, int kegiatanId, String judul, LocalDate tanggal, String kategori, String lokasi,
            String deskripsi) {
        this.gerejaid = gerejaid;
        this.kegiatanId = kegiatanId;
        this.judul = judul;
        this.tanggal = tanggal;
        this.kategori = kategori;
        this.lokasi = lokasi;
        this.deskripsi = deskripsi;
    }

    public String getDetail() {
        return "\nKegiatan: " + judul +
                "\nTanggal: " + tanggal +
                "\nKategori: " + kategori +
                "\nLokasi: " + lokasi +
                "\nDeskripsi: " + deskripsi;
    }

    public String getUpcoming() {
        return "Upcoming Kegiatan: " + judul;
    }

    public int getKegiatanId() {
        return kegiatanId;
    }

    public String getJudul() {
        return judul;
    }

    public String getKategori() {
        return kategori;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public String getLokasi() {
        return lokasi;
    }

    public String getDeskripsi() {
        return deskripsi;
    }
}