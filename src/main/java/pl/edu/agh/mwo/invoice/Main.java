package pl.edu.agh.mwo.invoice;
import pl.edu.agh.mwo.invoice.product.InvoiceNumber;
import pl.edu.agh.mwo.invoice.product.OtherProduct;
import pl.edu.agh.mwo.invoice.product.TaxFreeProduct;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        // Tworzenie generatora numerów faktur
        InvoiceNumber generator = new InvoiceNumber();

        // Tworzenie faktury
        Invoice invoice = new Invoice();
        invoice.addInvoiceNumber(generator.generateNumber());

        TaxFreeProduct laptop = new TaxFreeProduct("Laptop", new BigDecimal("4500.00"));
        OtherProduct mysz = new OtherProduct("Mysz bezprzewodowa", new BigDecimal ("120.50"));

        invoice.addProduct(laptop, 2);
        invoice.addProduct(mysz, 3);

        // Drukowanie faktury
        System.out.println("====================================");
        System.out.println(invoice.getProductListAsString());
        System.out.println("====================================");
        System.out.printf("Netto: %.2f zł%n", invoice.getNetTotal());
        System.out.printf("VAT: %.2f zł%n", invoice.getTaxTotal());
        System.out.printf("Brutto: %.2f zł%n", invoice.getGrossTotal());
        System.out.println("====================================");
    }
}
