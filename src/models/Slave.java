package src.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Slave {

    private final DataInputStream in;
    private final DataOutputStream out;

    private final Socket socket;

    public Slave(String hostname, int port) throws IOException {
        // Create a socket
        this.socket = new Socket(hostname, port);

        // Create input and output streams
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        // Broadcast connection to server
        broadcast("Slave " + socket.getLocalSocketAddress() + " connected to server.");

        while (true) {
            // Read data from the server
            String message = in.readUTF();

            // If Instruction received is STOP, then break the loop
            if (message.equals("STOP")) break;

            String[] range = message.split(" ");

            int start = Integer.parseInt(range[0]);

            int end = Integer.parseInt(range[1]);

            // Compute the Prime Numbers
            compute(start, end);
        }
    }

    /**
     * Broadcast a message to the server
     * @param message - the message to broadcast
     * @throws IOException 
     */
    public void broadcast(String message) throws IOException {
        out.writeUTF(message);
        out.flush();
    }

    /**
     * Compute the Prime Numbers from start to end
     * @param start - Start range
     * @param end - end Range
     */
    public void compute(int start, int end) {
        ArrayList<Integer> primes = new Calculator(start, end).execute();

        System.out.println("I saw " + primes.size() + " prime numbers.");
    }

    public static void main(String[] args) throws IOException {
        // Set parameters
        String hostname = "localhost";
        int port = 8080;
        
        // Create a Slave
        new Slave(hostname, port);
    }
}
