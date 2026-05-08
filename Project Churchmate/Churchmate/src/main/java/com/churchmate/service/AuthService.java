package com.churchmate.service;

import com.churchmate.dao.UserDAO;
import com.churchmate.model.User;
public class AuthService {
    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public boolean login(String username, String password) {
        User user = userDAO.findByUsername(username);

        if (user == null) {
            return false;
        }
        return user.getPassword().equals(password) && "admin".equalsIgnoreCase(user.getRole());
    }

    public void logout() {
        // Logout ditangani di sisi UI
    }
}
