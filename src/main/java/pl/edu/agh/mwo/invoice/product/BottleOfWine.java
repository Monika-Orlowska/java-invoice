package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

// Konkretne klasy produktów
public class BottleOfWine extends ExciseProduct {
    public BottleOfWine(String name, BigDecimal price) {
        super(name, price, new BigDecimal("0.23")); // VAT 23%
    }
}
