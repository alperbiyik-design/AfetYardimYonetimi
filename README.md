# Afet ve Acil Durum Yönetimi: Yardım Kaynaklarının Öncelik ve İhtiyaç Durumuna Göre Dağıtılması

Bu proje, **Nesneye Yönelik Programlama (OOP)** dersi proje ödevi kapsamında geliştirilmiş konsol tabanlı bir Java uygulamasıdır. Afet sonrasında farklı yıkım derecesine, yaralı sayısına, hassas nüfusa ve ulaşım durumuna sahip afet bölgelerinin aciliyet puanlarını çok kriterli algoritmalarla hesaplar ve depodaki yardım kaynaklarını (**Medikal, Gıda, Barınma, Isınma**) öncelik sırasına göre adil ve izlenebilir biçimde dağıtır.

## 1. Proje Gereksinimleri ve Karşılanma Tablosu

| Ödev Kuralı | Projedeki Karşılığı |
| :--- | :--- |
| **Konsol Uygulaması ve Kalıcı Veri** | `Main.java` üzerinden etkileşimli konsol menüsü; `DosyaVeriDeposu` ile `data/` klasörü altında CSV formatında (`kaynaklar.csv`, `bolgeler.csv`, `dagitim_gecmisi.csv`) otomatik kalıcı kayıt |
| **Sınıf, Nesne, Kalıtım, Soyutlama, Çok Biçimlilik, Kapsülleme** | `YardimKaynagi` soyut üst sınıfı ve ondan türeyen 4 alt sınıf (`MedikalKaynak`, `GidaKaynagi`, `BarinmaKaynagi`, `IsinmaKaynagi`); tüm alanlar `private` ve doğrulamalı `getter/setter` ile kapsüllenmiş |
| **En Az 4 SOLID Prensibi** | **5 SOLID prensibinin tamamı** (SRP, OCP, LSP, ISP, DIP) uygulanmıştır |
| **En Az 2 Tasarım Kalıbı** | **3 Tasarım Kalıbı** uygulanmıştır: **Strategy Pattern**, **Factory Pattern**, **Observer Pattern** |
| **En Az 8 Anlamlı Sınıf** | `afet` paketi altında **15 anlamlı sınıf/arayüz** + **1 test sınıfı** (Toplam 16 Java dosyası) |
| **En Az 8 Birim Testi** | `AfetYonetimTest.java` içerisinde **10 adet JUnit 5 birim testi** |
| **Projeye Uygun Algoritma** | Çok Kriterli Öncelik Sıralaması (Max-Priority) ve Etki Puanı Odaklı Kademeli Kaynak Tahsis Algoritması |

## 2. Uygulanan OOP İlkeleri

* **Soyutlama (Abstraction):** `YardimKaynagi` soyut sınıfı (`abstract class`) ve `OncelikHesaplamaStratejisi`, `VeriDeposu`, `DagitimGozlemcisi` arayüzleri (`interface`) ile sistemin temel sözleşmeleri soyutlanmıştır.
* **Kalıtım (Inheritance):** `MedikalKaynak`, `GidaKaynagi`, `BarinmaKaynagi` ve `IsinmaKaynagi` sınıfları `YardimKaynagi` soyut sınıfından kalıtım alır (`extends`).
* **Çok Biçimlilik (Polymorphism):** Her kaynak alt sınıfı `hesaplaBirimEtkiPuani()`, `getKategoriKodu()`, `getOzelDetay()` ve `toCsvSatiri()` metotlarını kendi özelliklerine göre ezer (`@Override`). Dağıtım esnasında en yüksek etkili kaynak seçilirken bu metotlar çok biçimli olarak çağrılır.
* **Kapsülleme (Encapsulation):** Tüm model sınıflarındaki alanlar `private` olarak tanımlanmış; negatif stok, negatif nüfus veya 1-10 aralığı dışındaki yıkım derecesi gibi hatalı veriler kurucu ve `setter` metotlarında engellenmiştir.

## 3. Uygulanan SOLID Prensipleri

1. **Single Responsibility Principle (SRP - Tek Sorumluluk):** `AfetBolgesi` yalnızca bölge verilerini, `DosyaVeriDeposu` yalnızca dosya okuma/yazma işlemlerini, `DagitimYoneticisi` yalnızca dağıtım koordinasyonunu, `AcilDurumBildirimcisi` ise yalnızca bildirim ve alarm üretimini üstlenir.
2. **Open/Closed Principle (OCP - Açık/Kapalı):** Sisteme yeni bir kaynak türü veya yeni bir öncelik hesaplama algoritması eklemek için mevcut `DagitimYoneticisi` kodu değiştirilmez; yalnızca `YardimKaynagi` veya `OncelikHesaplamaStratejisi` genişletilir.
3. **Liskov Substitution Principle (LSP - Liskov Yerine Geçme):** `MedikalKaynak`, `GidaKaynagi`, `BarinmaKaynagi` ve `IsinmaKaynagi` nesneleri, üst sınıf olan `YardimKaynagi` referansının beklendiği her yerde davranış bozukluğu olmadan birbirinin yerine kullanılabilir.
4. **Interface Segregation Principle (ISP - Arayüz Ayrımı):** Tek bir büyük arayüz yerine `OncelikHesaplamaStratejisi`, `DagitimGozlemcisi` ve `VeriDeposu` şeklinde amaca özgü ayrıştırılmış arayüzler kullanılmıştır.
5. **Dependency Inversion Principle (DIP - Bağımlılıkların Tersine Çevrilmesi):** Üst seviye `DagitimYoneticisi` sınıfı somut `DosyaVeriDeposu` veya somut strateji sınıflarına değil, `VeriDeposu` ve `OncelikHesaplamaStratejisi` arayüzlerine bağımlıdır (Constructor Injection).

## 4. Uygulanan Tasarım Kalıpları (Design Patterns)

1. **Strategy Pattern (Strateji Kalıbı):** `OncelikHesaplamaStratejisi` arayüzü ve `AgirlikliAciliyetStratejisi` ile `HassasGrupOdakliStrateji` sınıfları sayesinde afet bölgelerinin öncelik puanını hesaplayan algoritma çalışma zamanında (runtime) değiştirilebilir.
2. **Factory Pattern (Fabrika Kalıbı):** `KaynakFabrikasi` sınıfı, kullanıcı girdisinden veya CSV dosyasından okunan kategori koduna göre (`MEDIKAL`, `GIDA`, `BARINMA`, `ISINMA`) uygun alt sınıf nesnesini üretir.
3. **Observer Pattern (Gözlemci Kalıbı):** `DagitimGozlemcisi` arayüzü ve `AcilDurumBildirimcisi` sınıfı ile her sevkiyat yapıldığında ve bir kaynağın stoğu kritik eşiğin altına düştüğünde otomatik bildirim/alarm üretilir.

## 5. UML Sınıf Diyagramı

```mermaid
classDiagram
    direction TB

    class YardimKaynagi {
        <<abstract>>
        -String kaynakId
        -String ad
        -int stokMiktari
        -String birim
        -int kritikEsik
        +stokEkle(int miktar) void
        +stokDus(int miktar) boolean
        +kritikStokAltindaMi() boolean
        +getKategoriKodu()* String
        +hesaplaBirimEtkiPuani()* double
        +getOzelDetay()* String
        +toCsvSatiri()* String
    }

    class MedikalKaynak {
        -boolean sogukZincirGerekliMi
        -int acilMudahaleDerecesi
        +hesaplaBirimEtkiPuani() double
    }

    class GidaKaynagi {
        -int gunlukKaloriDegeri
        -boolean bozulabilirMi
        +hesaplaBirimEtkiPuani() double
    }

    class BarinmaKaynagi {
        -int kisiKapasitesi
        -boolean kisSartlarinaUygunMu
        +hesaplaBirimEtkiPuani() double
    }

    class IsinmaKaynagi {
        -int korumaDerecesi
        -String enerjiTuru
        +hesaplaBirimEtkiPuani() double
    }

    class KaynakFabrikasi {
        +kaynakOlustur()$ YardimKaynagi
        +csvSatirindanOlustur(String)$ YardimKaynagi
    }

    class AfetBolgesi {
        -String bolgeId
        -String bolgeAdi
        -int yikimDerecesi
        -int etkilenenNufus
        -int yaraliSayisi
        -int hassasGrupSayisi
        -boolean ulasimAcikMi
        -int beklemeSuresiSaat
        -Map~String, Integer~ ihtiyacListesi
        +ihtiyacBelirle(String, int) void
        +ihtiyacAzalt(String, int) void
    }

    class OncelikHesaplamaStratejisi {
        <<interface>>
        +oncelikPuaniHesapla(AfetBolgesi) double
        +getStratejiAdi() String
    }

    class AgirlikliAciliyetStratejisi {
        +oncelikPuaniHesapla(AfetBolgesi) double
    }

    class HassasGrupOdakliStrateji {
        +oncelikPuaniHesapla(AfetBolgesi) double
    }

    class DagitimGozlemcisi {
        <<interface>>
        +dagitimGerceklesti(DagitimKaydi) void
        +kritikStokUyarisi(YardimKaynagi) void
    }

    class AcilDurumBildirimcisi {
        -List~String~ bildirimGecmisi
        +dagitimGerceklesti(DagitimKaydi) void
        +kritikStokUyarisi(YardimKaynagi) void
    }

    class VeriDeposu {
        <<interface>>
        +kaynaklariKaydet(List~YardimKaynagi~) void
        +kaynaklariYukle() List~YardimKaynagi~
        +bolgeleriKaydet(List~AfetBolgesi~) void
        +bolgeleriYukle() List~AfetBolgesi~
        +dagitimKayitlariniKaydet(List~DagitimKaydi~) void
        +dagitimKayitlariniYukle() List~DagitimKaydi~
    }

    class DosyaVeriDeposu {
        -String klasorYolu
    }

    class DagitimKaydi {
        -String kayitId
        -String bolgeId
        -String kaynakId
        -int dagitilanMiktar
        -double bolgeOncelikPuani
        -String tarihSaat
    }

    class DagitimYoneticisi {
        -VeriDeposu veriDeposu
        -OncelikHesaplamaStratejisi strateji
        -List~DagitimGozlemcisi~ gozlemciler
        +bolgeleriOnceligeGoreSirala() List~AfetBolgesi~
        +oncelikliDagitimYap() List~DagitimKaydi~
    }

    YardimKaynagi <|-- MedikalKaynak
    YardimKaynagi <|-- GidaKaynagi
    YardimKaynagi <|-- BarinmaKaynagi
    YardimKaynagi <|-- IsinmaKaynagi
    KaynakFabrikasi ..> YardimKaynagi : üretir
    OncelikHesaplamaStratejisi <|.. AgirlikliAciliyetStratejisi
    OncelikHesaplamaStratejisi <|.. HassasGrupOdakliStrateji
    DagitimGozlemcisi <|.. AcilDurumBildirimcisi
    VeriDeposu <|.. DosyaVeriDeposu
    DagitimYoneticisi --> VeriDeposu
    DagitimYoneticisi --> OncelikHesaplamaStratejisi
    DagitimYoneticisi --> DagitimGozlemcisi
    DagitimYoneticisi --> YardimKaynagi
    DagitimYoneticisi --> AfetBolgesi
    DagitimYoneticisi --> DagitimKaydi