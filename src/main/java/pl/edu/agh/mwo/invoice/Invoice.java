package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
    private final Map<Product, Integer> products = new HashMap<>();
    private String invoiceNumber;

    // Metoda generująca listę produktów
    public String getProductListAsString() {
        StringBuilder result = new StringBuilder();
        String lineSeparator = System.lineSeparator();

        result.append("Invoice number: ")
                .append(invoiceNumber != null ? invoiceNumber : "Not assigned")
                .append(lineSeparator);

        products.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().getName()))
                .forEach(entry -> {
                    Product product = entry.getKey();
                    BigDecimal price = product.getPrice().setScale(2, RoundingMode.HALF_UP);
                    result.append(String.format(
                            "%s, Quantity: %d, Price: %s%s",
                            product.getName(),
                            entry.getValue(),
                            price,
                            lineSeparator
                    ));
                });

        int totalItems = products.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        result.append("Number of items: ").append(totalItems);
        return result.toString();
    }

    // Pozostałe metody bez zmian
    public void addInvoiceNumber(String invoiceNumber) {
        if (this.invoiceNumber != null) {
            throw new IllegalStateException("Invoice number has already been set.");
        }
        if (invoiceNumber == null || invoiceNumber.isEmpty()) {
            throw new IllegalArgumentException("Invoice number cannot be null or empty.");
        }
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void addProduct(Product product) {
        addProduct(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException();
        }
        products.put(product, quantity);
    }

    public BigDecimal getNetTotal() {
        BigDecimal totalNet = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalNet = totalNet.add(product.getPrice().multiply(quantity));
        }
        return totalNet;
    }

    public BigDecimal getTaxTotal() {
        return getGrossTotal().subtract(getNetTotal());
    }

    public BigDecimal getGrossTotal() {
        BigDecimal totalGross = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalGross = totalGross.add(product.getPriceWithTax().multiply(quantity));
        }
        return totalGross;
    }
}
