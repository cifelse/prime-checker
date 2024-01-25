package src.models;

import java.util.ArrayList;
import java.util.List;

public class PrimeChecker {
    private int start;
    private int end;

    /**
     * Constructor
     * @param start - The starting range to look for the Prime Numbers.
     * @param end - The ending range to look for the Prime Numbers.
     */
    public PrimeChecker (int start, int end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Get the Prime Numbers from the start to end (set by the Constructor).
     * @return the list of prime numbers from 2 to the upper limit set in the constructor.
     */
    public List<Integer> seek() {
        List<Integer> primes = new ArrayList<Integer>();
        for (int current = this.start; current <= this.end; current++) {
            if (isPrime(current)) {
                primes.add(current);
            }
        }
        return primes;
    }

    /**
     * This function checks if an integer n is prime.
     * @param n - integer to check.
     * @return true if n is prime, and false otherwise.
     */
    public boolean isPrime (int n) {
        if (n == 0 || n == 1) return false;

        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }

        return true;
    }
}
