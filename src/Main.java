package src;

import java.util.List;
import src.models.PrimeChecker;

public class Main {
    public static void main (String[] args) {
        PrimeChecker pc = new PrimeChecker(10000000, 0);

        List<Integer> primes = pc.run();

        System.out.printf("%d primes were found.\n", primes.size());
    }
}