package src.models;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Server {
    // Set the name of the Server
    public static final String NAME = "Server";

    // Set the port numbers
    public static final int SLAVE_PORT = 12345;
    public static final int CLIENT_PORT = 8000;

    // Set the batch size
    public static final int BATCH = 1000;

    // Create a list of slaves/workers
    public static final ArrayList<Socket> slaves = new ArrayList<Socket>();

    // Create a new Console
    private static final Console console = new Console(NAME);

    /**
     * The ClientHandler class is a Runnable class that listens for clients and handles their requests.
     */
    public static class ClientHandler implements Runnable {
        private final ServerSocket clientServerSocket;
        private DataInputStream in;
        private DataOutputStream out;

        /**
         * Constructor for the ClientHandler
         * @throws IOException - if an I/O error occurs
         */
        public ClientHandler() throws IOException {
            this.clientServerSocket = new ServerSocket(Server.CLIENT_PORT);
            console.log("Listening for clients at port " + Server.CLIENT_PORT);
        }

        /**
         * Broadcast a message to the client
         * @param message - the message to broadcast
         * @throws IOException - if an I/O error occurs
         */
        public void broadcast(String message) throws IOException {
            out.writeUTF("[" + NAME + "]: " + message);
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
         * Divide the range into subranges
         * @param start - the start of the range
         * @param end - the end of the range
         * @param divisions - the number of divisions
         * @return a list of subranges
         */
        public static List<int[]> divideRange(int start, int end, int divisions) {
            if (divisions <= 0) {
                throw new IllegalArgumentException("Number of divisions must be positive");
            }
    
            if (start > end) {
                throw new IllegalArgumentException("Start must be less than or equal to end");
            }
    
            int rangeLength = end - start + 1;
            int subRangeLength = rangeLength / divisions;
            int remainder = rangeLength % divisions;
    
            List<int[]> ranges = new ArrayList<>();
            int currentStart = start;
    
            for (int i = 0; i < divisions; i++) {
                int subrangeEnd = currentStart + subRangeLength - 1;
    
                if (i < remainder) {
                    subrangeEnd++;
                }
    
                ranges.add(new int[]{currentStart, subrangeEnd});
                currentStart = subrangeEnd + 1;
            }
    
            return ranges;
        }

        /**
         * The main function for the ClientHandler
         */
        @Override
        public void run() {
            while (true) {
                try {
                    // Wait and accept a client
                    Socket clientSocket = clientServerSocket.accept();

                    // Reset the list of primes
                    ArrayList<Integer> primes = new ArrayList<Integer>();
    
                    this.in = new DataInputStream(clientSocket.getInputStream());
                    this.out = new DataOutputStream(clientSocket.getOutputStream());
    
                    // Console Log
                    console.log("Client connected at " + clientSocket.getLocalSocketAddress());
    
                    // Broadcast to Client that connection is successful.
                    this.broadcast("You are successfully connected.");
    
                    // Receive client request
                    String raw = in.readUTF();

                    // Clean and get params
                    String[] params = raw.split(" ");
                    int start = Integer.parseInt(params[0]);
                    int end = Integer.parseInt(params[1]);
                    int threads = Integer.parseInt(params[2]);
    
                    // Send param confirmation to the client
                    this.broadcast("Getting the Prime Numbers in the range " + start + " to " + end + " with " +  threads + " thread/s.");
    
                    // Compute the Prime Numbers
                    if (slaves.size() == 0) {
                        ArrayList<Integer> results = new PrimeCalculator(start, end, threads).execute();
    
                        synchronized (primes) {
                            primes.addAll(results);
                        }
                    }
                    else {
                        // Track all threads
                        List<Thread> threadsList = new ArrayList<>();

                        // Divide the work into subranges (+1 add master)
                        List<int[]> ranges = divideRange(start, end, slaves.size() + 1);
                        
                        console.log("Handling the prime numbers from " + ranges.get(0)[0] + " to " + ranges.get(0)[1] + " using " + threads + " thread/s.");

                        // Create a new thread for the master to work on
                        Thread masterThread = new Thread(() -> {
                            ArrayList<Integer> results = new PrimeCalculator(ranges.get(0)[0], ranges.get(0)[1], threads).execute();

                            synchronized (primes) {
                                primes.addAll(results);
                            }

                            console.log("Master Thread is done with the work. It found " + results.size() + " Prime Numbers.");
                        });

                        threadsList.add(masterThread);
                        masterThread.start();
                    
                        for (int i = 0; i < slaves.size(); i++) {
                            Socket slave = slaves.get(i);
    
                            DataOutputStream out = new DataOutputStream(slave.getOutputStream());
                            DataInputStream in = new DataInputStream(slave.getInputStream());
    
                            // Send the ranges to the slaves to work on
                            out.writeUTF(ranges.get(i + 1)[0] + " " + ranges.get(i + 1)[1] + " " + threads);
                            out.flush();
    
                            // Create a new thread for each slave to listen to the slaves' responses
                            Thread thread = new Thread(() -> {
                                try {
                                    // While the Boolean signal is True
                                    while (in.readBoolean()) {
                                        // Receive the size of the ArrayList
                                        int size = in.readInt();

                                        // Receive the byte array
                                        byte[] byteBatch = new byte[size * 4];
                                        in.readFully(byteBatch);

                                        // Convert byte array back to ArrayList of Integers
                                        for (int j = 0; j < size; j++) {
                                            int value = ByteBuffer.wrap(byteBatch, j * 4, 4).getInt();
                                            primes.add(value);
                                        }
                                    }
                                }
                                catch (IOException e) {
                                    console.log(e.toString());
                                }
                            });
    
                            threadsList.add(thread);
                            thread.start();
                        }
                        
                        // Wait for all slaves to be finished
                        for (Thread thread : threadsList) {
                            try {
                                thread.join();
                            }
                            catch (InterruptedException e) {
                                console.log(e.toString());
                            }
                        }
                    }

                    console.log("Everyone is done with the computation, there are " + primes.size() + " in total.");

                    this.broadcast("Got it! There are " + primes.size() + " Prime Numbers from " + start + " to " + end);

                    // Send the Total Prime Numbers to the Client
                    this.broadcast(primes.size());

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
                        console.log("Uploading " + (i + BATCH < primes.size() ? i + 1 : primes.size()) + "/" + primes.size() + " prime numbers to the Client.");
                    }

                    // Send the END signal to the Client
                    broadcast(false);
    
                    console.log("Successfully served Client " + clientSocket.getLocalSocketAddress());
    
                    // Wait for the Client to disconnect
                    raw = in.readUTF();
    
                    if (raw.equals("END")) {
                        console.log("Client " + clientSocket.getLocalSocketAddress() + " disconnected.");
                        clientSocket.close();
                    }
                }
                catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    public static class SlaveHandler implements Runnable {
        private final ServerSocket slaveServerSocket;

        public SlaveHandler() throws IOException {
            this.slaveServerSocket = new ServerSocket(Server.SLAVE_PORT);
            console.log("Listening for slaves at port " + Server.SLAVE_PORT);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Socket slaveSocket = slaveServerSocket.accept();

                    DataInputStream in = new DataInputStream(slaveSocket.getInputStream());
                    DataOutputStream out = new DataOutputStream(slaveSocket.getOutputStream());

                    // Notify Server of Connection
                    System.out.println(in.readUTF());

                    // Broadcast confirmation
                    out.writeUTF("[Master]: You are successfully connected. Wait for instructions.");
                    out.flush();

                    // Add the slave to the list
                    synchronized (slaves) {
                        slaves.add(slaveSocket);
                    }
                }
                catch (IOException e) {
                    System.out.println("\n" + e);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        // Clear the console
        console.clear();

        // Create a new Thread for the Clients
        new Thread(new ClientHandler()).start();
        
        // Create a new Thread for the Slaves
        new Thread(new SlaveHandler()).start();
    }
}
