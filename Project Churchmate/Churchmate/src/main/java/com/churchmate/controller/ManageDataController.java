package com.churchmate.controller;

import com.churchmate.model.Gereja;
import com.churchmate.model.Ibadah;
import com.churchmate.model.Kegiatan;
import com.churchmate.service.ManageDataService;

import java.util.List;

public class ManageDataController {

    private final ManageDataService service;

    public ManageDataController(ManageDataService service) {
        this.service = service;
    }

    // ==========================================
    // Gereja
    // ==========================================

    public List<Gereja> getAllGereja() {
        return service.getAllGereja();
    }

    public void addGereja(Gereja gereja) {
        service.addGereja(gereja);
    }

    public void updateGereja(Gereja gereja) {
        service.updateGereja(gereja);
    }

    public void deleteGereja(int gerejaId) {
        service.deleteGereja(gerejaId);
    }

    public int getNextGerejaId() {
        return service.getNextGerejaId();
    }

    // ==========================================
    // Ibadah
    // ==========================================

    public List<Ibadah> getAllIbadah() {
        return service.getAllIbadah();
    }

    public void addIbadah(Ibadah ibadah) {
        service.addIbadah(ibadah);
    }

    public void updateIbadah(Ibadah ibadah) {
        service.updateIbadah(ibadah);
    }

    public void deleteIbadah(int ibadahId) {
        service.deleteIbadah(ibadahId);
    }

    public int getNextIbadahId() {
        return service.getNextIbadahId();
    }

    // ==========================================
    // Kegiatan
    // ==========================================

    public List<Kegiatan> getAllKegiatan() {
        return service.getAllKegiatan();
    }

    public void addKegiatan(Kegiatan kegiatan) {
        service.addKegiatan(kegiatan);
    }

    public void updateKegiatan(Kegiatan kegiatan) {
        service.updateKegiatan(kegiatan);
    }

    public void deleteKegiatan(int kegiatanId) {
        service.deleteKegiatan(kegiatanId);
    }

    public int getNextKegiatanId() {
        return service.getNextKegiatanId();
    }
}