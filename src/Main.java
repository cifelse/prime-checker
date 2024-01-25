/**
 * A Simple Java Program to showcase the advantages of having multiple threads to accomplish a task. 
 * @author Louis Lemsic, Amanda Perez
 * @version 0.1.0
 */
package src;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import src.models.Calculator;

public class Main {
    /**
     * The main function for getting an input from the user
     * @param scanner - the Scanner Object
     * @param prompt - question to prompt to the user
     * @return the input of the user
     */
    public static int getUserInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextInt();
    }

    public static void main(String[] args) {
        // User Input Phase
        Scanner sc = new Scanner(System.in);

        int nLimit = getUserInput(sc, "Enter upper bound of integers to check: ");
        int nThreads = getUserInput(sc, "Enter number of threads: ");

        // Close the Scanner
        sc.close();

        long startTime = System.nanoTime();

        // Calculation Proper
        new Calculator(nLimit, nThreads).execute();

        long endTime = System.nanoTime();

        // Display Time
        System.out.printf("Total Runtime: %d milliseconds.\n", (long) TimeUnit.NANOSECONDS.toMillis(endTime - startTime));
    }
}