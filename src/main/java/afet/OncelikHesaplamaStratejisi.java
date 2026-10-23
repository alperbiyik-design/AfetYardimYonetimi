package afet;

/**
 * Tasarım Kalıbı 2: Strategy Pattern (Strateji Tasarım Kalıbı Arayüzü).
 * Afet bölgelerinin aciliyet/öncelik puanını hesaplayan algoritmalar için ortak sözleşme.
 * SOLID - Interface Segregation (ISP), Open/Closed (OCP) ve Dependency Inversion (DIP) ilkelerini sağlar.
 */
public interface OncelikHesaplamaStratejisi {
    double oncelikPuaniHesapla(AfetBolgesi bolge);
    String getStratejiAdi();
}
