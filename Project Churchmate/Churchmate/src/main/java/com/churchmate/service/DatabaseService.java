package com.churchmate.service;

import java.util.ArrayList;
import java.util.List;

public class DatabaseService {

    /** untuk membuat sebuah storage sementara dalam bentuk list*/
    private final List<Object> storage = new ArrayList<>();

    /** untuk menyimpan data ke dalam storage*/
    public void save(Object entity) {
        storage.add(entity);
    }

    /** untuk mengambil data*/
    public List<Object> findAll() {
        return storage;
    }
}