package com.churchmate.controller;

import com.churchmate.model.Gereja;
import com.churchmate.model.Ibadah;
import com.churchmate.model.Kegiatan;
import com.churchmate.service.ManageDataService;

import java.util.List;

/**
 * ManageDataController menjembatani Admin UI dengan ManageDataService.
 */
public class ManageDataController {

    private static ManageDataController instance;
    public static ManageDataController getInstance() {
        if (instance == null) instance = new ManageDataController();
        return instance;
    }

    private final ManageDataService service = ManageDataService.getInstance();

    private ManageDataController() {}

    // ── GEREJA ─────────────────────────────────────────────────────────────
    public Gereja getGereja()              { return service.getGereja(); }
    public boolean updateGereja(Gereja g)  { return service.updateGereja(g); }

    // ── IBADAH ─────────────────────────────────────────────────────────────
    public List<Ibadah> getAllIbadah()        { return service.getAllIbadah(); }
    public boolean addIbadah(Ibadah ib)       { return service.addIbadah(ib); }
    public boolean updateIbadah(Ibadah ib)    { return service.updateIbadah(ib); }
    public void deleteIbadah(int id)          { service.deleteIbadah(id); }
    public int nextIbadahId()                 { return service.nextIbadahId(); }

    // ── KEGIATAN ───────────────────────────────────────────────────────────
    public List<Kegiatan> getAllKegiatan()        { return service.getAllKegiatan(); }
    public boolean addKegiatan(Kegiatan k)        { return service.addKegiatan(k); }
    public boolean updateKegiatan(Kegiatan k)     { return service.updateKegiatan(k); }
    public void deleteKegiatan(int id)            { service.deleteKegiatan(id); }
    public int nextKegiatanId()                   { return service.nextKegiatanId(); }
}
