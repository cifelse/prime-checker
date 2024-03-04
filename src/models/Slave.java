package src.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Slave implements Runnable {
    public static void main(String[] args) throws Exception {
        // Create a Slave
        new Thread(new Slave(Slave.HOSTNAME, Slave.PORT)).start();
    }

    // Hostname of the Server
    private static final String HOSTNAME = "localhost";

    // Port of the Server
    private static final int PORT = 12345;

    // Batch Size
    public static final int BATCH = 1000;

    // Name of the Slave
    private String name;

    // Create input and output streams
    private DataInputStream in;
    private DataOutputStream out;

    // Create a socket
    private Socket socket;

    // Create a Console
    private Console console;

    public Slave(String hostname, int port) {
        this.console = new Console("Slave");

        try {
            // Create a socket
            this.socket = new Socket(hostname, port);

            // Set the name of the Slave according to the Local Socket Address
            this.name = "Slave " + String.valueOf(this.socket.getLocalSocketAddress()).split(":")[1];
            console.setName(this.name);

            // Create input and output streams
            this.in = new DataInputStream(this.socket.getInputStream());
            this.out = new DataOutputStream(this.socket.getOutputStream());
        }
        catch (Exception e) {
            this.console.log("Unable to connect to the Master.");
        }
    }

    @Override
    public void run() {
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
                int threads = Integer.parseInt(range[2]);

                console.log("Received instructions to find the prime numbers from " + start + " to " + end + " using " + threads + " thread/s.");

                // Compute the Prime Numbers
                compute(start, end, threads);

                // Log completion
                console.log("Waiting for new instructions again.");
            }
        }
        catch (Exception e) {
            console.log("Our Master freaking died! Dobby is a free elf!");
        }
    }

    /**
     * Broadcast a message to the server
     * @param message - the message to broadcast
     * @throws IOException 
     */
    public void broadcast(String message) throws IOException {
        out.writeUTF("[" + name + "]: " + message);
        out.flush();
    }

    public void broadcast(int number) throws IOException {
        out.writeInt(number);
        out.flush();
    }

    public void broadcast(ArrayList<Integer> batch) throws IOException {
        // Convert integers to byte array
        byte[] byteBatch = new byte[batch.size() * 4]; // 4 bytes per integer

        for (int i = 0; i < batch.size(); i++) {
            ByteBuffer.wrap(byteBatch, i * 4, 4).putInt(batch.get(i));
        }

        // Send batch size first
        out.writeInt(batch.size());

        // Send byte array
        out.write(byteBatch);
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
        ArrayList<Integer> primes = new PrimeCalculator(start, end, threads).execute();

        ArrayList<Integer> batch = new ArrayList<Integer>();

        for (int i = 0; i < primes.size(); i += BATCH) {
            // Erase the batch
            batch.clear();
        
            // Broadcast that you are still sending a batch
            broadcast(true);
        
            // Fill the Batch
            for (int j = i; j < i + BATCH && j < primes.size(); j++) {
                batch.add(primes.get(j));
            }
        
            // Send the batch
            broadcast(batch);
        
            // UI/UX Console
            console.clear();
            console.log("Received instructions to find the prime numbers from " + start + " to " + end + " using " + threads + " thread/s.");
            console.log("Found " + primes.size() + "! Uploading " + i + "/" + primes.size() + " prime numbers to the Master.");
        }

        // By broadcasting false, you are telling the server that the prime numbers are done
        broadcast(false);

        console.clear();
        console.log("Received instructions to find the prime numbers from " + start + " to " + end + " using " + threads + " thread/s.");
        console.log("Successfully uploaded " + primes.size() + " prime numbers to the Master.");
    }
}
