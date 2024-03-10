/**
 * A Simple Java Program to showcase the advantages of having multiple threads to accomplish a task. 
 * @author Louis Lemsic, Amanda Perez, Justin Ayuyao
 * @version 0.2.0
 */

package src;

import java.util.ArrayList;

import src.models.Console;
import src.models.PrimeCalculator;

public class Main {
    public static void main(String[] args) {
        Console console = new Console();

        // User Input Phase
        int nThreads = console.input("Enter number of threads: ").nextInt();

        // Start Internal Time Tracker
        console.startTime();

        // Calculation Proper
        ArrayList<Integer> primes = new PrimeCalculator(nThreads).calculate();

        // Display Time and Results
        console.endTime();

        console.log("There are %d Prime Numbers found.\n", primes.size());
    }
}