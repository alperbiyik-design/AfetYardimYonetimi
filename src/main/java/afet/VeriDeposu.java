package afet;

import java.util.List;

/**
 * Kalıcı Veri Saklama Soyutlaması (SOLID - Dependency Inversion Principle).
 * Üst seviye iş mantığı sınıfları doğrudan dosya sistemine değil, bu arayüze bağımlıdır.
 */
public interface VeriDeposu {
    void kaynaklariKaydet(List<YardimKaynagi> kaynaklar);
    List<YardimKaynagi> kaynaklariYukle();

    void bolgeleriKaydet(List<AfetBolgesi> bolgeler);
    List<AfetBolgesi> bolgeleriYukle();

    void dagitimKayitlariniKaydet(List<DagitimKaydi> kayitlar);
    List<DagitimKaydi> dagitimKayitlariniYukle();
}
