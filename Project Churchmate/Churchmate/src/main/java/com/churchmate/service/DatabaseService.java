package com.churchmate.service;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.churchmate.model.Kegiatan;
import com.churchmate.model.Ibadah;
import com.churchmate.model.Gereja;


public class DatabaseService {
    /** ini untuk membuat storage sementara untuk menyimpan datanya */
    private final List<Object> storage = new ArrayList<>();

    /** ini untuk menyimpan datanya ke dalam storage */
    public void save(Object entity) {
        storage.add(entity);
    }

    /** ini untuk mencari datanya dalam storage */
    public List<Object> findAll() {
        return storage;
    }
}
