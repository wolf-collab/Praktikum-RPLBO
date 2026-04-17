package com.churchmate.model;

public class Gereja {
    private int gerejaId;
    private String nama;
    private String alamat;
    private String deskripsi;

    public Gereja(int gerejaId, String nama, String alamat, String deskripsi) {
        this.gerejaId = gerejaId;
        this.nama = nama;
        this.alamat = alamat;
        this.deskripsi = deskripsi;
    }
}
