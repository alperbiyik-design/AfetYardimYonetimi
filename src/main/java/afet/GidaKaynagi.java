package afet;

/**
 * Gıda ve İçme Suyu Kaynakları Sınıfı (Inheritance & Polymorphism).
 */
public class GidaKaynagi extends YardimKaynagi {
    private int gunlukKaloriDegeri;
    private boolean bozulabilirMi;

    public GidaKaynagi(String kaynakId, String ad, int stokMiktari, String birim,
                       int kritikEsik, int gunlukKaloriDegeri, boolean bozulabilirMi) {
        super(kaynakId, ad, stokMiktari, birim, kritikEsik);
        if (gunlukKaloriDegeri <= 0) {
            throw new IllegalArgumentException("Kalori değeri pozitif olmalıdır!");
        }
        this.gunlukKaloriDegeri = gunlukKaloriDegeri;
        this.bozulabilirMi = bozulabilirMi;
    }

    public int getGunlukKaloriDegeri() {
        return gunlukKaloriDegeri;
    }

    public boolean isBozulabilirMi() {
        return bozulabilirMi;
    }

    @Override
    public String getKategoriKodu() {
        return "GIDA";
    }

    @Override
    public double hesaplaBirimEtkiPuani() {
        double kaloriPuani = gunlukKaloriDegeri / 250.0;
        return bozulabilirMi ? kaloriPuani + 2.0 : kaloriPuani;
    }

    @Override
    public String getOzelDetay() {
        return "Kalori: " + gunlukKaloriDegeri + " kcal, Bozulabilir: " + (bozulabilirMi ? "Evet" : "Hayır");
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
                String.valueOf(gunlukKaloriDegeri),
                String.valueOf(bozulabilirMi));
    }
}
