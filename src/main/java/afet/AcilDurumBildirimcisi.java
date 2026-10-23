package afet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Somut Gözlemci (Concrete Observer).
 * Gerçekleşen sevkiyatları ve kritik stok alarmlarını takip eder ve raporlar.
 */
public class AcilDurumBildirimcisi implements DagitimGozlemcisi {
    private final List<String> bildirimGecmisi = new ArrayList<>();
    private boolean konsolaYazdir = true;

    public AcilDurumBildirimcisi() {
    }

    public AcilDurumBildirimcisi(boolean konsolaYazdir) {
        this.konsolaYazdir = konsolaYazdir;
    }

    @Override
    public void dagitimGerceklesti(DagitimKaydi kayit) {
        String mesaj = String.format("[SEVKİYAT BİLDİRİMİ] %s bölgesine %d adet %s sevk edildi. (Öncelik Skoru: %.2f)",
                kayit.getBolgeAdi(), kayit.getDagitilanMiktar(), kayit.getKaynakAdi(), kayit.getBolgeOncelikPuani());
        bildirimGecmisi.add(mesaj);
        if (konsolaYazdir) {
            System.out.println("  -> " + mesaj);
        }
    }

    @Override
    public void kritikStokUyarisi(YardimKaynagi kaynak) {
        String uyari = String.format("[KRİTİK STOK ALARMI] '%s' (%s) stoğu kritik seviyeye düştü! Kalan: %d %s (Eşik: %d)",
                kaynak.getAd(), kaynak.getKaynakId(), kaynak.getStokMiktari(), kaynak.getBirim(), kaynak.getKritikEsik());
        bildirimGecmisi.add(uyari);
        if (konsolaYazdir) {
            System.out.println("  !! " + uyari);
        }
    }

    public List<String> getBildirimGecmisi() {
        return Collections.unmodifiableList(bildirimGecmisi);
    }
}
