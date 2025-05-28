package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public class ProductTest {
    @Test
    public void testProductNameIsCorrect() {
        Product product = new OtherProduct("buty", new BigDecimal("100.0"));
        Assert.assertEquals("buty", product.getName());
    }

    @Test
    public void testProductPriceAndTaxWithDefaultTax() {
        Product product = new OtherProduct("Ogorki", new BigDecimal("100.0"));
        Assert.assertThat(new BigDecimal("100"), Matchers.comparesEqualTo(product.getPrice()));
        Assert.assertThat(new BigDecimal("0.23"), Matchers.comparesEqualTo(product.getTaxPercent()));
    }

    @Test
    public void testProductPriceAndTaxWithDairyProduct() {
        Product product = new DairyProduct("Szarlotka", new BigDecimal("100.0"));
        Assert.assertThat(new BigDecimal("100"), Matchers.comparesEqualTo(product.getPrice()));
        Assert.assertThat(new BigDecimal("0.08"), Matchers.comparesEqualTo(product.getTaxPercent()));
    }

    @Test
    public void testPriceWithTax() {
        Product product = new DairyProduct("Oscypek", new BigDecimal("100.0"));
        Assert.assertThat(new BigDecimal("108"), Matchers.comparesEqualTo(product.getPriceWithTax()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProductWithNullName() {
        new OtherProduct(null, new BigDecimal("100.0"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProductWithEmptyName() {
        new TaxFreeProduct("", new BigDecimal("100.0"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProductWithNullPrice() {
        new DairyProduct("Banany", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProductWithNegativePrice() {
        new TaxFreeProduct("Mandarynki", new BigDecimal("-1.00"));
    }

    @Test
    public void testBottleOfWineTaxAndPriceWithTax() {
        Product wine = new BottleOfWine("Czerwone wino", new BigDecimal("50.00"));
        // Zakładamy: VAT 23% + akcyza 5.56 PLN
        Assert.assertThat(new BigDecimal("0.23"), Matchers.comparesEqualTo(wine.getTaxPercent()));
        // Cena z VAT: 50 * 1.23 = 61.50, z akcyzą: 61.50 + 5.56 = 67.06
        Assert.assertThat(new BigDecimal("67.06"), Matchers.comparesEqualTo(wine.getPriceWithTax().setScale(2, RoundingMode.HALF_UP)));
    }

    @Test
    public void testExciseProductAkcyzaIsAddedOnce() {
        Product wine = new BottleOfWine("Białe wino", new BigDecimal("100.00"));
        // Cena z VAT: 100 * 1.23 = 123.00, z akcyzą: 123.00 + 5.56 = 128.56
        Assert.assertThat(new BigDecimal("128.56"), Matchers.comparesEqualTo(wine.getPriceWithTax().setScale(2, RoundingMode.HALF_UP)));
    }

    @Test
    public void testFuelCanisterHasNoTaxNorExcise() {
        Product fuel = new FuelCanister("Kanister benzyny", new BigDecimal("20.00"));
        // Powinien mieć VAT 0%
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(fuel.getTaxPercent()));
        // Cena z podatkiem powinna być równa cenie netto
        Assert.assertThat(new BigDecimal("20.00"), Matchers.comparesEqualTo(fuel.getPriceWithTax()));
    }

}
