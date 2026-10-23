package afet;

/**
 * Soyut Üst Sınıf (Abstraction & Encapsulation).
 * Tüm yardım kaynaklarının ortak özelliklerini ve davranış sözleşmesini tanımlar.
 * SOLID - Open/Closed (OCP) ve Liskov Substitution (LSP) prensiplerini destekler.
 */
public abstract class YardimKaynagi {
    private String kaynakId;
    private String ad;
    private int stokMiktari;
    private String birim;
    private int kritikEsik;

    public YardimKaynagi(String kaynakId, String ad, int stokMiktari, String birim, int kritikEsik) {
        if (kaynakId == null || kaynakId.trim().isEmpty()) {
            throw new IllegalArgumentException("Kaynak ID boş olamaz!");
        }
        if (ad == null || ad.trim().isEmpty()) {
            throw new IllegalArgumentException("Kaynak adı boş olamaz!");
        }
        if (stokMiktari < 0) {
            throw new IllegalArgumentException("Stok miktarı negatif olamaz!");
        }
        if (kritikEsik < 0) {
            throw new IllegalArgumentException("Kritik eşik negatif olamaz!");
        }
        this.kaynakId = kaynakId.trim();
        this.ad = ad.trim();
        this.stokMiktari = stokMiktari;
        this.birim = (birim == null || birim.trim().isEmpty()) ? "Adet" : birim.trim();
        this.kritikEsik = kritikEsik;
    }

    public String getKaynakId() {
        return kaynakId;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        if (ad == null || ad.trim().isEmpty()) {
            throw new IllegalArgumentException("Kaynak adı boş olamaz!");
        }
        this.ad = ad.trim();
    }

    public int getStokMiktari() {
        return stokMiktari;
    }

    public void setStokMiktari(int stokMiktari) {
        if (stokMiktari < 0) {
            throw new IllegalArgumentException("Stok miktarı negatif olamaz!");
        }
        this.stokMiktari = stokMiktari;
    }

    public String getBirim() {
        return birim;
    }

    public int getKritikEsik() {
        return kritikEsik;
    }

    public void setKritikEsik(int kritikEsik) {
        if (kritikEsik < 0) {
            throw new IllegalArgumentException("Kritik eşik negatif olamaz!");
        }
        this.kritikEsik = kritikEsik;
    }

    public void stokEkle(int miktar) {
        if (miktar <= 0) {
            throw new IllegalArgumentException("Eklenecek miktar pozitif olmalıdır!");
        }
        this.stokMiktari += miktar;
    }

    public boolean stokDus(int miktar) {
        if (miktar <= 0 || miktar > this.stokMiktari) {
            return false;
        }
        this.stokMiktari -= miktar;
        return true;
    }

    public boolean kritikStokAltindaMi() {
        return this.stokMiktari <= this.kritikEsik;
    }

    // Çok Biçimlilik (Polymorphism) için alt sınıfların ezmesi gereken soyut metotlar
    public abstract String getKategoriKodu();
    public abstract double hesaplaBirimEtkiPuani();
    public abstract String getOzelDetay();
    public abstract String toCsvSatiri();

    @Override
    public String toString() {
        String uyari = kritikStokAltindaMi() ? " [KRİTİK STOK!]" : "";
        return String.format("[%s] %-10s | %-22s | Stok: %5d %-6s | Etki Puanı: %5.2f | %s%s",
                kaynakId, getKategoriKodu(), ad, stokMiktari, birim,
                hesaplaBirimEtkiPuani(), getOzelDetay(), uyari);
    }
}