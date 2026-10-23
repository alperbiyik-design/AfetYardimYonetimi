package afet;

/**
 * Somut Strateji 2: Yaralı ve Hassas Grup (Bebek/Yaşlı/Engelli) Odaklı Öncelik Algoritması.
 * Tıbbi ve insani koruma odaklı operasyonlarda çalışma zamanında (runtime) seçilebilir.
 */
public class HassasGrupOdakliStrateji implements OncelikHesaplamaStratejisi {

    @Override
    public double oncelikPuaniHesapla(AfetBolgesi bolge) {
        double yaraliAgirligi = bolge.getYaraliSayisi() * 0.35;
        double hassasAgirligi = bolge.getHassasGrupSayisi() * 0.25;
        double yikimAgirligi = bolge.getYikimDerecesi() * 8.0;
        double beklemeAgirligi = bolge.getBeklemeSuresiSaat() * 3.0;

        double toplam = yaraliAgirligi + hassasAgirligi + yikimAgirligi + beklemeAgirligi;
        return Math.round(toplam * 100.0) / 100.0;
    }

    @Override
    public String getStratejiAdi() {
        return "Yaralı ve Hassas Grup Odaklı Öncelik Algoritması";
    }
}
