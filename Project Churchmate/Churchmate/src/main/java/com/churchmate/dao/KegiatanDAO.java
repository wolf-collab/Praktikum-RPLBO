package com.churchmate.dao;

import com.churchmate.model.Kegiatan;
import com.churchmate.service.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class KegiatanDAO {

    public void insert(Kegiatan kegiatan) {
        String sql = "INSERT OR REPLACE INTO kegiatan (kegiatan_id, gereja_id, judul, tanggal, kategori, lokasi, deskripsi) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, kegiatan.getKegiatanId());
            pstmt.setInt(2, kegiatan.getGerejaid());
            pstmt.setString(3, kegiatan.getJudul());
            pstmt.setString(4, kegiatan.getTanggal() != null ? kegiatan.getTanggal().toString() : null);
            pstmt.setString(5, kegiatan.getKategori());
            pstmt.setString(6, kegiatan.getLokasi());
            pstmt.setString(7, kegiatan.getDeskripsi());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting Kegiatan: " + e.getMessage());
        }
    }

    public List<Kegiatan> findAll() {
        List<Kegiatan> list = new ArrayList<>();
        String sql = "SELECT * FROM kegiatan";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Kegiatan kegiatan = new Kegiatan(
                        rs.getInt("gereja_id"),
                        rs.getInt("kegiatan_id"),
                        rs.getString("judul"),
                        rs.getString("tanggal") != null ? LocalDate.parse(rs.getString("tanggal")) : null,
                        rs.getString("kategori"),
                        rs.getString("lokasi"),
                        rs.getString("deskripsi")
                );
                list.add(kegiatan);
            }
        } catch (SQLException e) {
            System.err.println("Error finding all Kegiatan: " + e.getMessage());
        }
        return list;
    }

    public Kegiatan findById(int kegiatanId) {
        String sql = "SELECT * FROM kegiatan WHERE kegiatan_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, kegiatanId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Kegiatan(
                        rs.getInt("gereja_id"),
                        rs.getInt("kegiatan_id"),
                        rs.getString("judul"),
                        rs.getString("tanggal") != null ? LocalDate.parse(rs.getString("tanggal")) : null,
                        rs.getString("kategori"),
                        rs.getString("lokasi"),
                        rs.getString("deskripsi")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error finding Kegiatan by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Kegiatan> findByGerejaId(int gerejaId) {
        List<Kegiatan> list = new ArrayList<>();
        String sql = "SELECT * FROM kegiatan WHERE gereja_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, gerejaId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Kegiatan kegiatan = new Kegiatan(
                        rs.getInt("gereja_id"),
                        rs.getInt("kegiatan_id"),
                        rs.getString("judul"),
                        rs.getString("tanggal") != null ? LocalDate.parse(rs.getString("tanggal")) : null,
                        rs.getString("kategori"),
                        rs.getString("lokasi"),
                        rs.getString("deskripsi")
                );
                list.add(kegiatan);
            }
        } catch (SQLException e) {
            System.err.println("Error finding Kegiatan by Gereja ID: " + e.getMessage());
        }
        return list;
    }

    public void update(Kegiatan kegiatan) {
        String sql = "UPDATE kegiatan SET gereja_id = ?, judul = ?, tanggal = ?, kategori = ?, lokasi = ?, deskripsi = ? WHERE kegiatan_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, kegiatan.getGerejaid());
            pstmt.setString(2, kegiatan.getJudul());
            pstmt.setString(3, kegiatan.getTanggal() != null ? kegiatan.getTanggal().toString() : null);
            pstmt.setString(4, kegiatan.getKategori());
            pstmt.setString(5, kegiatan.getLokasi());
            pstmt.setString(6, kegiatan.getDeskripsi());
            pstmt.setInt(7, kegiatan.getKegiatanId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating Kegiatan: " + e.getMessage());
        }
    }

    public void delete(int kegiatanId) {
        String sql = "DELETE FROM kegiatan WHERE kegiatan_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, kegiatanId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting Kegiatan: " + e.getMessage());
        }
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM kegiatan";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting Kegiatan: " + e.getMessage());
        }
        return 0;
    }
}
