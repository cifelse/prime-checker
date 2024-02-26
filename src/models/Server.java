package src.models;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    // Set the name of the Server
    public static final String NAME = "Server";

    // Set the port numbers
    public static final int SLAVE_PORT = 12345;
    public static final int CLIENT_PORT = 8000;

    // Create a list of slaves and clients
    public static final CopyOnWriteArrayList<Socket> slaves = new CopyOnWriteArrayList<>();
    public static final CopyOnWriteArrayList<Integer> primes = new CopyOnWriteArrayList<>();

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

        /**
         * Divide the range into subranges
         * @param start - the start of the range
         * @param end - the end of the range
         * @param divisions - the number of divisions
         * @return
         */
        public static List<int[]> divideRange(int start, int end, int divisions) {
            if (divisions <= 0) {
                throw new IllegalArgumentException("Number of divisions must be positive");
            }

            if (start > end) {
                throw new IllegalArgumentException("Start must be less than or equal to end");
            }

            int rangeLength = end - start + 1;
            int subrangeLength = rangeLength / divisions;
            int remainder = rangeLength % divisions;

            List<int[]> ranges = new ArrayList<>();
            int currentStart = start;

            for (int i = 0; i < divisions; i++) {
                int subrangeEnd = currentStart + subrangeLength - 1;

                if (i < remainder) subrangeEnd++;

                ranges.add(new int[]{ currentStart, subrangeEnd });
                currentStart += subrangeLength;
            }

            return ranges;
        }
        /**
         * The main function for the ClientHandler
         */
        @Override
        public void run() {
            try {
                while (true) {
                    Socket clientSocket = clientServerSocket.accept();

                    this.in = new DataInputStream(clientSocket.getInputStream());
                    this.out = new DataOutputStream(clientSocket.getOutputStream());

                    // Console Log
                    console.log("Client connected at " + clientSocket.getLocalSocketAddress());

                    // Broadcast confirmation
                    this.broadcast("You are successfully connected.");

                    // Receive client request
                    String raw = in.readUTF();
                    String[] params = raw.split(" ");
                    int start = Integer.parseInt(params[0]);
                    int end = Integer.parseInt(params[1]);
                    int threads = Integer.parseInt(params[2]);

                    // Send confirmation to the client
                    this.broadcast("Received ranges " + start + " to " + end + " with " +  threads + " from the client.");

                    // Compute the Prime Numbers
                    if (slaves.size() == 0) {
                        List<Integer> results = new Calculator(start, end, threads).execute();

                        synchronized (primes) {
                            primes.addAll(results);
                        }
                    }
                    else {
                        // Divide the work into subranges
                        List<int[]> ranges = divideRange(start, end, slaves.size() + 1);
                        
                        // Create a new thread for the master to work on
                        new Thread(() -> {
                            List<Integer> results = new Calculator(ranges.get(0)[0], ranges.get(0)[1], threads).execute();

                            synchronized (primes) {
                                primes.addAll(results);
                            }
                        }).start();

                        // Track all threads
                        List<Thread> threadsList = new ArrayList<>();
                    
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
                                    synchronized (primes) {
                                        for (String prime : in.readUTF().split("]: ")[1].split(" ")) {
                                            primes.add(Integer.parseInt(prime));
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

                        for (Thread thread : threadsList) {
                            try {
                                thread.join();
                            }
                            catch (InterruptedException e) {
                                console.log(e.toString());
                            }
                        }

                        broadcast("Prime Numbers: " + primes);
                    }

                    console.log("Successfully served Client " + clientSocket.getLocalSocketAddress());

                    // Wait for the client to disconnect
                    raw = in.readUTF();

                    if (raw.equals("END")) {
                        console.log("Client " + clientSocket.getLocalSocketAddress() + " disconnected.");
                        clientSocket.close();
                    }
                }
            }
            catch (IOException e) {
                System.out.println("\n" + e);
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
            try {
                while (true) {
                    Socket slaveSocket = slaveServerSocket.accept();

                    DataInputStream in = new DataInputStream(slaveSocket.getInputStream());
                    DataOutputStream out = new DataOutputStream(slaveSocket.getOutputStream());

                    // Notify Server of Connection
                    console.log(in.readUTF());

                    // Broadcast confirmation
                    out.writeUTF("[Master]: You are successfully connected. Wait for instructions.");
                    out.flush();

                    // Add the slave to the list
                    slaves.add(slaveSocket);
                }
            }
            catch (IOException e) {
                System.out.println("\n" + e);
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
