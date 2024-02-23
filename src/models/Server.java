package src.models;

import java.io.*;
import java.net.*;

public class Server {
    public static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                System.out.println("Client connected from " + clientSocket.getInetAddress());

                // Create input and output streams
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
    
                // Read data from the client
                String message = in.readUTF();
                System.out.println("Client message: " + message);
    
                // Echo the message back to the client
                out.writeUTF("Server response: " + message);
                out.flush();
    
                clientSocket.close();
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
            new ClientHandler(serverSocket.accept()).run();
        }
    }
}
