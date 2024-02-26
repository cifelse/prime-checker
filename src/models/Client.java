package src.models;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    // Hostname of the Server
    private static final String HOSTNAME = "localhost";

    // Port of the Server
    private static final int PORT = 8000;

    // Create a Console
    private static final Console console = new Console("Client");

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

    public static void main(String[] args) throws IOException {
        // Clear the console
        console.clear();

        // User Input
        Scanner sc = new Scanner(System.in);

        int start = getUserInput(sc, "Enter start range: ");
        int end = getUserInput(sc, "Enter end range: ");
        int threads = getUserInput(sc, "Enter thread count: ");

        sc.close();

        // Clear the console
        console.clear();

        console.log("Sending the parameters [" + start + ", " + end + ", " + threads + "] to the server.");

        // Create a socket
        Socket socket = new Socket(HOSTNAME, PORT);

        // Create input and output streams
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());

        // Receive a response from the server
        String response = in.readUTF();
        System.out.println(response);

        // Send the instructions to the server
        out.writeUTF(start + " " + end + " " + threads);
        out.flush();

        // Wait the confirmation from the server
        response = in.readUTF();
        System.out.println(response);

        // Wait for the answer from the server
        response = in.readUTF();
        System.out.println(response);

        // Send the END signal to the server
        out.writeUTF("END");
        out.flush();

        // Close the connection
        socket.close();
    }
}
