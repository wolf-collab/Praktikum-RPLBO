package com.churchmate.model;

import java.time.LocalDateTime;

public class Ibadah {
    private int ibadahId;
    private String nama;
    private LocalDateTime jadwal;
    private String lokasi;

    public Ibadah(int ibadahId, String nama, LocalDateTime jadwal, String lokasi) {
        this.ibadahId = ibadahId;
        this.nama = nama;
        this.jadwal = jadwal;
        this.lokasi = lokasi;
    }
}
