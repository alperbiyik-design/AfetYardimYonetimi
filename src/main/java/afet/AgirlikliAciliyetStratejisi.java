package afet;

/**
 * Somut Strateji 1: Çok Kriterli Ağırlıklı Aciliyet Algoritması.
 * Yıkım derecesi, yaralı sayısı, etkilenen nüfus, hassas gruplar ve bekleme süresini ağırlıklı olarak hesaplar.
 */
public class AgirlikliAciliyetStratejisi implements OncelikHesaplamaStratejisi {

    @Override
    public double oncelikPuaniHesapla(AfetBolgesi bolge) {
        double yikimPuani = bolge.getYikimDerecesi() * 12.0;
        double yaraliPuani = bolge.getYaraliSayisi() * 0.15;
        double nufusPuani = bolge.getEtkilenenNufus() * 0.02;
        double hassasGrupPuani = bolge.getHassasGrupSayisi() * 0.08;
        double beklemePuani = bolge.getBeklemeSuresiSaat() * 2.5;
        double ulasimKatsayisi = bolge.isUlasimAcikMi() ? 1.0 : 0.85; // Ulaşım kapalıysa hava/özel koridor gerektirir

        double hamPuan = yikimPuani + yaraliPuani + nufusPuani + hassasGrupPuani + beklemePuani;
        return Math.round(hamPuan * ulasimKatsayisi * 100.0) / 100.0;
    }

    @Override
    public String getStratejiAdi() {
        return "Çok Kriterli Ağırlıklı Aciliyet Algoritması";
    }
}