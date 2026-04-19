package com.churchmate.controller;

import com.churchmate.service.ManageDataService;
import java.util.List;

public class ManageDataController {
    private ManageDataService manageDataService;

    public ManageDataController() {
        this.manageDataService = new ManageDataService();
    }

    public void addData(Object data) {
        manageDataService.saveToDatabase(data);
    }

    public void updateData(int id, Object data) {
        // Logika update
    }

    public void deleteData(int id) {
        manageDataService.removeFromDatabase(id);
    }

    public List<Object> getData() {
        return manageDataService.fetchData("SELECT * FROM data");
    }
}