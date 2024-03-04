package src.models;

import java.util.ArrayList;
import java.util.List;

public class PrimeCalculator {
    private int start;
    private int end;
    private int threads;

    /**
     * Create a Calculator 
     */
    public PrimeCalculator() {
        this.start = 0;
        this.end = (int) Math.pow(10, 7);
        this.threads = 1;
    }

    /**
     * Constructor for Calculator Object with custom Threads.
     * @param threads - Number of Threads allowed to use.
     */
    public PrimeCalculator(int threads) {
        this.start = 0;
        this.end = (int) Math.pow(10, 7);
        this.threads = threads;
    }

    /**
     * Constructor for Calculator Object with custom Threads.
     * @param start - the starting range
     * @param end - the ending range
     * @param threads - the number of threads
     */
    public PrimeCalculator(int start, int end, int threads) {
        this.start = start;
        this.end = end;
        this.threads = threads;
    }

    /**
     * A special function that identifies different divisions to be given
     * to different threads later on.
     * 
     * @return an ArrayList with the start, divisions and the last number
     */
    private List<Integer> getDivisions() {
        List<Integer> divisions = new ArrayList<Integer>();
        
        // Initialize the starting point of the range
        int startRange = this.start;

        // Calculate the size of each part
        int partSize = (int) Math.ceil((float) (this.end - startRange) / (float) this.threads);
        
        // Loop through the number of parts
        for (int i = 0; i < threads; i++) {
            // Add the start point of the range to the list
            divisions.add(startRange);

            // Update the starting point for the next range
            startRange = startRange + partSize;
        }

        // Include the Last Number
        divisions.add(this.end);

        return divisions;
    }

    private class SubRange {
        public int start;
        public int end;

        public SubRange(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    /**
     * A special function that identifies different divisions to be given
     * to different threads later on.
     * 
     * @return an ArrayList with the start, divisions and the last number
     */
    public ArrayList<SubRange> getSubRanges() {
        ArrayList<SubRange> subRanges = new ArrayList<SubRange>();
        
        // Initialize the starting point of the range
        int startRange = this.start;

        // Calculate the interval of each part
        int interval = (this.end - startRange + 1) / this.threads;

        // Loop through the number of parts
        for (int i = 0; i < threads - 1; i++) {
            // Add the start point of the range to the list
            subRanges.add(new SubRange(startRange, startRange + interval));

            // Update the starting point for the next range
            startRange += interval;
        }

        // Add the last part
        subRanges.add(new SubRange(startRange, this.end));

        return subRanges;
    }

    /**
     * The main function that determines the Prime numbers given the end and threads
     * @return ArrayList of Prime Numbers
     */
    public ArrayList<Integer> execute() {
        ArrayList<Integer> primes = new ArrayList<>();
        ArrayList<Thread> threads = new ArrayList<>();

        // Identify the Divisions
        List<Integer> divisions = getDivisions();

        // Delegate those Divisions and Create Threads
        for (int i = 0; i < divisions.size() - 1; i++) {
            final int startRange = divisions.get(i);
            final int endRange = (i + 1 == divisions.size() - 1) ? divisions.get(i + 1) : divisions.get(i + 1) - 1;
            
            // Create a new thread for each range
            Thread thread = new Thread(() -> {
                List<Integer> threadPrimes = new PrimeChecker(startRange, endRange).seek();
                // Thread-safe access to primes
                synchronized (primes) {
                    primes.addAll(threadPrimes);
                }
            });

            threads.add(thread);
            thread.start();
        }

        // Wait for all Threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return primes;
    }

    public class PrimeChecker {
        private int start;
        private int end;
    
        /**
         * Constructor
         * @param start - The starting range to look for the Prime Numbers.
         * @param end - The ending range to look for the Prime Numbers.
         */
        public PrimeChecker(int start, int end) {
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
        private boolean isPrime(int n) {
            if (n == 0 || n == 1) return false;
    
            for (int i = 2; i * i <= n; i++) {
                if (n % i == 0) {
                    return false;
                }
            }
    
            return true;
        }
    }
}
