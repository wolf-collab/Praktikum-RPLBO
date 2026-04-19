package com.churchmate.service;

import java.util.List;
import java.util.ArrayList;

public class ManageDataService {
    private DatabaseService databaseService;
    private List<String> validationRules = new ArrayList<>();

    public ManageDataService() {
        this.databaseService = new DatabaseService();
    }

    public boolean validateData(Object data) {
        return true;
    }

    public void saveToDatabase(Object data) {
        databaseService.save(data);
    }

    public void removeFromDatabase(int id) {
        databaseService.delete(id);
    }

    public List<Object> fetchData(String query) {
        return databaseService.findAll();
    }
}