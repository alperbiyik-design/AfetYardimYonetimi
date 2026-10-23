package afet;

import java.util.List;
import java.util.Scanner;

/**
 * Konsol Üzerinden Çalışan Ana Uygulama Sınıfı.
 * Kullanıcı etkileşimini, menü yönetimini ve raporlamayı sağlar.
 */
public class Main {
    private static final String VERI_KLASORU = "data";

    public static void main(String[] args) {
        VeriDeposu depo = new DosyaVeriDeposu(VERI_KLASORU);
        OncelikHesaplamaStratejisi varsayilanStrateji = new AgirlikliAciliyetStratejisi();
        DagitimYoneticisi yonetici = new DagitimYoneticisi(depo, varsayilanStrateji);

        AcilDurumBildirimcisi bildirimci = new AcilDurumBildirimcisi(true);
        yonetici.gozlemciEkle(bildirimci);

        // İlk açılışta örnek verileri otomatik oluştur
        yonetici.varsayilanVerileriYukleEgerBossa();

        Scanner scanner = new Scanner(System.in);
        boolean devam = true;

        System.out.println("==========================================================================");
        System.out.println("   AFET VE ACİL DURUM YÖNETİMİ - YARDIM KAYNAKLARI DAĞITIM SİSTEMİ");
        System.out.println("==========================================================================");

        while (devam) {
            System.out.println("\n--- ANA MENÜ (Aktif Algoritma: " + yonetici.getStrateji().getStratejiAdi() + ") ---");
            System.out.println("1. Depodaki Yardım Kaynaklarını Listele");
            System.out.println("2. Afet Bölgelerini Öncelik Sırasına Göre Listele");
            System.out.println("3. Yeni Yardım Kaynağı Ekle (Factory Pattern)");
            System.out.println("4. Yeni Afet Bölgesi ve İhtiyaç Kaydı Ekle");
            System.out.println("5. Öncelik ve İhtiyaç Durumuna Göre Otomatik Dağıtımı Başlat");
            System.out.println("6. Öncelik Hesaplama Algoritmasını Değiştir (Strategy Pattern)");
            System.out.println("7. Geçmiş Dağıtım Kayıtlarını ve Bildirim Loglarını Görüntüle");
            System.out.println("0. Verileri Kaydet ve Çıkış Yap");
            System.out.print("Seçiminiz: ");

            String secim = scanner.nextLine().trim();
            System.out.println();

            try {
                switch (secim) {
                    case "1":
                        kaynaklariYazdir(yonetici);
                        break;
                    case "2":
                        bolgeleriOncelikliYazdir(yonetici);
                        break;
                    case "3":
                        yeniKaynakEkleMenu(scanner, yonetici);
                        break;
                    case "4":
                        yeniBolgeEkleMenu(scanner, yonetici);
                        break;
                    case "5":
                        otomatikDagitimiCalistir(yonetici);
                        break;
                    case "6":
                        stratejiDegistirMenu(scanner, yonetici);
                        break;
                    case "7":
                        gecmisRaporlariYazdir(yonetici, bildirimci);
                        break;
                    case "0":
                        yonetici.verileriKaliciKaydet();
                        System.out.println("Tüm veriler '" + VERI_KLASORU + "/' klasörüne kalıcı olarak kaydedildi. İyi günler!");
                        devam = false;
                        break;
                    default:
                        System.out.println("Geçersiz seçim! Lütfen 0-7 arasında bir değer giriniz.");
                }
            } catch (Exception e) {
                System.out.println("[HATA] İşlem sırasında bir sorun oluştu: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void kaynaklariYazdir(DagitimYoneticisi yonetici) {
        System.out.println("=== DEPODAKİ YARDIM KAYNAKLARI (Çok Biçimli Etki Puanı ile) ===");
        List<YardimKaynagi> kaynaklar = yonetici.getKaynaklar();
        if (kaynaklar.isEmpty()) {
            System.out.println("Depoda kayıtlı kaynak bulunmamaktadır.");
            return;
        }
        for (YardimKaynagi k : kaynaklar) {
            System.out.println(k);
        }
    }

    private static void bolgeleriOncelikliYazdir(DagitimYoneticisi yonetici) {
        System.out.println("=== ÖNCELİK SIRASINA GÖRE AFET BÖLGELERİ ===");
        List<AfetBolgesi> sirali = yonetici.bolgeleriOnceligeGoreSirala();
        int sira = 1;
        for (AfetBolgesi b : sirali) {
            double skor = yonetici.getStrateji().oncelikPuaniHesapla(b);
            System.out.printf("%d. [Öncelik Skoru: %6.2f] %s%n", sira++, skor, b);
        }
    }

    private static void yeniKaynakEkleMenu(Scanner scanner, DagitimYoneticisi yonetici) {
        System.out.println("=== YENİ YARDIM KAYNAĞI EKLE ===");
        System.out.print("Kategori seçiniz (MEDIKAL / GIDA / BARINMA / ISINMA): ");
        String kategori = scanner.nextLine().trim().toUpperCase();

        System.out.print("Kaynak ID (Örn: K-501): ");
        String id = scanner.nextLine().trim();

        System.out.print("Kaynak Adı: ");
        String ad = scanner.nextLine().trim();

        System.out.print("Stok Miktarı: ");
        int stok = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Birim (Adet / Koli / Set / Paket): ");
        String birim = scanner.nextLine().trim();

        System.out.print("Kritik Stok Eşiği: ");
        int kritik = Integer.parseInt(scanner.nextLine().trim());

        String p1;
        String p2;
        switch (kategori) {
            case "MEDIKAL":
                System.out.print("Soğuk zincir gerekli mi? (true / false): ");
                p1 = scanner.nextLine().trim();
                System.out.print("Acil müdahale derecesi (1-5): ");
                p2 = scanner.nextLine().trim();
                break;
            case "GIDA":
                System.out.print("Günlük kalori değeri (kcal, örn: 2000): ");
                p1 = scanner.nextLine().trim();
                System.out.print("Bozulabilir gıda mı? (true / false): ");
                p2 = scanner.nextLine().trim();
                break;
            case "BARINMA":
                System.out.print("Kişi kapasitesi (örn: 6): ");
                p1 = scanner.nextLine().trim();
                System.out.print("Kış şartlarına uygun mu? (true / false): ");
                p2 = scanner.nextLine().trim();
                break;
            case "ISINMA":
                System.out.print("Soğuktan koruma derecesi (1-10): ");
                p1 = scanner.nextLine().trim();
                System.out.print("Enerji/Isıtma türü (Termal / Elektrik / LPG): ");
                p2 = scanner.nextLine().trim();
                break;
            default:
                System.out.println("Geçersiz kategori girdiniz!");
                return;
        }

        YardimKaynagi yeni = KaynakFabrikasi.kaynakOlustur(kategori, id, ad, stok, birim, kritik, p1, p2);
        yonetici.kaynakEkle(yeni);
        System.out.println("[BAŞARILI] Kaynak eklendi ve dosyaya kaydedildi: " + yeni);
    }

    private static void yeniBolgeEkleMenu(Scanner scanner, DagitimYoneticisi yonetici) {
        System.out.println("=== YENİ AFET BÖLGESİ EKLE ===");
        System.out.print("Bölge ID (Örn: B-04): ");
        String id = scanner.nextLine().trim();

        System.out.print("Bölge Adı (Örn: Malatya - Doğanşehir): ");
        String ad = scanner.nextLine().trim();

        System.out.print("Yıkım Derecesi (1-10): ");
        int yikim = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Etkilenen Nüfus: ");
        int nufus = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Yaralı Sayısı: ");
        int yarali = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Hassas Grup (Bebek/Yaşlı/Engelli) Sayısı: ");
        int hassas = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Kara yolu ulaşımı açık mı? (true / false): ");
        boolean ulasim = Boolean.parseBoolean(scanner.nextLine().trim());

        System.out.print("Bekleme Süresi (Saat): ");
        int bekleme = Integer.parseInt(scanner.nextLine().trim());

        AfetBolgesi bolge = new AfetBolgesi(id, ad, yikim, nufus, yarali, hassas, ulasim, bekleme);

        System.out.print("MEDIKAL İhtiyaç Miktarı: ");
        bolge.ihtiyacBelirle("MEDIKAL", Integer.parseInt(scanner.nextLine().trim()));

        System.out.print("GIDA İhtiyaç Miktarı: ");
        bolge.ihtiyacBelirle("GIDA", Integer.parseInt(scanner.nextLine().trim()));

        System.out.print("BARINMA İhtiyaç Miktarı: ");
        bolge.ihtiyacBelirle("BARINMA", Integer.parseInt(scanner.nextLine().trim()));

        System.out.print("ISINMA İhtiyaç Miktarı: ");
        bolge.ihtiyacBelirle("ISINMA", Integer.parseInt(scanner.nextLine().trim()));

        yonetici.bolgeEkle(bolge);
        System.out.println("[BAŞARILI] Afet bölgesi eklendi ve kalıcı olarak kaydedildi!");
    }

    private static void otomatikDagitimiCalistir(DagitimYoneticisi yonetici) {
        System.out.println("=== ÖNCELİK VE İHTİYAÇ ODAKLI DAĞITIM BAŞLATILIYOR ===");
        List<DagitimKaydi> yapilanlar = yonetici.oncelikliDagitimYap();
        if (yapilanlar.isEmpty()) {
            System.out.println("Dağıtılacak uygun stok veya bekleyen bölge ihtiyacı bulunamadı.");
        } else {
            System.out.println("\n[ÖZET] Toplam " + yapilanlar.size() + " sevkiyat işlemi tamamlandı ve diske kaydedildi.");
        }
    }

    private static void stratejiDegistirMenu(Scanner scanner, DagitimYoneticisi yonetici) {
        System.out.println("=== ÖNCELİK HESAPLAMA STRATEJİSİ SEÇİMİ ===");
        System.out.println("1. Çok Kriterli Ağırlıklı Aciliyet Algoritması (Yıkım + Nüfus + Yaralı + Ulaşım)");
        System.out.println("2. Yaralı ve Hassas Grup Odaklı Öncelik Algoritması (Tıbbi & İnsani Odak)");
        System.out.print("Seçiminiz (1 veya 2): ");
        String s = scanner.nextLine().trim();
        if ("2".equals(s)) {
            yonetici.setStrateji(new HassasGrupOdakliStrateji());
        } else {
            yonetici.setStrateji(new AgirlikliAciliyetStratejisi());
        }
        System.out.println("[GÜNCELLENDİ] Aktif Strateji: " + yonetici.getStrateji().getStratejiAdi());
    }

    private static void gecmisRaporlariYazdir(DagitimYoneticisi yonetici, AcilDurumBildirimcisi bildirimci) {
        System.out.println("=== GEÇMİŞ DAĞITIM KAYITLARI (data/dagitim_gecmisi.csv) ===");
        List<DagitimKaydi> gecmis = yonetici.getDagitimGecmisi();
        if (gecmis.isEmpty()) {
            System.out.println("Henüz gerçekleştirilmiş bir dağıtım kaydı bulunmamaktadır.");
        } else {
            for (DagitimKaydi d : gecmis) {
                System.out.println(d);
            }
        }

        System.out.println("\n=== SON OTURUM ACİL DURUM & STOK BİLDİRİMLERİ (Observer) ===");
        List<String> bildirimler = bildirimci.getBildirimGecmisi();
        if (bildirimler.isEmpty()) {
            System.out.println("Bu oturumda henüz bildirim oluşmadı.");
        } else {
            for (String b : bildirimler) {
                System.out.println(b);
            }
        }
    }
}