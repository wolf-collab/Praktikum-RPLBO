package com.churchmate.model;

public class Gereja {
    private int gerejaId;
    private String nama;
    private String alamat;
    private String notelp;
    private String deskripsi;
    private String website;
    private String email;

    public Gereja(int gerejaId, String nama, String alamat, String notelp, String deskripsi, String website,
            String email) {
        this.gerejaId = gerejaId;
        this.nama = nama;
        this.alamat = alamat;
        this.notelp = notelp;
        this.deskripsi = deskripsi;
        this.website = website;
        this.email = email;

    }

    public String getInfo() {
        return "\n Nama Gereja: " + nama +
                "\nAlamat:" + alamat +
                "\nNoTelp:" + notelp +
                "\nDeskripsi:" + deskripsi +
                "\nWebsite:" + website +
                "\nEmail:" + email;
    }

    public int getGerejaId() {
        return gerejaId;
    }

    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getWebsite() {
        return website;
    }

    public String getEmail() {
        return email;
    }

    public String getNoTelp() {
        return notelp;
    }
}