package src.models;

import java.util.ArrayList;
import java.util.List;

public class Calculator {
    private int nLimit;
    private int nThreads;

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

    //TODO
    public List<Integer> execute() {
        List<Integer> divisions = getDivisions();

        System.out.println(divisions);

        return divisions;
    }
}
