package afet;

/**
 * Tasarım Kalıbı 1: Factory Pattern (Fabrika Tasarım Kalıbı).
 * Kategori koduna veya CSV satırına göre uygun YardimKaynagi alt sınıfını üretir.
 * SOLID - Open/Closed ve Single Responsibility prensiplerini destekler.
 */
public class KaynakFabrikasi {

    public static YardimKaynagi kaynakOlustur(String kategoriKodu, String id, String ad,
                                              int stok, String birim, int kritikEsik,
                                              String ozelParametre1, String ozelParametre2) {
        if (kategoriKodu == null) {
            throw new IllegalArgumentException("Kategori kodu boş olamaz!");
        }
        String tip = kategoriKodu.trim().toUpperCase();
        switch (tip) {
            case "MEDIKAL":
                boolean sogukZincir = Boolean.parseBoolean(ozelParametre1.trim());
                int aciliyet = Integer.parseInt(ozelParametre2.trim());
                return new MedikalKaynak(id, ad, stok, birim, kritikEsik, sogukZincir, aciliyet);

            case "GIDA":
                int kalori = Integer.parseInt(ozelParametre1.trim());
                boolean bozulabilir = Boolean.parseBoolean(ozelParametre2.trim());
                return new GidaKaynagi(id, ad, stok, birim, kritikEsik, kalori, bozulabilir);

            case "BARINMA":
                int kapasite = Integer.parseInt(ozelParametre1.trim());
                boolean kisaUygun = Boolean.parseBoolean(ozelParametre2.trim());
                return new BarinmaKaynagi(id, ad, stok, birim, kritikEsik, kapasite, kisaUygun);

            case "ISINMA":
                int koruma = Integer.parseInt(ozelParametre1.trim());
                String enerji = ozelParametre2.trim();
                return new IsinmaKaynagi(id, ad, stok, birim, kritikEsik, koruma, enerji);

            default:
                throw new IllegalArgumentException("Geçersiz kaynak kategorisi: " + kategoriKodu);
        }
    }

    public static YardimKaynagi csvSatirindanOlustur(String csvSatiri) {
        String[] parcalar = csvSatiri.split(";");
        if (parcalar.length < 8) {
            throw new IllegalArgumentException("Hatalı CSV kaynak satırı: " + csvSatiri);
        }
        return kaynakOlustur(
                parcalar[0],
                parcalar[1],
                parcalar[2],
                Integer.parseInt(parcalar[3].trim()),
                parcalar[4],
                Integer.parseInt(parcalar[5].trim()),
                parcalar[6],
                parcalar[7]
        );
    }
}