package afet;

/**
 * Gerçekleştirilen her yardım sevkiyatının kalıcı kaydını temsil eden sınıf (Encapsulation).
 */
public class DagitimKaydi {
    private String kayitId;
    private String bolgeId;
    private String bolgeAdi;
    private String kaynakId;
    private String kaynakAdi;
    private String kategoriKodu;
    private int dagitilanMiktar;
    private double bolgeOncelikPuani;
    private String tarihSaat;

    public DagitimKaydi(String kayitId, String bolgeId, String bolgeAdi, String kaynakId,
                        String kaynakAdi, String kategoriKodu, int dagitilanMiktar,
                        double bolgeOncelikPuani, String tarihSaat) {
        this.kayitId = kayitId;
        this.bolgeId = bolgeId;
        this.bolgeAdi = bolgeAdi;
        this.kaynakId = kaynakId;
        this.kaynakAdi = kaynakAdi;
        this.kategoriKodu = kategoriKodu;
        this.dagitilanMiktar = dagitilanMiktar;
        this.bolgeOncelikPuani = bolgeOncelikPuani;
        this.tarihSaat = tarihSaat;
    }

    public String getKayitId() {
        return kayitId;
    }

    public String getBolgeId() {
        return bolgeId;
    }

    public String getBolgeAdi() {
        return bolgeAdi;
    }

    public String getKaynakId() {
        return kaynakId;
    }

    public String getKaynakAdi() {
        return kaynakAdi;
    }

    public String getKategoriKodu() {
        return kategoriKodu;
    }

    public int getDagitilanMiktar() {
        return dagitilanMiktar;
    }

    public double getBolgeOncelikPuani() {
        return bolgeOncelikPuani;
    }

    public String getTarihSaat() {
        return tarihSaat;
    }

    public String toCsvSatiri() {
        return String.join(";",
                kayitId,
                bolgeId,
                bolgeAdi,
                kaynakId,
                kaynakAdi,
                kategoriKodu,
                String.valueOf(dagitilanMiktar),
                String.valueOf(bolgeOncelikPuani),
                tarihSaat);
    }

    public static DagitimKaydi csvSatirindanOlustur(String csvSatiri) {
        String[] p = csvSatiri.split(";");
        if (p.length < 9) {
            throw new IllegalArgumentException("Hatalı CSV dağıtım kaydı satırı: " + csvSatiri);
        }
        return new DagitimKaydi(
                p[0], p[1], p[2], p[3], p[4], p[5],
                Integer.parseInt(p[6].trim()),
                Double.parseDouble(p[7].trim()),
                p[8]
        );
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Bölge: %-18s (Skor: %6.2f) | Kaynak: %-20s (%s) | Miktar: %4d",
                kayitId, tarihSaat, bolgeAdi, bolgeOncelikPuani, kaynakAdi, kategoriKodu, dagitilanMiktar);
    }
}
