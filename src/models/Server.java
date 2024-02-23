package src.models;

import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    public static final CopyOnWriteArrayList<SlaveHandler> slaves = new CopyOnWriteArrayList<>();
    public static class SlaveHandler implements Runnable {
        private final Socket clientSocket;

        public SlaveHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                // Create input and output streams
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
    
                // Read data from the client
                String message = in.readUTF();
                System.out.println(message);

                // Send Confirmation
                out.writeUTF("Master: You are successfully connected.\n");
                out.flush();
    
                // TODO
                out.writeUTF("1 10 8");
                out.flush();
            }
            catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 8080; // Change this to your desired port number

        // Create a server socket
        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("Server started on port " + port);

        // Continuously accept connections  
        while (true) {
            // Accept a new connection for each Client
            new SlaveHandler(serverSocket.accept()).run();
        }
    }
}
