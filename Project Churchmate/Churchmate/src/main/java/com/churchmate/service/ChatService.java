package com.churchmate.service;

import com.churchmate.model.Gereja;
import com.churchmate.model.Ibadah;
import com.churchmate.model.Kegiatan;
import com.churchmate.dao.AlkitabDAO;
import com.churchmate.dao.RenunganDAO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatService {

    private final DatabaseService db;
    private final AlkitabDAO alkitabDAO;

    // Tanggal "hari ini" yang digunakan sebagai acuan waktu relatif
    private final LocalDate today;

    public ChatService(DatabaseService db) {
        this.db = db;
        this.alkitabDAO = new AlkitabDAO();
        this.today = LocalDate.now();
    }

    public String processMessage(String message) {
        String query = message.toLowerCase().trim();

        // --- Ayat Alkitab (dicek LEBIH DULU sebelum renungan) ---
        // Prioritas: jika ada format kitab:pasal atau kata "alkitab"/"ayat"
        if (query.contains("alkitab") || query.contains("bacaan") || isAyatQuery(query)) {
            return handleAlkitabQuery(query);
        }

        // --- Renungan ("firman" tanpa format ayat = renungan harian) ---
        if (query.contains("renungan") || query.contains("firman")) {
            RenunganDAO renunganDAO = new RenunganDAO();
            return renunganDAO.getRenunganHariIni();
        }

        // --- Ibadah (termasuk cari berdasarkan nama pendeta) ---
        if (query.contains("ibadah") || query.contains("pendeta") || query.contains("siapa")
                || query.contains("tema")) {
            return handleIbadahQuery(query);
        }

        // --- Kegiatan ---
        if (query.contains("kegiatan") || query.contains("acara")) {
            return handleKegiatanQuery(query);
        }

        // --- Gereja ---
        if (query.contains("gereja") || query.contains("alamat") || query.contains("kontak")
                || query.contains("telepon") || query.contains("email") || query.contains("website")) {
            return handleGerejaQuery(query);
        }

        // --- Fallback: cari berdasarkan data ---
        List<Object> data = db.findAll();
        for (Object obj : data) {
            if (obj instanceof Ibadah ibadah) {
                if (matchesKeyword(query, ibadah.getNamaibadah())
                        || matchesKeyword(query, ibadah.getPendeta())) {
                    return handleIbadahQuery(query);
                }
            } else if (obj instanceof Kegiatan kegiatan) {
                if (matchesKeyword(query, kegiatan.getJudul()) || matchesKeyword(query, kegiatan.getKategori())) {
                    return handleKegiatanQuery(query);
                }
            }
        }

        return "Maaf, informasi tidak ditemukan. Anda bisa bertanya tentang:\n"
                + "• Ibadah (jadwal, pendeta, tema, lokasi)\n"
                + "• Kegiatan / acara gereja\n"
                + "• Informasi gereja (alamat, kontak)\n"
                + "• Ayat Alkitab (contoh: \"Yohanes 3:16\" atau \"Kejadian pasal 1 ayat 1\")\n"
                + "• Renungan hari ini";
    }

    // =====================================================================
    // ALKITAB
    // =====================================================================

    /**
     * Deteksi apakah query kemungkinan merupakan permintaan ayat Alkitab,
     * contoh: "yohanes 3:16", "kejadian pasal 1 ayat 1", "yoh 3:14-17"
     */
    private boolean isAyatQuery(String query) {
        // Format: "NamaKitab angka:angka(-angka)?" (e.g. "yohanes 3:16", "1 yohanes 2:5-7", "yoh 3:16")
        Pattern pola = Pattern.compile("(\\d+\\s+)?[a-z]+(\\s[a-z]+)*\\s+\\d+:\\d+(-\\d+)?");
        if (pola.matcher(query).find()) return true;
        // Format: "pasal X ayat Y" atau "pasal X ayat Y-Z" atau "pasal X ayat Y sampai Z"
        if (query.contains("pasal") && query.contains("ayat")) return true;
        return false;
    }

    /**
     * Menangani query ayat Alkitab.
     * Contoh input yang didukung:
     *   - "yohanes 3:16"
     *   - "yoh 3:14-17"
     *   - "1 yohanes 2:5"
     *   - "kejadian pasal 1 ayat 1"
     *   - "kejadian pasal 1 ayat 1 sampai 3"
     */
    private String handleAlkitabQuery(String query) {
        // Pola 1: "NamaKitab angka:angka(-angka)?"
        Pattern polaNormal = Pattern.compile("((?:\\d+\\s+)?[a-z]+(?:\\s[a-z]+)*)\\s+(\\d+):(\\d+)(?:-(\\d+))?");
        Matcher m1 = polaNormal.matcher(query);
        if (m1.find()) {
            String kitab = m1.group(1).trim();
            int pasal = Integer.parseInt(m1.group(2));
            int ayatDari = Integer.parseInt(m1.group(3));
            int ayatSampai = (m1.group(4) != null) ? Integer.parseInt(m1.group(4)) : ayatDari;
            return getFirmanResponseRange(kitab, pasal, ayatDari, ayatSampai);
        }

        // Pola 2: "NamaKitab pasal X ayat Y(?: sampai/hingga/- Z)?"
        Pattern polaPasalAyat = Pattern.compile("((?:\\d+\\s+)?[a-z]+(?:\\s[a-z]+)*)\\s+pasal\\s+(\\d+)\\s+ayat\\s+(\\d+)(?:(?:\\s+(?:sampai|hingga|s/d|\\-)\\s+)(\\d+))?");
        Matcher m2 = polaPasalAyat.matcher(query);
        if (m2.find()) {
            String kitab = m2.group(1).trim();
            int pasal = Integer.parseInt(m2.group(2));
            int ayatDari = Integer.parseInt(m2.group(3));
            int ayatSampai = (m2.group(4) != null) ? Integer.parseInt(m2.group(4)) : ayatDari;
            return getFirmanResponseRange(kitab, pasal, ayatDari, ayatSampai);
        }

        // Pola 3: "pasal X ayat Y" tanpa nama kitab
        Pattern polaTanpaKitab = Pattern.compile("pasal\\s+(\\d+)\\s+ayat\\s+(\\d+)");
        Matcher m3 = polaTanpaKitab.matcher(query);
        if (m3.find()) {
            return "Mohon sebutkan nama kitabnya juga, contoh:\n"
                    + "\"Yohanes pasal 3 ayat 16\" atau \"Yohanes 3:16\"";
        }

        // Jika ada keyword "alkitab" atau "bacaan" tapi format tidak dikenali
        return "Untuk mencari ayat Alkitab, gunakan format:\n"
                + "• \"Yoh 3:16\"\n"
                + "• \"Yohanes 3:14-17\"\n"
                + "• \"1 Korintus 13:4\"\n"
                + "• \"Kejadian pasal 1 ayat 1\"\n\n"
                + "Gunakan fitur Baca Alkitab di sidebar untuk membaca per pasal.";
    }

    private static final java.util.Map<String, String> NAMA_KITAB_MAP = new java.util.LinkedHashMap<>();
    static {
        NAMA_KITAB_MAP.put("Kej", "Kejadian");
        NAMA_KITAB_MAP.put("Kel", "Keluaran");
        NAMA_KITAB_MAP.put("Im", "Imamat");
        NAMA_KITAB_MAP.put("Bil", "Bilangan");
        NAMA_KITAB_MAP.put("Ul", "Ulangan");
        NAMA_KITAB_MAP.put("Yos", "Yosua");
        NAMA_KITAB_MAP.put("Hak", "Hakim-hakim");
        NAMA_KITAB_MAP.put("Rut", "Rut");
        NAMA_KITAB_MAP.put("1 Sam", "1 Samuel");
        NAMA_KITAB_MAP.put("2 Sam", "2 Samuel");
        NAMA_KITAB_MAP.put("1 Raj", "1 Raja-raja");
        NAMA_KITAB_MAP.put("2 Raj", "2 Raja-raja");
        NAMA_KITAB_MAP.put("1 Taw", "1 Tawarikh");
        NAMA_KITAB_MAP.put("2 Taw", "2 Tawarikh");
        NAMA_KITAB_MAP.put("Ezr", "Ezra");
        NAMA_KITAB_MAP.put("Neh", "Nehemia");
        NAMA_KITAB_MAP.put("Est", "Ester");
        NAMA_KITAB_MAP.put("Ayb", "Ayub");
        NAMA_KITAB_MAP.put("Mzm", "Mazmur");
        NAMA_KITAB_MAP.put("Ams", "Amsal");
        NAMA_KITAB_MAP.put("Pkh", "Pengkhotbah");
        NAMA_KITAB_MAP.put("Kid", "Kidung Agung");
        NAMA_KITAB_MAP.put("Yes", "Yesaya");
        NAMA_KITAB_MAP.put("Yer", "Yeremia");
        NAMA_KITAB_MAP.put("Rat", "Ratapan");
        NAMA_KITAB_MAP.put("Yeh", "Yehezkiel");
        NAMA_KITAB_MAP.put("Dan", "Daniel");
        NAMA_KITAB_MAP.put("Hos", "Hosea");
        NAMA_KITAB_MAP.put("Yl", "Yoel");
        NAMA_KITAB_MAP.put("Am", "Amos");
        NAMA_KITAB_MAP.put("Ob", "Obaja");
        NAMA_KITAB_MAP.put("Yun", "Yunus");
        NAMA_KITAB_MAP.put("Mi", "Mikha");
        NAMA_KITAB_MAP.put("Nah", "Nahum");
        NAMA_KITAB_MAP.put("Hab", "Habakuk");
        NAMA_KITAB_MAP.put("Zef", "Zefanya");
        NAMA_KITAB_MAP.put("Hag", "Hagai");
        NAMA_KITAB_MAP.put("Za", "Zakharia");
        NAMA_KITAB_MAP.put("Mal", "Maleakhi");
        NAMA_KITAB_MAP.put("Mat", "Matius");
        NAMA_KITAB_MAP.put("Mrk", "Markus");
        NAMA_KITAB_MAP.put("Luk", "Lukas");
        NAMA_KITAB_MAP.put("Yoh", "Yohanes");
        NAMA_KITAB_MAP.put("Kis", "Kisah Para Rasul");
        NAMA_KITAB_MAP.put("Rom", "Roma");
        NAMA_KITAB_MAP.put("1 Kor", "1 Korintus");
        NAMA_KITAB_MAP.put("2 Kor", "2 Korintus");
        NAMA_KITAB_MAP.put("Gal", "Galatia");
        NAMA_KITAB_MAP.put("Ef", "Efesus");
        NAMA_KITAB_MAP.put("Flp", "Filipi");
        NAMA_KITAB_MAP.put("Kol", "Kolose");
        NAMA_KITAB_MAP.put("1 Tes", "1 Tesalonika");
        NAMA_KITAB_MAP.put("2 Tes", "2 Tesalonika");
        NAMA_KITAB_MAP.put("1 Tim", "1 Timotius");
        NAMA_KITAB_MAP.put("2 Tim", "2 Timotius");
        NAMA_KITAB_MAP.put("Tit", "Titus");
        NAMA_KITAB_MAP.put("Flm", "Filemon");
        NAMA_KITAB_MAP.put("Ibr", "Ibrani");
        NAMA_KITAB_MAP.put("Yak", "Yakobus");
        NAMA_KITAB_MAP.put("1 Pet", "1 Petrus");
        NAMA_KITAB_MAP.put("2 Pet", "2 Petrus");
        NAMA_KITAB_MAP.put("1 Yoh", "1 Yohanes");
        NAMA_KITAB_MAP.put("2 Yoh", "2 Yohanes");
        NAMA_KITAB_MAP.put("3 Yoh", "3 Yohanes");
        NAMA_KITAB_MAP.put("Yud", "Yudas");
        NAMA_KITAB_MAP.put("Why", "Wahyu");
    }

    private String getFirmanResponseRange(String kitabInput, int pasal, int ayatDari, int ayatSampai) {
        String kitabInLowerCase = kitabInput.toLowerCase();
        String singkatanDB = null;
        String namaLengkap = null;

        // 1. Coba exact match (Singkatan atau Nama Lengkap)
        for (java.util.Map.Entry<String, String> entry : NAMA_KITAB_MAP.entrySet()) {
            if (entry.getKey().toLowerCase().equals(kitabInLowerCase) || 
                entry.getValue().toLowerCase().equals(kitabInLowerCase)) {
                singkatanDB = entry.getKey();
                namaLengkap = entry.getValue();
                break;
            }
        }

        // 2. Coba partial match (awalan nama lengkap)
        if (singkatanDB == null) {
            for (java.util.Map.Entry<String, String> entry : NAMA_KITAB_MAP.entrySet()) {
                String fullName = entry.getValue().toLowerCase();
                String fullNameNoSpace = fullName.replace(" ", "");
                String inputNoSpace = kitabInLowerCase.replace(" ", "");
                
                if (fullName.startsWith(kitabInLowerCase) || fullNameNoSpace.startsWith(inputNoSpace)) {
                    singkatanDB = entry.getKey();
                    namaLengkap = entry.getValue();
                    break;
                }
            }
        }

        // 3. Fallback ke getAllKitab() jika ada kitab tambahan di CSV yang tidak ada di map
        if (singkatanDB == null) {
            List<String> semuaKitab = alkitabDAO.getAllKitab();
            for (String k : semuaKitab) {
                if (k.toLowerCase().startsWith(kitabInLowerCase)) {
                    singkatanDB = k;
                    namaLengkap = k; // tidak tahu nama lengkapnya
                    break;
                }
            }
        }

        if (singkatanDB == null) {
            return "Kitab \"" + toTitleCase(kitabInput) + "\" tidak ditemukan.\n"
                    + "Pastikan ejaan benar, contoh: Kejadian (Kej), Yohanes (Yoh), Roma, Wahyu.";
        }
        
        // Pastikan urutan benar
        if (ayatSampai < ayatDari) {
            int temp = ayatDari;
            ayatDari = ayatSampai;
            ayatSampai = temp;
        }

        // Ambil firman menggunakan singkatan (karena tb.csv pakai singkatan)
        StringBuilder sb = new StringBuilder();
        sb.append("📖 ").append(namaLengkap).append(" ").append(pasal).append(":");
        
        if (ayatDari == ayatSampai) {
            sb.append(ayatDari).append("\n\n");
            String firman = alkitabDAO.getFirman(singkatanDB, pasal, ayatDari);
            if (firman.equals("Ayat tidak ditemukan.")) {
                return "Ayat " + namaLengkap + " " + pasal + ":" + ayatDari + " tidak ditemukan.\n"
                        + "Periksa nomor pasal dan ayat yang Anda masukkan.";
            }
            sb.append("\"").append(firman).append("\"");
        } else {
            sb.append(ayatDari).append("-").append(ayatSampai).append("\n\n");
            boolean foundAny = false;
            for (int a = ayatDari; a <= ayatSampai; a++) {
                String firman = alkitabDAO.getFirman(singkatanDB, pasal, a);
                if (!firman.equals("Ayat tidak ditemukan.")) {
                    sb.append(a).append(". \"").append(firman).append("\"\n");
                    foundAny = true;
                }
            }
            if (!foundAny) {
                return "Ayat " + namaLengkap + " " + pasal + ":" + ayatDari + "-" + ayatSampai + " tidak ditemukan.";
            }
        }

        return sb.toString().trim();
    }

    /** Mengubah string menjadi Title Case (huruf pertama setiap kata kapital) */
    private String toTitleCase(String input) {
        if (input == null || input.isEmpty()) return input;
        String[] words = input.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }

    // =====================================================================
    // DATE MATCHING (termasuk tanggal relatif)
    // =====================================================================

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

    /**
     * Mengekstrak tanggal referensi dari query yang mengandung ekspresi waktu relatif.
     * Mengembalikan null jika tidak ada ekspresi relatif yang cocok.
     * Contoh yang didukung:
     *   - "hari ini"       → today
     *   - "besok"          → today + 1
     *   - "lusa"           → today + 2
     *   - "minggu depan"   → today + 7
     *   - "2 hari lagi"    → today + 2
     *   - "3 minggu lagi"  → today + 21
     *   - "bulan depan"    → today + 30
     */
    private LocalDate resolveRelativeDate(String query) {
        if (query.contains("hari ini") || query.contains("sekarang")) {
            return today;
        }
        if (query.contains("besok") || query.contains("esok")) {
            return today.plusDays(1);
        }
        if (query.contains("lusa")) {
            return today.plusDays(2);
        }
        if (query.contains("minggu depan") || query.contains("pekan depan")) {
            return today.plusDays(7);
        }
        if (query.contains("bulan depan")) {
            return today.plusMonths(1);
        }

        // Pola: "X hari lagi / ke depan"
        Pattern pHari = Pattern.compile("(\\d+)\\s*hari\\s*(lagi|ke\\s*depan|kedepan)?");
        Matcher mh = pHari.matcher(query);
        if (mh.find()) {
            int n = Integer.parseInt(mh.group(1));
            return today.plusDays(n);
        }

        // Pola: "X minggu lagi / ke depan"
        Pattern pMinggu = Pattern.compile("(\\d+)\\s*minggu\\s*(lagi|ke\\s*depan|kedepan)?");
        Matcher mm = pMinggu.matcher(query);
        if (mm.find()) {
            int n = Integer.parseInt(mm.group(1));
            return today.plusWeeks(n);
        }

        // Pola: "X bulan lagi / ke depan"
        Pattern pBulan = Pattern.compile("(\\d+)\\s*bulan\\s*(lagi|ke\\s*depan|kedepan)?");
        Matcher mb = pBulan.matcher(query);
        if (mb.find()) {
            int n = Integer.parseInt(mb.group(1));
            return today.plusMonths(n);
        }

        return null;
    }

    private boolean matchesDate(String query, LocalDate date) {
        if (date == null)
            return false;

        // Cek ekspresi relatif
        LocalDate relativeDate = resolveRelativeDate(query);
        if (relativeDate != null) {
            return date.isEqual(relativeDate);
        }

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

    // =====================================================================
    // FIND BEST MATCHES (generic)
    // =====================================================================

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

    // =====================================================================
    // IBADAH
    // =====================================================================

    public List<Ibadah> findMatchingIbadah(String query) {
        return findBestMatches(
                query,
                Ibadah.class,
                ibadah -> query.contains(ibadah.getNamaibadah().toLowerCase())
                        || (ibadah.getPendeta() != null
                                && query.contains(ibadah.getPendeta().toLowerCase())),
                ibadah -> matchesKeyword(query, ibadah.getNamaibadah())
                        || matchesKeyword(query, ibadah.getPendeta()),
                Ibadah::getTglIbadah);
    }

    private String handleIbadahQuery(String query) {
        List<Ibadah> listIbadah = findMatchingIbadah(query);

        if (listIbadah.isEmpty()) {
            return "Maaf, informasi ibadah tidak ditemukan";
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
            return ibadah.getNamaibadah() + " pada tanggal " +
                    ibadah.getTglIbadah() + " dilaksanakan di " + ibadah.getLokasi() + ".";
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

    // =====================================================================
    // KEGIATAN
    // =====================================================================

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
            return "Maaf, informasi kegiatan tidak ditemukan";
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
            return kegiatan.getJudul() + " pada tanggal " +
                    kegiatan.getTanggal() + " dilaksanakan di " + kegiatan.getLokasi() + ".";
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

    // =====================================================================
    // GEREJA
    // =====================================================================

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