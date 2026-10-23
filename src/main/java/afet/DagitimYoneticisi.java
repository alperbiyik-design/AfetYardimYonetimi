package afet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Yardım Kaynaklarının Öncelik ve İhtiyaç Durumuna Göre Dağıtılmasını Yöneten Ana Sınıf.
 *
 * Uygulanan İlkeler:
 * - SOLID (SRP, OCP, LSP, ISP, DIP)
 * - Strategy Pattern (OncelikHesaplamaStratejisi)
 * - Observer Pattern (DagitimGozlemcisi)
 * - Çok Kriterli Öncelik Sıralaması ve Kademeli/Etki Odaklı Kaynak Tahsis Algoritması
 */
public class DagitimYoneticisi {
    private final VeriDeposu veriDeposu;
    private OncelikHesaplamaStratejisi strateji;
    private final List<DagitimGozlemcisi> gozlemciler;

    private final List<YardimKaynagi> kaynaklar;
    private final List<AfetBolgesi> bolgeler;
    private final List<DagitimKaydi> dagitimGecmisi;

    public DagitimYoneticisi(VeriDeposu veriDeposu, OncelikHesaplamaStratejisi strateji) {
        this.veriDeposu = veriDeposu;
        this.strateji = strateji;
        this.gozlemciler = new ArrayList<>();
        this.kaynaklar = new ArrayList<>(veriDeposu.kaynaklariYukle());
        this.bolgeler = new ArrayList<>(veriDeposu.bolgeleriYukle());
        this.dagitimGecmisi = new ArrayList<>(veriDeposu.dagitimKayitlariniYukle());
    }

    public void gozlemciEkle(DagitimGozlemcisi gozlemci) {
        if (gozlemci != null && !gozlemciler.contains(gozlemci)) {
            gozlemciler.add(gozlemci);
        }
    }

    public void setStrateji(OncelikHesaplamaStratejisi yeniStrateji) {
        if (yeniStrateji == null) {
            throw new IllegalArgumentException("Strateji boş olamaz!");
        }
        this.strateji = yeniStrateji;
    }

    public OncelikHesaplamaStratejisi getStrateji() {
        return strateji;
    }

    public void kaynakEkle(YardimKaynagi kaynak) {
        for (YardimKaynagi mevcut : kaynaklar) {
            if (mevcut.getKaynakId().equalsIgnoreCase(kaynak.getKaynakId())) {
                mevcut.stokEkle(kaynak.getStokMiktari());
                verileriKaliciKaydet();
                return;
            }
        }
        kaynaklar.add(kaynak);
        verileriKaliciKaydet();
    }

    public void bolgeEkle(AfetBolgesi bolge) {
        bolgeler.removeIf(b -> b.getBolgeId().equalsIgnoreCase(bolge.getBolgeId()));
        bolgeler.add(bolge);
        verileriKaliciKaydet();
    }

    /**
     * PROJE ALGORİTMASI 1: Afet Bölgelerini Seçili Stratejiye Göre Azalan Öncelik Sırasına Dizme.
     * Öncelik puanı eşit olan bölgelerde yıkım derecesi ve yaralı sayısı yüksek olan öne geçer.
     */
    public List<AfetBolgesi> bolgeleriOnceligeGoreSirala() {
        List<AfetBolgesi> sirali = new ArrayList<>(bolgeler);
        sirali.sort((b1, b2) -> {
            double p1 = strateji.oncelikPuaniHesapla(b1);
            double p2 = strateji.oncelikPuaniHesapla(b2);
            int cmp = Double.compare(p2, p1); // Büyükten küçüğe (Max-Priority)
            if (cmp != 0) return cmp;
            int yikimCmp = Integer.compare(b2.getYikimDerecesi(), b1.getYikimDerecesi());
            if (yikimCmp != 0) return yikimCmp;
            return Integer.compare(b2.getYaraliSayisi(), b1.getYaraliSayisi());
        });
        return sirali;
    }

    /**
     * Belirli bir kategorideki kaynakları birim etki puanına göre (en etkili kaynak ilk sırada) sıralar.
     */
    public List<YardimKaynagi> kategoriKaynaklariniEtkiyeGoreGetir(String kategoriKodu) {
        List<YardimKaynagi> uygunlar = new ArrayList<>();
        for (YardimKaynagi k : kaynaklar) {
            if (k.getKategoriKodu().equalsIgnoreCase(kategoriKodu) && k.getStokMiktari() > 0) {
                uygunlar.add(k);
            }
        }
        uygunlar.sort(Comparator.comparingDouble(YardimKaynagi::hesaplaBirimEtkiPuani).reversed());
        return uygunlar;
    }

    /**
     * PROJE ALGORİTMASI 2: Öncelik ve İhtiyaç Durumuna Göre Otomatik Kaynak Dağıtım Algoritması.
     *
     * Çalışma Mantığı:
     * 1. Tüm afet bölgeleri aktif OncelikHesaplamaStratejisi ile puanlanır ve en yüksek öncelikliden
     *    en düşüğe doğru sıralanır.
     * 2. Her bölge için kategori bazlı (MEDIKAL, GIDA, BARINMA, ISINMA) açık ihtiyaçlar kontrol edilir.
     * 3. İlgili kategorideki kaynaklar, polimorfik hesaplaBirimEtkiPuani() değerine göre en yüksek etkili
     *    kaynaktan başlanarak tahsis edilir.
     * 4. Stok düşülür, bölgenin kalan ihtiyacı güncellenir, zaman damgalı DagitimKaydi oluşturulur,
     *    Observer'lara sevkiyat ve kritik stok uyarıları gönderilir ve tüm veriler diske kaydedilir.
     */
    public List<DagitimKaydi> oncelikliDagitimYap() {
        List<DagitimKaydi> yeniKayitlar = new ArrayList<>();
        List<AfetBolgesi> oncelikliBolgeler = bolgeleriOnceligeGoreSirala();
        String[] kategoriler = {"MEDIKAL", "GIDA", "BARINMA", "ISINMA"};
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (AfetBolgesi bolge : oncelikliBolgeler) {
            double bolgeSkoru = strateji.oncelikPuaniHesapla(bolge);

            for (String kategori : kategoriler) {
                int kalanIhtiyac = bolge.getKategoriIhtiyaci(kategori);
                if (kalanIhtiyac <= 0) {
                    continue;
                }

                List<YardimKaynagi> uygunKaynaklar = kategoriKaynaklariniEtkiyeGoreGetir(kategori);
                for (YardimKaynagi kaynak : uygunKaynaklar) {
                    if (kalanIhtiyac <= 0) break;
                    int mevcutStok = kaynak.getStokMiktari();
                    if (mevcutStok <= 0) continue;

                    int sevkMiktari = Math.min(kalanIhtiyac, mevcutStok);
                    boolean basarili = kaynak.stokDus(sevkMiktari);
                    if (basarili) {
                        bolge.ihtiyacAzalt(kategori, sevkMiktari);
                        kalanIhtiyac -= sevkMiktari;

                        String kayitId = "SEV-" + (dagitimGecmisi.size() + 101);
                        String simdi = LocalDateTime.now().format(fmt);
                        DagitimKaydi kayit = new DagitimKaydi(
                                kayitId,
                                bolge.getBolgeId(),
                                bolge.getBolgeAdi(),
                                kaynak.getKaynakId(),
                                kaynak.getAd(),
                                kategori,
                                sevkMiktari,
                                bolgeSkoru,
                                simdi
                        );

                        dagitimGecmisi.add(kayit);
                        yeniKayitlar.add(kayit);

                        // Gözlemcilere (Observer) sevkiyat ve kritik stok bildirimi gönder
                        for (DagitimGozlemcisi g : gozlemciler) {
                            g.dagitimGerceklesti(kayit);
                            if (kaynak.kritikStokAltindaMi()) {
                                g.kritikStokUyarisi(kaynak);
                            }
                        }
                    }
                }
            }
        }

        verileriKaliciKaydet();
        return yeniKayitlar;
    }

    public void verileriKaliciKaydet() {
        veriDeposu.kaynaklariKaydet(kaynaklar);
        veriDeposu.bolgeleriKaydet(bolgeler);
        veriDeposu.dagitimKayitlariniKaydet(dagitimGecmisi);
    }

    public List<YardimKaynagi> getKaynaklar() {
        return Collections.unmodifiableList(kaynaklar);
    }

    public List<AfetBolgesi> getBolgeler() {
        return Collections.unmodifiableList(bolgeler);
    }

    public List<DagitimKaydi> getDagitimGecmisi() {
        return Collections.unmodifiableList(dagitimGecmisi);
    }

    /**
     * İlk çalıştırmada veri dosyaları boşsa örnek afet bölgeleri ve yardım kaynaklarını yükler.
     */
    public void varsayilanVerileriYukleEgerBossa() {
        if (!kaynaklar.isEmpty() || !bolgeler.isEmpty()) {
            return;
        }

        // Örnek Yardım Kaynakları (Factory Pattern ile)
        kaynaklar.add(KaynakFabrikasi.kaynakOlustur("MEDIKAL", "K-101", "Acil Travma ve İlk Yardım Kiti", 350, "Set", 50, "false", "5"));
        kaynaklar.add(KaynakFabrikasi.kaynakOlustur("MEDIKAL", "K-102", "Soğuk Zincir Tetanoz & Antibiyotik Paketi", 180, "Kutu", 40, "true", "5"));
        kaynaklar.add(KaynakFabrikasi.kaynakOlustur("GIDA", "K-201", "24 Saatlik Hazır Kumanya Paketi", 900, "Paket", 150, "2400", "false"));
        kaynaklar.add(KaynakFabrikasi.kaynakOlustur("GIDA", "K-202", "Bebek Maması ve Steril Su Seti", 300, "Koli", 60, "1800", "true"));
        kaynaklar.add(KaynakFabrikasi.kaynakOlustur("BARINMA", "K-301", "Kışlık İzolasyonlu Aile Çadırı", 140, "Adet", 30, "6", "true"));
        kaynaklar.add(KaynakFabrikasi.kaynakOlustur("ISINMA", "K-401", "Termal Polar Battaniye ve Uyku Tulumu", 500, "Set", 80, "8", "Termal"));

        // Örnek Afet Bölgeleri
        AfetBolgesi b1 = new AfetBolgesi("B-01", "Hatay - Antakya Merkez", 9, 14500, 620, 1850, true, 14);
        b1.ihtiyacBelirle("MEDIKAL", 260);
        b1.ihtiyacBelirle("GIDA", 500);
        b1.ihtiyacBelirle("BARINMA", 70);
        b1.ihtiyacBelirle("ISINMA", 220);

        AfetBolgesi b2 = new AfetBolgesi("B-02", "Kahramanmaraş - Pazarcık", 8, 9800, 410, 1200, true, 12);
        b2.ihtiyacBelirle("MEDIKAL", 180);
        b2.ihtiyacBelirle("GIDA", 400);
        b2.ihtiyacBelirle("BARINMA", 50);
        b2.ihtiyacBelirle("ISINMA", 180);

        AfetBolgesi b3 = new AfetBolgesi("B-03", "Adıyaman - Gölbaşı", 7, 6400, 190, 750, false, 18);
        b3.ihtiyacBelirle("MEDIKAL", 120);
        b3.ihtiyacBelirle("GIDA", 320);
        b3.ihtiyacBelirle("BARINMA", 40);
        b3.ihtiyacBelirle("ISINMA", 150);

        bolgeler.add(b1);
        bolgeler.add(b2);
        bolgeler.add(b3);

        verileriKaliciKaydet();
    }
}
