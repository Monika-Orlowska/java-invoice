package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

// Klasa bazowa dla produktów z akcyzą
public abstract class ExciseProduct extends Product {
    private static final BigDecimal EXCISE_TAX = new BigDecimal("5.56");

    public ExciseProduct(String name, BigDecimal price, BigDecimal taxPercent) {
        super(name, price, taxPercent);
    }

    @Override
    public BigDecimal getPriceWithTax() {
        return super.getPriceWithTax().add(EXCISE_TAX);
    }

    public BigDecimal getExciseTax() {
        return EXCISE_TAX;
    }
}

