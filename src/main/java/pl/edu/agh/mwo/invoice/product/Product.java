package pl.edu.agh.mwo.invoice.product;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class Product {
    private final String name;

    private final BigDecimal price;

    private final BigDecimal taxPercent;

    protected Product(String name, BigDecimal price, BigDecimal tax)
            throws IllegalArgumentException {
        if (name == null || price == null || tax == null || name.isEmpty() || name.isBlank()) { //najpierw cos, co sie sprawdza szybko, potem cos, co dluzej
            throw new IllegalArgumentException("Wartosc nie moze byc null lub pusta.");
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cena nie moze byc ujemna.");
        }


        this.name = name;
        this.price = price;
        this.taxPercent = tax;
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public BigDecimal getTaxPercent() {
        return this.taxPercent;
    }

    public BigDecimal getPriceWithTax() {
       return this.price.multiply(this.getTaxPercent()).add(this.getPrice());
    }
}
