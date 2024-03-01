package src.models;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Client {
    // Hostname of the Server
    private static final String HOSTNAME = "localhost";

    // Port of the Server
    private static final int PORT = 8000;

    // Create a Console
    private static final Console console = new Console("Client");

    /**
     * The main function for getting an input from the user
     * @param scanner - the Scanner Object
     * @param prompt - question to prompt to the user
     * @return the input of the user
     */
    public static String getUserInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    
    public static void main(String[] args) throws IOException {
        int[] threads = {16, 8, 4, 2, 1, 32, 64, 128, 256, 512, 1024};
        int start = 0, end = 100000000;
        ArrayList<String> results = new ArrayList<>();
        for (int t : threads){
            float average = 0;

            for (int i = 0; i < 3; i++){
                run_main(start, end, t);
            }

            for (int i = 0; i < 5; i++){
                long start_time = System.currentTimeMillis();
                run_main(start, end, t);
                long duration = System.currentTimeMillis() - start_time;

                average += duration;
            }
            average /= 5;

            results.add("Time: " + String.valueOf(average) + " Threads: " + t);
        }

        System.out.println();
        for (String s: results)
            System.out.println(s);
    }

    public static void run_main(int start, int end, int threads) throws IOException {
        // Clear the console
        // console.clear();

        // User Input
        // Scanner sc = new Scanner(System.in);

        // int start = Integer.parseInt(getUserInput(sc, "Enter start range: "));
        // int end = Integer.parseInt(getUserInput(sc, "Enter end range: "));
        // int threads = Integer.parseInt(getUserInput(sc, "Enter thread count: "));

        // Clear the console
        console.clear();

        console.log("Sending the parameters [" + start + ", " + end + ", " + threads + "] to the server.\n");

        // Create a socket
        Socket socket = new Socket(HOSTNAME, PORT);

        // Create input and output streams
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());

        // Receive the first response from the server (Connection Success)
        System.out.println(in.readUTF());

        // Send the instructions to the server
        out.writeUTF(start + " " + end + " " + threads);
        out.flush();

        // Receive the second response from the server (Param Confirmation)
        System.out.println(in.readUTF());

        // Receive the third response from the server (Done Computation)
        System.out.println(in.readUTF());

        // Receive the total prime numbers from the server (Sending Commence)
        int total = in.readInt();

        // Receive the Prime Numbers
        ArrayList<Integer> primes = new ArrayList<Integer>();
        
        // While the Boolean signal is True
        while (in.readBoolean()) {
            // Receive the size of the ArrayList
            int size = in.readInt();

            // Receive the byte array
            byte[] byteBatch = new byte[size * 4];
            in.readFully(byteBatch);

            // Convert byte array back to ArrayList of Integers
            for (int i = 0; i < size; i++) {
                int value = ByteBuffer.wrap(byteBatch, i * 4, 4).getInt();
                primes.add(value);
            }
        
            console.clear();
            console.log("There are " + total + " Prime Numbers from " + start + " to " + end + ".");
            console.log("Downloading " + primes.size() + "/" + total + " Prime Numbers.");
        }

        // Send the END signal to the server
        out.writeUTF("END");
        out.flush();

        console.log("There are " + primes.size() + " Prime Numbers from " + start + " to " + end + ".");
        // String choice = getUserInput(sc, "[Client]: Download successful. Shall I proceed to show them? [Y/n]: ");

        // Show the Prime Numbers
        // if (!choice.isEmpty() && (choice.contains("Y") || choice.contains("y"))) {
        //     console.clear();
        //     console.log(primes.stream().map(Object::toString).collect(Collectors.joining(", ")));
        // }

        // Close the Scanner
        // sc.close();

        // Close the connection
        socket.close();
        System.gc();
    }
}
