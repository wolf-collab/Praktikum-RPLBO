package com.churchmate.model;

import java.util.Date;

public class Kegiatan {
    private int kegiatanId;
    private String judul;
    private Date tanggal;
    private String kategori;

    public Kegiatan(int kegiatanId, String judul, Date tanggal, String kategori) {
        this.kegiatanId = kegiatanId;
        this.judul = judul;
        this.tanggal = tanggal;
        this.kategori = kategori;
    }

    public String getDetail() { return "Kegiatan: " + judul; }
    public String getUpcoming() { return "Upcoming Kegiatan: " + judul; }
}