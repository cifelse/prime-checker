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
        broadcast("Slave " + socket.getLocalSocketAddress() + ": Ready for work!");

        // Receive confirmation
        System.out.println(in.readUTF());

        while (true) {
            // Read data from the server
            String message = in.readUTF();

            // If Instruction received is STOP, then break the loop
            if (message.equals("STOP")) break;

            String[] range = message.split(" ");

            int start = Integer.parseInt(range[0]);

            int end = Integer.parseInt(range[1]);

            int thread = Integer.parseInt(range[2]);

            // Compute the Prime Numbers
            compute(start, end, thread);
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
     * @param threads - number of threads
     */
    public void compute(int start, int end, int threads) {
        ArrayList<Integer> primes = new Calculator(start, end, threads).execute();

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
