package com.churchmate.service;

import com.churchmate.model.Gereja;
import com.churchmate.model.Ibadah;
import com.churchmate.model.Kegiatan;

import java.util.ArrayList;
import java.util.List;

public class ChatService {

    private final DatabaseService db;

    public ChatService(DatabaseService db) {
        this.db = db;
    }

    public String processMessage(String message) {
        String query = message.toLowerCase();

        if (query.contains("ibadah") || query.contains("pendeta") || query.contains("siapa")
                || query.contains("tema")) {
            return handleIbadahQuery(query);
        } else if (query.contains("kegiatan") || query.contains("acara")) {
            return handleKegiatanQuery(query);
        } else if (query.contains("gereja") || query.contains("alamat") || query.contains("kontak")
                || query.contains("telepon") || query.contains("email") || query.contains("website")) {
            return handleGerejaQuery(query);
        }

        List<Object> data = db.findAll();
        for (Object obj : data) {
            if (obj instanceof Ibadah ibadah) {
                if (matchesKeyword(query, ibadah.getNamaibadah())) {
                    return handleIbadahQuery(query);
                }
            } else if (obj instanceof Kegiatan kegiatan) {
                if (matchesKeyword(query, kegiatan.getJudul()) || matchesKeyword(query, kegiatan.getKategori())) {
                    return handleKegiatanQuery(query);
                }
            }
        }

        return "Maaf, informasi tidak ditemukan";
    }

    private boolean matchesKeyword(String query, String name) {
        if (name == null)
            return false;
        String lowerName = name.toLowerCase();
        if (query.contains(lowerName))
            return true;

        String[] words = lowerName.split("\\s+");
        for (String word : words) {

            if (word.equals("ibadah") || word.equals("kegiatan") || word.equals("gereja")) {
                continue;
            }
            if (word.length() > 3 && query.contains(word)) {
                return true;
            }
        }
        return false;
    }

    private String handleIbadahQuery(String query) {
        List<Object> data = db.findAll();
        List<Ibadah> exactMatches = new ArrayList<>();
        List<Ibadah> partialMatches = new ArrayList<>();
        List<Ibadah> allIbadah = new ArrayList<>();

        for (Object obj : data) {
            if (obj instanceof Ibadah ibadah) {
                allIbadah.add(ibadah);
                if (query.contains(ibadah.getNamaibadah().toLowerCase())) {
                    exactMatches.add(ibadah);
                } else if (matchesKeyword(query, ibadah.getNamaibadah())) {
                    partialMatches.add(ibadah);
                }
            }
        }

        List<Ibadah> listIbadah = exactMatches;
        if (listIbadah.isEmpty()) {
            listIbadah = partialMatches;
        }

        if (listIbadah.isEmpty()) {
            listIbadah = allIbadah;
        }

        if (listIbadah.isEmpty()) {
            return "Maaf, informasi tidak ditemukan";
        }

        if (listIbadah.size() > 1) {
            StringBuilder panduan = new StringBuilder(
                    "Informasi ibadah apa yang ingin Anda ketahui? Pilihan yang tersedia:\n");
            for (Ibadah ibadah : listIbadah) {
                panduan.append("- ").append(ibadah.getNamaibadah()).append("\n");
            }
            return panduan.toString().trim();
        }

        Ibadah ibadah = listIbadah.get(0);

        if (query.contains("kapan") || query.contains("tanggal") || query.contains("jam")) {
            return "Ibadah " + ibadah.getNamaibadah() +
                    " dilaksanakan pada " + ibadah.getTglIbadah() +
                    " pukul " + ibadah.getJam() + ".";
        }

        if (query.contains("pendeta") || query.contains("siapa")) {
            return "Pendeta untuk " + ibadah.getNamaibadah() +
                    " adalah " + ibadah.getPendeta() + ".";
        }

        if (query.contains("tema")) {
            return "Tema " + ibadah.getNamaibadah() +
                    " adalah \"" + ibadah.getTema() + "\".";
        }

        if (query.contains("lokasi") || query.contains("dimana") || query.contains("di mana")) {
            return ibadah.getNamaibadah() +
                    " dilaksanakan di " + ibadah.getLokasi() + ".";
        }

        return ibadah.getUpcoming();
    }

    private String handleKegiatanQuery(String query) {
        List<Object> data = db.findAll();
        List<Kegiatan> exactMatches = new ArrayList<>();
        List<Kegiatan> partialMatches = new ArrayList<>();
        List<Kegiatan> allKegiatan = new ArrayList<>();

        for (Object obj : data) {
            if (obj instanceof Kegiatan kegiatan) {
                allKegiatan.add(kegiatan);
                if (query.contains(kegiatan.getJudul().toLowerCase()) || query.contains(kegiatan.getKategori().toLowerCase())) {
                    exactMatches.add(kegiatan);
                } else if (matchesKeyword(query, kegiatan.getJudul()) || matchesKeyword(query, kegiatan.getKategori())) {
                    partialMatches.add(kegiatan);
                }
            }
        }

        List<Kegiatan> listKegiatan = exactMatches;
        if (listKegiatan.isEmpty()) {
            listKegiatan = partialMatches;
        }

        if (listKegiatan.isEmpty()) {
            listKegiatan = allKegiatan;
        }

        if (listKegiatan.isEmpty()) {
            return "Maaf, informasi tidak ditemukan";
        }

        if (listKegiatan.size() > 1) {
            StringBuilder panduan = new StringBuilder("Kegiatan apa yang ingin Anda ketahui? Pilihan yang tersedia:\n");
            for (Kegiatan kegiatan : listKegiatan) {
                panduan.append("- ").append(kegiatan.getJudul()).append("\n");
            }
            return panduan.toString().trim();
        }

        Kegiatan kegiatan = listKegiatan.get(0);

        if (query.contains("kapan") || query.contains("tanggal")) {
            return "Kegiatan " + kegiatan.getJudul() +
                    " dilaksanakan pada " + kegiatan.getTanggal() + ".";
        }

        if (query.contains("lokasi") || query.contains("dimana") || query.contains("di mana")) {
            return "Kegiatan " + kegiatan.getJudul() +
                    " dilaksanakan di " + kegiatan.getLokasi() + ".";
        }

        if (query.contains("kategori")) {
            return "Kategori kegiatan " + kegiatan.getJudul() +
                    " adalah " + kegiatan.getKategori() + ".";
        }

        return kegiatan.getDetail();
    }

    private String handleGerejaQuery(String query) {
        List<Object> data = db.findAll();

        for (Object obj : data) {
            if (obj instanceof Gereja gereja) {

                if (query.contains("alamat") || query.contains("dimana") || query.contains("di mana")) {
                    return "Alamat gereja: " + gereja.getAlamat();
                }

                if (query.contains("telepon") || query.contains("kontak") || query.contains("nomor")) {
                    return "Nomor telepon gereja: " + gereja.getNoTelp();
                }

                if (query.contains("email")) {
                    return "Email gereja: " + gereja.getEmail();
                }

                if (query.contains("website")) {
                    return "Website gereja: " + gereja.getWebsite();
                }

                return gereja.getInfo();
            }
        }

        return "Maaf, informasi tidak ditemukan";
    }
}