package src.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Slave {
    // Hostname of the Server
    private static final String HOSTNAME = "localhost";

    // Port of the Server
    private static final int PORT = 12345;

    // Name of the Slave
    private String name;

    // Create input and output streams
    private final DataInputStream in;
    private final DataOutputStream out;

    // Create a socket
    private final Socket socket;

    // Create a Console
    private final Console console;

    public Slave(String hostname, int port) throws IOException {
        // Create a socket
        this.socket = new Socket(hostname, port);

        // Set the name of the Slave according to the Local Socket Address
        this.name = "Slave " + String.valueOf(this.socket.getLocalSocketAddress()).split(":")[1];

        this.console = new Console(name);

        // Create input and output streams
        this.in = new DataInputStream(this.socket.getInputStream());
        this.out = new DataOutputStream(this.socket.getOutputStream());

        try {
            // Broadcast connection to server
            broadcast("Ready for work!");

            // Receive confirmation
            console.log(in.readUTF());

            while (true) {
                // Read data from the server
                String message = in.readUTF();

                // If Instruction received is END, then break the loop
                if (message.equals("END")) break;

                // Split the message into start, end, and thread
                String[] range = message.split(" ");
                int start = Integer.parseInt(range[0]);
                int end = Integer.parseInt(range[1]);
                int thread = Integer.parseInt(range[2]);

                // Log the message
                console.log("Received instructions to compute the prime numbers from " + start + " to " + end + " using " + thread + " threads.");

                // Compute the Prime Numbers
                compute(start, end, thread);

                // Log completion
                console.log("Computation completed. Waiting for new instructions again.");
            }
        }
        catch (Exception e) {
            // Check if the server is dead
            if (e instanceof java.io.EOFException) {
                console.log("Our Master freaking died! Dobby is a free elf!");
            }
            else {
                console.log(e.getMessage());
            }
        }
    }

    /**
     * Broadcast a message to the server
     * @param message - the message to broadcast
     * @throws IOException 
     */
    public void broadcast(String message) throws IOException {
        out.writeUTF("\n[" + name + "]: " + message + "\n");
        out.flush();
    }

    public void broadcast(int number) throws IOException {
        out.writeInt(number);
        out.flush();
    }

    public void broadcast(boolean status) throws IOException {
        out.writeBoolean(status);
        out.flush();
    }

    /**
     * Compute the Prime Numbers from start to end
     * @param start - Start range
     * @param end - end Range
     * @param threads - number of threads
     */
    public void compute(int start, int end, int threads) throws IOException {
        ArrayList<Integer> primes = new Calculator(start, end, threads).execute();

        console.log("I got the prime numbers!");

        // Send the Results to the Server. one prime at a time
        for (int i = 0; i < primes.size(); i++) {
            // By broadcasting true, you are telling the server that the next number is a prime number
            broadcast(true);
            broadcast(primes.get(i));
        }

        // By broadcasting false, you are telling the server that the prime numbers are done
        broadcast(false);
    }

    public static void main(String[] args) throws IOException {
        // Clear the console
        new Console().clear();
        
        // Create a Slave
        new Slave(Slave.HOSTNAME, Slave.PORT);
    }
}
