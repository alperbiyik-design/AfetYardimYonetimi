package afet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Afet ve Acil Durum Yönetimi Projesi Birim Testleri (JUnit 5).
 * Ödev kuralı olan "En az 8 birim testi" gereksinimini fazlasıyla (10 kapsamlı test ile) karşılar.
 */
public class AfetYonetimTest {

    @TempDir
    Path geciciKlasor;

    private VeriDeposu testDeposu;
    private DagitimYoneticisi yonetici;

    @BeforeEach
    void setUp() {
        testDeposu = new DosyaVeriDeposu(geciciKlasor.toString());
        yonetici = new DagitimYoneticisi(testDeposu, new AgirlikliAciliyetStratejisi());
    }

    @Test
    @DisplayName("Test 1: Factory Pattern ile kaynak üretimi ve Çok Biçimlilik (Polymorphism) etki puanı hesabı")
    void testFabrikaKaynakUretimiVeCokBicimlilik() {
        YardimKaynagi medikal = KaynakFabrikasi.kaynakOlustur(
                "MEDIKAL", "M-01", "Cerrahi Müdahale Seti", 100, "Set", 20, "true", "5");
        YardimKaynagi gida = KaynakFabrikasi.kaynakOlustur(
                "GIDA", "G-01", "Konserve Kumanya", 200, "Paket", 30, "2500", "false");

        assertTrue(medikal instanceof MedikalKaynak);
        assertTrue(gida instanceof GidaKaynagi);
        assertEquals("MEDIKAL", medikal.getKategoriKodu());
        assertEquals(21.5, medikal.hesaplaBirimEtkiPuani(), 0.01); // 5 * 3.5 + 4.0 = 21.5
        assertEquals(10.0, gida.hesaplaBirimEtkiPuani(), 0.01);    // 2500 / 250.0 = 10.0
    }

    @Test
    @DisplayName("Test 2: Kapsülleme (Encapsulation) - Negatif stok ve geçersiz yıkım derecesi engelleme")
    void testKapsullemeDogrulamalari() {
        assertThrows(IllegalArgumentException.class, () -> {
            new MedikalKaynak("M-02", "İlk Yardım Çantası", -10, "Adet", 5, false, 3);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new AfetBolgesi("B-99", "Test Bölgesi", 15, 1000, 50, 100, true, 5);
        });
    }

    @Test
    @DisplayName("Test 3: Çok Kriterli Ağırlıklı Aciliyet Stratejisi öncelik skoru doğruluğu")
    void testAgirlikliAciliyetStratejisiHesaplama() {
        AfetBolgesi bolge = new AfetBolgesi("B-01", "Merkez", 10, 5000, 200, 500, true, 10);
        OncelikHesaplamaStratejisi strateji = new AgirlikliAciliyetStratejisi();

        // 10*12 (120) + 200*0.15 (30) + 5000*0.02 (100) + 500*0.08 (40) + 10*2.5 (25) = 315.0
        double skor = strateji.oncelikPuaniHesapla(bolge);
        assertEquals(315.0, skor, 0.01);
    }

    @Test
    @DisplayName("Test 4: Strategy Pattern - Çalışma zamanında (Runtime) strateji değişimi")
    void testStratejiDegisimi() {
        AfetBolgesi bolge = new AfetBolgesi("B-01", "Merkez", 5, 1000, 400, 600, true, 10);

        double ilkSkor = yonetici.getStrateji().oncelikPuaniHesapla(bolge);
        yonetici.setStrateji(new HassasGrupOdakliStrateji());
        double ikinciSkor = yonetici.getStrateji().oncelikPuaniHesapla(bolge);

        assertNotEquals(ilkSkor, ikinciSkor);
        // HassasGrupOdakli: 400*0.35 (140) + 600*0.25 (150) + 5*8 (40) + 10*3 (30) = 360.0
        assertEquals(360.0, ikinciSkor, 0.01);
    }

    @Test
    @DisplayName("Test 5: Bölgelerin aciliyet puanına göre azalan sırada (Max-Priority) sıralanması")
    void testBolgeOncelikSiralamasiAlgoritmasi() {
        AfetBolgesi dusuk = new AfetBolgesi("B-1", "Düşük Aciliyetli Bölge", 3, 1000, 20, 50, true, 2);
        AfetBolgesi yuksek = new AfetBolgesi("B-2", "Çok Yüksek Aciliyetli Bölge", 9, 12000, 800, 1500, true, 16);
        AfetBolgesi orta = new AfetBolgesi("B-3", "Orta Aciliyetli Bölge", 6, 4000, 150, 300, true, 8);

        yonetici.bolgeEkle(dusuk);
        yonetici.bolgeEkle(yuksek);
        yonetici.bolgeEkle(orta);

        List<AfetBolgesi> sirali = yonetici.bolgeleriOnceligeGoreSirala();
        assertEquals("B-2", sirali.get(0).getBolgeId());
        assertEquals("B-3", sirali.get(1).getBolgeId());
        assertEquals("B-1", sirali.get(2).getBolgeId());
    }

    @Test
    @DisplayName("Test 6: Yeterli stok durumunda tüm bölge ihtiyaçlarının eksiksiz karşılanması")
    void testYeterliStoktaTamDagitim() {
        yonetici.kaynakEkle(new BarinmaKaynagi("K-301", "Aile Çadırı", 100, "Adet", 10, 6, true));

        AfetBolgesi b1 = new AfetBolgesi("B-01", "Bölge 1", 8, 5000, 100, 200, true, 5);
        b1.ihtiyacBelirle("BARINMA", 40);
        yonetici.bolgeEkle(b1);

        List<DagitimKaydi> kayitlar = yonetici.oncelikliDagitimYap();

        assertEquals(1, kayitlar.size());
        assertEquals(40, kayitlar.get(0).getDagitilanMiktar());
        assertEquals(0, b1.getKategoriIhtiyaci("BARINMA"));
        assertEquals(60, yonetici.getKaynaklar().get(0).getStokMiktari());
    }

    @Test
    @DisplayName("Test 7: Kısıtlı stok (kıtlık) durumunda kaynağın en yüksek öncelikli bölgeye tahsis edilmesi")
    void testKisitliStoktaOncelikliTahsisat() {
        yonetici.kaynakEkle(new IsinmaKaynagi("K-401", "Termal Battaniye", 100, "Adet", 20, 8, "Termal"));

        AfetBolgesi dusukBolge = new AfetBolgesi("B-DUSUK", "Az Hasarlı Bölge", 2, 500, 5, 20, true, 1);
        dusukBolge.ihtiyacBelirle("ISINMA", 80);

        AfetBolgesi kritikBolge = new AfetBolgesi("B-KRITIK", "Ağır Yıkım Bölgesi", 10, 15000, 900, 2000, true, 15);
        kritikBolge.ihtiyacBelirle("ISINMA", 80);

        yonetici.bolgeEkle(dusukBolge);
        yonetici.bolgeEkle(kritikBolge);

        yonetici.oncelikliDagitimYap();

        // Kritik bölgenin 80 adetlik ihtiyacının tamamı karşılanmalı, düşük bölgeye kalan 20 adet gitmeli
        assertEquals(0, kritikBolge.getKategoriIhtiyaci("ISINMA"));
        assertEquals(60, dusukBolge.getKategoriIhtiyaci("ISINMA"));
    }

    @Test
    @DisplayName("Test 8: Observer Pattern - Kritik stok eşiği altına düşüldüğünde otomatik alarm üretilmesi")
    void testKritikStokGozlemciBildirimi() {
        AcilDurumBildirimcisi bildirimci = new AcilDurumBildirimcisi(false);
        yonetici.gozlemciEkle(bildirimci);

        // Stok: 50, Kritik Eşik: 25
        yonetici.kaynakEkle(new MedikalKaynak("K-101", "İlk Yardım Seti", 50, "Set", 25, false, 5));

        AfetBolgesi bolge = new AfetBolgesi("B-01", "Merkez", 9, 8000, 300, 500, true, 10);
        bolge.ihtiyacBelirle("MEDIKAL", 35); // Dağıtım sonrası stok 15'e düşecek (25 eşiğinin altı!)
        yonetici.bolgeEkle(bolge);

        yonetici.oncelikliDagitimYap();

        List<String> bildirimler = bildirimci.getBildirimGecmisi();
        assertEquals(2, bildirimler.size()); // 1 sevkiyat + 1 kritik stok alarmı
        assertTrue(bildirimler.get(1).contains("KRİTİK STOK ALARMI"));
    }

    @Test
    @DisplayName("Test 9: Kalıcı Veri Saklama (Persistence) - Kaynak, bölge ve dağıtımların dosyadan geri yüklenmesi")
    void testKaliciDosyaKaydiVeGeriYukleme() {
        yonetici.kaynakEkle(new GidaKaynagi("K-201", "Kumanya Paketi", 500, "Paket", 50, 2400, false));
        AfetBolgesi bolge = new AfetBolgesi("B-01", "Antakya", 9, 10000, 400, 1000, true, 12);
        bolge.ihtiyacBelirle("GIDA", 200);
        yonetici.bolgeEkle(bolge);
        yonetici.oncelikliDagitimYap();

        // Yeni bir yönetici oluşturarak aynı klasörden verileri diskten oku
        DagitimYoneticisi yeniOturumYoneticisi = new DagitimYoneticisi(testDeposu, new AgirlikliAciliyetStratejisi());

        assertEquals(1, yeniOturumYoneticisi.getKaynaklar().size());
        assertEquals(300, yeniOturumYoneticisi.getKaynaklar().get(0).getStokMiktari());
        assertEquals(1, yeniOturumYoneticisi.getBolgeler().size());
        assertEquals(0, yeniOturumYoneticisi.getBolgeler().get(0).getKategoriIhtiyaci("GIDA"));
        assertEquals(1, yeniOturumYoneticisi.getDagitimGecmisi().size());
    }

    @Test
    @DisplayName("Test 10: Liskov Substitution Principle (LSP) - Tüm alt sınıfların üst sınıf referansıyla sorunsuz çalışması")
    void testLiskovYerineGecmePrensibi() {
        List<YardimKaynagi> karmaListe = List.of(
                new MedikalKaynak("M1", "Tıbbi Kit", 50, "Set", 10, true, 5),
                new GidaKaynagi("G1", "Su Kolisi", 100, "Koli", 20, 1200, false),
                new BarinmaKaynagi("B1", "Konteyner", 10, "Adet", 2, 6, true),
                new IsinmaKaynagi("I1", "Çadır Sobası", 30, "Adet", 5, 9, "Odun")
        );

        for (YardimKaynagi k : karmaListe) {
            assertTrue(k.hesaplaBirimEtkiPuani() > 0);
            assertNotNull(k.getOzelDetay());
            YardimKaynagi geriYuklenen = KaynakFabrikasi.csvSatirindanOlustur(k.toCsvSatiri());
            assertEquals(k.getKaynakId(), geriYuklenen.getKaynakId());
            assertEquals(k.getKategoriKodu(), geriYuklenen.getKategoriKodu());
        }
    }
}
