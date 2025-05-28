package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import pl.edu.agh.mwo.invoice.product.*;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(Enclosed.class)
public class InvoiceTest {
    private Invoice invoice;

    @Before
    public void createEmptyInvoiceForTheTest() {
        invoice = new Invoice();
    }

    @Test
    public void testEmptyInvoiceHasEmptySubtotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTaxAmount() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithTwoDifferentProducts() {
        Product onions = new TaxFreeProduct("Warzywa", new BigDecimal("10"));
        Product apples = new TaxFreeProduct("Owoce", new BigDecimal("10"));
        invoice.addProduct(onions);
        invoice.addProduct(apples);
        Assert.assertThat(new BigDecimal("20"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithManySameProducts() {
        Product onions = new TaxFreeProduct("Warzywa", BigDecimal.valueOf(10));
        invoice.addProduct(onions, 100);
        Assert.assertThat(new BigDecimal("1000"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        Assert.assertThat(invoice.getNetTotal(), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasProperSubtotalForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasProperTaxValueForManyProduct() {
        // tax: 0
        invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
        // tax: 8
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
        // tax: 2.30
        invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testInvoiceHasProperTotalValueForManyProduct() {
        // price with tax: 200
        invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
        // price with tax: 108
        invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
        // price with tax: 12.30
        invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
        // 2x kubek - price: 10
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        // 3x kozi serek - price: 30
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        // 1000x pinezka - price: 10
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
        // 2x chleb - price with tax: 10
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        // 3x chedar - price with tax: 32.40
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        // 1000x pinezka - price with tax: 12.30
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("54.70"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithZeroQuantity() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithNegativeQuantity() {
        invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddingNullProduct() {
        invoice.addProduct(null);
    }

    public static class InvoiceNumberTest {
        private InvoiceNumber generator;

        @Before
        public void setUp() {
            generator = new InvoiceNumber();
        }

        @Test
        public void testGenerateNumber_FormatAndUniqueness() {
            String number1 = generator.generateNumber();
            String number2 = generator.generateNumber();

            String currentYear = String.valueOf(LocalDate.now().getYear());
            assertTrue(number1.startsWith(currentYear + "/"));
            assertTrue(number2.startsWith(currentYear + "/"));
            assertNotEquals(number1, number2);
            assertEquals(9, number1.length()); // 4 + 1 + 4 = 9
        }

        @Test
        public void testGenerateNumber_Sequential() {
            generator.clear();
            String number1 = generator.generateNumber();
            String number2 = generator.generateNumber();
            //Sequence accurate test:
            assertTrue(number1.endsWith("/0001"));
            assertTrue(number2.endsWith("/0002"));
        }

        @Test
        public void testGenerateNumber_MaxNumbersPerYear() {
            generator.clear();
            // Simulate generating 9999 numbers
            for (int i = 0; i < 9999; i++) {
                generator.generateNumber();
            }
            // Next call should throw
            assertThrows(IllegalStateException.class, generator::generateNumber);
        }

        @Test
        public void testAddInvoiceNumber_Success() {
            Invoice invoice = new Invoice();
            invoice.addInvoiceNumber("2025/0001");
            assertEquals("2025/0001", invoice.getInvoiceNumber());
        }

        @Test
       public void testAddInvoiceNumber_AlreadySet() {
            Invoice invoice = new Invoice();
            invoice.addInvoiceNumber("2025/0001");
            assertThrows(IllegalStateException.class, () -> invoice.addInvoiceNumber("2025/0002"));
        }

        @Test
        public void testAddInvoiceNumber_NullOrEmpty() {
            Invoice invoice = new Invoice();
            assertThrows(IllegalArgumentException.class, () -> invoice.addInvoiceNumber(null));
            assertThrows(IllegalArgumentException.class, () -> invoice.addInvoiceNumber(""));
        }
        @Test
        public void testGenerateNumber_OnlyDigitsAndSlash() {
            InvoiceNumber generator = new InvoiceNumber();
            String invoiceNumber = generator.generateNumber();

            // Regex: 4 digits, slash, 4 digits
            assertTrue("Invoice number should contain only digits and one slash in the correct position",
                    invoiceNumber.matches("^\\d{4}/\\d{4}$"));
        }
    }
    @Test //new method "GetProductListAsString tests
    public void testProductListFormatting() {
        // Given
        invoice.addInvoiceNumber("2025/0428");
        Product laptop = new TaxFreeProduct("Laptop", new BigDecimal("4500"));
        Product mouse = new OtherProduct("Mysz", new BigDecimal("120.50"));

        // When
        invoice.addProduct(laptop, 2);
        invoice.addProduct(mouse, 3);
        String result = invoice.getProductListAsString();

        // Then
        String expected = """
                Invoice number: 2025/0428
                Laptop, Quantity: 2, Price: 4500.00
                Mysz, Quantity: 3, Price: 120.50
                Number of items: 5
                """.trim();
        assertEquals(expected.replaceAll("\n", System.lineSeparator()), result);
    }


        @Test
    public void testProductListWithoutInvoiceNumber() {
        // When
        String result = invoice.getProductListAsString();

        // Then
        assertTrue(result.startsWith("Invoice number: Not assigned"));
    }

    //Border values for quantities
    @Test
    public void testMinimumQuantity() {
        // Given
        Product pen = new TaxFreeProduct("Długopis", new BigDecimal("5"));

        // When
        invoice.addProduct(pen, 1);

        // Then
        assertEquals(5, invoice.getNetTotal().intValue());
    }

    @Test
    public void testMaximumQuantity() {
        // Given
        Product pin = new TaxFreeProduct("Pinezka", new BigDecimal("0.01"));

        // When
        invoice.addProduct(pin, Integer.MAX_VALUE);

        // Then
        assertEquals(21474836.47, invoice.getNetTotal().doubleValue(), 0.01);
    }
    //Wyjątki

    @Test
    public void testAddProductNullValidationMessage() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> invoice.addProduct(null));
        assertEquals("Product cannot be null", exception.getMessage());
    }

    @Test
    public void testAddInvoiceNumberEmptyValidationMessage() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> invoice.addInvoiceNumber(""));
        assertEquals("Invoice number cannot be null or empty", exception.getMessage());
    }
    //
    @Test
    public void testTaxCalculationEdgeCase() {
        // Given
        Product zeroTaxProduct = new TaxFreeProduct("Test", new BigDecimal("100"));

        // When
        invoice.addProduct(zeroTaxProduct);

        // Then
        assertEquals(BigDecimal.ZERO, invoice.getTaxTotal());
    }

    @Test
    public void testGrossTotalWithDifferentTaxRates() {
        // Given
        Product p1 = new TaxFreeProduct("Bread", new BigDecimal("10"));
        Product p2 = new DairyProduct("Milk", new BigDecimal("5"));

        // When
        invoice.addProduct(p1);
        invoice.addProduct(p2);

        // Then
        assertEquals(new BigDecimal("15.40"), invoice.getGrossTotal().setScale(2, RoundingMode.HALF_UP));
    }

    //Duplicate tests
    public static class DuplicateProductInvoiceTest {
        private Invoice invoice;
        private Product laptop;

        @Before
        public void setUp() {
            invoice = new Invoice();
            laptop = new TaxFreeProduct("Laptop", new BigDecimal("4500.00"));
        }

        @Test
        public void testAddingSameProductTwice() {
            // Dodaj produkt dwukrotnie
            invoice.addProduct(laptop, 2);
            invoice.addProduct(laptop, 3);

            // Sprawdź, czy w outputcie jest tylko jedna pozycja z ilością 5
            String output = invoice.getProductListAsString();
            assertTrue(output.contains("Laptop") && output.contains("Quantity: 5"));

            // Sprawdź sumę netto: 5 * 4500.00 = 22500.00
            assertEquals(new BigDecimal("22500.00"), invoice.getNetTotal());
        }
    }
    //Excise tests
    @Test
    public void testExciseTaxCalculation() {
        BottleOfWine wine = new BottleOfWine("Testowe wino", new BigDecimal("100.00"));
        Invoice invoice = new Invoice();
        invoice.addProduct(wine, 1);

        BigDecimal expectedTax = new BigDecimal("23.00").add(wine.getExciseTax());
        assertEquals(expectedTax, invoice.getTaxTotal());
    }



}
