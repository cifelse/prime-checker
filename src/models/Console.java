/**
 * A custom Console class to easily access the cmd/console
 * @author Louis Lemsic
 * @version 1.0.0
 */

package src.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Console class for easy logging, user input, and clearing the console.
 */
public class Console {
    // Name of the Console
    private String name;

    // Scanner for User Inputs in the Console
    private final Scanner sc;

    // Time Tracker
    private long millis;

    /**
     * Default Console Constructor
    */
    public Console() {
        this.name = "";
        this.sc = new Scanner(System.in);
        this.millis = System.nanoTime();
        
        try {
            clear();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Console Constructor with name
    * @param name - the name of the Console
    */
    public Console(String name) {
        this.name = name;
        this.sc = new Scanner(System.in);
        this.millis = System.nanoTime();
        
        try {
            clear();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Console Constructor with name and scanner
    * @param name - the name of the Console
    * @param sc - the Scanner Object
    */
    public Console(String name, Scanner sc) {
        this.name = name;
        this.sc = sc;
        this.millis = System.nanoTime();
        
        try {
            clear();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the name of the Console
    * @return the name of the Console
    */
    public String getName() {
        return this.name;
    }

    /**
     * Set the name of the Console
    */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The main function for getting an input from the user
    * @param prompt - question to prompt to the user
    * @return the input of the user
    */
    public Scanner input(String prompt) {
        System.out.print(formatMessage(prompt));
        return this.sc;
    }

    /**
     * Start the internal timer for tracking
    */
    public void startTime() {
        this.millis = System.nanoTime();
    }

    /**
     * End the internal timer for tracking and return the result
    * @return - time elapsed
    */
    public String endTime() {
        long time = (long) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - this.millis);

        return time < 10000 ? time + " milliseconds" : TimeUnit.MILLISECONDS.toSeconds(time) + " seconds";
    }

    /**
     * Clear the console
    */
    public void clear() throws IOException {
        System.out.print("\033[H\033[2J");
        Runtime.getRuntime().exec("clear");
    }

    /**
     * Create a breakline
    */
    public void log() {
        System.out.println();
    }

    /**
     * Logs a message to the console with optional formatting.
    * @param message - The message to log.
    * @param args - Arguments for string formatting (similar to printf).
    */
    public void log(String message, Object... args) {
        System.out.printf("%s\n", String.format(formatMessage(message), args));
    }

    /**
     * Logs a value of any data type to the console.
    * @param value - The value to log.
    */
    public void log(Object value) {
        System.out.print(formatMessage((String) value) + "\n");
    }

    /**
     * Logs the values of an ArrayList
    * @param al - ArrayList
    */
    public void log(ArrayList<Integer> al) {
        System.out.println(al.stream().map(Object::toString).collect(Collectors.joining(", ")));
    }

    /**
     * Logs the values of an ArrayList
    * @param al - ArrayList
    * @param delimiter - the string that separates each element of the ArrayList when logging
    */
    public void log(ArrayList<Integer> al, String delimiter) {
        System.out.println(al.stream().map(Object::toString).collect(Collectors.joining(delimiter)));
    }

    /**
     * The main function that is responsible in the format of the console logs
    * @param message String to be formatted
    * @return - the formatted String
    */
    private String formatMessage(String message) {
        if (message.length() > 0 && message.charAt(0) == '[')
            return message;

        String header = this.name.length() < 1 ? "" : "[" + name + "]: ";

        // Only check \n in the first element of the string:
        if (message.length() > 0 && message.charAt(0) == '\n')
            return (message.contains("\n") ? "\n" + header : header) + message.replaceFirst("\n", "");

        return header + message;
    }
}