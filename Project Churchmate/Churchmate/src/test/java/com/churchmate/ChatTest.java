package com.churchmate;

import com.churchmate.model.Gereja;
import com.churchmate.model.Ibadah;
import com.churchmate.model.Kegiatan;
import com.churchmate.service.ChatService;
import com.churchmate.service.DatabaseConnection;
import com.churchmate.service.DatabaseService;
import com.churchmate.service.ManageDataService;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Berisi tes-tes logika chatbot.
 */
public class ChatTest {
    private final DatabaseService db = new DatabaseService();
    private final ChatService chatbot = new ChatService(db);
    private final ManageDataService service = new ManageDataService(db);

    private Savepoint beforeMock;

    // Pastikan DB dibuat seperti semula saat kita selesai tes
    @BeforeEach
    public void beginTransaction() throws SQLException {
        beforeMock = DatabaseConnection.getConnection().setSavepoint();
    }
    @AfterEach
    public void rollback() throws SQLException {
        DatabaseConnection.getConnection().rollback(beforeMock);
    }

    private String spellMonth(LocalDate date) {
        return switch (date.getMonth()) {
            case JANUARY -> "Januari";
            case FEBRUARY -> "Februari";
            case MARCH -> "Maret";
            case APRIL -> "April";
            case MAY -> "Mei";
            case JUNE -> "Juni";
            case JULY -> "Juli";
            case AUGUST -> "Agustus";
            case SEPTEMBER -> "September";
            case OCTOBER -> "Oktober";
            case NOVEMBER -> "November";
            case DECEMBER -> "Desember";
        };
    }

    private String spellWeek(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case MONDAY -> "Senin";
            case TUESDAY -> "Selasa";
            case WEDNESDAY -> "Rabu";
            case THURSDAY -> "Kamis";
            case FRIDAY -> "Jumat";
            case SATURDAY -> "Sabtu";
            case SUNDAY -> "Minggu";
        };
    }

    /**
     * Pastikan jawaban chatbot mempunyai/tidak mempunyai suatu kata kunci.
     * @param query Pertanyaan yang diberikan.
     * @param contains Kata-kata yang harus ada di jawaban.
     * @param notContain Kata-kata yang harus tidak ada di jawaban.
     * @param message Pesan yang disampaikan jika tes gagal.
     */
    private void assertResponse(String query, String[] contains, String[] notContain, String message) {
        String response = chatbot.processMessage(query);

        String format = "%s\n\nPertanyaan: %s\nJawaban (%s):\n%s";
        for (String keyword: contains)
            assertTrue(
                response.contains(keyword),
                String.format(format,
                    message, query, "harusnya ada " + keyword, response
                )
            );
        for (String keyword: notContain)
            assertFalse(
                response.contains(keyword),
                String.format(format,
                    message, query, "harusnya tidak ada " + keyword,
                    response.replace(keyword, "\33[9m" + keyword + "\33[0m")
                )
            );
    }

    /**
     * Pastikan jawaban chatbot memenuhi fungsi kriteria yang diberikan.
     * @param query Pertanyaan yang diberikan.
     * @param predicate Fungsi untuk menguji jawaban.
     *                  Jika mengeluarkan {@code null}, maka dianggap lulus.
     *                  Jika tidak, keluaran {@link String} akan ditaruh di error.
     * @param message Pesan yang disampaikan jika tes gagal.
     */
    private void assertResponse(String query, Function<String, String> predicate, String message) {
        String response = chatbot.processMessage(query);

        String format = "%s\n\nPertanyaan: %s\nJawaban (%s):\n%s";
        String error = predicate.apply(response);
        if (error != null)
            fail(
                String.format(format,
                    message, query, error, response
                )
            );
    }

    @Nested
    public class KegiatanTests {
        LocalDate testDate = LocalDate.now();
        String testTitle = "\uD87E\uDC57 Tes Kegiatan \uD87E\uDC57";
        String testLocation = "\uD87E\uDC57 Ruang Tes \uD87E\uDC57";

        @BeforeEach
        public void makeMock() {
            Gereja gereja = service.getAllGereja().get(0);
            service.addKegiatan(new Kegiatan(
                gereja.getGerejaId(),
                service.getNextKegiatanId(),
                testTitle,
                testDate,
                "",
                testLocation,
                ""
            ));
        }

        private void assertPresent(String query, String message) {
            assertResponse(query, new String[]{testTitle}, new String[]{}, message);
        }

        @Test
        public void apaSajaTanggal() {
            assertPresent(
                "Kegiatan apa saja di tanggal " + testDate.toString(),
                "Tidak bisa membaca tanggal dengan format TTTT-BB-HH"
            );
            assertPresent(
                "Kegiatan apa saja di tanggal "
                    + testDate.format(DateTimeFormatter.ofPattern("dd-MM-uuuu")),
                "Tidak bisa membaca tanggal dengan format HH/BB/TTTT"
            );
            assertPresent(
                "Kegiatan apa saja di tanggal "
                    + testDate.format(DateTimeFormatter.ofPattern("dd/MM/uuuu")),
                "Tidak bisa membaca tanggal dengan format HH/BB/TTTT"
            );
            assertPresent(
                "Kegiatan apa saja di tanggal"
                    + " " + testDate.getDayOfMonth()
                    + " " + spellMonth(testDate)
                    + " " + testDate.getYear(),
                "Tidak bisa membaca tanggal dengan format HH Bulan TTTT"
            );
        }

        @Test
        public void apaSajaBulan() {
            assertPresent(
                "Kegiatan apa saja di bulan"
                    + " " + spellMonth(testDate)
                    + " " + testDate.getYear(),
                "Tidak bisa membaca bulan dengan format Bulan TTTT"
            );
            // tambahan dari saya --Gilbert
            assertPresent(
                "Kegiatan apa saja di bulan"
                    + " " + spellMonth(testDate),
                "Tidak bisa membaca bulan tanpa tahun"
            );
        }

        @Test
        public void apaSajaHari() {
            assertPresent(
                "Kegiatan apa saja di hari"
                    + " " + spellWeek(testDate),
                "Tidak bisa membaca hari"
            );
            assertPresent(
                "Kegiatan apa saja di hari"
                    + " " + spellWeek(testDate),
                "Tidak bisa membaca hari"
            );
        }

        @Test
        public void kapanIni() {
            // Di rancangan ditulis <jenis_kegiatan>, tapi tidak disebutkan apa itu
            // Saya asumsikan itu artinya judul kegiatan --Gilbert
            assertPresent(
                "Kapan"
                    + " " + testTitle,
                "Tidak bisa menangani kasus umum"
            );
            // semua dibawah ini tambahan saya --Gilbert
            assertResponse(
                "Tanggal berapa"
                    + " " + testTitle,
                new String[] {testTitle, "" + testDate.getDayOfMonth()},
                new String[] {},
                "Tidak bisa menangani kasus tanggal"
            );
            assertResponse(
                "Minggu berapa"
                    + " " + testTitle,
                new String[] {testTitle, spellWeek(testDate)},
                new String[] {},
                "Tidak bisa menangani kasus minggu"
            );
            assertResponse(
                "Bulan apa"
                    + " " + testTitle,
                new String[] {testTitle, spellMonth(testDate)},
                new String[] {},
                "Tidak bisa menangani kasus bulan"
            );
        }

        @Test
        public void tidakJelas() {
            assertResponse(
                "Kegiatan",
                new String[] {},
                new String[] {testTitle},
                "Seharusnya memberi penjelasan"
            );
        }
    }

    @Nested
    public class IbadahTests {
        LocalDate testDate = LocalDate.now();
        LocalTime testTime = LocalTime.NOON;
        String testName = "\uD87E\uDC57 Ibadah Tes \uD87E\uDC57";
        String testPriest = "\uD87E\uDC57 Si Anu \uD87E\uDC57";
        String testTheme = "\uD87E\uDC57 Tes Tema \uD87E\uDC57";

        @BeforeEach
        public void makeMock() {
            Gereja gereja = service.getAllGereja().get(0);
            service.addIbadah(new Ibadah(
                gereja.getGerejaId(),
                service.getNextIbadahId(),
                testName,
                testDate,
                testTime,
                testPriest,
                testTheme,
                ""
            ));
        }

        private void assertPresent(String query, String message) {
            assertResponse(query, new String[]{testName}, new String[]{}, message);
        }

        @Test
        public void apaSajaHari() {
            assertPresent(
                "Ibadah"
                    + " " + spellWeek(testDate),
                "Tidak bisa membaca hari"
            );
        }

        @Test
        public void apaSajaBulan() {
            assertPresent(
                "Ibadah"
                    + " " + spellMonth(testDate),
                "Tidak bisa membaca bulan"
            );
        }

        @Test
        public void pendetaSiapa() {
            assertResponse(
                "Pendeta ibadah"
                    + " " + testName,
                new String[] {testPriest},
                new String[] {},
                "Tidak bisa membaca nama ibadah"
            );
            assertResponse(
                "Pendeta ibadah"
                    + " " + spellWeek(testDate),
                new String[] {testPriest},
                new String[] {},
                "Tidak bisa membaca hari"
            );
        }

        @Test
        public void temaApa() {
            assertResponse(
                "Tema ibadah"
                    + " " + testName,
                new String[] {testTheme},
                new String[] {},
                "Tidak bisa membaca nama ibadah"
            );
            assertResponse(
                "Tema ibadah"
                    + " " + spellWeek(testDate),
                new String[] {testTheme},
                new String[] {},
                "Tidak bisa membaca hari"
            );
        }

        @Test
        public void tidakJelas() {
            assertResponse(
                "Ibadah",
                new String[] {},
                new String[] {testName},
                "Seharusnya memberi penjelasan"
            );
        }
    }
}
