package rpn.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public Client() {

    }

    public static void main(String args[]) {
        try {
            Socket socket = new Socket("0.0.0.0", 43594);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String s;

            while ((s = in.readLine()) != null) {
                System.out.println("Server says: " + s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}