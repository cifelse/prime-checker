package src.models;

import java.util.ArrayList;
import java.util.List;

public class Calculator {
    private int start;
    private int end;
    private int nThreads;

    /**
     * Default Constructor for Calculator Object
     */
    public Calculator() {
        this.nThreads = 1;
    }

    /**
     * Constructor for Calculator Object with custom Threads.
     * @param start - the starting range
     * @param end - the ending range
     * @param threads - the number of threads
     */
    public Calculator(int start, int end, int threads) {
        this.start = start;
        this.end = end;
        this.nThreads = threads;
    }

    /**
     * Constructor for Calculator Object with custom Threads.
     * @param nThreads - Number of Threads allowed to use.
     */
    public Calculator(int nThreads) {
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
        
        // Initialize the starting point of the range
        int startRange = this.start;

        int count = this.end - this.start  + 1; 

        // Calculate the size of each part
        int partSize = (int) Math.ceil((float)(this.end - startRange) / (float)this.nThreads);
        
        // Loop through the number of parts
        for (int i = 0; i < nThreads; i++) {
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
    private ArrayList<SubRange> getSubRanges() {
        ArrayList<SubRange> subRanges = new ArrayList<SubRange>();
        
        // Initialize the starting point of the range
        int startRange = this.start;

        // Calculate the interval of each part
        int interval = (this.end - startRange + 1) / this.nThreads;

        // Loop through the number of parts
        for (int i = 0; i < nThreads - 1; i++) {
            // Add the start point of the range to the list
            subRanges.add(new SubRange(startRange, startRange + interval));

            // Update the starting point for the next range
            startRange += interval;
        }

        // Add the last part
        subRanges.add(new SubRange(startRange, this.end));

        return subRanges;
    }

    // // Identify the Divisions
    // ArrayList<SubRange> subRanges = getSubRanges();

    // // Delegate those Divisions and Create Threads
    // for (int i = 0; i < subRanges.size() - 1; i++) {
    //     // Place in a separate variable to avoid the Thread-related final variable problem
    //     int start = subRanges.get(i).start;
    //     int end = subRanges.get(i).end;

    //     // Create a new thread for each range
    //     Thread thread = new Thread(() -> {
    //         List<Integer> threadPrimes = new PrimeChecker(start, end).seek();
    //         // Thread-safe access to primes
    //         synchronized (primes) {
    //             primes.addAll(threadPrimes);
    //         }
    //     });

    //     threads.add(thread);
    //     thread.start();
    // }

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
}
