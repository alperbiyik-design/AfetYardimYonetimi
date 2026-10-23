package afet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Verileri disk üzerinde CSV formatında kalıcı olarak saklayan sınıf (SRP & DIP).
 */
public class DosyaVeriDeposu implements VeriDeposu {
    private final String klasorYolu;
    private final String kaynakDosyasi;
    private final String bolgeDosyasi;
    private final String dagitimDosyasi;

    public DosyaVeriDeposu(String klasorYolu) {
        this.klasorYolu = klasorYolu;
        File klasor = new File(klasorYolu);
        if (!klasor.exists()) {
            klasor.mkdirs();
        }
        this.kaynakDosyasi = klasorYolu + File.separator + "kaynaklar.csv";
        this.bolgeDosyasi = klasorYolu + File.separator + "bolgeler.csv";
        this.dagitimDosyasi = klasorYolu + File.separator + "dagitim_gecmisi.csv";
    }

    @Override
    public void kaynaklariKaydet(List<YardimKaynagi> kaynaklar) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(kaynakDosyasi), StandardCharsets.UTF_8))) {
            for (YardimKaynagi k : kaynaklar) {
                writer.write(k.toCsvSatiri());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Kaynaklar dosyaya kaydedilirken hata oluştu: " + e.getMessage());
        }
    }

    @Override
    public List<YardimKaynagi> kaynaklariYukle() {
        List<YardimKaynagi> liste = new ArrayList<>();
        File f = new File(kaynakDosyasi);
        if (!f.exists()) {
            return liste;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String satir;
            while ((satir = reader.readLine()) != null) {
                if (!satir.trim().isEmpty()) {
                    liste.add(KaynakFabrikasi.csvSatirindanOlustur(satir));
                }
            }
        } catch (Exception e) {
            System.err.println("Kaynaklar dosyadan okunurken hata oluştu: " + e.getMessage());
        }
        return liste;
    }

    @Override
    public void bolgeleriKaydet(List<AfetBolgesi> bolgeler) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(bolgeDosyasi), StandardCharsets.UTF_8))) {
            for (AfetBolgesi b : bolgeler) {
                writer.write(b.toCsvSatiri());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Bölgeler dosyaya kaydedilirken hata oluştu: " + e.getMessage());
        }
    }

    @Override
    public List<AfetBolgesi> bolgeleriYukle() {
        List<AfetBolgesi> liste = new ArrayList<>();
        File f = new File(bolgeDosyasi);
        if (!f.exists()) {
            return liste;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String satir;
            while ((satir = reader.readLine()) != null) {
                if (!satir.trim().isEmpty()) {
                    liste.add(AfetBolgesi.csvSatirindanOlustur(satir));
                }
            }
        } catch (Exception e) {
            System.err.println("Bölgeler dosyadan okunurken hata oluştu: " + e.getMessage());
        }
        return liste;
    }

    @Override
    public void dagitimKayitlariniKaydet(List<DagitimKaydi> kayitlar) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(dagitimDosyasi), StandardCharsets.UTF_8))) {
            for (DagitimKaydi d : kayitlar) {
                writer.write(d.toCsvSatiri());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Dağıtım kayıtları dosyaya kaydedilirken hata oluştu: " + e.getMessage());
        }
    }

    @Override
    public List<DagitimKaydi> dagitimKayitlariniYukle() {
        List<DagitimKaydi> liste = new ArrayList<>();
        File f = new File(dagitimDosyasi);
        if (!f.exists()) {
            return liste;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String satir;
            while ((satir = reader.readLine()) != null) {
                if (!satir.trim().isEmpty()) {
                    liste.add(DagitimKaydi.csvSatirindanOlustur(satir));
                }
            }
        } catch (Exception e) {
            System.err.println("Dağıtım kayıtları dosyadan okunurken hata oluştu: " + e.getMessage());
        }
        return liste;
    }
}