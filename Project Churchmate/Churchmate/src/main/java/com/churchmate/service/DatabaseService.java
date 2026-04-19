package com.churchmate.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DatabaseService {
    /** sebagai tempat menyimpan data sementara */
    private final List<Object> storage = new ArrayList<>();

    /** untuk menyimpan data ke dalam storage */
    public void save(Object entity) {
        if (entity != null) {
            storage.add(entity);
        }
    }

    /** untuk mencari data berdasarkan id yang ada pada field object */
    public Object findById(int id) {
        for (Object entity : storage) {
            if (extractId(entity) == id) {
                return entity;
            }
        }
        return null;
    }

    /** sebagai tempat untuk melihat semua object dalam storage */
    public List<Object> findAll() {
        return new ArrayList<>(storage);
    }

    /** untuk menghapus data berdasarkan id */
    public void delete(int id) {
        Iterator<Object> iterator = storage.iterator();
        while (iterator.hasNext()) {
            if (extractId(iterator.next()) == id) {
                iterator.remove();
                return;
            }
        }
    }

    private int extractId(Object entity) {
        if (entity == null) {
            return -1;
        }

        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.getType() == int.class && field.getName().toLowerCase().endsWith("id")) {
                try {
                    field.setAccessible(true);
                    return field.getInt(entity);
                } catch (IllegalAccessException ignored) {
                    return -1;
                }
            }
        }

        return -1;
    }
}
