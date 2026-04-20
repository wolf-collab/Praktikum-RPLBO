package com.churchmate.dao;

import com.churchmate.model.Ibadah;
import com.churchmate.service.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class IbadahDAO {

    public void insert(Ibadah ibadah) {
        String sql = "INSERT OR REPLACE INTO ibadah (ibadah_id, gereja_id, nama_ibadah, tgl_ibadah, jam, pendeta, tema, lokasi) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, ibadah.getIbadahId());
            pstmt.setInt(2, ibadah.getGerejaid());
            pstmt.setString(3, ibadah.getNamaibadah());
            pstmt.setString(4, ibadah.getTglIbadah() != null ? ibadah.getTglIbadah().toString() : null);
            pstmt.setString(5, ibadah.getJam() != null ? ibadah.getJam().toString() : null);
            pstmt.setString(6, ibadah.getPendeta());
            pstmt.setString(7, ibadah.getTema());
            pstmt.setString(8, ibadah.getLokasi());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting Ibadah: " + e.getMessage());
        }
    }

    public List<Ibadah> findAll() {
        List<Ibadah> list = new ArrayList<>();
        String sql = "SELECT * FROM ibadah";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Ibadah ibadah = new Ibadah(
                        rs.getInt("gereja_id"),
                        rs.getInt("ibadah_id"),
                        rs.getString("nama_ibadah"),
                        rs.getString("tgl_ibadah") != null ? LocalDate.parse(rs.getString("tgl_ibadah")) : null,
                        rs.getString("jam") != null ? LocalTime.parse(rs.getString("jam")) : null,
                        rs.getString("pendeta"),
                        rs.getString("tema"),
                        rs.getString("lokasi")
                );
                list.add(ibadah);
            }
        } catch (SQLException e) {
            System.err.println("Error finding all Ibadah: " + e.getMessage());
        }
        return list;
    }

    public Ibadah findById(int ibadahId) {
        String sql = "SELECT * FROM ibadah WHERE ibadah_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, ibadahId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Ibadah(
                        rs.getInt("gereja_id"),
                        rs.getInt("ibadah_id"),
                        rs.getString("nama_ibadah"),
                        rs.getString("tgl_ibadah") != null ? LocalDate.parse(rs.getString("tgl_ibadah")) : null,
                        rs.getString("jam") != null ? LocalTime.parse(rs.getString("jam")) : null,
                        rs.getString("pendeta"),
                        rs.getString("tema"),
                        rs.getString("lokasi")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error finding Ibadah by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Ibadah> findByGerejaId(int gerejaId) {
        List<Ibadah> list = new ArrayList<>();
        String sql = "SELECT * FROM ibadah WHERE gereja_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, gerejaId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Ibadah ibadah = new Ibadah(
                        rs.getInt("gereja_id"),
                        rs.getInt("ibadah_id"),
                        rs.getString("nama_ibadah"),
                        rs.getString("tgl_ibadah") != null ? LocalDate.parse(rs.getString("tgl_ibadah")) : null,
                        rs.getString("jam") != null ? LocalTime.parse(rs.getString("jam")) : null,
                        rs.getString("pendeta"),
                        rs.getString("tema"),
                        rs.getString("lokasi")
                );
                list.add(ibadah);
            }
        } catch (SQLException e) {
            System.err.println("Error finding Ibadah by Gereja ID: " + e.getMessage());
        }
        return list;
    }

    public void update(Ibadah ibadah) {
        String sql = "UPDATE ibadah SET gereja_id = ?, nama_ibadah = ?, tgl_ibadah = ?, jam = ?, pendeta = ?, tema = ?, lokasi = ? WHERE ibadah_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, ibadah.getGerejaid());
            pstmt.setString(2, ibadah.getNamaibadah());
            pstmt.setString(3, ibadah.getTglIbadah() != null ? ibadah.getTglIbadah().toString() : null);
            pstmt.setString(4, ibadah.getJam() != null ? ibadah.getJam().toString() : null);
            pstmt.setString(5, ibadah.getPendeta());
            pstmt.setString(6, ibadah.getTema());
            pstmt.setString(7, ibadah.getLokasi());
            pstmt.setInt(8, ibadah.getIbadahId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating Ibadah: " + e.getMessage());
        }
    }

    public void delete(int ibadahId) {
        String sql = "DELETE FROM ibadah WHERE ibadah_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, ibadahId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting Ibadah: " + e.getMessage());
        }
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM ibadah";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting Ibadah: " + e.getMessage());
        }
        return 0;
    }
}
