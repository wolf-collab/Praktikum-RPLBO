package com.churchmate.model;

import java.time.LocalDate;
import java.util.Date;
import java.time.LocalTime;

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

    public int getKegiatanId() {
        return kegiatanId;
    }

    public String getJudul() {
        return judul;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public String getKategori() {
        return kategori;
    }
}
