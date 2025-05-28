package pl.edu.agh.mwo.invoice;

import pl.edu.agh.mwo.invoice.product.*;
import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        // Tworzenie generatora numerów faktur
        InvoiceNumber generator = new InvoiceNumber();

        // Tworzenie faktury
        Invoice invoice = new Invoice();
        invoice.addInvoiceNumber(generator.generateNumber());

        TaxFreeProduct laptop = new TaxFreeProduct("Laptop", new BigDecimal("4500.00"));
        OtherProduct mysz = new OtherProduct("Mysz bezprzewodowa", new BigDecimal("120.50"));
        BottleOfWine wine = new BottleOfWine("Wino czerwone", new BigDecimal("50.00"));
        FuelCanister fuel = new FuelCanister("Kanister benzyny", new BigDecimal("20.00"));
        FuelCanister fuel2 = new FuelCanister("Olej napędowy", new BigDecimal("35.80"));

        invoice.addProduct(wine, 12);
        invoice.addProduct(fuel, 7);
        invoice.addProduct(laptop, 2);
        invoice.addProduct(mysz, 4);
        invoice.addProduct(fuel2, 1);
        invoice.addProduct(wine, 4);

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
