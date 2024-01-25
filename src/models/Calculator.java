package src.models;

import java.util.ArrayList;
import java.util.List;

public class Calculator {
    private int nLimit;
    private int nThreads;

    /**
     * Default Constructor for Calculator Object
     * @param nLimit - The upper bound integer in looking for Prime Numbers.
     */
    public Calculator(int nLimit) {
        this.nLimit = nLimit;
        this.nThreads = 1;
    }

    /**
     * Constructor for Calculator Object
     * @param nLimit - The upper bound integer in looking for Prime Numbers.
     * @param nThreads - Number of Threads allowed to use.
     */
    public Calculator(int nLimit, int nThreads) {
        this.nLimit = nLimit;
        this.nThreads = nThreads;
    }

    /**
     * A special function that identifies different divisions to be given
     * to different threads later on.
     * 
     * @return an ArrayList with the start, divisions and the last number
     */
    private List<Integer> getDivisions() {
        List<Integer> divisions = new ArrayList<Integer>();

        // Calculate the size of each part
        int partSize = nLimit / nThreads;

        // Initialize the starting point of the range
        int startRange = 1;

        // Loop through the number of parts
        for (int i = 0; i < nThreads; i++) {
            // Add the start point of the range to the list
            divisions.add(startRange);

            // Update the starting point for the next range
            startRange = startRange + partSize;
        }

        divisions.add(startRange - 1);

        return divisions;
    }

    /**
     * The main function that determines the Prime numbers given the limit and threads
     * @return ArrayList of Prime Numbers
     */
    public List<Integer> execute() {
        List<Integer> primes = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        // Identify the Divisions
        List<Integer> divisions = getDivisions();

        // Delegate those Divisions and Create Threads
        for (int i = 0; i < divisions.size() - 1; i++) {
            final int startRange = divisions.get(i);
            final int endRange = (i + 1 == divisions.size() - 1) ? divisions.get(i + 1) : divisions.get(i + 1) - 1;

            // Create a new thread for each range
            Thread thread = new Thread(() -> {
                List<Integer> threadPrimes = new PrimeChecker(startRange, endRange).seek();
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
}
