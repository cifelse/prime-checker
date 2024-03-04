package src.models;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Client {
    // Hostname of the Server
    private static final String HOSTNAME = "localhost";

    // Port of the Server
    private static final int PORT = 8000;

    // Create a Console
    private static final Console console = new Console("Client");

    public static void main(String[] args) throws IOException {
        int start = console.input("Enter start range: ").nextInt();
        int end = console.input("Enter end range: ").nextInt();
        int threads = console.input("Enter thread count: ").nextInt();

        // Clear the console
        console.clear();

        // 
        console.log("Sending the parameters [" + start + ", " + end + ", " + threads + "] to the server.\n");

        // Create a socket
        Socket socket = new Socket(HOSTNAME, PORT);

        // Create input and output streams
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());

        // Receive the first response from the server (Connection Success)
        System.out.println(in.readUTF());

        // Send the instructions to the server
        out.writeUTF(start + " " + end + " " + threads);
        out.flush();

        // Receive the second response from the server (Param Confirmation)
        System.out.println(in.readUTF());

        // Receive the third response from the server (Done Computation)
        System.out.println(in.readUTF());

        // Receive the total prime numbers from the server (Sending Commence)
        int total = in.readInt();

        // Receive the Prime Numbers
        ArrayList<Integer> primes = new ArrayList<Integer>();
        
        // While the Boolean signal is True
        while (in.readBoolean()) {
            // Receive the size of the ArrayList
            int size = in.readInt();

            // Receive the byte array
            byte[] byteBatch = new byte[size * 4];
            in.readFully(byteBatch);

            // Convert byte array back to ArrayList of Integers
            for (int i = 0; i < size; i++) {
                int value = ByteBuffer.wrap(byteBatch, i * 4, 4).getInt();
                primes.add(value);
            }
        
            console.clear();
            console.log("There are " + total + " Prime Numbers from " + start + " to " + end + ".");
            console.log("Downloading " + primes.size() + "/" + total + " Prime Numbers.");
        }

        // Send the END signal to the server
        out.writeUTF("END");
        out.flush();

        console.log("There are " + primes.size() + " Prime Numbers from " + start + " to " + end + ".");

        // Show the Prime Numbers if the user answered Y
        String choice = console.input("Download successful. Shall I proceed to show them? [Y/n]: ").next();

        if (!choice.isEmpty() && (choice.contains("Y") || choice.contains("y"))) {
            console.clear();
            console.log(primes);
        }

        // Close the connection
        socket.close();
    }
}
