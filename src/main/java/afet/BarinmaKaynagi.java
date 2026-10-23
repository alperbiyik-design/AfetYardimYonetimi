package afet;

/**
 * Barınma (Çadır, Konteyner vb.) Kaynakları Sınıfı (Inheritance & Polymorphism).
 */
public class BarinmaKaynagi extends YardimKaynagi {
    private int kisiKapasitesi;
    private boolean kisSartlarinaUygunMu;

    public BarinmaKaynagi(String kaynakId, String ad, int stokMiktari, String birim,
                          int kritikEsik, int kisiKapasitesi, boolean kisSartlarinaUygunMu) {
        super(kaynakId, ad, stokMiktari, birim, kritikEsik);
        if (kisiKapasitesi <= 0) {
            throw new IllegalArgumentException("Kişi kapasitesi pozitif olmalıdır!");
        }
        this.kisiKapasitesi = kisiKapasitesi;
        this.kisSartlarinaUygunMu = kisSartlarinaUygunMu;
    }

    public int getKisiKapasitesi() {
        return kisiKapasitesi;
    }

    public boolean isKisSartlarinaUygunMu() {
        return kisSartlarinaUygunMu;
    }

    @Override
    public String getKategoriKodu() {
        return "BARINMA";
    }

    @Override
    public double hesaplaBirimEtkiPuani() {
        double baz = kisiKapasitesi * 2.2;
        return kisSartlarinaUygunMu ? baz + 5.0 : baz;
    }

    @Override
    public String getOzelDetay() {
        return "Kapasite: " + kisiKapasitesi + " Kişi, Kışa Uygun: " + (kisSartlarinaUygunMu ? "Evet" : "Hayır");
    }

    @Override
    public String toCsvSatiri() {
        return String.join(";",
                getKategoriKodu(),
                getKaynakId(),
                getAd(),
                String.valueOf(getStokMiktari()),
                getBirim(),
                String.valueOf(getKritikEsik()),
                String.valueOf(kisiKapasitesi),
                String.valueOf(kisSartlarinaUygunMu));
    }
}