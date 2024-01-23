package src.models;

import java.util.ArrayList;
import java.util.List;

public class PrimeChecker {
    private int nLimit;
    private int nThreads;

    /**
     * Constructor for Prime Checker
     * @param nLimit - Upper limit of the Integer
     * @param nThreads - Number of Threads
     */
    public PrimeChecker (int nLimit, int nThreads) {
        this.nLimit = nLimit;
        this.nThreads = nThreads;
    }

    /**
     * Get the Prime Numbers from 2 to o the upper limit set in the constructor without threading
     * @return the list of prime numbers from 2 to the upper limit set in the constructor
     */
    public List<Integer> run () {
        List<Integer> primes = new ArrayList<Integer>();

        for (int current_num = 2; current_num <= nLimit; current_num++) {
            if (this.isPrime(current_num)) {
                primes.add(current_num);
            }
        }

        return primes;
    }

    /**
     * Get the Prime Numbers from 2 to o the upper limit set in the constructor with multiple threads
     * @return the list of prime numbers from 2 to the upper limit set in the constructor
     */
    public List<Integer> runThreads() {
        // TODO: Add Threading Algorithm

        List<Integer> primes = new ArrayList<Integer>();

        for (int current_num = 2; current_num <= nLimit; current_num++) {
            if (this.isPrime(current_num)) {
                primes.add(current_num);
            }
        }

        return primes;
    }

    /**
     * This function checks if an integer n is prime.
     * @param n - integer to check
     * @return true if n is prime, and false otherwise.
     */
    public boolean isPrime (int n) {
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
