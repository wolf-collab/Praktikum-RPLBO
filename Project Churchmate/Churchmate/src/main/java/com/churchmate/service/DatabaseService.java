package com.churchmate.service;

import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;

// Asumsi EntityManager adalah class bawaan/dummy untuk ORM
class EntityManager {}

public class DatabaseService {
    private Connection connection;
    private EntityManager entityManager;

    public void save(Object entity) {
        // Insert logika query
    }

    public Object findById(int id) {
        return null;
    }

    public List<Object> findAll() {
        return new ArrayList<>();
    }

    public void delete(int id) {
        // Delete logika query
    }
}