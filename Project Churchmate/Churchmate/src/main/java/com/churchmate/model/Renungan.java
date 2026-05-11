package com.churchmate.model;

public class Renungan {
    private int id;
    private String tanggal;
    private String judul;
    private String ayatReferensi;
    private String isiRenungan;

    // Constructor untuk mengambil data dari database (dengan ID)
    public Renungan(int id, String tanggal, String judul, String ayatReferensi, String isiRenungan) {
        this.id = id;
        this.tanggal = tanggal;
        this.judul = judul;
        this.ayatReferensi = ayatReferensi;
        this.isiRenungan = isiRenungan;
    }

    // Constructor untuk menyimpan data baru ke database (tanpa ID, karena AutoIncrement)
    public Renungan(String tanggal, String judul, String ayatReferensi, String isiRenungan) {
        this.tanggal = tanggal;
        this.judul = judul;
        this.ayatReferensi = ayatReferensi;
        this.isiRenungan = isiRenungan;
    }

    // Getter dan Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getAyatReferensi() { return ayatReferensi; }
    public void setAyatReferensi(String ayatReferensi) { this.ayatReferensi = ayatReferensi; }

    public String getIsiRenungan() { return isiRenungan; }
    public void setIsiRenungan(String isiRenungan) { this.isiRenungan = isiRenungan; }
}