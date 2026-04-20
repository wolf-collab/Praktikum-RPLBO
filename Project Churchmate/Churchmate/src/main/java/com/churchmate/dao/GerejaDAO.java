package com.churchmate.dao;

import com.churchmate.model.Gereja;
import com.churchmate.service.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GerejaDAO {

    public void insert(Gereja gereja) {
        String sql = "INSERT OR REPLACE INTO gereja (gereja_id, nama, alamat, notelp, deskripsi, website, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, gereja.getGerejaId());
            pstmt.setString(2, gereja.getNama());
            pstmt.setString(3, gereja.getAlamat());
            pstmt.setString(4, gereja.getNoTelp());
            pstmt.setString(5, gereja.getDeskripsi());
            pstmt.setString(6, gereja.getWebsite());
            pstmt.setString(7, gereja.getEmail());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting Gereja: " + e.getMessage());
        }
    }

    public List<Gereja> findAll() {
        List<Gereja> list = new ArrayList<>();
        String sql = "SELECT * FROM gereja";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Gereja gereja = new Gereja(
                        rs.getInt("gereja_id"),
                        rs.getString("nama"),
                        rs.getString("alamat"),
                        rs.getString("notelp"),
                        rs.getString("deskripsi"),
                        rs.getString("website"),
                        rs.getString("email")
                );
                list.add(gereja);
            }
        } catch (SQLException e) {
            System.err.println("Error finding all Gereja: " + e.getMessage());
        }
        return list;
    }

    public Gereja findById(int gerejaId) {
        String sql = "SELECT * FROM gereja WHERE gereja_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, gerejaId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Gereja(
                        rs.getInt("gereja_id"),
                        rs.getString("nama"),
                        rs.getString("alamat"),
                        rs.getString("notelp"),
                        rs.getString("deskripsi"),
                        rs.getString("website"),
                        rs.getString("email")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error finding Gereja by ID: " + e.getMessage());
        }
        return null;
    }

    public void update(Gereja gereja) {
        String sql = "UPDATE gereja SET nama = ?, alamat = ?, notelp = ?, deskripsi = ?, website = ?, email = ? WHERE gereja_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, gereja.getNama());
            pstmt.setString(2, gereja.getAlamat());
            pstmt.setString(3, gereja.getNoTelp());
            pstmt.setString(4, gereja.getDeskripsi());
            pstmt.setString(5, gereja.getWebsite());
            pstmt.setString(6, gereja.getEmail());
            pstmt.setInt(7, gereja.getGerejaId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating Gereja: " + e.getMessage());
        }
    }

    public void delete(int gerejaId) {
        String sql = "DELETE FROM gereja WHERE gereja_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, gerejaId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting Gereja: " + e.getMessage());
        }
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM gereja";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting Gereja: " + e.getMessage());
        }
        return 0;
    }
}
