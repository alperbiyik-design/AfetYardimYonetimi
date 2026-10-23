package afet;

/**
 * Tasarım Kalıbı 3: Observer Pattern (Gözlemci Tasarım Kalıbı Arayüzü).
 * Yardım dağıtımı gerçekleştiğinde veya kritik stok uyarısı oluştuğunda tetiklenir.
 */
public interface DagitimGozlemcisi {
    void dagitimGerceklesti(DagitimKaydi kayit);
    void kritikStokUyarisi(YardimKaynagi kaynak);
}
