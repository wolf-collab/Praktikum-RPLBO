package com.churchmate.service;

import com.churchmate.model.Gereja;
import com.churchmate.model.Ibadah;
import com.churchmate.model.Kegiatan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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

    private boolean matchesDate(String query, LocalDate date) {
        if (date == null)
            return false;

        // Cek format tanggal (YYYY-MM-DD)
        if (query.contains(date.toString()))
            return true;

        // Cek format DD-MM-YYYY atau DD/MM/YYYY
        String format1 = String.format("%02d-%02d-%04d", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
        String format2 = String.format("%02d/%02d/%04d", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
        if (query.contains(format1) || query.contains(format2))
            return true;

        // Cek format DD Bulan YYYY (misalnya 17 agustus 2026 atau 17 agustus)
        String[] namaBulan = { "januari", "februari", "maret", "april", "mei", "juni", "juli", "agustus", "september",
                "oktober", "november", "desember" };
        String bulanStr = namaBulan[date.getMonthValue() - 1];

        // cek format bulan (misalnya bulan mei 2026 atau mei)
        if (query.contains("bulan " + bulanStr) ||
                query.contains(bulanStr + " " + date.getYear())) {
            return true;
        }

        String format3 = String.format("%02d %s %04d", date.getDayOfMonth(), bulanStr, date.getYear());
        String format4 = String.format("%d %s %04d", date.getDayOfMonth(), bulanStr, date.getYear());
        String format5 = String.format("%02d %s", date.getDayOfMonth(), bulanStr);
        String format6 = String.format("%d %s", date.getDayOfMonth(), bulanStr);

        if (query.contains(format3) || query.contains(format4) ||
                query.contains(format5) || query.contains(format6)) {
            return true;
        }

        return false;
    }

    private <T> List<T> findBestMatches(String query, Class<T> clazz,
            Function<T, Boolean> nameMatcher,
            Function<T, Boolean> keywordMatcher,
            Function<T, LocalDate> dateExtractor) {
        List<Object> data = db.findAll();

        List<T> exactDateAndNameMatches = new ArrayList<>();
        List<T> exactDateAndKeywordMatches = new ArrayList<>();
        List<T> exactDateMatches = new ArrayList<>();
        List<T> exactMatches = new ArrayList<>();
        List<T> partialMatches = new ArrayList<>();
        List<T> allItems = new ArrayList<>();

        for (Object obj : data) {
            if (clazz.isInstance(obj)) {
                T item = clazz.cast(obj);
                allItems.add(item);

                boolean matchName = nameMatcher.apply(item);
                boolean matchKeyword = keywordMatcher.apply(item);
                LocalDate date = dateExtractor.apply(item);
                boolean matchDate = matchesDate(query, date);

                if (matchDate && matchName) {
                    exactDateAndNameMatches.add(item);
                } else if (matchDate && matchKeyword) {
                    exactDateAndKeywordMatches.add(item);
                } else if (matchDate) {
                    exactDateMatches.add(item);
                } else if (matchName) {
                    exactMatches.add(item);
                } else if (matchKeyword) {
                    partialMatches.add(item);
                }
            }
        }

        if (!exactDateAndNameMatches.isEmpty())
            return exactDateAndNameMatches;
        if (!exactDateAndKeywordMatches.isEmpty())
            return exactDateAndKeywordMatches;
        if (!exactDateMatches.isEmpty())
            return exactDateMatches;
        if (!exactMatches.isEmpty())
            return exactMatches;
        if (!partialMatches.isEmpty())
            return partialMatches;
        return allItems;
    }

    public List<Ibadah> findMatchingIbadah(String query) {
        return findBestMatches(
                query,
                Ibadah.class,
                ibadah -> query.contains(ibadah.getNamaibadah().toLowerCase()),
                ibadah -> matchesKeyword(query, ibadah.getNamaibadah()),
                Ibadah::getTglIbadah);
    }

    private String handleIbadahQuery(String query) {
        List<Ibadah> listIbadah = findMatchingIbadah(query);

        if (listIbadah.isEmpty()) {
            return "Maaf, informasi tidak ditemukan";
        }

        if (listIbadah.size() > 1) {
            return handleMultipleIbadahMatches(query, listIbadah);
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

    /** untuk menangani jika data ibadah lebih dari 1 */
    private String handleMultipleIbadahMatches(String query, List<Ibadah> listIbadah) {
        StringBuilder response = new StringBuilder("Terdapat beberapa data ibadah yang sesuai:\n");

        for (Ibadah ibadah : listIbadah) {
            if (query.contains("kapan") || query.contains("tanggal") || query.contains("jam")) {
                response.append("")
                        .append(ibadah.getNamaibadah())
                        .append(" dilaksanakan pada ")
                        .append(ibadah.getTglIbadah())
                        .append(" pukul ")
                        .append(ibadah.getJam())
                        .append(".\n");
            } else if (query.contains("pendeta") || query.contains("siapa")) {
                response.append("")
                        .append(ibadah.getNamaibadah())
                        .append(" pada ")
                        .append(ibadah.getTglIbadah())
                        .append(" pukul ")
                        .append(ibadah.getJam())
                        .append(" dipimpin oleh ")
                        .append(ibadah.getPendeta())
                        .append(".\n");
            } else if (query.contains("tema")) {
                response.append("")
                        .append(ibadah.getNamaibadah())
                        .append(" pada ")
                        .append(ibadah.getTglIbadah())
                        .append(" memiliki tema \"")
                        .append(ibadah.getTema())
                        .append("\".\n");
            } else if (query.contains("lokasi") || query.contains("dimana") || query.contains("di mana")) {
                response.append("")
                        .append(ibadah.getNamaibadah())
                        .append(" pada ")
                        .append(ibadah.getTglIbadah())
                        .append(" dilaksanakan di ")
                        .append(ibadah.getLokasi())
                        .append(".\n");
            } else {
                response.append("")
                        .append(ibadah.getUpcoming())
                        .append("\n");
            }
        }

        return response.toString().trim();
    }

    public List<Kegiatan> findMatchingKegiatan(String query) {
        return findBestMatches(
                query,
                Kegiatan.class,
                kegiatan -> query.contains(kegiatan.getJudul().toLowerCase())
                        || query.contains(kegiatan.getKategori().toLowerCase()),
                kegiatan -> matchesKeyword(query, kegiatan.getJudul()) || matchesKeyword(query, kegiatan.getKategori()),
                Kegiatan::getTanggal);
    }

    private String handleKegiatanQuery(String query) {
        List<Kegiatan> listKegiatan = findMatchingKegiatan(query);

        if (listKegiatan.isEmpty()) {
            return "Maaf, informasi tidak ditemukan";
        }

        if (listKegiatan.size() > 1) {
            return handleMultipleKegiatanMatches(query, listKegiatan);
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

    private String handleMultipleKegiatanMatches(String query, List<Kegiatan> listKegiatan) {
        StringBuilder response = new StringBuilder("Terdapat beberapa data kegiatan yang sesuai:\n");

        for (Kegiatan kegiatan : listKegiatan) {
            if (query.contains("kapan") || query.contains("tanggal")) {
                response.append("")
                        .append(kegiatan.getJudul())
                        .append(" dilaksanakan pada ")
                        .append(kegiatan.getTanggal())
                        .append(".\n");
            } else if (query.contains("lokasi") || query.contains("dimana") || query.contains("di mana")) {
                response.append("")
                        .append(kegiatan.getJudul())
                        .append(" pada ")
                        .append(kegiatan.getTanggal())
                        .append(" dilaksanakan di ")
                        .append(kegiatan.getLokasi())
                        .append(".\n");
            } else if (query.contains("kategori")) {
                response.append("")
                        .append(kegiatan.getJudul())
                        .append(" pada ")
                        .append(kegiatan.getTanggal())
                        .append(" memiliki kategori ")
                        .append(kegiatan.getKategori())
                        .append(".\n");
            } else {
                response.append("")
                        .append(kegiatan.getDetail())
                        .append("\n");
            }
        }

        return response.toString().trim();
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