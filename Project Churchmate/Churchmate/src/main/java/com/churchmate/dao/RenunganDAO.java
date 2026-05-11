package com.churchmate.dao;

import com.churchmate.model.Renungan;
import com.churchmate.service.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RenunganDAO {

    // 1. CHATBOT: Ambil renungan hari ini
    public String getRenunganHariIni() {
        String result = "Maaf, belum ada renungan untuk hari ini.";
        String hariIni = LocalDate.now().toString();
        String sql = "SELECT * FROM renungan WHERE tanggal = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hariIni);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String judul = rs.getString("judul");
                String ayat = rs.getString("ayat_referensi");
                String isi = rs.getString("isi_renungan");

                result = "📖 *" + judul + "*\n" +
                        "Ayat: " + ayat + "\n\n" +
                        isi;
            }
        } catch (SQLException e) {
            System.out.println("Error ambil renungan hari ini: " + e.getMessage());
        }
        return result;
    }

    // 2. ADMIN: Tambah Renungan
    public boolean tambahRenungan(Renungan r) {
        String sql = "INSERT INTO renungan (tanggal, judul, ayat_referensi, isi_renungan) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, r.getTanggal());
            pstmt.setString(2, r.getJudul());
            pstmt.setString(3, r.getAyatReferensi());
            pstmt.setString(4, r.getIsiRenungan());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error tambah renungan: " + e.getMessage());
            return false;
        }
    }

    // 3. ADMIN: Ambil Semua Renungan (Untuk Tabel)
    public List<Renungan> getAllRenungan() {
        List<Renungan> list = new ArrayList<>();
        String sql = "SELECT * FROM renungan";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Renungan r = new Renungan(
                        rs.getInt("id"),
                        rs.getString("tanggal"),
                        rs.getString("judul"),
                        rs.getString("ayat_referensi"),
                        rs.getString("isi_renungan")
                );
                list.add(r);
            }
        } catch (SQLException e) {
            System.out.println("Error ambil semua renungan: " + e.getMessage());
        }
        return list;
    }

    // 4. ADMIN: Update Renungan
    public boolean updateRenungan(Renungan r) {
        String sql = "UPDATE renungan SET tanggal=?, judul=?, ayat_referensi=?, isi_renungan=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, r.getTanggal());
            pstmt.setString(2, r.getJudul());
            pstmt.setString(3, r.getAyatReferensi());
            pstmt.setString(4, r.getIsiRenungan());
            pstmt.setInt(5, r.getId());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error update renungan: " + e.getMessage());
            return false;
        }
    }

    // 5. ADMIN: Hapus Renungan
    public boolean deleteRenungan(int id) {
        String sql = "DELETE FROM renungan WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error hapus renungan: " + e.getMessage());
            return false;
        }
    }
}