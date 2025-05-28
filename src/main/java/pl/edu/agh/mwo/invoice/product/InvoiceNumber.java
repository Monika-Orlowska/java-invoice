package pl.edu.agh.mwo.invoice.product;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InvoiceNumber {
    private final Map<Integer, Set<Integer>> usedNumbers = new HashMap<>();

    public synchronized String generateNumber() {
        int year = LocalDate.now().getYear();
        Set<Integer> numbers = usedNumbers.computeIfAbsent(year, y -> new HashSet<>());

        int nextNumber = 1;
        while (numbers.contains(nextNumber)) {
            nextNumber++;
        }
        if (nextNumber > 9999) {
            throw new IllegalStateException("No more numbers available for year " + year);
        }
        numbers.add(nextNumber);

        return String.format("%04d/%04d", year, nextNumber);
    }

    // For testing and resetting the generator
    public void clear() {
        usedNumbers.clear();
    }
}
