package afet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Afet Bölgesi Sınıfı (Encapsulation).
 * Bölgenin yıkım derecesini, demografik aciliyet verilerini ve kategori bazlı yardım ihtiyaçlarını tutar.
 */
public class AfetBolgesi {
    private String bolgeId;
    private String bolgeAdi;
    private int yikimDerecesi;        // 1 - 10 arası
    private int etkilenenNufus;
    private int yaraliSayisi;
    private int hassasGrupSayisi;     // Bebek, yaşlı ve engelli birey sayısı
    private boolean ulasimAcikMi;
    private int beklemeSuresiSaat;
    private Map<String, Integer> ihtiyacListesi; // KategoriKodu -> İhtiyaç Miktarı

    public AfetBolgesi(String bolgeId, String bolgeAdi, int yikimDerecesi, int etkilenenNufus,
                       int yaraliSayisi, int hassasGrupSayisi, boolean ulasimAcikMi, int beklemeSuresiSaat) {
        if (bolgeId == null || bolgeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Bölge ID boş olamaz!");
        }
        if (bolgeAdi == null || bolgeAdi.trim().isEmpty()) {
            throw new IllegalArgumentException("Bölge adı boş olamaz!");
        }
        if (yikimDerecesi < 1 || yikimDerecesi > 10) {
            throw new IllegalArgumentException("Yıkım derecesi 1 ile 10 arasında olmalıdır!");
        }
        if (etkilenenNufus < 0 || yaraliSayisi < 0 || hassasGrupSayisi < 0 || beklemeSuresiSaat < 0) {
            throw new IllegalArgumentException("Nüfus ve süre verileri negatif olamaz!");
        }
        this.bolgeId = bolgeId.trim();
        this.bolgeAdi = bolgeAdi.trim();
        this.yikimDerecesi = yikimDerecesi;
        this.etkilenenNufus = etkilenenNufus;
        this.yaraliSayisi = yaraliSayisi;
        this.hassasGrupSayisi = hassasGrupSayisi;
        this.ulasimAcikMi = ulasimAcikMi;
        this.beklemeSuresiSaat = beklemeSuresiSaat;
        this.ihtiyacListesi = new HashMap<>();
    }

    public String getBolgeId() {
        return bolgeId;
    }

    public String getBolgeAdi() {
        return bolgeAdi;
    }

    public int getYikimDerecesi() {
        return yikimDerecesi;
    }

    public void setYikimDerecesi(int yikimDerecesi) {
        if (yikimDerecesi < 1 || yikimDerecesi > 10) {
            throw new IllegalArgumentException("Yıkım derecesi 1 ile 10 arasında olmalıdır!");
        }
        this.yikimDerecesi = yikimDerecesi;
    }

    public int getEtkilenenNufus() {
        return etkilenenNufus;
    }

    public int getYaraliSayisi() {
        return yaraliSayisi;
    }

    public void setYaraliSayisi(int yaraliSayisi) {
        if (yaraliSayisi < 0) {
            throw new IllegalArgumentException("Yaralı sayısı negatif olamaz!");
        }
        this.yaraliSayisi = yaraliSayisi;
    }

    public int getHassasGrupSayisi() {
        return hassasGrupSayisi;
    }

    public boolean isUlasimAcikMi() {
        return ulasimAcikMi;
    }

    public void setUlasimAcikMi(boolean ulasimAcikMi) {
        this.ulasimAcikMi = ulasimAcikMi;
    }

    public int getBeklemeSuresiSaat() {
        return beklemeSuresiSaat;
    }

    public void setBeklemeSuresiSaat(int beklemeSuresiSaat) {
        if (beklemeSuresiSaat < 0) {
            throw new IllegalArgumentException("Bekleme süresi negatif olamaz!");
        }
        this.beklemeSuresiSaat = beklemeSuresiSaat;
    }

    public void ihtiyacBelirle(String kategoriKodu, int miktar) {
        if (kategoriKodu == null || kategoriKodu.trim().isEmpty()) {
            throw new IllegalArgumentException("Kategori kodu boş olamaz!");
        }
        if (miktar < 0) {
            throw new IllegalArgumentException("İhtiyaç miktarı negatif olamaz!");
        }
        this.ihtiyacListesi.put(kategoriKodu.trim().toUpperCase(), miktar);
    }

    public int getKategoriIhtiyaci(String kategoriKodu) {
        if (kategoriKodu == null) return 0;
        return this.ihtiyacListesi.getOrDefault(kategoriKodu.trim().toUpperCase(), 0);
    }

    public void ihtiyacAzalt(String kategoriKodu, int karsilananMiktar) {
        String anahtar = kategoriKodu.trim().toUpperCase();
        int mevcut = getKategoriIhtiyaci(anahtar);
        int kalan = Math.max(0, mevcut - karsilananMiktar);
        this.ihtiyacListesi.put(anahtar, kalan);
    }

    public Map<String, Integer> getIhtiyacListesi() {
        return Collections.unmodifiableMap(ihtiyacListesi);
    }

    public int getToplamKalanIhtiyac() {
        int toplam = 0;
        for (int m : ihtiyacListesi.values()) {
            toplam += m;
        }
        return toplam;
    }

    public String toCsvSatiri() {
        StringJoiner ihtiyacJoiner = new StringJoiner(",");
        for (Map.Entry<String, Integer> entry : ihtiyacListesi.entrySet()) {
            ihtiyacJoiner.add(entry.getKey() + ":" + entry.getValue());
        }
        String ihtiyacStr = ihtiyacJoiner.length() == 0 ? "YOK" : ihtiyacJoiner.toString();
        return String.join(";",
                bolgeId,
                bolgeAdi,
                String.valueOf(yikimDerecesi),
                String.valueOf(etkilenenNufus),
                String.valueOf(yaraliSayisi),
                String.valueOf(hassasGrupSayisi),
                String.valueOf(ulasimAcikMi),
                String.valueOf(beklemeSuresiSaat),
                ihtiyacStr);
    }

    public static AfetBolgesi csvSatirindanOlustur(String csvSatiri) {
        String[] p = csvSatiri.split(";");
        if (p.length < 9) {
            throw new IllegalArgumentException("Hatalı CSV bölge satırı: " + csvSatiri);
        }
        AfetBolgesi bolge = new AfetBolgesi(
                p[0],
                p[1],
                Integer.parseInt(p[2].trim()),
                Integer.parseInt(p[3].trim()),
                Integer.parseInt(p[4].trim()),
                Integer.parseInt(p[5].trim()),
                Boolean.parseBoolean(p[6].trim()),
                Integer.parseInt(p[7].trim())
        );
        String ihtiyacBolumu = p[8].trim();
        if (!ihtiyacBolumu.equals("YOK") && !ihtiyacBolumu.isEmpty()) {
            String[] ciftler = ihtiyacBolumu.split(",");
            for (String cift : ciftler) {
                String[] kv = cift.split(":");
                if (kv.length == 2) {
                    bolge.ihtiyacBelirle(kv[0].trim(), Integer.parseInt(kv[1].trim()));
                }
            }
        }
        return bolge;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-22s | Yıkım: %2d/10 | Nüfus: %5d | Yaralı: %4d | Hassas: %4d | Yol: %-6s | Bekleme: %2d sa | İhtiyaçlar: %s",
                bolgeId, bolgeAdi, yikimDerecesi, etkilenenNufus, yaraliSayisi, hassasGrupSayisi,
                (ulasimAcikMi ? "Açık" : "Kapalı"), beklemeSuresiSaat, ihtiyacListesi.toString());
    }
}
