package com.churchmate.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Ibadah {
    private int gerejaid;
    private int ibadahId;
    private String namaibadah;
    private LocalDate tglibadah;
    private LocalTime jam;
    public String pendeta;
    public String tema;
    private String lokasi;

    public Ibadah(int gerejaid, int ibadahId, String namaibadah, LocalDate tglibadah, LocalTime jam, String pendeta, String tema, String lokasi) {
        this.gerejaid = gerejaid;
        this.ibadahId = ibadahId;
        this.namaibadah = namaibadah;
        this.tglibadah = tglibadah;
        this.jam = jam;
        this.pendeta = pendeta;
        this.tema = tema;
        this.lokasi = lokasi;
    }

    public String getUpcoming() {
        return "• " + namaibadah +
                "\n  Tanggal: " + tglibadah +
                "\n  Jam: " + jam +
                "\n  Pendeta: " + pendeta +
                "\n  Tema: " + tema +
                "\n  Lokasi: " + lokasi;
    }

    public int getGerejaid() {
        return gerejaid;
    }

    public int getIbadahId() {
        return ibadahId;
    }

    public String getNamaibadah() {
        return namaibadah;
    }

    public String getLokasi() {
        return lokasi;
    }

    public LocalDate getTglIbadah() {
        return tglibadah;
    }

    public LocalTime getJam() {
        return jam;
    }

    public String getPendeta() {
        return pendeta;
    }

    public String getTema() {
        return tema;
    }
}