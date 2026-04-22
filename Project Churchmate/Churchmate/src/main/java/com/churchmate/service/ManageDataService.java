package com.churchmate.service;

import com.churchmate.dao.GerejaDAO;
import com.churchmate.dao.IbadahDAO;
import com.churchmate.dao.KegiatanDAO;
import com.churchmate.model.Gereja;
import com.churchmate.model.Ibadah;
import com.churchmate.model.Kegiatan;

import java.util.List;

public class ManageDataService {

    private final GerejaDAO gerejaDAO;
    private final IbadahDAO ibadahDAO;
    private final KegiatanDAO kegiatanDAO;

    public ManageDataService(DatabaseService db) {
        this.gerejaDAO = db.getGerejaDAO();
        this.ibadahDAO = db.getIbadahDAO();
        this.kegiatanDAO = db.getKegiatanDAO();
    }

    // ==========================================
    // CRUD Gereja
    // ==========================================

    public List<Gereja> getAllGereja() {
        return gerejaDAO.findAll();
    }

    public void addGereja(Gereja gereja) {
        gerejaDAO.insert(gereja);
    }

    public void updateGereja(Gereja gereja) {
        gerejaDAO.update(gereja);
    }

    public void deleteGereja(int gerejaId) {
        gerejaDAO.delete(gerejaId);
    }

    // ==========================================
    // CRUD Ibadah
    // ==========================================

    public List<Ibadah> getAllIbadah() {
        return ibadahDAO.findAll();
    }

    public void addIbadah(Ibadah ibadah) {
        ibadahDAO.insert(ibadah);
    }

    public void updateIbadah(Ibadah ibadah) {
        ibadahDAO.update(ibadah);
    }

    public void deleteIbadah(int ibadahId) {
        ibadahDAO.delete(ibadahId);
    }

    // ==========================================
    // CRUD Kegiatan
    // ==========================================

    public List<Kegiatan> getAllKegiatan() {
        return kegiatanDAO.findAll();
    }

    public void addKegiatan(Kegiatan kegiatan) {
        kegiatanDAO.insert(kegiatan);
    }

    public void updateKegiatan(Kegiatan kegiatan) {
        kegiatanDAO.update(kegiatan);
    }

    public void deleteKegiatan(int kegiatanId) {
        kegiatanDAO.delete(kegiatanId);
    }

    // ==========================================
    // Helper: next available ID
    // ==========================================

    public int getNextGerejaId() {
        List<Gereja> list = gerejaDAO.findAll();
        int max = 0;
        for (Gereja g : list) {
            if (g.getGerejaId() > max) max = g.getGerejaId();
        }
        return max + 1;
    }

    public int getNextIbadahId() {
        List<Ibadah> list = ibadahDAO.findAll();
        int max = 0;
        for (Ibadah i : list) {
            if (i.getIbadahId() > max) max = i.getIbadahId();
        }
        return max + 1;
    }

    public int getNextKegiatanId() {
        List<Kegiatan> list = kegiatanDAO.findAll();
        int max = 0;
        for (Kegiatan k : list) {
            if (k.getKegiatanId() > max) max = k.getKegiatanId();
        }
        return max + 1;
    }
}