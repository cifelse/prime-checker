package src.models;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        String hostname = "localhost";
        int port = 8080;
        
        // Create a socket
        Socket socket = new Socket(hostname, port);

        // Create input and output streams
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        // Send a message to the server
        String message = "Hello from the client!";
        out.writeUTF(message);
        out.flush();

        // Receive a response from the server
        String response = in.readUTF();
        System.out.println("Server response: " + response);

        // Close the connection
        socket.close();
    }
}
