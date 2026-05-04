package com.churchmate.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class AlkitabDAO {

    private static class AyatRecord {
        String kitab;
        int pasal;
        int ayat;
        String firman;

        AyatRecord(String kitab, int pasal, int ayat, String firman) {
            this.kitab = kitab;
            this.pasal = pasal;
            this.ayat = ayat;
            this.firman = firman;
        }
    }

    private final List<AyatRecord> databaseCSV = new ArrayList<>();
    private List<String> urutanKitab = new ArrayList<>();

    public AlkitabDAO() {
        bacaDariCSV();
    }

    private void bacaDariCSV() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("tb.csv")) {
            if (is == null) {
                System.err.println("FILE TIDAK DITEMUKAN: Pastikan tb.csv ada di src/main/resources");
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            boolean isHeader = true;
            LinkedHashSet<String> kitabSet = new LinkedHashSet<>();

            while ((line = br.readLine()) != null) {
                if (isHeader || line.trim().isEmpty()) {
                    isHeader = false;
                    continue;
                }

                // Menggunakan regex untuk memecah CSV dengan lebih aman
                // (menangani koma di dalam tanda kutip)
                String[] kolom = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (kolom.length >= 5) {
                    try {
                        String kitab = kolom[1].trim();
                        int pasal = Integer.parseInt(kolom[2].trim());
                        int ayat = Integer.parseInt(kolom[3].trim());
                        String firman = kolom[4].trim();

                        // Bersihkan tanda kutip
                        if (firman.startsWith("\"") && firman.endsWith("\"")) {
                            firman = firman.substring(1, firman.length() - 1);
                        }
                        firman = firman.replace("\"\"", "\"");

                        databaseCSV.add(new AyatRecord(kitab, pasal, ayat, firman));
                        kitabSet.add(kitab);
                    } catch (Exception e) {
                        // Lewati baris yang gagal di-parse
                    }
                }
            }
            urutanKitab = new ArrayList<>(kitabSet);
            System.out.println("BERHASIL: Memuat " + databaseCSV.size() + " ayat.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllKitab() {
        return urutanKitab;
    }

    public List<Integer> getPasalByKitab(String kitab) {
        return databaseCSV.stream()
                .filter(a -> a.kitab.equalsIgnoreCase(kitab))
                .map(a -> a.pasal)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Integer> getAyatByKitabAndPasal(String kitab, int pasal) {
        return databaseCSV.stream()
                .filter(a -> a.kitab.equalsIgnoreCase(kitab) && a.pasal == pasal)
                .map(a -> a.ayat)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public String getFirman(String kitab, int pasal, int ayat) {
        return databaseCSV.stream()
                .filter(a -> a.kitab.equalsIgnoreCase(kitab) && a.pasal == pasal && a.ayat == ayat)
                .map(a -> a.firman)
                .findFirst()
                .orElse("Ayat tidak ditemukan.");
    }
}