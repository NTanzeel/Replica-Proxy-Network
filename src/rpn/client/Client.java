package rpn.client;

import java.io.DataInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public Client() {

    }

    public static void main(String args[]) {
        try {
            Socket socket = new Socket("0.0.0.0", 43594);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            DataInputStream in = new DataInputStream(socket.getInputStream());


            int response = in.readInt();

            System.out.println("Reached.");

            if (response == -1) {
                System.out.println("Unable to connect to server, limit reached.");
                socket.close();
            } else {
                System.out.println("Connected to server with ID: " + response);
            }

            while (true) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}