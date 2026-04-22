package com.churchmate.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:churchmate.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
        }
        return connection;
    }

    public static void initializeDatabase() {
        try (Statement stmt = getConnection().createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS gereja ("
                    + "gereja_id INTEGER PRIMARY KEY,"
                    + "nama TEXT NOT NULL,"
                    + "alamat TEXT,"
                    + "notelp TEXT,"
                    + "deskripsi TEXT,"
                    + "website TEXT,"
                    + "email TEXT"
                    + ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS ibadah ("
                    + "ibadah_id INTEGER PRIMARY KEY,"
                    + "gereja_id INTEGER NOT NULL,"
                    + "nama_ibadah TEXT NOT NULL,"
                    + "tgl_ibadah TEXT,"
                    + "jam TEXT,"
                    + "pendeta TEXT,"
                    + "tema TEXT,"
                    + "lokasi TEXT,"
                    + "FOREIGN KEY (gereja_id) REFERENCES gereja(gereja_id)"
                    + ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS kegiatan ("
                    + "kegiatan_id INTEGER PRIMARY KEY,"
                    + "gereja_id INTEGER NOT NULL,"
                    + "judul TEXT NOT NULL,"
                    + "tanggal TEXT,"
                    + "kategori TEXT,"
                    + "lokasi TEXT,"
                    + "deskripsi TEXT,"
                    + "FOREIGN KEY (gereja_id) REFERENCES gereja(gereja_id)"
                    + ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS user ("
                    + "user_id INTEGER PRIMARY KEY,"
                    + "username TEXT NOT NULL,"
                    + "password TEXT NOT NULL,"
                    + "role TEXT"
                    + ")");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
