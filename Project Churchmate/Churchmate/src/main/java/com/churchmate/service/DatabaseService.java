package com.churchmate.service;

import com.churchmate.dao.GerejaDAO;
import com.churchmate.dao.IbadahDAO;
import com.churchmate.dao.KegiatanDAO;
import com.churchmate.dao.UserDAO;
import com.churchmate.model.Gereja;
import com.churchmate.model.Ibadah;
import com.churchmate.model.Kegiatan;
import com.churchmate.model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseService {

    private final GerejaDAO gerejaDAO;
    private final IbadahDAO ibadahDAO;
    private final KegiatanDAO kegiatanDAO;
    private final UserDAO userDAO;

    public DatabaseService() {
        DatabaseConnection.initializeDatabase();
        this.gerejaDAO = new GerejaDAO();
        this.ibadahDAO = new IbadahDAO();
        this.kegiatanDAO = new KegiatanDAO();
        this.userDAO = new UserDAO();
    }

    /** untuk menyimpan data ke dalam database */
    public void save(Object entity) {
        if (entity instanceof Gereja) {
            gerejaDAO.insert((Gereja) entity);
        } else if (entity instanceof Ibadah) {
            ibadahDAO.insert((Ibadah) entity);
        } else if (entity instanceof Kegiatan) {
            kegiatanDAO.insert((Kegiatan) entity);
        } else if (entity instanceof User) {
            userDAO.insert((User) entity);
        }
    }

    /** untuk mengambil data */
    public List<Object> findAll() {
        List<Object> all = new ArrayList<>();
        all.addAll(gerejaDAO.findAll());
        all.addAll(ibadahDAO.findAll());
        all.addAll(kegiatanDAO.findAll());
        return all;
    }

    /** untuk mengupdate data di database */
    public void update(Object entity) {
        if (entity instanceof Gereja) {
            gerejaDAO.update((Gereja) entity);
        } else if (entity instanceof Ibadah) {
            ibadahDAO.update((Ibadah) entity);
        } else if (entity instanceof Kegiatan) {
            kegiatanDAO.update((Kegiatan) entity);
        } else if (entity instanceof User) {
            userDAO.update((User) entity);
        }
    }

    /** untuk menghapus data dari database */
    public void delete(Object entity) {
        if (entity instanceof Gereja) {
            gerejaDAO.delete(((Gereja) entity).getGerejaId());
        } else if (entity instanceof Ibadah) {
            ibadahDAO.delete(((Ibadah) entity).getIbadahId());
        } else if (entity instanceof Kegiatan) {
            kegiatanDAO.delete(((Kegiatan) entity).getKegiatanId());
        } else if (entity instanceof User) {
            userDAO.delete(((User) entity).getUserId());
        }
    }

    // ==========================================
    // Akses langsung ke DAO untuk operasi spesifik
    // ==========================================

    public GerejaDAO getGerejaDAO() {
        return gerejaDAO;
    }

    public IbadahDAO getIbadahDAO() {
        return ibadahDAO;
    }

    public KegiatanDAO getKegiatanDAO() {
        return kegiatanDAO;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    /** cek apakah database sudah berisi data awal */
    public boolean isEmpty() {
        return gerejaDAO.count() == 0
                && ibadahDAO.count() == 0
                && kegiatanDAO.count() == 0;
    }
}