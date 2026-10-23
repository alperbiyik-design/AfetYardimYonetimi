package afet;

/**
 * Isınma (Battaniye, Çadır Sobası, Elektrikli/Gazlı Isıtıcı) Kaynakları Sınıfı.
 */
public class IsinmaKaynagi extends YardimKaynagi {
    private int korumaDerecesi; // 1 - 10 arası soğuktan koruma seviyesi
    private String enerjiTuru;  // Termal, Elektrik, LPG, Odun/Kömür

    public IsinmaKaynagi(String kaynakId, String ad, int stokMiktari, String birim,
                         int kritikEsik, int korumaDerecesi, String enerjiTuru) {
        super(kaynakId, ad, stokMiktari, birim, kritikEsik);
        if (korumaDerecesi < 1 || korumaDerecesi > 10) {
            throw new IllegalArgumentException("Koruma derecesi 1 ile 10 arasında olmalıdır!");
        }
        this.korumaDerecesi = korumaDerecesi;
        this.enerjiTuru = (enerjiTuru == null || enerjiTuru.trim().isEmpty()) ? "Termal" : enerjiTuru.trim();
    }

    public int getKorumaDerecesi() {
        return korumaDerecesi;
    }

    public String getEnerjiTuru() {
        return enerjiTuru;
    }

    @Override
    public String getKategoriKodu() {
        return "ISINMA";
    }

    @Override
    public double hesaplaBirimEtkiPuani() {
        return korumaDerecesi * 1.8;
    }

    @Override
    public String getOzelDetay() {
        return "Koruma: " + korumaDerecesi + "/10, Tür: " + enerjiTuru;
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
                String.valueOf(korumaDerecesi),
                enerjiTuru);
    }
}
