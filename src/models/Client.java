package src.models;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        String hostname = "localhost";
        int port = 8080;

        int start = 0;
        int end = 10;
        
        // Create a socket
        Socket socket = new Socket(hostname, port);

        // Create input and output streams
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());

        // Send the instructions to the master
        out.writeUTF(start + " " + end);
        out.flush();

        // Receive a response from the server
        String response = in.readUTF();
        System.out.println(response);

        // Close the connection
        socket.close();
    }
}
