package src.models;

/**
 * Console class for easy logging and clearing the console.
 */
public class Console {
    // Name of the Console
    private String name;

    /**
     * Default Console Constructor
     */
    public Console() {
        this.name = "Console";
    }

    /**
     * Console Constructor with name
     * @param name - the name of the Console
     */
    public Console(String name) {
        this.name = name;
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
     * @param name - the name of the Console
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Clear the console
     */
    public void clear() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }

    /**
     * Log a message to the console
     * @param message - the message to log
     */
    public void log(String message) {
        System.out.println(message.indexOf("[") == 0 ? message : "[" + name + "]: " + message);
    }
}