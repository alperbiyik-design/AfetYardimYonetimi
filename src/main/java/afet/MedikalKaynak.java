package afet;

/**
 * Tıbbi ve İlk Yardım Kaynakları Sınıfı (Inheritance & Polymorphism).
 */
public class MedikalKaynak extends YardimKaynagi {
    private boolean sogukZincirGerekliMi;
    private int acilMudahaleDerecesi; // 1 - 5 arası

    public MedikalKaynak(String kaynakId, String ad, int stokMiktari, String birim,
                         int kritikEsik, boolean sogukZincirGerekliMi, int acilMudahaleDerecesi) {
        super(kaynakId, ad, stokMiktari, birim, kritikEsik);
        if (acilMudahaleDerecesi < 1 || acilMudahaleDerecesi > 5) {
            throw new IllegalArgumentException("Acil müdahale derecesi 1 ile 5 arasında olmalıdır!");
        }
        this.sogukZincirGerekliMi = sogukZincirGerekliMi;
        this.acilMudahaleDerecesi = acilMudahaleDerecesi;
    }

    public boolean isSogukZincirGerekliMi() {
        return sogukZincirGerekliMi;
    }

    public int getAcilMudahaleDerecesi() {
        return acilMudahaleDerecesi;
    }

    @Override
    public String getKategoriKodu() {
        return "MEDIKAL";
    }

    @Override
    public double hesaplaBirimEtkiPuani() {
        double bazPuan = acilMudahaleDerecesi * 3.5;
        return sogukZincirGerekliMi ? bazPuan + 4.0 : bazPuan;
    }

    @Override
    public String getOzelDetay() {
        return "Soğuk Zincir: " + (sogukZincirGerekliMi ? "Evet" : "Hayır")
                + ", Aciliyet: " + acilMudahaleDerecesi + "/5";
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
                String.valueOf(sogukZincirGerekliMi),
                String.valueOf(acilMudahaleDerecesi));
    }
}
