package com.churchmate.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Ibadah {
    private int ibadahId;
    private int gerejaId;
    private String namaIbadah;
    private LocalDate tglIbadah;
    private LocalTime jam;
    private String pendeta;
    private String tema;
    private String lokasi;

    public Ibadah(int ibadahId, int gerejaId, String namaIbadah, LocalDate tglIbadah,
                  LocalTime jam, String pendeta, String tema, String lokasi) {
        this.ibadahId = ibadahId;
        this.gerejaId = gerejaId;
        this.namaIbadah = namaIbadah;
        this.tglIbadah = tglIbadah;
        this.jam = jam;
        this.pendeta = pendeta;
        this.tema = tema;
        this.lokasi = lokasi;
    }

    public int getIbadahId() { return ibadahId; }
    public int getGerejaId() { return gerejaId; }
    public String getNamaIbadah() { return namaIbadah; }
    public LocalDate getTglIbadah() { return tglIbadah; }
    public LocalTime getJam() { return jam; }
    public String getPendeta() { return pendeta; }
    public String getTema() { return tema; }
    public String getLokasi() { return lokasi; }

    public void setNamaIbadah(String namaIbadah) { this.namaIbadah = namaIbadah; }
    public void setTglIbadah(LocalDate tglIbadah) { this.tglIbadah = tglIbadah; }
    public void setJam(LocalTime jam) { this.jam = jam; }
    public void setPendeta(String pendeta) { this.pendeta = pendeta; }
    public void setTema(String tema) { this.tema = tema; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }

    public String getJadwal() {
        return namaIbadah + " | " + tglIbadah + " | " + jam + " | " + lokasi;
    }

    public String getUpcoming() {
        return "• " + namaIbadah + "\n  Tanggal: " + tglIbadah
                + "\n  Jam: " + jam + "\n  Pendeta: " + pendeta
                + "\n  Tema: " + tema + "\n  Lokasi: " + lokasi;
    }
}
