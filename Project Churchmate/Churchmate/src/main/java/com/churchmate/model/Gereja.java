package com.churchmate.model;

public class Gereja {
    private int gerejaId;
    private String nama;
    private String alamat;
    private String noTelp;
    private String deskripsi;
    private String website;
    private String email;

    public Gereja(int gerejaId, String nama, String alamat, String noTelp,
                  String deskripsi, String website, String email) {
        this.gerejaId = gerejaId;
        this.nama = nama;
        this.alamat = alamat;
        this.noTelp = noTelp;
        this.deskripsi = deskripsi;
        this.website = website;
        this.email = email;
    }

    public int getGerejaId() { return gerejaId; }
    public String getNama() { return nama; }
    public String getAlamat() { return alamat; }
    public String getNoTelp() { return noTelp; }
    public String getDeskripsi() { return deskripsi; }
    public String getWebsite() { return website; }
    public String getEmail() { return email; }

    public void setNama(String nama) { this.nama = nama; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
    public void setNoTelp(String noTelp) { this.noTelp = noTelp; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public void setWebsite(String website) { this.website = website; }
    public void setEmail(String email) { this.email = email; }

    public String getInfo() {
        return "Nama: " + nama + "\nAlamat: " + alamat + "\nTelepon: " + noTelp
                + "\nEmail: " + email + "\nWebsite: " + website + "\nDeskripsi: " + deskripsi;
    }
}
